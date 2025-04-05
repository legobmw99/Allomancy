package com.legobmw99.allomancy.test.util;

import net.minecraft.gametest.framework.GameTestAssertException;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.Rotation;
import net.neoforged.testframework.TestFramework;
import net.neoforged.testframework.gametest.GameTestData;
import net.neoforged.testframework.impl.TestFrameworkImpl;
import net.neoforged.testframework.impl.test.AbstractTest;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Consumer;

// Similar to net.neoforged.testframework.impl.test.MethodBasedGameTestTest
public class CallbackTest extends AbstractTest.Dynamic {
    private final Consumer<AllomancyTestHelper> callback;

    public CallbackTest(String id,
                        Consumer<AllomancyTestHelper> callback,
                        String structureName,
                        @Nullable String group,
                        @Nullable String description) {
        this.callback = callback;

        this.id = id;
        this.visuals = new Visuals(Component.literal(TestFrameworkImpl.capitaliseWords(id, "_")),
                                   description == null ? List.of() : List.of(Component.literal(description)));
        if (group != null) {
            this.groups.add(group);
        }
        this.gameTestData =
                new GameTestData(null, structureName, true, 1, 1, this::onGameTest, 100, 0, Rotation.NONE, false,
                                 false);
    }

    CallbackTest(String id, Consumer<AllomancyTestHelper> callback, String structureName) {
        this(id, callback, structureName, null, null);
    }

    @Override
    public void init(TestFramework framework) {
        super.init(framework);

        onGameTest(AllomancyTestHelper.class, helper -> {
            try {
                callback.accept(helper);
            } catch (GameTestAssertException exception) {
                throw exception;
            } catch (Throwable exception) {
                throw new RuntimeException("Encountered exception running callback-based gametest test: " + callback,
                                           exception);
            }
        });
    }
}
