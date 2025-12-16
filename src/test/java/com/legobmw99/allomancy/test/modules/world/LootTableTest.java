package com.legobmw99.allomancy.test.modules.world;

import com.legobmw99.allomancy.Allomancy;
import com.legobmw99.allomancy.modules.combat.CombatSetup;
import com.legobmw99.allomancy.modules.consumables.ConsumeSetup;
import com.legobmw99.allomancy.test.util.AllomancyTestHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.entity.DecoratedPotBlockEntity;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.neoforged.testframework.annotation.ForEachTest;
import net.neoforged.testframework.annotation.TestHolder;
import net.neoforged.testframework.gametest.EmptyTemplate;
import net.neoforged.testframework.gametest.GameTest;

@ForEachTest(groups = "item")
public class LootTableTest {
    @GameTest
    @EmptyTemplate("1x2x1")
    @TestHolder(description = "Tests that daggers shows up in vanilla loot tables")
    public static void daggerLootInjected(AllomancyTestHelper helper) {
        helper.startSequence().thenWaitUntil(() -> {
            helper.setBlock(BlockPos.ZERO, Blocks.CHEST);
            helper
                    .getBlockEntity(BlockPos.ZERO, ChestBlockEntity.class)
                    .setLootTable(BuiltInLootTables.END_CITY_TREASURE);
            helper.breakBlock(BlockPos.ZERO, ItemStack.EMPTY, null);
            helper.assertItemEntityPresent(CombatSetup.OBSIDIAN_DAGGER.get());
            helper.killAllEntitiesOfClass(ItemEntity.class);
        }).thenSucceed();
    }

    @GameTest
    @EmptyTemplate("3x3x3")
    @TestHolder(description = "Tests lerasium drops from the pot loot table")
    public static void lerasiumInLoot(AllomancyTestHelper helper) {
        helper.startSequence().thenExecute(() -> {
            helper.setBlock(BlockPos.ZERO, Blocks.DECORATED_POT);
            helper
                    .getBlockEntity(BlockPos.ZERO, DecoratedPotBlockEntity.class)
                    .setLootTable(ResourceKey.create(Registries.LOOT_TABLE, Allomancy.id("pots/well_plain")));

            helper.breakBlock(BlockPos.ZERO, ItemStack.EMPTY, null);
            helper.assertItemEntityPresent(ConsumeSetup.LERASIUM_NUGGET.get());
            helper.killAllEntitiesOfClass(ItemEntity.class);
        }).thenSucceed();
    }

    @GameTest
    @EmptyTemplate("3x3x3")
    @TestHolder(description = "Tests lerasium doesn't drop for mistborn")
    public static void lerasiumTestsPlayer(AllomancyTestHelper helper) {
        helper.startSequence().thenExecute(() -> {
            var player = helper.makeMistbornPlayer();
            helper.setBlock(BlockPos.ZERO, Blocks.DECORATED_POT);
            helper
                    .getBlockEntity(BlockPos.ZERO, DecoratedPotBlockEntity.class)
                    .setLootTable(ResourceKey.create(Registries.LOOT_TABLE, Allomancy.id("pots/well_plain")));
            // NOTE: currently, real pots don't do this, neither does breaking,
            // so this is fairly artificial.
            helper.getBlockEntity(BlockPos.ZERO, DecoratedPotBlockEntity.class).unpackLootTable(player);
            helper.breakBlock(BlockPos.ZERO, ItemStack.EMPTY, player);
            helper.assertItemEntityNotPresent(ConsumeSetup.LERASIUM_NUGGET.get());
        }).thenSucceed();
    }
}
