package com.legobmw99.allomancy.datagen;

import com.legobmw99.allomancy.Allomancy;
import com.legobmw99.allomancy.modules.materials.world.DaggerLootModifier;
import com.legobmw99.allomancy.modules.materials.world.LerasiumLootModifier;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.predicates.AnyOfCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.neoforged.neoforge.common.data.GlobalLootModifierProvider;
import net.neoforged.neoforge.common.loot.LootTableIdCondition;

public class LootModifiers extends GlobalLootModifierProvider {

    public LootModifiers(PackOutput output) {
        super(output, Allomancy.MODID);
    }

    private static final ResourceLocation DUNGEON = new ResourceLocation("minecraft:chests/simple_dungeon");
    private static final ResourceLocation DESERT = new ResourceLocation("minecraft:chests/desert_pyramid");
    private static final ResourceLocation JUNGLE = new ResourceLocation("minecraft:chests/jungle_temple");
    private static final ResourceLocation WOODLAND = new ResourceLocation("minecraft:chests/woodland_mansion");
    private static final ResourceLocation END_CITY = new ResourceLocation("minecraft:chests/end_city_treasure");
    private static final ResourceLocation OUTPOST = new ResourceLocation("minecraft:chests/pillager_outpost");

    @Override
    protected void start() {
        var lerasiumLocations = new LootItemCondition[]{
                AnyOfCondition.anyOf(LootTableIdCondition.builder(DUNGEON), LootTableIdCondition.builder(DESERT), LootTableIdCondition.builder(JUNGLE),
                                     LootTableIdCondition.builder(WOODLAND), LootTableIdCondition.builder(END_CITY)).build()};
        add("lerasium_loot", new LerasiumLootModifier(lerasiumLocations, 5));

        var daggerLocations = new LootItemCondition[]{
                AnyOfCondition.anyOf(LootTableIdCondition.builder(WOODLAND), LootTableIdCondition.builder(END_CITY), LootTableIdCondition.builder(OUTPOST)).build()};
        add("unbreakable_dagger_loot", new DaggerLootModifier(daggerLocations, 20));
    }
}
