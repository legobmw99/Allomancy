package com.legobmw99.allomancy.modules.powers.command;

import com.legobmw99.allomancy.modules.powers.data.AllomancyCapability;
import com.legobmw99.allomancy.modules.powers.network.AllomancyDataPacket;
import com.legobmw99.allomancy.network.Network;
import com.legobmw99.allomancy.util.Metal;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.tree.LiteralCommandNode;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.EntityArgument;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.text.TranslationTextComponent;

import java.util.function.Predicate;

public class AllomancyPowerCommand {

    protected static final String[] names = new String[Metal.values().length + 1];

    static {
        int i = 0;
        for (Metal mt : Metal.values()) {
            names[i++] = mt.getName();
        }
        names[i] = "all";
    }

    private static Predicate<CommandSource> permissions(int level) {
        return (player) -> player.hasPermission(level);
    }

    public static void register(CommandDispatcher<CommandSource> dispatcher) {

        LiteralArgumentBuilder<CommandSource> root = Commands.literal("allomancy").requires(permissions(0));
        root.then(Commands
                          .literal("get")
                          .requires(permissions(0))
                          .executes(ctx -> getPowers(ctx, false))
                          .then(Commands.argument("targets", EntityArgument.players()).executes(ctx -> getPowers(ctx, true))));

        root.then(Commands
                          .literal("add")
                          .requires(permissions(2))
                          .then(Commands
                                        .argument("type", AllomancyPowerType.INSTANCE)
                                        .executes(ctx -> addPower(ctx, false))
                                        .then(Commands.argument("targets", EntityArgument.players()).executes(ctx -> addPower(ctx, true)))));

        root.then(Commands
                          .literal("remove")
                          .requires(permissions(2))
                          .then(Commands
                                        .argument("type", AllomancyPowerType.INSTANCE)
                                        .executes(ctx -> removePower(ctx, false))
                                        .then(Commands.argument("targets", EntityArgument.players()).executes(ctx -> removePower(ctx, true)))));


        LiteralCommandNode<CommandSource> command = dispatcher.register(root);

        dispatcher.register(Commands.literal("ap").requires(permissions(0)).redirect(command));
    }


    private static int getPowers(CommandContext<CommandSource> ctx, boolean hasPlayer) throws CommandSyntaxException {
        int i = 0;
        if (hasPlayer) {
            for (ServerPlayerEntity p : EntityArgument.getPlayers(ctx, "targets")) {
                getPowers(ctx, p);
                i++;
            }
        } else {
            getPowers(ctx, ctx.getSource().getPlayerOrException());
            i = 1;
        }

        return i;
    }

    private static void getPowers(CommandContext<CommandSource> ctx, ServerPlayerEntity player) {
        StringBuilder powers = new StringBuilder();
        player.getCapability(AllomancyCapability.PLAYER_CAP).ifPresent(data -> {
            if (data.isMistborn()) {
                powers.append("all");
            } else if (data.isUninvested()) {
                powers.append("none");
            } else {
                for (Metal mt : Metal.values()) {
                    if (data.hasPower(mt)) {
                        if (powers.length() == 0) {
                            powers.append(mt.getName());
                        } else {
                            powers.append(", ").append(mt.getName());
                        }
                    }
                }
            }
        });
        ctx.getSource().sendSuccess(new TranslationTextComponent("commands.allomancy.getpowers", player.getDisplayName(), powers.toString()), true);
    }

    private static int addPower(CommandContext<CommandSource> ctx, boolean hasPlayer) throws CommandSyntaxException {
        int i = 0;
        if (hasPlayer) {
            for (ServerPlayerEntity p : EntityArgument.getPlayers(ctx, "targets")) {
                addPower(ctx, p);
                i++;
            }
        } else {
            addPower(ctx, ctx.getSource().getPlayerOrException());
            i = 1;
        }
        return i;
    }

    private static void addPower(CommandContext<CommandSource> ctx, ServerPlayerEntity player) {
        String type = ctx.getArgument("type", String.class);
        player.getCapability(AllomancyCapability.PLAYER_CAP).ifPresent(data -> {
            if (type.equalsIgnoreCase("all")) {
                data.setMistborn();
            } else {
                Metal mt = Metal.valueOf(type.toUpperCase());
                data.addPower(mt);
            }

            Network.sendTo(new AllomancyDataPacket(data, player), player);
        });
        ctx.getSource().sendSuccess(new TranslationTextComponent("commands.allomancy.addpower", player.getDisplayName(), type), true);
    }

    private static int removePower(CommandContext<CommandSource> ctx, boolean hasPlayer) throws CommandSyntaxException {
        int i = 0;
        if (hasPlayer) {
            for (ServerPlayerEntity p : EntityArgument.getPlayers(ctx, "targets")) {
                removePower(ctx, p);
                i++;
            }
        } else {
            removePower(ctx, ctx.getSource().getPlayerOrException());
            i = 1;
        }
        return i;
    }

    private static void removePower(CommandContext<CommandSource> ctx, ServerPlayerEntity player) {
        String type = ctx.getArgument("type", String.class);
        player.getCapability(AllomancyCapability.PLAYER_CAP).ifPresent(data -> {
            if (type.equalsIgnoreCase("all")) {
                data.setUninvested();
            } else {
                Metal mt = Metal.valueOf(type.toUpperCase());
                data.revokePower(mt);
            }
            Network.sendTo(new AllomancyDataPacket(data, player), player);
        });

        ctx.getSource().sendSuccess(new TranslationTextComponent("commands.allomancy.removepower", player.getDisplayName(), type), true);
    }


}
