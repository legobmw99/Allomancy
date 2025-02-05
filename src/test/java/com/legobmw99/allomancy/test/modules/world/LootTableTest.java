package com.legobmw99.allomancy.test.modules.world;

import com.legobmw99.allomancy.modules.combat.CombatSetup;
import com.legobmw99.allomancy.test.util.AllomancyTestHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.neoforged.testframework.annotation.ForEachTest;
import net.neoforged.testframework.annotation.TestHolder;
import net.neoforged.testframework.gametest.EmptyTemplate;

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
}
