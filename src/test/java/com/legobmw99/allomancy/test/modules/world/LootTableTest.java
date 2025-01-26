package com.legobmw99.allomancy.test.modules.world;

import com.legobmw99.allomancy.modules.combat.CombatSetup;
import com.legobmw99.allomancy.modules.consumables.ConsumeSetup;
import com.legobmw99.allomancy.test.util.AllomancyTestHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BrushableBlockEntity;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.neoforged.testframework.annotation.ForEachTest;
import net.neoforged.testframework.annotation.TestHolder;
import net.neoforged.testframework.gametest.EmptyTemplate;

@ForEachTest(groups = "item")
public class LootTableTest {
    @GameTest
    @EmptyTemplate("1x2x1")
    @TestHolder(description = "Tests that lerasium shows up in vanilla loot tables")
    public static void lerasiumLootInjected(AllomancyTestHelper helper) {
        helper.startSequence().thenWaitUntil(() -> {
            helper.setBlock(BlockPos.ZERO, Blocks.CHEST);
            helper
                    .getBlockEntity(BlockPos.ZERO, ChestBlockEntity.class)
                    .setLootTable(BuiltInLootTables.SIMPLE_DUNGEON);
            helper.breakBlock(BlockPos.ZERO, ItemStack.EMPTY, null);
            helper.assertItemEntityPresent(ConsumeSetup.LERASIUM_NUGGET.get());
            helper.killAllEntitiesOfClass(ItemEntity.class);
        }).thenSucceed();
    }

    @GameTest(timeoutTicks = 200)
    @EmptyTemplate(value = "1x5x1", floor = true)
    @TestHolder(description = "Tests that lerasium can be found in the well")
    public static void lerasiumLootInjectedBrushing(AllomancyTestHelper helper) {
        var player = helper.makeTickingPlayer();
        player.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(Items.BRUSH));
        BlockPos pos = BlockPos.ZERO.above();
        helper.startSequence().thenWaitUntil(() -> {
            helper.setBlock(pos, Blocks.SUSPICIOUS_SAND);
            BrushableBlockEntity sus = helper.getBlockEntity(pos, BrushableBlockEntity.class);
            sus.setLootTable(BuiltInLootTables.DESERT_WELL_ARCHAEOLOGY, 0);
            sus.brush(0, helper.getLevel(), player, Direction.UP, player.getItemInHand(InteractionHand.MAIN_HAND));
            helper.assertValueEqual(sus.getItem().getItem(), ConsumeSetup.LERASIUM_NUGGET.get(), "brushed item");
            helper.breakBlock(pos, ItemStack.EMPTY, null);
        }).thenSucceed();
    }

    @GameTest
    @EmptyTemplate(value = "1x5x1", floor = true)
    @TestHolder(description = "Tests that lerasium doesn't show up in vanilla well if mistborn")
    public static void lerasiumLootInjectedBrushingMistborn(AllomancyTestHelper helper) {
        var player = helper.makeMistbornPlayer();
        player.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(Items.BRUSH));
        BlockPos pos = BlockPos.ZERO.above();
        helper.startSequence().thenExecuteFor(99, () -> {
            helper.setBlock(pos, Blocks.SUSPICIOUS_SAND);
            BrushableBlockEntity sus = helper.getBlockEntity(pos, BrushableBlockEntity.class);
            sus.setLootTable(BuiltInLootTables.DESERT_WELL_ARCHAEOLOGY, 0);
            sus.brush(0, helper.getLevel(), player, Direction.UP, player.getItemInHand(InteractionHand.MAIN_HAND));
            helper.assertFalse(sus.getItem().getItem() == ConsumeSetup.LERASIUM_NUGGET.get(), "Still got lerasium");
            helper.breakBlock(pos, ItemStack.EMPTY, null);
        }).thenSucceed();
    }

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
