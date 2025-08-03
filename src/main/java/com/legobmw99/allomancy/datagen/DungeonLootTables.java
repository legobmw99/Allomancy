package com.legobmw99.allomancy.datagen;

import com.legobmw99.allomancy.Allomancy;
import com.legobmw99.allomancy.modules.combat.CombatSetup;
import com.legobmw99.allomancy.modules.consumables.ConsumeSetup;
import net.minecraft.data.loot.LootTableSubProvider;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.EmptyLootItem;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.SetNbtFunction;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;

import java.util.function.BiConsumer;

public class DungeonLootTables implements LootTableSubProvider {
    @Override
    public void generate(BiConsumer<ResourceLocation, LootTable.Builder> writer) {

        CompoundTag nbt = new CompoundTag();
        nbt.putBoolean("Unbreakable", true);
        Allomancy.LOGGER.debug("Creating Loot Table for Obsidian Dagger inject");
        LootPool.Builder dagger_builder = LootPool
                .lootPool()
                .name("main")
                .setRolls(ConstantValue.exactly(1))
                .add(LootItem
                             .lootTableItem(CombatSetup.OBSIDIAN_DAGGER.get())
                             .apply(SetNbtFunction.setTag(nbt))
                             .setWeight(1))
                .add(EmptyLootItem.emptyItem().setWeight(19));
        writer.accept(new ResourceLocation(Allomancy.MODID, "inject/obsidian_dagger"),
                      LootTable.lootTable().withPool(dagger_builder));

        writer.accept(Allomancy.rl("pots/well_plain"), LootTable
                .lootTable()
                .withPool(LootPool
                                  .lootPool()
                                  .setRolls(ConstantValue.exactly(1.0F))
                                  .add(LootItem.lootTableItem(ConsumeSetup.LERASIUM_NUGGET.get()))));

    }
}
