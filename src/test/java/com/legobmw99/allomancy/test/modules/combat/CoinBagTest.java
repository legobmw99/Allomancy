package com.legobmw99.allomancy.test.modules.combat;

import com.legobmw99.allomancy.api.enums.Metal;
import com.legobmw99.allomancy.modules.combat.CombatSetup;
import com.legobmw99.allomancy.modules.materials.MaterialsSetup;
import com.legobmw99.allomancy.modules.powers.data.AllomancerAttachment;
import com.legobmw99.allomancy.test.AllomancyTestHelper;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.ItemStack;
import net.neoforged.testframework.annotation.ForEachTest;
import net.neoforged.testframework.annotation.TestHolder;
import net.neoforged.testframework.gametest.EmptyTemplate;

import java.util.Set;

@ForEachTest(groups = "items")

public class CoinBagTest {

    @GameTest
    @EmptyTemplate
    @TestHolder
    public static void coinBagShoots(AllomancyTestHelper helper) {
        var player = helper.makeMistbornPlayer();
        player.preventItemPickup();
        var data = player.getData(AllomancerAttachment.ALLOMANCY_DATA);
        data.setBurning(Metal.STEEL, true);

        var nugget = MaterialsSetup.NUGGETS.get(Metal.CADMIUM.getIndex()).get();
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
}
