package com.legobmw99.allomancy.modules.powers.command;

import com.legobmw99.allomancy.api.enums.Metal;
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
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class AllomancyPowerType implements ArgumentType<String> {

    public static final AllomancyPowerType INSTANCE = new AllomancyPowerType();
    private static final Set<String> types = Arrays.stream(Metal.values()).map(Metal::getName).collect(Collectors.toSet());
    private static final DynamicCommandExceptionType unknown_power = new DynamicCommandExceptionType(o -> new TranslationTextComponent("commands.allomancy.unrecognized", o));

    static {
        types.add("all");
        types.add("random");
    }

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
        return ISuggestionProvider.suggest(types, builder).toCompletableFuture();
    }

    @Override
    public Collection<String> getExamples() {
        return types;
    }
}
