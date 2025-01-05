package com.legobmw99.allomancy.test.modules.combat;

import com.legobmw99.allomancy.api.enums.Metal;
import com.legobmw99.allomancy.modules.combat.CombatSetup;
import com.legobmw99.allomancy.modules.powers.data.AllomancerAttachment;
import com.legobmw99.allomancy.test.AllomancyTestHelper;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.neoforged.testframework.annotation.ForEachTest;
import net.neoforged.testframework.annotation.TestHolder;
import net.neoforged.testframework.gametest.EmptyTemplate;

@ForEachTest(groups = "items")
public class KolossBladeTest {

    @GameTest
    @EmptyTemplate("1x3x1")
    @TestHolder
    public static void kolossBladeDebuffs(AllomancyTestHelper helper) {
        var player = helper.makeTickingPlayer();

        helper
                .startSequence()
                .thenExecute(
                        () -> player.setItemInHand(InteractionHand.MAIN_HAND, CombatSetup.KOLOSS_BLADE.toStack()))
                .thenExecuteAfter(5, () -> {
                    helper.assertMobEffectPresent(player, MobEffects.DIG_SLOWDOWN, "Blade didn't cause fatigue");
                    helper.assertMobEffectPresent(player, MobEffects.WEAKNESS, "Blade didn't cause weakness");
                    helper.assertMobEffectPresent(player, MobEffects.MOVEMENT_SLOWDOWN,
                                                  "Blade didn't cause slowness");
                })
                .thenSucceed();
    }

    @GameTest
    @EmptyTemplate("1x3x1")
    @TestHolder
    public static void kolossBladePewter(AllomancyTestHelper helper) {
        var player = helper.makeMistbornPlayer();
        var data = player.getData(AllomancerAttachment.ALLOMANCY_DATA);

        data.setBurning(Metal.PEWTER, true);

        helper
                .startSequence()
                .thenExecute(
                        () -> player.setItemInHand(InteractionHand.MAIN_HAND, CombatSetup.KOLOSS_BLADE.toStack()))
                .thenExecuteAfter(5, () -> {
                    helper.assertMobEffectAbsent(player, MobEffects.DIG_SLOWDOWN, "Blade still caused fatigue");
                    helper.assertMobEffectAbsent(player, MobEffects.WEAKNESS, "Blade still caused weakness");
                    helper.assertMobEffectAbsent(player, MobEffects.MOVEMENT_SLOWDOWN, "Blade still caused slowness");
                })
                .thenSucceed();
    }

    @GameTest
    @EmptyTemplate("1x3x1")
    @TestHolder
    public static void kolossBladeStrength(AllomancyTestHelper helper) {
        var player = helper.makeTickingPlayer();

        player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, -1, 2));

        helper
                .startSequence()
                .thenExecute(
                        () -> player.setItemInHand(InteractionHand.MAIN_HAND, CombatSetup.KOLOSS_BLADE.toStack()))
                .thenExecuteAfter(5, () -> {
                    helper.assertMobEffectAbsent(player, MobEffects.DIG_SLOWDOWN, "Blade still caused fatigue");
                    helper.assertMobEffectAbsent(player, MobEffects.WEAKNESS, "Blade still caused weakness");
                    helper.assertMobEffectAbsent(player, MobEffects.MOVEMENT_SLOWDOWN, "Blade still caused slowness");
                })
                .thenSucceed();
    }
}