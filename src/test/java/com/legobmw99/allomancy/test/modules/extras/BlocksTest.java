package com.legobmw99.allomancy.test.modules.extras;

import com.legobmw99.allomancy.modules.extras.ExtrasSetup;
import com.legobmw99.allomancy.test.util.AllomancyTestHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LeverBlock;
import net.minecraft.world.level.block.entity.DispenserBlockEntity;
import net.minecraft.world.level.block.state.properties.AttachFace;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.neoforged.testframework.annotation.ForEachTest;
import net.neoforged.testframework.annotation.TestHolder;
import net.neoforged.testframework.gametest.EmptyTemplate;
import net.neoforged.testframework.gametest.ExtendedGameTestHelper;

@ForEachTest(groups = "block")
public class BlocksTest {


    @GameTest
    @EmptyTemplate
    @TestHolder(description = "Tests that blocks have expected capability")
    public static void haveCaps(ExtendedGameTestHelper helper) {

        BlockPos button = new BlockPos(1, 2, 1);
        BlockPos inv_button = button.east();
        BlockPos lever = button.west();
        BlockPos bell = button.north();

        helper
                .startSequence()
                .thenExecute(() -> helper.setBlock(bell, Blocks.BELL))
                .thenExecute(() -> helper.setBlock(button.below(), Blocks.REDSTONE_LAMP))
                .thenExecute(() -> helper.setBlock(button, ExtrasSetup.IRON_BUTTON
                        .get()
                        .defaultBlockState()
                        .setValue(LeverBlock.FACE, AttachFace.FLOOR)))
                .thenExecute(() -> helper.setBlock(inv_button.below(), Blocks.REDSTONE_LAMP))
                .thenExecute(() -> helper.setBlock(inv_button, ExtrasSetup.INVERTED_IRON_BUTTON
                        .get()
                        .defaultBlockState()
                        .setValue(LeverBlock.FACE, AttachFace.FLOOR)))
                .thenExecute(() -> helper.setBlock(lever.below(), Blocks.REDSTONE_LAMP))
                .thenExecute(() -> helper.setBlock(lever, ExtrasSetup.IRON_LEVER
                        .get()
                        .defaultBlockState()
                        .setValue(LeverBlock.FACE, AttachFace.FLOOR)))
                .thenExecute(() -> {
                    helper.requireCapability(ExtrasSetup.ALLOMANTICALLY_USABLE_BLOCK, bell, null);
                    helper.requireCapability(ExtrasSetup.ALLOMANTICALLY_USABLE_BLOCK, button, null);
                    helper.requireCapability(ExtrasSetup.ALLOMANTICALLY_USABLE_BLOCK, inv_button, null);
                    helper.requireCapability(ExtrasSetup.ALLOMANTICALLY_USABLE_BLOCK, lever, null);
                })
                .thenSucceed();

    }


    @GameTest
    @EmptyTemplate
    @TestHolder(description = "Tests that you can't just click to use the iron redstone components")
    public static void cantJustClick(AllomancyTestHelper helper) {

        BlockPos button = new BlockPos(1, 2, 1);
        BlockPos inv_button = button.east();
        BlockPos lever = button.west();

        var player = helper.makeTickingPlayer();
        helper
                .startSequence()
                .thenExecute(() -> helper.setBlock(button.below(), Blocks.REDSTONE_LAMP))
                .thenExecute(() -> helper.setBlock(button, ExtrasSetup.IRON_BUTTON
                        .get()
                        .defaultBlockState()
                        .setValue(LeverBlock.FACE, AttachFace.FLOOR)))
                .thenExecute(() -> helper.setBlock(inv_button.below(), Blocks.REDSTONE_LAMP))
                .thenExecute(() -> helper.setBlock(inv_button, ExtrasSetup.INVERTED_IRON_BUTTON
                        .get()
                        .defaultBlockState()
                        .setValue(LeverBlock.FACE, AttachFace.FLOOR)))
                .thenExecute(() -> helper.setBlock(lever.below(), Blocks.REDSTONE_LAMP))
                .thenExecute(() -> helper.setBlock(lever, ExtrasSetup.IRON_LEVER
                        .get()
                        .defaultBlockState()
                        .setValue(LeverBlock.FACE, AttachFace.FLOOR)))
                .thenExecute(() -> {
                    helper.useBlock(button, player);
                    helper.useBlock(inv_button, player);
                    helper.useBlock(lever, player);
                })
                .thenExecuteAfter(5, () -> {
                    helper.assertBlockProperty(button.below(), BlockStateProperties.LIT, false);
                    helper.assertBlockProperty(inv_button.below(), BlockStateProperties.LIT, false);
                    helper.assertBlockProperty(lever.below(), BlockStateProperties.LIT, false);
                })
                .thenSucceed();

    }


    @GameTest
    @EmptyTemplate("3x4x3")
    @TestHolder(description = "Tests that wind charges don't activate the iron redstone components")
    public static void chargesDoNothing(ExtendedGameTestHelper helper) {

        BlockPos button = new BlockPos(1, 1, 1);
        BlockPos inv_button = button.east();
        BlockPos lever = button.west();

        helper
                .startSequence()
                .thenExecute(() -> helper.setBlock(button.below(), Blocks.REDSTONE_LAMP))
                .thenExecute(() -> helper.setBlock(button, ExtrasSetup.IRON_BUTTON
                        .get()
                        .defaultBlockState()
                        .setValue(LeverBlock.FACE, AttachFace.FLOOR)))
                .thenExecute(() -> helper.setBlock(inv_button.below(), Blocks.REDSTONE_LAMP))
                .thenExecute(() -> helper.setBlock(inv_button, ExtrasSetup.INVERTED_IRON_BUTTON
                        .get()
                        .defaultBlockState()
                        .setValue(LeverBlock.FACE, AttachFace.FLOOR)))
                .thenExecute(() -> helper.setBlock(lever.below(), Blocks.REDSTONE_LAMP))
                .thenExecute(() -> helper.setBlock(lever, ExtrasSetup.IRON_LEVER
                        .get()
                        .defaultBlockState()
                        .setValue(LeverBlock.FACE, AttachFace.FLOOR)))
                .thenExecute(() -> helper.setBlock(1, 3, 1, Blocks.DISPENSER
                        .defaultBlockState()
                        .setValue(BlockStateProperties.FACING, Direction.DOWN)))
                .thenExecute(() -> helper
                        .getBlockEntity(1, 3, 1, DispenserBlockEntity.class)
                        .setItem(0, new ItemStack(Items.WIND_CHARGE, 1)))
                .thenExecute(() -> helper.pulseRedstone(1, 3, 2, 0))
                .thenExecuteAfter(10, () -> {
                    helper.assertBlockProperty(button.below(), BlockStateProperties.LIT, false);
                    helper.assertBlockProperty(inv_button.below(), BlockStateProperties.LIT, false);
                    helper.assertBlockProperty(lever.below(), BlockStateProperties.LIT, false);
                })
                .thenSucceed();

    }
}
