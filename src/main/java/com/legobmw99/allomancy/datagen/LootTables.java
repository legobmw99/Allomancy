package com.legobmw99.allomancy.datagen;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.legobmw99.allomancy.Allomancy;
import com.legobmw99.allomancy.modules.combat.CombatSetup;
import com.legobmw99.allomancy.modules.consumables.ConsumeSetup;
import com.legobmw99.allomancy.modules.extras.ExtrasSetup;
import com.legobmw99.allomancy.modules.materials.MaterialsSetup;
import net.minecraft.advancements.critereon.EnchantmentPredicate;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.advancements.critereon.MinMaxBounds;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraft.data.HashCache;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.AlternativesEntry;
import net.minecraft.world.level.storage.loot.entries.EmptyLootItem;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.ApplyBonusCount;
import net.minecraft.world.level.storage.loot.functions.ApplyExplosionDecay;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.functions.SetNbtFunction;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.predicates.MatchTool;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;
import net.minecraftforge.fmllegacy.RegistryObject;

import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class LootTables extends LootTableProvider {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
    protected final Map<Block, LootTable.Builder> lootTables = new HashMap<>();
    private final DataGenerator gen;

    public LootTables(DataGenerator dataGeneratorIn) {
        super(dataGeneratorIn);
        this.gen = dataGeneratorIn;
    }

    private void addBlockTables() {
        for (int i = 0; i < MaterialsSetup.ORE_METALS.length; i++) {
            var ore = MaterialsSetup.ORE_BLOCKS.get(i).get();
            var ds = MaterialsSetup.DEEPSLATE_ORE_BLOCKS.get(i).get();
            var raw = MaterialsSetup.RAW_ORE_ITEMS.get(i).get();
            var rawb = MaterialsSetup.RAW_ORE_BLOCKS.get(i).get();

            addSilkTouchBlock(ore.getRegistryName().getPath(), ore, raw, 1, 1);
            addSilkTouchBlock(ds.getRegistryName().getPath(), ds, raw, 1, 1);
            addSimpleBlock(rawb.getRegistryName().getPath(), rawb);

        }

        addSimpleBlock("iron_button", ExtrasSetup.IRON_BUTTON.get());
        addSimpleBlock("iron_lever", ExtrasSetup.IRON_LEVER.get());

        for (RegistryObject<Block> rblock : MaterialsSetup.STORAGE_BLOCKS) {
            if (rblock != null) {
                Block block = rblock.get();
                addSimpleBlock(block.getRegistryName().getPath(), block);
            }
        }

    }

    // Useful boilerplate from McJtyLib
    protected void addSimpleBlock(String name, Block block) {
        Allomancy.LOGGER.debug("Creating Loot Table for block " + block.getRegistryName());
        LootPool.Builder builder = LootPool.lootPool().name(name).setRolls(ConstantValue.exactly(1)).add(LootItem.lootTableItem(block));

        this.lootTables.put(block, LootTable.lootTable().withPool(builder));
    }

    protected void addSilkTouchBlock(String name, Block block, Item lootItem, float min, float max) {
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
        this.lootTables.put(block, LootTable.lootTable().withPool(builder));
    }

    @Override
    public void run(HashCache cache) {
        addBlockTables();

        Map<ResourceLocation, LootTable> tables;
        tables = new HashMap<>();
        for (Map.Entry<Block, LootTable.Builder> entry : this.lootTables.entrySet()) {
            tables.put(entry.getKey().getLootTable(), entry.getValue().setParamSet(LootContextParamSets.BLOCK).build());
        }

        // Lerasium Inject
        Allomancy.LOGGER.debug("Creating Loot Table for Lerasium inject");
        LootPool.Builder leras_builder = LootPool
                .lootPool()
                .name("main")
                .setRolls(ConstantValue.exactly(1))
                .add(LootItem.lootTableItem(ConsumeSetup.LERASIUM_NUGGET.get()).setWeight(4))
                .add(EmptyLootItem.emptyItem().setWeight(16));
        tables.put(new ResourceLocation(Allomancy.MODID, "/inject/lerasium"), LootTable.lootTable().withPool(leras_builder).build());

        CompoundTag nbt = new CompoundTag();
        nbt.putBoolean("Unbreakable", true);
        Allomancy.LOGGER.debug("Creating Loot Table for Obsidian Dagger inject");
        LootPool.Builder dagger_builder = LootPool
                .lootPool()
                .name("main")
                .setRolls(ConstantValue.exactly(1))
                .add(LootItem.lootTableItem(CombatSetup.OBSIDIAN_DAGGER.get()).apply(SetNbtFunction.setTag(nbt)).setWeight(1))
                .add(EmptyLootItem.emptyItem().setWeight(19));
        tables.put(new ResourceLocation(Allomancy.MODID, "/inject/obsidian_dagger"), LootTable.lootTable().withPool(dagger_builder).build());


        writeTables(cache, tables);
    }

    private void writeTables(HashCache cache, Map<ResourceLocation, LootTable> tables) {
        Path outputFolder = this.gen.getOutputFolder();
        tables.forEach((key, lootTable) -> {
            Path path = outputFolder.resolve("data/" + key.getNamespace() + "/loot_tables/" + key.getPath() + ".json");
            try {
                DataProvider.save(GSON, cache, net.minecraft.world.level.storage.loot.LootTables.serialize(lootTable), path);
            } catch (IOException e) {
                Allomancy.LOGGER.error("Couldn't write loot table {}", path, e);
            }
        });
    }

    @Override
    public String getName() {
        return "Allomancy Loot Tables";
    }
}
