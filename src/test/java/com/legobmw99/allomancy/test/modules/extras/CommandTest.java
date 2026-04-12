package com.legobmw99.allomancy.test.modules.extras;

import com.legobmw99.allomancy.modules.powers.data.AllomancerAttachment;
import com.legobmw99.allomancy.test.util.TattleTaleStack;
import net.minecraft.server.permissions.LevelBasedPermissionSet;
import net.minecraft.world.level.GameType;
import net.neoforged.testframework.annotation.TestHolder;
import net.neoforged.testframework.gametest.EmptyTemplate;
import net.neoforged.testframework.gametest.ExtendedGameTestHelper;
import net.neoforged.testframework.gametest.GameTest;

import java.util.Optional;

public class CommandTest {

    @GameTest
    @EmptyTemplate("1x3x1")
    @TestHolder(description = "Tests that you can add and remove powers", groups = "command_op")
    public static void allomancyPowerWorks(ExtendedGameTestHelper helper) {
        var player = helper.makeTickingMockServerPlayerInLevel(GameType.DEFAULT_MODE);
        AllomancerAttachment.get(player).setUninvested();
        helper
                .getLevel()
                .getServer()
                .getPlayerList()
                .op(player.nameAndId(), Optional.of(LevelBasedPermissionSet.GAMEMASTER), Optional.empty());

        var stack = TattleTaleStack.createCommandSourceStack(player);

        helper.startSequence()
              // add random power
              .thenExecute(() -> helper
                      .getLevel()
                      .getServer()
                      .getCommands()
                      .performPrefixedCommand(stack, "/ap add random")).thenExecuteAfter(5, () -> {
                  helper.assertFalse(stack.hadError(), "Command failed: " + stack.errors());
                  helper.assertFalse(AllomancerAttachment.get(player).isUninvested(), "Player is still " +
                                                                                      "uninvested");

              })
              // remove it
              .thenExecute(() -> helper
                      .getLevel()
                      .getServer()
                      .getCommands()
                      .performPrefixedCommand(stack, "/allomancy remove random")).thenExecuteAfter(5, () -> {
                  helper.assertFalse(stack.hadError(), "Command failed: " + stack.errors());
                  helper.assertTrue(AllomancerAttachment.get(player).isUninvested(), "Player is still invested");
              }).thenSucceed();
    }

    @GameTest
    @EmptyTemplate("1x3x1")
    @TestHolder(description = "Tests that normal players can't use /ap", groups = "command_deop")
    public static void allomancyPowerNeedsPerms(ExtendedGameTestHelper helper) {
        var player = helper.makeTickingMockServerPlayerInLevel(GameType.DEFAULT_MODE);
        AllomancerAttachment.get(player).setUninvested();
        var stack = TattleTaleStack.createCommandSourceStack(player);
        player.level().getServer().getPlayerList().deop(player.nameAndId());

        helper.startSequence()
              // add random power
              .thenExecute(() -> helper
                      .getLevel()
                      .getServer()
                      .getCommands()
                      .performPrefixedCommand(stack, "/ap add random")).thenExecuteAfter(5, () -> {

                  helper.assertTrue(stack.hadError(), "No permission error");
                  helper.assertTrue(AllomancerAttachment.get(player).isUninvested(),
                                    "Player got invested" + AllomancerAttachment.get(player).getPowerCount());
              }).thenSucceed();
    }

    @GameTest
    @EmptyTemplate("1x3x1")
    @TestHolder(description = "Tests that anyone can use /ap get", groups = "command_deop")
    public static void allomancyGet(ExtendedGameTestHelper helper) {
        var player = helper.makeTickingMockServerPlayerInLevel(GameType.DEFAULT_MODE);
        player.level().getServer().getPlayerList().deop(player.nameAndId());
        AllomancerAttachment.get(player).setMistborn();
        var stack = TattleTaleStack.createCommandSourceStack(player);

        helper.startSequence()
              // add random power
              .thenExecute(() -> helper
                      .getLevel()
                      .getServer()
                      .getCommands()
                      .performPrefixedCommand(stack, "/allomancy get")).thenExecuteAfter(5, () -> {
                  helper.assertFalse(stack.hadError(), "Command failed: " + stack.errors());
                  helper.assertValueEqual(stack.results(),
                                          player.getScoreboardName() + " currently has Allomantic powers: all",
                                          "Get command result");
              }).thenSucceed();
    }


}

