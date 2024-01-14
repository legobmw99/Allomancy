package com.legobmw99.allomancy.datagen;

import com.legobmw99.allomancy.Allomancy;
import com.legobmw99.allomancy.modules.extras.ExtrasSetup;
import com.legobmw99.allomancy.modules.materials.MaterialsSetup;
import net.minecraft.advancements.critereon.EnchantmentPredicate;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.advancements.critereon.MinMaxBounds;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.loot.LootTableSubProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.AlternativesEntry;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.ApplyBonusCount;
import net.minecraft.world.level.storage.loot.functions.ApplyExplosionDecay;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.predicates.MatchTool;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;

import java.util.function.BiConsumer;
import java.util.function.Supplier;

public class BlockLootTables implements LootTableSubProvider {


    // Useful boilerplate from McJtyLib
    protected static void addSimpleBlock(BiConsumer<ResourceLocation, LootTable.Builder> writer, String name, Block block) {
        Allomancy.LOGGER.debug("Creating Loot Table for block " + BuiltInRegistries.BLOCK.getKey(block));
        LootPool.Builder builder = LootPool.lootPool().name(name).setRolls(ConstantValue.exactly(1)).add(LootItem.lootTableItem(block));

        writer.accept(new ResourceLocation(Allomancy.MODID, "blocks/" + name), LootTable.lootTable().withPool(builder));
    }

    protected static void addSilkTouchBlock(BiConsumer<ResourceLocation, LootTable.Builder> writer, String name, Block block, Item lootItem, float min, float max) {
        LootPool.Builder builder = LootPool
                .lootPool()
                .name(name)
                .setRolls(ConstantValue.exactly(1))
                .add(AlternativesEntry.alternatives(LootItem
                                                            .lootTableItem(block)
                                                            .when(MatchTool.toolMatches(ItemPredicate.Builder
                                                                                                .item()
                                                                                                .hasEnchantment(new EnchantmentPredicate(Enchantments.SILK_TOUCH,
                                                                                                                                         MinMaxBounds.Ints.atLeast(1))))), LootItem
                                                            .lootTableItem(lootItem)
                                                            .apply(SetItemCountFunction.setCount(UniformGenerator.between(min, max)))
                                                            .apply(ApplyBonusCount.addUniformBonusCount(Enchantments.BLOCK_FORTUNE, 1))
                                                            .apply(ApplyExplosionDecay.explosionDecay())));
        writer.accept(new ResourceLocation(Allomancy.MODID, "blocks/" + name), LootTable.lootTable().withPool(builder));
    }


    @Override
    public void generate(BiConsumer<ResourceLocation, LootTable.Builder> writer) {
        for (int i = 0; i < MaterialsSetup.ORE_METALS.length; i++) {
            var ore = MaterialsSetup.ORE_BLOCKS.get(i).get();
            var ds = MaterialsSetup.DEEPSLATE_ORE_BLOCKS.get(i).get();
            var raw = MaterialsSetup.RAW_ORE_ITEMS.get(i).get();
            var rawb = MaterialsSetup.RAW_ORE_BLOCKS.get(i).get();

            addSilkTouchBlock(writer, BuiltInRegistries.BLOCK.getKey(ore).getPath(), ore, raw, 1, 1);
            addSilkTouchBlock(writer, BuiltInRegistries.BLOCK.getKey(ds).getPath(), ds, raw, 1, 1);
            addSimpleBlock(writer, BuiltInRegistries.BLOCK.getKey(rawb).getPath(), rawb);

        }

        addSimpleBlock(writer, "iron_button", ExtrasSetup.IRON_BUTTON.get());
        addSimpleBlock(writer, "inverted_iron_button", ExtrasSetup.INVERTED_IRON_BUTTON.get());
        addSimpleBlock(writer, "iron_lever", ExtrasSetup.IRON_LEVER.get());

        for (Supplier<Block> rblock : MaterialsSetup.STORAGE_BLOCKS) {
            if (rblock != null) {
                Block block = rblock.get();
                addSimpleBlock(writer, BuiltInRegistries.BLOCK.getKey(block).getPath(), block);
            }
        }
    }
}
