package com.legobmw99.allomancy.test.modules.extras;

import com.legobmw99.allomancy.modules.powers.data.AllomancerAttachment;
import com.legobmw99.allomancy.test.util.TattleTaleStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;
import net.neoforged.testframework.annotation.ForEachTest;
import net.neoforged.testframework.annotation.TestHolder;
import net.neoforged.testframework.gametest.EmptyTemplate;
import net.neoforged.testframework.gametest.ExtendedGameTestHelper;
import net.neoforged.testframework.gametest.GameTest;

@ForEachTest(groups = "command")
public class CommandTest {

    @GameTest
    @EmptyTemplate("1x3x1")
    @TestHolder(description = "Tests that you can add and remove powers")
    public static void allomancyPowerWorks(ExtendedGameTestHelper helper) {
        var player = helper.makeOpMockPlayer(Commands.LEVEL_GAMEMASTERS);
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
    @TestHolder(description = "Tests that normal players can't use /ap")
    public static void allomancyPowerNeedsPerms(ExtendedGameTestHelper helper) {
        var player = helper.makeOpMockPlayer(Commands.LEVEL_ALL);
        var stack = TattleTaleStack.createCommandSourceStack(player);

        helper.startSequence()
              // add random power
              .thenExecute(() -> helper
                      .getLevel()
                      .getServer()
                      .getCommands()
                      .performPrefixedCommand(stack, "/ap add random")).thenExecuteAfter(5, () -> {

                  helper.assertTrue(stack.hadError(), "No permission error");
                  helper.assertTrue(AllomancerAttachment.get(player).isUninvested(), "Player got invested");
              }).thenSucceed();
    }

    @GameTest
    @EmptyTemplate("1x3x1")
    @TestHolder(description = "Tests that anyone can use /ap get")
    public static void allomancyGet(ExtendedGameTestHelper helper) {
        var player = helper.makeOpMockPlayer(Commands.LEVEL_ALL);
        player.snapTo(Vec3.atCenterOf(helper.absolutePos(BlockPos.ZERO)));
        var data = AllomancerAttachment.get(player);
        data.setMistborn();
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

