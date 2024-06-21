package com.legobmw99.allomancy.modules.extras.command;

import com.legobmw99.allomancy.api.data.IAllomancerData;
import com.legobmw99.allomancy.api.enums.Metal;
import com.legobmw99.allomancy.modules.powers.data.AllomancerAttachment;
import com.legobmw99.allomancy.modules.powers.network.Network;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.tree.LiteralCommandNode;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

public class AllomancyPowerCommand {

    private static final DynamicCommandExceptionType ERROR_CANT_ADD =
            new DynamicCommandExceptionType(s -> Component.translatable("commands.allomancy.err_add", s));
    private static final DynamicCommandExceptionType ERROR_CANT_REMOVE =
            new DynamicCommandExceptionType(s -> Component.translatable("commands.allomancy.err_remove", s));

    private static Predicate<CommandSourceStack> permissions(int level) {
        return (player) -> player.hasPermission(level);
    }

    private static Collection<ServerPlayer> sender(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        return Collections.singleton(ctx.getSource().getPlayerOrException());
    }

    private static Collection<ServerPlayer> target(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        return EntityArgument.getPlayers(ctx, "targets");
    }


    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {

        LiteralArgumentBuilder<CommandSourceStack> root = Commands.literal("allomancy").requires(permissions(0));
        root.then(Commands
                          .literal("get")
                          .requires(permissions(0))
                          .executes(ctx -> handleMultiPlayer(ctx, sender(ctx), AllomancyPowerCommand::getPowers))
                          .then(Commands
                                        .argument("targets", EntityArgument.players())
                                        .executes(ctx -> handleMultiPlayer(ctx, target(ctx),
                                                                           AllomancyPowerCommand::getPowers))));

        root.then(Commands
                          .literal("add")
                          .requires(permissions(2))
                          .then(Commands
                                        .argument("type", AllomancyPowerType.INSTANCE)
                                        .executes(ctx -> handleMultiPlayer(ctx, sender(ctx),
                                                                           AllomancyPowerCommand::addPower))
                                        .then(Commands
                                                      .argument("targets", EntityArgument.players())
                                                      .executes(ctx -> handleMultiPlayer(ctx, target(ctx),
                                                                                         AllomancyPowerCommand::addPower)))));

        root.then(Commands
                          .literal("remove")
                          .requires(permissions(2))
                          .then(Commands
                                        .argument("type", AllomancyPowerType.INSTANCE)
                                        .executes(ctx -> handleMultiPlayer(ctx, sender(ctx),
                                                                           AllomancyPowerCommand::removePower))
                                        .then(Commands
                                                      .argument("targets", EntityArgument.players())
                                                      .executes(ctx -> handleMultiPlayer(ctx, target(ctx),
                                                                                         AllomancyPowerCommand::removePower)))));


        LiteralCommandNode<CommandSourceStack> command = dispatcher.register(root);

        dispatcher.register(Commands.literal("ap").requires(permissions(0)).redirect(command));
    }


    /**
     * Abstraction to handle possibly multiple players
     *
     * @param ctx     Command context
     * @param players Collection of players
     * @param toApply Function to apply to all players or sender
     * @return The number of players successfully applied to
     * @throws CommandSyntaxException
     */
    private static int handleMultiPlayer(CommandContext<CommandSourceStack> ctx,
                                         Collection<ServerPlayer> players,
                                         CheckedBiCon<CommandContext<CommandSourceStack>, ServerPlayer> toApply) throws CommandSyntaxException {
        int i = 0;

        for (ServerPlayer p : players) {
            toApply.accept(ctx, p);
            i++;
        }

        return i;
    }

    private static void getPowers(CommandContext<CommandSourceStack> ctx, ServerPlayer player) {
        StringBuilder powers = new StringBuilder();
        var data = player.getData(AllomancerAttachment.ALLOMANCY_DATA);

        if (data.isMistborn()) {
            powers.append("all");
        } else if (data.isUninvested()) {
            powers.append("none");
        } else {
            for (Metal mt : Metal.values()) {
                if (data.hasPower(mt)) {
                    if (powers.isEmpty()) {
                        powers.append(mt.getName());
                    } else {
                        powers.append(", ").append(mt.getName());
                    }
                }
            }
        }
        ctx
                .getSource()
                .sendSuccess(() -> Component.translatable("commands.allomancy.getpowers", player.getDisplayName(),
                                                          powers.toString()), true);
    }

    private static void addPower(CommandContext<CommandSourceStack> ctx,
                                 ServerPlayer player) throws CommandSyntaxException {
        handlePowerChange(ctx, player, IAllomancerData::setMistborn, data -> Predicate.not(data::hasPower),
                          (mt, data) -> data.addPower(mt), ERROR_CANT_ADD::create, "commands.allomancy.addpower");
    }

    private static void removePower(CommandContext<CommandSourceStack> ctx,
                                    ServerPlayer player) throws CommandSyntaxException {
        handlePowerChange(ctx, player, IAllomancerData::setUninvested, (data) -> data::hasPower,
                          (mt, data) -> data.revokePower(mt), ERROR_CANT_REMOVE::create,
                          "commands.allomancy.removepower");
    }

    /**
     * Function abstraction for both add and remove
     *
     * @param ctx            The command context
     * @param player         The player
     * @param all            Function to call with 'all' type, either setMistborn or setUninvested
     * @param filterFunction Either data -> data.hasMetal or its inverse
     * @param single         Either metal -> data.addPower or its inverse
     * @param exception      Function to create an exception
     * @param success        String used when successful
     * @throws CommandSyntaxException
     */
    private static void handlePowerChange(CommandContext<CommandSourceStack> ctx,
                                          ServerPlayer player,
                                          Consumer<IAllomancerData> all,
                                          Function<IAllomancerData, Predicate<Metal>> filterFunction,
                                          BiConsumer<Metal, IAllomancerData> single,
                                          Function<String, CommandSyntaxException> exception,
                                          String success) throws CommandSyntaxException {

        String type = ctx.getArgument("type", String.class);
        var data = player.getData(AllomancerAttachment.ALLOMANCY_DATA);

        if (type.equalsIgnoreCase("all")) {
            all.accept(data);
        } else {
            Predicate<Metal> filter = filterFunction.apply(data);

            if (type.equalsIgnoreCase("random")) {
                List<Metal> metalList = Arrays.asList(Metal.values());
                Collections.shuffle(metalList);
                Metal mt = metalList.stream().filter(filter).findFirst().orElseThrow(() -> exception.apply(type));
                single.accept(mt, data);
            } else {
                Metal mt = Metal.valueOf(type.toUpperCase());
                if (filter.test(mt)) {
                    single.accept(mt, data);
                } else {
                    throw exception.apply(type);
                }
            }
        }
        Network.syncAllomancerData(player);

        ctx.getSource().sendSuccess(() -> Component.translatable(success, player.getDisplayName(), type), true);

    }


    @FunctionalInterface
    private interface CheckedBiCon<T, U> {
        void accept(T t, U u) throws CommandSyntaxException;
    }

}
