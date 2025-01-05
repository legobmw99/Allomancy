package com.legobmw99.allomancy.test.modules.consumables;

import com.legobmw99.allomancy.api.enums.Metal;
import com.legobmw99.allomancy.modules.consumables.ConsumeSetup;
import com.legobmw99.allomancy.modules.consumables.item.VialItem;
import com.legobmw99.allomancy.modules.consumables.item.component.FlakeStorage;
import com.legobmw99.allomancy.modules.powers.data.AllomancerAttachment;
import com.legobmw99.allomancy.test.AllomancyTestHelper;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.world.Difficulty;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.testframework.annotation.ForEachTest;
import net.neoforged.testframework.annotation.TestHolder;
import net.neoforged.testframework.gametest.EmptyTemplate;

import static com.legobmw99.allomancy.modules.powers.data.AllomancerData.MAX_STORAGE;

@ForEachTest(groups = "items")
public class ConsumingTest {

    @GameTest
    @EmptyTemplate("1x10x1")
    @TestHolder
    public static void lerasiumMakesMistborn(AllomancyTestHelper helper) {
        var player = helper.makeTickingPlayer();

        helper
                .startSequence()
                .thenMap(() -> helper.useItem(player, ConsumeSetup.LERASIUM_NUGGET))
                .thenWaitUntil(4, () -> {
                    helper.assertTrue(player.getItemInHand(InteractionHand.MAIN_HAND).isEmpty(),
                                      "Lerasium never got eaten");
                    helper.assertTrue(player.getData(AllomancerAttachment.ALLOMANCY_DATA).isMistborn(),
                                      "Player never got invested");
                    helper.assertFalse(helper.getEntities(EntityType.LIGHTNING_BOLT).isEmpty(),
                                       "Didn't spawn lightning");
                })
                .thenIdle(4)
                .thenSucceed();
    }


    @GameTest
    @EmptyTemplate("1x3x1")
    @TestHolder
    public static void mistbornCantEatLerasium(AllomancyTestHelper helper) {
        var player = helper.makeMistbornPlayer();

        helper
                .startSequence()
                .thenMap(() -> helper.useItem(player, ConsumeSetup.LERASIUM_NUGGET))
                .thenExecute(res -> helper.assertValueEqual(res, InteractionResult.FAIL, "Still consumed lerasium"))
                .thenExecute(() -> helper.assertPlayerHasItem(player, ConsumeSetup.LERASIUM_NUGGET.get()))
                .thenSucceed();
    }


    @GameTest
    @EmptyTemplate("1x3x1")
    @TestHolder
    public static void vialIncreasesStorage(AllomancyTestHelper helper) {
        var player = helper.makeTickingPlayer();
        var vial = new ItemStack(ConsumeSetup.VIAL.get(), 1);
        VialItem.fillVial(vial, new FlakeStorage.Mutable().add(Metal.IRON).add(Metal.GOLD).toImmutable());
        var data = player.getData(AllomancerAttachment.ALLOMANCY_DATA);
        for (int i = 0; i < MAX_STORAGE; i++) {
            data.incrementStored(Metal.GOLD);
        }

        helper.startSequence().thenMap(() -> helper.useItem(player, vial)).thenWaitUntil(6, () -> {
            helper.assertValueEqual(player.getData(AllomancerAttachment.ALLOMANCY_DATA).getStored(Metal.IRON), 1,
                                    "Player never got more metal");
            helper.assertValueEqual(player.getData(AllomancerAttachment.ALLOMANCY_DATA).getStored(Metal.GOLD),
                                    MAX_STORAGE, "Player got too much gold");
            helper.assertFalse(player.getItemInHand(InteractionHand.MAIN_HAND).has(ConsumeSetup.FLAKE_STORAGE),
                               "Vial never got eaten");
        }).thenSucceed();
    }

    @GameTest
    @EmptyTemplate("1x3x1")
    @TestHolder
    public static void vialStopsWhenFull(AllomancyTestHelper helper) {
        var player = helper.makeTickingPlayer();
        var vial = new ItemStack(ConsumeSetup.VIAL.get(), 1);
        VialItem.fillVial(vial, new FlakeStorage.Mutable().add(Metal.IRON).toImmutable());
        var data = player.getData(AllomancerAttachment.ALLOMANCY_DATA);
        for (int i = 0; i < MAX_STORAGE; i++) {
            for (Metal m : Metal.values()) {
                data.incrementStored(m);
            }
        }

        helper
                .startSequence()
                .thenMap(() -> helper.useItem(player, vial))
                .thenExecute(res -> helper.assertValueEqual(res, InteractionResult.FAIL, "Still consumed vial"))
                .thenExecute(() -> {
                    helper.assertValueEqual(player.getData(AllomancerAttachment.ALLOMANCY_DATA).getStored(Metal.IRON),
                                            MAX_STORAGE, "Player never got more metal");
                    helper.assertTrue(player.getItemInHand(InteractionHand.MAIN_HAND).has(ConsumeSetup.FLAKE_STORAGE),
                                      "Vial got eaten");
                })
                .thenSucceed();
    }


    @GameTest
    @EmptyTemplate("1x3x1")
    @TestHolder
    public static void specialItemConsumption(AllomancyTestHelper helper) {
        var player = helper.makeTickingPlayer();
        var vial = new ItemStack(ConsumeSetup.VIAL.get(), 1);
        VialItem.fillVial(vial, new FlakeStorage.Mutable().add(Metal.IRON).add(Metal.GOLD).toImmutable());


        helper
                .startSequence()
                .thenExecute(() -> helper.requireDifficulty(Difficulty.HARD))
                .thenMap(() -> helper.useItem(player, Items.GOLDEN_APPLE))
                .thenExecute(res -> helper.assertValueEqual(res, InteractionResult.CONSUME, "Didn't eat"))
                .thenWaitUntil(32, () -> {
                    helper.assertValueEqual(player.getData(AllomancerAttachment.ALLOMANCY_DATA).getStored(Metal.GOLD),
                                            1, "Player never got more metal");
                    helper.assertTrue(player.getItemInHand(InteractionHand.MAIN_HAND).isEmpty(),
                                      "Gapple never got eaten");
                })
                .thenMap(() -> helper.useItem(player, Items.ENCHANTED_GOLDEN_APPLE))
                .thenExecute(res -> helper.assertValueEqual(res, InteractionResult.CONSUME, "Didn't eat"))
                .thenWaitUntil(32, () -> {
                    helper.assertValueEqual(player.getData(AllomancerAttachment.ALLOMANCY_DATA).getStored(Metal.GOLD),
                                            10, "Player never got more metal");
                    helper.assertTrue(player.getItemInHand(InteractionHand.MAIN_HAND).isEmpty(),
                                      "Gapple never got eaten");
                })
                .thenSucceed();
    }

}