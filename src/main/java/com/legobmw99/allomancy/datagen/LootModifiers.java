package com.legobmw99.allomancy.datagen;

import com.legobmw99.allomancy.Allomancy;
import com.legobmw99.allomancy.modules.materials.world.DaggerLootModifier;
import com.legobmw99.allomancy.modules.materials.world.LerasiumLootModifier;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.predicates.AnyOfCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.neoforged.neoforge.common.data.GlobalLootModifierProvider;
import net.neoforged.neoforge.common.loot.LootTableIdCondition;

import java.util.concurrent.CompletableFuture;

class LootModifiers extends GlobalLootModifierProvider {

    LootModifiers(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries, Allomancy.MODID);
    }

    private static final ResourceLocation DUNGEON = ResourceLocation.withDefaultNamespace("chests/simple_dungeon");
    private static final ResourceLocation DESERT = ResourceLocation.withDefaultNamespace("chests/desert_pyramid");
    private static final ResourceLocation JUNGLE = ResourceLocation.withDefaultNamespace("chests/jungle_temple");
    private static final ResourceLocation WOODLAND = ResourceLocation.withDefaultNamespace("chests/woodland_mansion");
    private static final ResourceLocation END_CITY =
            ResourceLocation.withDefaultNamespace("chests/end_city_treasure");
    private static final ResourceLocation OUTPOST = ResourceLocation.withDefaultNamespace("chests/pillager_outpost");
    private static final ResourceLocation WELL = ResourceLocation.withDefaultNamespace("archaeology/desert_well");

    @Override
    protected void start() {
        var lerasiumLocations = new LootItemCondition[]{
                AnyOfCondition.anyOf(LootTableIdCondition.builder(DUNGEON), LootTableIdCondition.builder(DESERT),
                                     LootTableIdCondition.builder(JUNGLE), LootTableIdCondition.builder(WOODLAND),
                                     LootTableIdCondition.builder(END_CITY)).build()};
        add("lerasium_loot", new LerasiumLootModifier(lerasiumLocations, 5));

        add("lerasium_well",
            new LerasiumLootModifier(new LootItemCondition[]{new LootTableIdCondition.Builder(WELL).build()}, 6));

        var daggerLocations = new LootItemCondition[]{
                AnyOfCondition.anyOf(LootTableIdCondition.builder(WOODLAND), LootTableIdCondition.builder(END_CITY),
                                     LootTableIdCondition.builder(OUTPOST)).build()};
        add("unbreakable_dagger_loot", new DaggerLootModifier(daggerLocations, 20));
    }
}
