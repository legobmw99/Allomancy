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
 * and on demand this list is swapped in. Reads should never contend for a lock.
 * This is a simplified version of the ideas in something like https://github.com/jonhoo/left-right/,
 * or double buffered rendering, but we want to clear our list after it is swapped out and
 * assume a single reader that works relatively quickly, so we block to clear
 */
public class DoubleBufferList<T> {
    private final List<T> list_a = new ArrayList<>();
    private final List<T> list_b = new ArrayList<>();

    private final Lock lock_a = new ReentrantLock();
    private final Lock lock_b = new ReentrantLock();

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
        if (this.AorB.get() % 2 == 0) {
            this.lock_a.lock();
            try {
                this.list_a.forEach(f);
            } finally {
                this.lock_a.unlock();
            }
        } else {
            this.lock_b.lock();
            try {
                this.list_b.forEach(f);
            } finally {
                this.lock_b.unlock();
            }
        }
    }


    /**
     * Write to the unobserved list.
     * Nothing added here wil be visible until
     * a commitAndClear is completed.
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
    public void commitAndClear() {
        int newAB = this.AorB.incrementAndGet();
        if (newAB % 2 != 0) {
            this.lock_a.lock();
            this.list_a.clear();
            this.lock_a.unlock();
        } else {
            this.lock_b.lock();
            this.list_b.clear();
            this.lock_b.unlock();
        }
    }

    public void clearBothAsync(ExecutorService ex) {
        ex.submit(() -> {
            this.lock_a.lock();
            try {
                this.list_a.clear();
            } finally {
                this.lock_a.unlock();
            }
            this.lock_b.lock();
            try {
                this.list_b.clear();
            } finally {
                this.lock_b.unlock();
            }
        });
    }
}