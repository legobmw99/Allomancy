package com.legobmw99.allomancy.datagen;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.legobmw99.allomancy.Allomancy;
import com.legobmw99.allomancy.modules.consumables.ConsumeSetup;
import com.legobmw99.allomancy.modules.extras.ExtrasSetup;
import com.legobmw99.allomancy.modules.materials.MaterialsSetup;
import net.minecraft.block.Block;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DirectoryCache;
import net.minecraft.data.IDataProvider;
import net.minecraft.data.LootTableProvider;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.*;
import net.minecraft.world.storage.loot.conditions.SurvivesExplosion;

import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class LootTables extends LootTableProvider {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();

    private DataGenerator gen;
    protected final Map<Block, LootTable.Builder> lootTables = new HashMap<>();

    public LootTables(DataGenerator dataGeneratorIn) {
        super(dataGeneratorIn);
        gen = dataGeneratorIn;
    }

    private void addBlockTables() {
        addSimpleBlock("copper_ore", MaterialsSetup.COPPER_ORE.get());
        addSimpleBlock("tin_ore", MaterialsSetup.TIN_ORE.get());
        addSimpleBlock("zinc_ore", MaterialsSetup.ZINC_ORE.get());
        addSimpleBlock("lead_ore", MaterialsSetup.LEAD_ORE.get());
        addSimpleBlock("iron_button", ExtrasSetup.IRON_BUTTON.get());
        addSimpleBlock("iron_lever", ExtrasSetup.IRON_LEVER.get());
    }

    // Useful boilerplate from McJtyLib
    protected void addSimpleBlock(String name, Block block) {
        Allomancy.LOGGER.debug("Creating Loot Table for block " + block.getRegistryName());
        LootPool.Builder builder = LootPool.builder()
                .name(name)
                .rolls(ConstantRange.of(1))
                .addEntry(ItemLootEntry.builder(block))
                .acceptCondition(SurvivesExplosion.builder());

        lootTables.put(block, LootTable.builder().addLootPool(builder));
    }

    @Override
    public void act(DirectoryCache cache) {
        addBlockTables();

        Map<ResourceLocation, LootTable> tables;
        tables = new HashMap<>();
        for (Map.Entry<Block, LootTable.Builder> entry : lootTables.entrySet()) {
            tables.put(entry.getKey().getLootTable(), entry.getValue().setParameterSet(LootParameterSets.BLOCK).build());
        }

        // Lerasium Inject
        Allomancy.LOGGER.debug("Creating Loot Table for Lerasium inject");
        LootPool.Builder leras_builder = LootPool.builder()
                .name("main")
                .rolls(ConstantRange.of(1))
                .addEntry(ItemLootEntry.builder(ConsumeSetup.LERASIUM_NUGGET.get()).weight(7))
                .addEntry(EmptyLootEntry.func_216167_a().weight(13));
        tables.put(new ResourceLocation(Allomancy.MODID, "/inject/lerasium"),
                LootTable.builder().addLootPool(leras_builder).build());


        writeTables(cache, tables);
    }

    private void writeTables(DirectoryCache cache, Map<ResourceLocation, LootTable> tables) {
        Path outputFolder = this.gen.getOutputFolder();
        tables.forEach((key, lootTable) -> {
            Path path = outputFolder.resolve("data/" + key.getNamespace() + "/loot_tables/" + key.getPath() + ".json");
            try {
                IDataProvider.save(GSON, cache, LootTableManager.toJson(lootTable), path);
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
