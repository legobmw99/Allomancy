package com.legobmw99.allomancy.test.modules.powers;

import com.legobmw99.allomancy.Allomancy;
import com.legobmw99.allomancy.api.enums.Metal;
import com.legobmw99.allomancy.modules.combat.CombatSetup;
import com.legobmw99.allomancy.modules.powers.data.AllomancerAttachment;
import com.legobmw99.allomancy.test.util.AllomancyTestHelper;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.testframework.annotation.ForEachTest;
import net.neoforged.testframework.annotation.TestHolder;
import net.neoforged.testframework.gametest.EmptyTemplate;

@ForEachTest(groups = "powers")
public class ActivePowerTest {
    // todo can test client powers somehow?

    @GameTest(batch = "chrome_wipe")
    @EmptyTemplate(value = "5x3x5", floor = true)
    @TestHolder(description = "Tests that chrome attacking wipes players")
    public static void chromeHitWipesPlayer(AllomancyTestHelper helper) {
        var player = helper.makeMistbornPlayer();
        var data = player.getData(AllomancerAttachment.ALLOMANCY_DATA);

        var player2 = helper.makeMistbornPlayer();
        player2.moveToCorner();
        player2.setItemSlot(EquipmentSlot.HEAD, ItemStack.EMPTY);
        var data2 = player2.getData(AllomancerAttachment.ALLOMANCY_DATA);

        var player3 = helper.makeMistbornPlayer();
        player3.moveTo(helper.absoluteVec(new BlockPos(4, 1, 4).getCenter()).subtract(0, 0.5, 0));
        var data3 = player3.getData(AllomancerAttachment.ALLOMANCY_DATA);

        helper.startSequence().thenExecute(() -> {
            data.setBurning(Metal.CHROMIUM, true);
        }).thenExecute(() -> {
            player.lookAt(EntityAnchorArgument.Anchor.EYES, player2.position());
            player2.hurtServer(player.serverLevel(), player.damageSources().playerAttack(player), 1);
        }).thenExecuteAfter(1, () -> {
            helper.assertTrue(data2.getStored(Metal.STEEL) == 0, "Player2 wasn't wiped");
        }).thenExecute(() -> {
            player.lookAt(EntityAnchorArgument.Anchor.EYES, player3.position());
            player3.hurtServer(player.serverLevel(), player.damageSources().playerAttack(player), 1);
        }).thenExecuteAfter(1, () -> {
            helper.assertTrue(data3.getStored(Metal.STEEL) == 10, "Player3 was wiped");
            helper.assertPlayerHasAdvancement(player3, Allomancy.rl("main/tin_foil_hat"));
        }).thenSucceed();
    }

    @GameTest
    @EmptyTemplate(value = "5x3x5", floor = true)
    @TestHolder(description = "Tests that duralumin and pewter let you instakill with the Koloss sword")
    public static void duraluminPewterDamageOutput(AllomancyTestHelper helper) {
        var player = helper.makeMistbornPlayer();
        var data = player.getData(AllomancerAttachment.ALLOMANCY_DATA);
        helper
                .startSequence()
                .thenExecute(() -> {
                    data.setBurning(Metal.PEWTER, true);
                    data.setBurning(Metal.DURALUMIN, true);
                })
                .thenIdle(1)
                .thenMap(() -> helper.spawnWithNoFreeWill(EntityType.WITHER, 1, 0, 1))
                .thenExecute(
                        wither -> wither.hurtServer(player.serverLevel(), player.damageSources().playerAttack(player),
                                                    4))
                .thenExecute(
                        wither -> helper.assertTrue(Math.abs((wither.getMaxHealth() - 12) - wither.getHealth()) < 1,
                                                    "Wither damaged an unexpected amount"))
                .thenExecute(
                        () -> player.setItemInHand(InteractionHand.MAIN_HAND, CombatSetup.KOLOSS_BLADE.toStack()))
                .thenExecute(
                        wither -> wither.hurtServer(player.serverLevel(), player.damageSources().playerAttack(player),
                                                    1))
                .thenExecute(wither -> helper.assertTrue(wither.isDeadOrDying(), "Wither lived"))
                .thenSucceed();
    }


    @GameTest
    @EmptyTemplate(value = "5x3x5", floor = true)
    @TestHolder(description = "Tests that duralumin and electrum moves you to your spawn")
    public static void duraluminElectrumMovesToSpawn(AllomancyTestHelper helper) {
        var player = helper.makeMistbornPlayer();
        player.moveToCorner();
        var data = player.getData(AllomancerAttachment.ALLOMANCY_DATA);

        var farCorner = helper.absolutePos(new BlockPos(4, 0, 4));
        player.setRespawnPosition(Level.OVERWORLD, farCorner, 0.0f, true, true);
        data.setBurning(Metal.ELECTRUM, true);
        data.setBurning(Metal.DURALUMIN, true);


        helper.succeedOnTickWhen(1, () -> {
            helper.assertFalse(data.isBurning(Metal.ELECTRUM), "Electrum didn't extinguish");
            helper.assertTrue(data.getStored(Metal.ELECTRUM) == 0, "Electrum didn't run out");
            helper.assertTrue(data.getStored(Metal.DURALUMIN) == 0, "Duralumin didn't run out");
            helper.assertEntityInstancePresent(player, new BlockPos(4, 2, 4));
        });
    }

    @GameTest
    @EmptyTemplate(value = "5x3x5", floor = true)
    @TestHolder(description = "Tests that duralumin and gold moves you to your death location")
    public static void duraluminGoldMovesToDeath(AllomancyTestHelper helper) {
        var player = helper.makeMistbornPlayer();
        player.moveToCorner();
        var data = player.getData(AllomancerAttachment.ALLOMANCY_DATA);

        var farCorner = helper.absolutePos(new BlockPos(4, 0, 4));
        // can't test actual death due to it creating a non-GameTestPlayer
        data.setDeathLoc(farCorner, player.level().dimension());
        data.setBurning(Metal.GOLD, true);
        data.setBurning(Metal.DURALUMIN, true);
        helper.succeedOnTickWhen(1, () -> {
            helper.assertFalse(data.isBurning(Metal.GOLD), "Gold didn't extinguish");
            helper.assertTrue(data.getStored(Metal.GOLD) == 0, "Gold didn't run out");
            helper.assertTrue(data.getStored(Metal.DURALUMIN) == 0, "Duralumin didn't run out");
            helper.assertEntityInstancePresent(player, new BlockPos(4, 2, 4));
        });
    }
}
