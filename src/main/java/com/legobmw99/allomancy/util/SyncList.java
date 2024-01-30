package com.legobmw99.allomancy.util;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Consumer;

/**
 * A wrapper around 2 array lists that allows "concurrent" modification
 * while maintaining very fast reads at the cost of slower/buffered writes.
 * Writes are issued to a list which is not currently a candidate for iteration,
 * and on demand this list is swapped in. The lock required to swap is only held
 * for the length of one atomic integer increment.
 * This is a simplified version of the ideas in something like https://github.com/jonhoo/left-right/,
 * but we assume a single reader that works relatively quickly, so we block do to our atomic swap
 */
public class SyncList<T> {
    private final List<T> list_a = new ArrayList<>();
    private final List<T> list_b = new ArrayList<>();

    private final Lock swapLock = new ReentrantLock();

    /**
     * When this is even, we are reading A and writing B
     */
    private final AtomicInteger AorB = new AtomicInteger(0);

    /**
     * Intended to be invoked from the main thread.
     * Holds a lock to prevent swapping for entire iteration duration.
     * Writes will still proceed to the unobserved list.
     */
    public void forEach(Consumer<T> f) {
        this.swapLock.lock();
        try {
            if (this.AorB.get() % 2 == 0) {
                this.list_a.forEach(f);
            } else {
                this.list_b.forEach(f);
            }
        } finally {
            this.swapLock.unlock();
        }
    }

    /**
     * Write to the unobserved list.
     * Nothing added here wil be visible until
     * a swapAndClearOld is completed.
     */
    public void add(T t) {
        if (this.AorB.get() % 2 != 0) {
            this.list_a.add(t);
        } else {
            this.list_b.add(t);
        }
    }

    /**
     * Swap which list is observable as soon as the lock can be acquired,
     * and clear the now-unobserved list.
     * Intended to be invoked from a thread other than main
     */
    public void swapAndClearOld() {
        this.swapLock.lock();
        int newAB = this.AorB.incrementAndGet();
        this.swapLock.unlock();
        if (newAB % 2 != 0) {
            this.list_a.clear();
        } else {
            this.list_b.clear();
        }
    }

    public void clearBothAsync(ExecutorService ex) {
        ex.submit(() -> {
            this.swapLock.lock();
            try {
                this.list_a.clear();
                this.list_b.clear();
            } finally {
                this.swapLock.unlock();
            }
        });
    }
}
