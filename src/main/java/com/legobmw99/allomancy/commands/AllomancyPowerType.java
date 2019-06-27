package com.legobmw99.allomancy.commands;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.command.ISuggestionProvider;
import net.minecraft.util.text.TranslationTextComponent;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

public class AllomancyPowerType implements ArgumentType<String> {

    private static final Set<String> types = new HashSet<>(Arrays.asList("none", "iron_misting", "steel_misting", "tin_misting", "pewter_misting", "zinc_misting", "brass_misting", "copper_misting", "bronze_misting", "mistborn"));
    private final DynamicCommandExceptionType unknown_power = new DynamicCommandExceptionType(str -> new TranslationTextComponent("commands.allomancy.unrecognized", str));

    protected static final AllomancyPowerType INSTANCE = new AllomancyPowerType();

    @Override
    public String parse(StringReader reader) throws CommandSyntaxException {
        String in = reader.readUnquotedString();
        if (types.contains(in)) {
            return in;
        }
        throw unknown_power.create(in);
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        return ISuggestionProvider.suggest(types, builder);
    }

    @Override
    public Collection<String> getExamples() {
        return types;
    }
}
