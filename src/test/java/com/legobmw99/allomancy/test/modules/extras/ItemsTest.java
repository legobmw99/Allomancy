package com.legobmw99.allomancy.test.modules.extras;

import com.legobmw99.allomancy.Allomancy;
import com.legobmw99.allomancy.modules.extras.ExtrasSetup;
import com.legobmw99.allomancy.test.util.AllomancyTestHelper;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EntityType;
import net.neoforged.testframework.annotation.ForEachTest;
import net.neoforged.testframework.annotation.TestHolder;
import net.neoforged.testframework.gametest.EmptyTemplate;
import net.neoforged.testframework.gametest.GameTest;

@ForEachTest(groups = "item")
public class ItemsTest {

    @GameTest
    @EmptyTemplate(value = "5x3x5", floor = true)
    @TestHolder(description = "Tests that the earring gets charged when killing special mobs")
    public static void earringKillSpecialCharges(AllomancyTestHelper helper) {
        var player = helper.makeTickingPlayer();
        helper
                .startSequence()
                .thenMap(() -> helper.spawnWithNoFreeWill(EntityType.WITHER_SKELETON, 1, 1, 1))
                .thenMap(helper::withLowHealth)
                .thenExecute(
                        () -> player.setItemInHand(InteractionHand.MAIN_HAND, ExtrasSetup.BRONZE_EARRING.toStack()))
                .thenExecuteFor(4, wither -> {
                    player.lookAt(EntityAnchorArgument.Anchor.EYES, wither, EntityAnchorArgument.Anchor.EYES);
                    player.attack(wither);
                })
                .thenExecute(wither -> {
                    helper.assertTrue(wither.isDeadOrDying(), "Wither skeleton lived");
                    helper.assertPlayerHasAdvancement(player, Allomancy.id("main/bloody"));
                    helper.assertEntityIsHolding(new BlockPos(2, 3, 2), EntityType.PLAYER,
                                                 ExtrasSetup.CHARGED_BRONZE_EARRING.get());
                })
                .thenSucceed();
    }


    @GameTest
    @EmptyTemplate(value = "5x3x5", floor = true)
    @TestHolder(description = "Tests that the earring does not get charged when killing normal mobs")
    public static void earringKillNormalDoesnt(AllomancyTestHelper helper) {
        var player = helper.makeTickingPlayer();
        helper
                .startSequence()
                .thenMap(() -> helper.spawnWithNoFreeWill(EntityType.SKELETON, 1, 1, 1))
                .thenMap(helper::withLowHealth)
                .thenExecute(
                        () -> player.setItemInHand(InteractionHand.MAIN_HAND, ExtrasSetup.BRONZE_EARRING.toStack()))
                .thenExecuteFor(4, skele -> {
                    player.lookAt(EntityAnchorArgument.Anchor.EYES, skele, EntityAnchorArgument.Anchor.EYES);
                    player.attack(skele);
                })
                .thenExecute(skele -> {
                    helper.assertTrue(skele.isDeadOrDying(), "Skeleton lived");
                    helper.assertPlayerLacksAdvancement(player, Allomancy.id("main/bloody"));
                    helper.assertEntityIsHolding(new BlockPos(2, 3, 2), EntityType.PLAYER,
                                                 ExtrasSetup.BRONZE_EARRING.get());
                })
                .thenSucceed();
    }
}
