package com.legobmw99.allomancy.modules.powers.command;

import com.legobmw99.allomancy.modules.powers.network.AllomancyCapabilityPacket;
import com.legobmw99.allomancy.modules.powers.util.AllomancyCapability;
import com.legobmw99.allomancy.modules.powers.util.Metal;
import com.legobmw99.allomancy.network.Network;
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
        for (Metal mt : Metal.values())
            names[i++] = mt.getName();
        names[i] = "all";
    }

    private static Predicate<CommandSource> permissions(int level) {
        return (player) -> player.hasPermissionLevel(level);
    }

    public static void register(CommandDispatcher<CommandSource> dispatcher) {

        LiteralArgumentBuilder<CommandSource> root = Commands.literal("allomancy").requires(permissions(0));
        root.then(Commands.literal("get").requires(permissions(0))
                .executes(ctx -> getPowers(ctx, false))
                .then(Commands.argument("targets", EntityArgument.players())
                        .executes(ctx -> getPowers(ctx, true))));

        root.then(Commands.literal("add").requires(permissions(2))
                .then(Commands.argument("type", AllomancyPowerType.INSTANCE)
                        .executes(ctx -> addPower(ctx, false))
                        .then(Commands.argument("targets", EntityArgument.players())
                                .executes(ctx -> addPower(ctx, true)))));

        root.then(Commands.literal("remove").requires(permissions(2))
                .then(Commands.argument("type", AllomancyPowerType.INSTANCE)
                        .executes(ctx -> removePower(ctx, false))
                        .then(Commands.argument("targets", EntityArgument.players())
                                .executes(ctx -> removePower(ctx, true)))));


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
            getPowers(ctx, ctx.getSource().asPlayer());
            i = 1;
        }

        return i;
    }

    private static void getPowers(CommandContext<CommandSource> ctx, ServerPlayerEntity player) {
        AllomancyCapability cap = AllomancyCapability.forPlayer(player);
        String powers = "";
        if (cap.isMistborn()) {
            powers = "all";
        } else if (cap.isUninvested()) {
            powers = "none";
        } else {
            for (Metal mt : Metal.values()) {
                if (cap.hasPower(mt)) {
                    if (powers.equals("")) {
                        powers = mt.getName();
                    } else {
                        powers = powers + ", " + mt.getName();
                    }
                }
            }
        }
        ctx.getSource().sendFeedback(new TranslationTextComponent("commands.allomancy.getpowers", player.getDisplayName(), powers), true);
    }

    private static int addPower(CommandContext<CommandSource> ctx, boolean hasPlayer) throws CommandSyntaxException {
        int i = 0;
        if (hasPlayer) {
            for (ServerPlayerEntity p : EntityArgument.getPlayers(ctx, "targets")) {
                addPower(ctx, p);
                i++;
            }
        } else {
            addPower(ctx, ctx.getSource().asPlayer());
            i = 1;
        }
        return i;
    }

    private static void addPower(CommandContext<CommandSource> ctx, ServerPlayerEntity player) {
        AllomancyCapability cap = AllomancyCapability.forPlayer(player);
        String type = ctx.getArgument("type", String.class);
        if (type.equalsIgnoreCase("all")) {
            cap.setMistborn();
        } else {
            Metal mt = Metal.valueOf(type.toUpperCase());
            cap.addPower(mt);
        }

        Network.sendTo(new AllomancyCapabilityPacket(cap, player.getEntityId()), player);
        ctx.getSource().sendFeedback(new TranslationTextComponent("commands.allomancy.addpower", player.getDisplayName(), type), true);
    }

    private static int removePower(CommandContext<CommandSource> ctx, boolean hasPlayer) throws CommandSyntaxException {
        int i = 0;
        if (hasPlayer) {
            for (ServerPlayerEntity p : EntityArgument.getPlayers(ctx, "targets")) {
                removePower(ctx, p);
                i++;
            }
        } else {
            removePower(ctx, ctx.getSource().asPlayer());
            i = 1;
        }
        return i;
    }

    private static void removePower(CommandContext<CommandSource> ctx, ServerPlayerEntity player) {
        AllomancyCapability cap = AllomancyCapability.forPlayer(player);
        String type = ctx.getArgument("type", String.class);
        if (type.equalsIgnoreCase("all")) {
            cap.setUninvested();
        } else {
            Metal mt = Metal.valueOf(type.toUpperCase());
            cap.revokePower(mt);
        }

        Network.sendTo(new AllomancyCapabilityPacket(cap, player.getEntityId()), player);
        ctx.getSource().sendFeedback(new TranslationTextComponent("commands.allomancy.removepower", player.getDisplayName(), type), true);
    }


}
