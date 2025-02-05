package com.legobmw99.allomancy.test.modules.world;

import com.legobmw99.allomancy.modules.consumables.ConsumeSetup;
import com.legobmw99.allomancy.modules.world.WorldSetup;
import com.legobmw99.allomancy.test.util.AllomancyTestHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.world.item.Items;
import net.neoforged.testframework.annotation.ForEachTest;
import net.neoforged.testframework.annotation.TestHolder;
import net.neoforged.testframework.gametest.EmptyTemplate;

@ForEachTest(groups = "block")
public class BlocksTest {
    @GameTest
    @EmptyTemplate(value = "1x3x1", floor = true)
    @TestHolder(description = "Tests that lerasium is made by dropping nether stars in liquid")
    public static void liquidLerasConversion(AllomancyTestHelper helper) {
        helper
                .startSequence()
                .thenExecute(() -> helper.setBlock(BlockPos.ZERO.above(), WorldSetup.LIQUID_LERASIUM.get()))
                .thenExecute(() -> helper.spawnItem(Items.NETHER_STAR, BlockPos.ZERO.above(2).getCenter()))
                .thenIdle(5)
                .thenExecute(() -> {
                    helper.assertItemEntityPresent(ConsumeSetup.LERASIUM_NUGGET.get());
                    helper.assertItemEntityNotPresent(Items.NETHER_STAR);
                })
                .thenSucceed();

    }

    @GameTest
    @EmptyTemplate(value = "2x3x2", floor = true)
    @TestHolder(description = "Tests that liquid lerasium cannot spread")
    public static void liquidLerasNoSpread(AllomancyTestHelper helper) {
        BlockPos actualLeras = BlockPos.ZERO.above(2);
        helper
                .startSequence()
                .thenExecute(() -> helper.setBlock(actualLeras, WorldSetup.LIQUID_LERASIUM.get()))
                .thenIdle(20)
                .thenExecute(() -> {
                    BlockPos.betweenClosed(helper.getBounds()).forEach(toTest -> {
                        if (!toTest.equals(actualLeras)) {
                            helper.assertBlockNotPresent(WorldSetup.LIQUID_LERASIUM.get(), toTest);
                        }
                    });
                })
                .thenSucceed();

    }
}
