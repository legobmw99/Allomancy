package com.legobmw99.allomancy.test.modules.combat;

import com.legobmw99.allomancy.Allomancy;
import com.legobmw99.allomancy.api.enums.Metal;
import com.legobmw99.allomancy.modules.combat.CombatSetup;
import com.legobmw99.allomancy.modules.powers.data.AllomancerAttachment;
import com.legobmw99.allomancy.modules.world.WorldSetup;
import com.legobmw99.allomancy.test.util.AllomancyTestHelper;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;
import net.neoforged.testframework.annotation.ForEachTest;
import net.neoforged.testframework.annotation.TestHolder;
import net.neoforged.testframework.gametest.EmptyTemplate;
import net.neoforged.testframework.gametest.GameTest;

import java.util.Set;

@ForEachTest(groups = "item")
public class CoinBagTest {

    @GameTest
    @EmptyTemplate
    @TestHolder(description = "Tests that clicking the coin bag creates an entity which drops the nugget fired")
    public static void coinBagShoots(AllomancyTestHelper helper) {
        var player = helper.makeMistbornPlayer();
        player.preventItemPickup();
        var data = player.getData(AllomancerAttachment.ALLOMANCY_DATA);
        data.setBurning(Metal.STEEL, true);

        var nugget = WorldSetup.NUGGETS.get(Metal.CADMIUM.getIndex()).get();
        player.setItemInHand(InteractionHand.OFF_HAND, new ItemStack(nugget, 1));

        helper
                .startSequence()
                .thenMap(() -> helper.useItem(player, CombatSetup.COIN_BAG))
                .thenExecute(res -> helper.assertTrue(res instanceof InteractionResult.Success, "failed to fire"))
                .thenExecute(() -> {
                    helper.assertFalse(player.getInventory().hasAnyOf(Set.of(nugget)), "Player didn't spend ammo");
                    helper.assertFalse(helper.getEntities(CombatSetup.NUGGET_PROJECTILE.get()).isEmpty(),
                                       "Didn't spawn coin");
                })
                .thenExecuteAfter(10, () -> helper.assertItemEntityPresent(nugget))
                .thenSucceed();
    }

    @GameTest
    @EmptyTemplate
    @TestHolder(description = "Tests that the coin bag can shoot to kill")
    public static void coinBagKills(AllomancyTestHelper helper) {
        var player = helper.makeMistbornPlayer();
        player.preventItemPickup();
        var data = player.getData(AllomancerAttachment.ALLOMANCY_DATA);
        data.setBurning(Metal.STEEL, true);

        var nugget = WorldSetup.NUGGETS.get(Metal.CADMIUM.getIndex()).get();
        player.setItemInHand(InteractionHand.OFF_HAND, new ItemStack(nugget, 1));

        var chicken = helper.spawnWithNoFreeWill(EntityType.CHICKEN, BlockPos.ZERO);
        helper.withLowHealth(chicken);

        player.lookAt(EntityAnchorArgument.Anchor.EYES, chicken.position());

        helper
                .startSequence()
                .thenMap(() -> helper.useItem(player, CombatSetup.COIN_BAG))
                .thenExecute(res -> helper.assertTrue(res instanceof InteractionResult.Success, "failed to fire"))
                .thenExecute(() -> {
                    helper.assertFalse(player.getInventory().hasAnyOf(Set.of(nugget)), "Player didn't spend ammo");
                    helper.assertFalse(helper.getEntities(CombatSetup.NUGGET_PROJECTILE.get()).isEmpty(),
                                       "Didn't spawn coin");
                })
                .thenExecuteAfter(1, () -> {
                    helper.assertEntityNotPresent(EntityType.CHICKEN);
                })
                .thenExecuteAfter(2, () -> {
                    helper.assertItemEntityNotPresent(nugget);
                    helper.assertPlayerHasAdvancement(player, Allomancy.rl("main/coinshot"));
                })
                .thenSucceed();
    }

    @GameTest
    @EmptyTemplate
    @TestHolder(description = "Tests that clicking the coin bag does nothing without steel")
    public static void coinBagNeedsSteel(AllomancyTestHelper helper) {
        var player = helper.makeMistbornPlayer();
        player.preventItemPickup();

        var nugget = WorldSetup.NUGGETS.get(Metal.CADMIUM.getIndex()).get();
        player.setItemInHand(InteractionHand.OFF_HAND, new ItemStack(nugget, 1));

        helper
                .startSequence()
                .thenMap(() -> helper.useItem(player, CombatSetup.COIN_BAG))
                .thenExecute(res -> helper.assertTrue(res instanceof InteractionResult.Fail, "Still fired"))
                .thenExecute(() -> {
                    helper.assertTrue(player.getInventory().hasAnyOf(Set.of(nugget)), "Player spent ammo");
                    helper.assertTrue(helper.getEntities(CombatSetup.NUGGET_PROJECTILE.get()).isEmpty(),
                                      "Still spawned coin");
                })
                .thenSucceed();
    }
}
