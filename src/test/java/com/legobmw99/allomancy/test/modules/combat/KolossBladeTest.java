package com.legobmw99.allomancy.test.modules.combat;

import com.legobmw99.allomancy.api.enums.Metal;
import com.legobmw99.allomancy.modules.combat.CombatSetup;
import com.legobmw99.allomancy.modules.powers.data.AllomancerAttachment;
import com.legobmw99.allomancy.test.util.AllomancyTestHelper;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.neoforged.testframework.annotation.ForEachTest;
import net.neoforged.testframework.annotation.TestHolder;
import net.neoforged.testframework.gametest.EmptyTemplate;
import net.neoforged.testframework.gametest.GameTest;

@ForEachTest(groups = "item")
public class KolossBladeTest {

    @GameTest
    @EmptyTemplate("1x3x1")
    @TestHolder(description = "Tests that a not-strong player gets debuffs from holding a Koloss sword")
    public static void kolossBladeDebuffs(AllomancyTestHelper helper) {
        var player = helper.makeTickingPlayer();

        helper
                .startSequence()
                .thenExecute(
                        () -> player.setItemInHand(InteractionHand.MAIN_HAND, CombatSetup.KOLOSS_BLADE.toStack()))
                .thenExecuteAfter(5, () -> {
                    helper.assertMobEffectPresent(player, MobEffects.MINING_FATIGUE,
                                                  Component.literal("Blade didn't cause fatigue"));
                    helper.assertMobEffectPresent(player, MobEffects.WEAKNESS,
                                                  Component.literal("Blade didn't cause weakness"));
                    helper.assertMobEffectPresent(player, MobEffects.SLOWNESS,
                                                  Component.literal("Blade didn't cause slowness"));
                })
                .thenSucceed();
    }

    @GameTest
    @EmptyTemplate("1x3x1")
    @TestHolder(description = "Tests that a pewter burner can wield a Koloss sword")
    public static void kolossBladePewter(AllomancyTestHelper helper) {
        var player = helper.makeMistbornPlayer();
        var data = player.getData(AllomancerAttachment.ALLOMANCY_DATA);

        data.setBurning(Metal.PEWTER, true);

        helper
                .startSequence()
                .thenExecute(
                        () -> player.setItemInHand(InteractionHand.MAIN_HAND, CombatSetup.KOLOSS_BLADE.toStack()))
                .thenExecuteAfter(5, () -> {
                    helper.assertMobEffectAbsent(player, MobEffects.MINING_FATIGUE,
                                                 Component.literal("Blade still caused fatigue"));
                    helper.assertMobEffectAbsent(player, MobEffects.WEAKNESS,
                                                 Component.literal("Blade still caused weakness"));
                    helper.assertMobEffectAbsent(player, MobEffects.SLOWNESS,
                                                 Component.literal("Blade still caused slowness"));
                })
                .thenSucceed();
    }

    @GameTest
    @EmptyTemplate("1x3x1")
    @TestHolder(description = "Tests that a strenghtened player can wield a Koloss sword")
    public static void kolossBladeStrength(AllomancyTestHelper helper) {
        var player = helper.makeTickingPlayer();

        player.addEffect(new MobEffectInstance(MobEffects.STRENGTH, -1, 2));

        helper
                .startSequence()
                .thenExecute(
                        () -> player.setItemInHand(InteractionHand.MAIN_HAND, CombatSetup.KOLOSS_BLADE.toStack()))
                .thenExecuteAfter(5, () -> {
                    helper.assertMobEffectAbsent(player, MobEffects.MINING_FATIGUE,
                                                 Component.literal("Blade still caused fatigue"));
                    helper.assertMobEffectAbsent(player, MobEffects.WEAKNESS,
                                                 Component.literal("Blade still caused weakness"));
                    helper.assertMobEffectAbsent(player, MobEffects.SLOWNESS,
                                                 Component.literal("Blade still caused slowness"));
                })
                .thenSucceed();
    }
}
