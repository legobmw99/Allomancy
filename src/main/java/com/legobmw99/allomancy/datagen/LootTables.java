package com.legobmw99.allomancy.datagen;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.legobmw99.allomancy.Allomancy;
import com.legobmw99.allomancy.modules.combat.CombatSetup;
import com.legobmw99.allomancy.modules.consumables.ConsumeSetup;
import com.legobmw99.allomancy.modules.extras.ExtrasSetup;
import com.legobmw99.allomancy.modules.materials.MaterialsSetup;
import net.minecraft.block.Block;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DirectoryCache;
import net.minecraft.data.IDataProvider;
import net.minecraft.data.LootTableProvider;
import net.minecraft.loot.*;
import net.minecraft.loot.conditions.SurvivesExplosion;
import net.minecraft.loot.functions.SetNBT;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.RegistryObject;

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
        gen = dataGeneratorIn;
    }

    private void addBlockTables() {
        addSimpleBlock("aluminum_ore", MaterialsSetup.ALUMINUM_ORE.get());
        addSimpleBlock("cadmium_ore", MaterialsSetup.CADMIUM_ORE.get());
        addSimpleBlock("chromium_ore", MaterialsSetup.CHROMIUM_ORE.get());
        addSimpleBlock("copper_ore", MaterialsSetup.COPPER_ORE.get());
        addSimpleBlock("lead_ore", MaterialsSetup.LEAD_ORE.get());
        addSimpleBlock("silver_ore", MaterialsSetup.SILVER_ORE.get());
        addSimpleBlock("tin_ore", MaterialsSetup.TIN_ORE.get());
        addSimpleBlock("zinc_ore", MaterialsSetup.ZINC_ORE.get());
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
        LootPool.Builder builder = LootPool.builder().name(name).rolls(ConstantRange.of(1)).addEntry(ItemLootEntry.builder(block)).acceptCondition(SurvivesExplosion.builder());

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
        LootPool.Builder leras_builder = LootPool
                .builder()
                .name("main")
                .rolls(ConstantRange.of(1))
                .addEntry(ItemLootEntry.builder(ConsumeSetup.LERASIUM_NUGGET.get()).weight(4))
                .addEntry(EmptyLootEntry.func_216167_a().weight(16));
        tables.put(new ResourceLocation(Allomancy.MODID, "/inject/lerasium"), LootTable.builder().addLootPool(leras_builder).build());

        CompoundNBT nbt = new CompoundNBT();
        nbt.putBoolean("Unbreakable", true);
        Allomancy.LOGGER.debug("Creating Loot Table for Obsidian Dagger inject");
        LootPool.Builder dagger_builder = LootPool
                .builder()
                .name("main")
                .rolls(ConstantRange.of(1))
                .addEntry(ItemLootEntry.builder(CombatSetup.OBSIDIAN_DAGGER.get()).acceptFunction(SetNBT.builder(nbt)).weight(1))
                .addEntry(EmptyLootEntry.func_216167_a().weight(19));
        tables.put(new ResourceLocation(Allomancy.MODID, "/inject/obsidian_dagger"), LootTable.builder().addLootPool(dagger_builder).build());


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
