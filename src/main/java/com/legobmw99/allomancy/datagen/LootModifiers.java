package com.legobmw99.allomancy.datagen;

import com.legobmw99.allomancy.Allomancy;
import com.legobmw99.allomancy.modules.world.loot.DaggerLootModifier;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.storage.loot.predicates.AnyOfCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.neoforged.neoforge.common.data.GlobalLootModifierProvider;
import net.neoforged.neoforge.common.loot.LootTableIdCondition;

import java.util.concurrent.CompletableFuture;

class LootModifiers extends GlobalLootModifierProvider {

    LootModifiers(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries, Allomancy.MODID);
    }

    private static final Identifier WOODLAND = Identifier.withDefaultNamespace("chests/woodland_mansion");
    private static final Identifier END_CITY = Identifier.withDefaultNamespace("chests/end_city_treasure");
    private static final Identifier OUTPOST = Identifier.withDefaultNamespace("chests/pillager_outpost");

    @Override
    protected void start() {

        var daggerLocations = new LootItemCondition[]{
                AnyOfCondition.anyOf(LootTableIdCondition.builder(WOODLAND), LootTableIdCondition.builder(END_CITY),
                                     LootTableIdCondition.builder(OUTPOST)).build()};
        add("unbreakable_dagger_loot", new DaggerLootModifier(daggerLocations, 20));
    }
}
