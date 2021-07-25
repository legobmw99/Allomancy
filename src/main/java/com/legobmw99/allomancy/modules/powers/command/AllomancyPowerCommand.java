package com.legobmw99.allomancy.modules.powers.command;

import com.legobmw99.allomancy.api.data.IAllomancerData;
import com.legobmw99.allomancy.modules.powers.data.AllomancerCapability;
import com.legobmw99.allomancy.network.Network;
import com.legobmw99.allomancy.api.enums.Metal;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.tree.LiteralCommandNode;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.EntityArgument;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.common.util.NonNullConsumer;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

public class AllomancyPowerCommand {

    private static final DynamicCommandExceptionType ERROR_CANT_ADD = new DynamicCommandExceptionType(s -> new TranslationTextComponent("commands.allomancy.err_add", s));
    private static final DynamicCommandExceptionType ERROR_CANT_REMOVE = new DynamicCommandExceptionType(s -> new TranslationTextComponent("commands.allomancy.err_remove", s));

    private static Predicate<CommandSource> permissions(int level) {
        return (player) -> player.hasPermission(level);
    }

    private static Collection<ServerPlayerEntity> sender(CommandContext<CommandSource> ctx) throws CommandSyntaxException {
        return Collections.singleton(ctx.getSource().getPlayerOrException());
    }

    private static Collection<ServerPlayerEntity> target(CommandContext<CommandSource> ctx) throws CommandSyntaxException {
        return EntityArgument.getPlayers(ctx, "target");
    }


    public static void register(CommandDispatcher<CommandSource> dispatcher) {

        LiteralArgumentBuilder<CommandSource> root = Commands.literal("allomancy").requires(permissions(0));
        root.then(Commands
                          .literal("get")
                          .requires(permissions(0))
                          .executes(ctx -> handleMultiPlayer(ctx, sender(ctx), AllomancyPowerCommand::getPowers))
                          .then(Commands.argument("targets", EntityArgument.players()).executes(ctx -> handleMultiPlayer(ctx, target(ctx), AllomancyPowerCommand::getPowers))));

        root.then(Commands
                          .literal("add")
                          .requires(permissions(2))
                          .then(Commands
                                        .argument("type", AllomancyPowerType.INSTANCE)
                                        .executes(ctx -> handleMultiPlayer(ctx, sender(ctx), AllomancyPowerCommand::addPower))
                                        .then(Commands
                                                      .argument("targets", EntityArgument.players())
                                                      .executes(ctx -> handleMultiPlayer(ctx, target(ctx), AllomancyPowerCommand::addPower)))));

        root.then(Commands
                          .literal("remove")
                          .requires(permissions(2))
                          .then(Commands
                                        .argument("type", AllomancyPowerType.INSTANCE)
                                        .executes(ctx -> handleMultiPlayer(ctx, sender(ctx), AllomancyPowerCommand::removePower))
                                        .then(Commands
                                                      .argument("targets", EntityArgument.players())
                                                      .executes(ctx -> handleMultiPlayer(ctx, target(ctx), AllomancyPowerCommand::removePower)))));


        LiteralCommandNode<CommandSource> command = dispatcher.register(root);

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
    private static int handleMultiPlayer(CommandContext<CommandSource> ctx,
                                         Collection<ServerPlayerEntity> players,
                                         CheckedBiCon<CommandContext<CommandSource>, ServerPlayerEntity> toApply) throws CommandSyntaxException {
        int i = 0;

        for (ServerPlayerEntity p : players) {
            toApply.accept(ctx, p);
            i++;
        }

        return i;
    }

    private static void getPowers(CommandContext<CommandSource> ctx, ServerPlayerEntity player) {
        StringBuilder powers = new StringBuilder();
        player.getCapability(AllomancerCapability.PLAYER_CAP).ifPresent(data -> {
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

    private static void addPower(CommandContext<CommandSource> ctx, ServerPlayerEntity player) throws CommandSyntaxException {
        handlePowerChange(ctx, player, IAllomancerData::setMistborn, data -> (mt -> !data.hasPower(mt)), mt -> (data -> data.addPower(mt)), ERROR_CANT_ADD::create,
                          "commands.allomancy.addpower");
    }

    private static void removePower(CommandContext<CommandSource> ctx, ServerPlayerEntity player) throws CommandSyntaxException {
        handlePowerChange(ctx, player, IAllomancerData::setUninvested, (data) -> data::hasPower, (mt) -> (data -> data.revokePower(mt)), ERROR_CANT_REMOVE::create,
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
    private static void handlePowerChange(CommandContext<CommandSource> ctx,
                                          ServerPlayerEntity player,
                                          NonNullConsumer<IAllomancerData> all,
                                          Function<IAllomancerData, Predicate<Metal>> filterFunction,
                                          Function<Metal, NonNullConsumer<IAllomancerData>> single,
                                          Function<String, CommandSyntaxException> exception,
                                          String success) throws CommandSyntaxException {

        String type = ctx.getArgument("type", String.class);

        if (type.equalsIgnoreCase("all")) {
            player.getCapability(AllomancerCapability.PLAYER_CAP).ifPresent(all);
        } else {
            Predicate<Metal> filter = player.getCapability(AllomancerCapability.PLAYER_CAP).map(filterFunction::apply).orElse((m) -> false);

            if (type.equalsIgnoreCase("random")) {
                List<Metal> metalList = Arrays.asList(Metal.values());
                Collections.shuffle(metalList);
                Metal mt = metalList.stream().filter(filter).findFirst().orElseThrow(() -> exception.apply(type));
                player.getCapability(AllomancerCapability.PLAYER_CAP).ifPresent(single.apply(mt));
            } else {
                Metal mt = Metal.valueOf(type.toUpperCase());
                if (filter.test(mt)) {
                    player.getCapability(AllomancerCapability.PLAYER_CAP).ifPresent(single.apply(mt));
                } else {
                    throw exception.apply(type);
                }
            }
        }
        Network.sync(player);

        ctx.getSource().sendSuccess(new TranslationTextComponent(success, player.getDisplayName(), type), true);

    }


    @FunctionalInterface
    private interface CheckedBiCon<T, U> {
        void accept(T t, U u) throws CommandSyntaxException;
    }

}
