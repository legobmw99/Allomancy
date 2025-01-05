package com.legobmw99.allomancy.test.modules.extras;

import com.legobmw99.allomancy.modules.powers.data.AllomancerAttachment;
import net.minecraft.commands.CommandSource;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import net.neoforged.testframework.annotation.ForEachTest;
import net.neoforged.testframework.annotation.TestHolder;
import net.neoforged.testframework.gametest.EmptyTemplate;
import net.neoforged.testframework.gametest.ExtendedGameTestHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

@ForEachTest(groups = "command")
public class CommandTest {

    @GameTest
    @EmptyTemplate("1x3x1")
    @TestHolder
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

                  helper.assertTrue(stack.errors.isEmpty(), "Command failed: " + stack.errors());
                  helper.assertFalse(player.getData(AllomancerAttachment.ALLOMANCY_DATA).isUninvested(),
                                     "Player is still uninvested");

              })
              // remove it
              .thenExecute(() -> helper
                      .getLevel()
                      .getServer()
                      .getCommands()
                      .performPrefixedCommand(stack, "/allomancy remove random")).thenExecuteAfter(5, () -> {
                  helper.assertTrue(stack.errors.isEmpty(), "Command failed: " + stack.errors());
                  helper.assertTrue(player.getData(AllomancerAttachment.ALLOMANCY_DATA).isUninvested(),
                                    "Player is still invested");
              }).thenSucceed();
    }

    @GameTest
    @EmptyTemplate("1x3x1")
    @TestHolder
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

                  helper.assertFalse(stack.errors.isEmpty(), "No permission error");
                  helper.assertTrue(player.getData(AllomancerAttachment.ALLOMANCY_DATA).isUninvested(),
                                    "Player got invested");
              }).thenSucceed();
    }

    @GameTest
    @EmptyTemplate("1x3x1")
    @TestHolder
    public static void allomancyGet(ExtendedGameTestHelper helper) {
        var player = helper.makeOpMockPlayer(Commands.LEVEL_ALL);
        player.moveTo(Vec3.atCenterOf(helper.absolutePos(BlockPos.ZERO)));
        var data = player.getData(AllomancerAttachment.ALLOMANCY_DATA);
        data.setMistborn();
        var stack = TattleTaleStack.createCommandSourceStack(player);

        helper.startSequence()
              // add random power
              .thenExecute(() -> helper
                      .getLevel()
                      .getServer()
                      .getCommands()
                      .performPrefixedCommand(stack, "/allomancy get")).thenExecuteAfter(5, () -> {
                  helper.assertTrue(stack.errors.isEmpty(), "Command failed: " + stack.errors());
                  helper.assertValueEqual(stack.results(),
                                          player.getScoreboardName() + " currently has Allomantic powers: all",
                                          "Get command result");
              }).thenSucceed();
    }


    public final static class TattleTaleStack extends CommandSourceStack {
        public static TattleTaleStack createCommandSourceStack(ServerPlayer player) {
            return new TattleTaleStack(CommandSource.NULL, player.position(), player.getRotationVector(),
                                       player.serverLevel(), player.getPermissionLevel(),
                                       player.getName().getString(), player.getDisplayName(),
                                       player.level().getServer(), player);
        }

        public TattleTaleStack(CommandSource p_81302_,
                               Vec3 p_81303_,
                               Vec2 p_81304_,
                               ServerLevel p_81305_,
                               int p_81306_,
                               String p_81307_,
                               Component p_81308_,
                               MinecraftServer p_81309_,
                               Entity p_81310_) {
            super(p_81302_, p_81303_, p_81304_, p_81305_, p_81306_, p_81307_, p_81308_, p_81309_, p_81310_);
        }

        final List<Component> errors = new ArrayList<>();
        final List<Component> results = new ArrayList<>();

        public String errors() {
            return commaSeparate(this.errors);

        }

        public String results() {
            return commaSeparate(this.results);
        }

        @Override
        public void sendSuccess(Supplier<Component> messageSupplier, boolean allowLogging) {
            var comp = messageSupplier.get();
            results.add(comp);

            super.sendSuccess(() -> comp, allowLogging);
        }

        @Override
        public void sendSystemMessage(Component message) {
            results.add(message);

            super.sendSystemMessage(message);
        }

        @Override
        public void sendFailure(Component failure) {
            errors.add(failure);
            super.sendFailure(failure);
        }

        static String commaSeparate(List<Component> components) {
            StringBuilder s = new StringBuilder();
            for (var r : components) {
                s.append(r.getString());
                s.append(", ");
            }
            var len = s.length();
            if (len > 2) {
                s.delete(len - 2, len);
            }
            return s.toString();
        }
    }


}

