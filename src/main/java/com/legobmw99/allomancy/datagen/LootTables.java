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
        this.gen = dataGeneratorIn;
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
        LootPool.Builder builder = LootPool
                .lootPool()
                .name(name)
                .setRolls(ConstantRange.exactly(1))
                .add(ItemLootEntry.lootTableItem(block))
                .when(SurvivesExplosion.survivesExplosion());

        this.lootTables.put(block, LootTable.lootTable().withPool(builder));
    }

    @Override
    public void run(DirectoryCache cache) {
        addBlockTables();

        Map<ResourceLocation, LootTable> tables;
        tables = new HashMap<>();
        for (Map.Entry<Block, LootTable.Builder> entry : this.lootTables.entrySet()) {
            tables.put(entry.getKey().getLootTable(), entry.getValue().setParamSet(LootParameterSets.BLOCK).build());
        }

        // Lerasium Inject
        Allomancy.LOGGER.debug("Creating Loot Table for Lerasium inject");
        LootPool.Builder leras_builder = LootPool
                .lootPool()
                .name("main")
                .setRolls(ConstantRange.exactly(1))
                .add(ItemLootEntry.lootTableItem(ConsumeSetup.LERASIUM_NUGGET.get()).setWeight(4))
                .add(EmptyLootEntry.emptyItem().setWeight(16));
        tables.put(new ResourceLocation(Allomancy.MODID, "/inject/lerasium"), LootTable.lootTable().withPool(leras_builder).build());

        CompoundNBT nbt = new CompoundNBT();
        nbt.putBoolean("Unbreakable", true);
        Allomancy.LOGGER.debug("Creating Loot Table for Obsidian Dagger inject");
        LootPool.Builder dagger_builder = LootPool
                .lootPool()
                .name("main")
                .setRolls(ConstantRange.exactly(1))
                .add(ItemLootEntry.lootTableItem(CombatSetup.OBSIDIAN_DAGGER.get()).apply(SetNBT.setTag(nbt)).setWeight(1))
                .add(EmptyLootEntry.emptyItem().setWeight(19));
        tables.put(new ResourceLocation(Allomancy.MODID, "/inject/obsidian_dagger"), LootTable.lootTable().withPool(dagger_builder).build());


        writeTables(cache, tables);
    }

    private void writeTables(DirectoryCache cache, Map<ResourceLocation, LootTable> tables) {
        Path outputFolder = this.gen.getOutputFolder();
        tables.forEach((key, lootTable) -> {
            Path path = outputFolder.resolve("data/" + key.getNamespace() + "/loot_tables/" + key.getPath() + ".json");
            try {
                IDataProvider.save(GSON, cache, LootTableManager.serialize(lootTable), path);
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
