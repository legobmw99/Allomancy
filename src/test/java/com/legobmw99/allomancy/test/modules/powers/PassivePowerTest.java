package com.legobmw99.allomancy.test.modules.powers;

import com.legobmw99.allomancy.api.enums.Metal;
import com.legobmw99.allomancy.modules.materials.MaterialsSetup;
import com.legobmw99.allomancy.modules.powers.data.AllomancerAttachment;
import com.legobmw99.allomancy.test.AllomancyTest;
import com.legobmw99.allomancy.test.AllomancyTestHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.FarmBlock;
import net.minecraft.world.level.block.entity.FurnaceBlockEntity;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.neoforged.testframework.annotation.ForEachTest;
import net.neoforged.testframework.annotation.RegisterStructureTemplate;
import net.neoforged.testframework.annotation.TestHolder;
import net.neoforged.testframework.gametest.EmptyTemplate;
import net.neoforged.testframework.gametest.StructureTemplateBuilder;

import java.util.List;

@ForEachTest(groups = "passive_powers")
public class PassivePowerTest {

    @GameTest
    @EmptyTemplate("1x3x1")
    @TestHolder
    public static void aluminiumDrainsInstantly(AllomancyTestHelper helper) {
        var player = helper.makeMistbornPlayer();
        var data = player.getData(AllomancerAttachment.ALLOMANCY_DATA);

        player.addEffect(new MobEffectInstance(MobEffects.FIRE_RESISTANCE, -1, 0, true, true));

        data.setBurning(Metal.PEWTER, true);
        data.setBurning(Metal.ALUMINUM, true);


        helper.succeedOnTickWhen(1, () -> {
            helper.assertFalse(data.isBurning(Metal.ALUMINUM), "Aluminum still burning after a tick");
            helper.assertTrue(data.getStored(Metal.PEWTER) == 0,
                              "Pewter (burning) was not drained by burning Aluminum");
            helper.assertTrue(data.getStored(Metal.STEEL) == 0,
                              "Steel (extinguished) was not drained by burning Aluminum");
            helper.assertFalse(data.isBurning(Metal.PEWTER), "Pewter still burning after empty");
            helper.assertTrue(player.getActiveEffects().isEmpty(), "Aluminium didn't remove effects");
        });
    }

    @GameTest
    @EmptyTemplate("1x3x1")
    @TestHolder
    public static void duraluminDrainsWhenExtinguished(AllomancyTestHelper helper) {
        var player = helper.makeMistbornPlayer();
        var data = player.getData(AllomancerAttachment.ALLOMANCY_DATA);
        data.setBurning(Metal.PEWTER, true);
        data.drainMetals(List.of(Metal.DURALUMIN).toArray(Metal[]::new));
        data.incrementStored(Metal.DURALUMIN);
        data.setBurning(Metal.DURALUMIN, true);

        // wait until it burns out
        helper.succeedOnTickWhen(41, () -> {
            helper.assertFalse(data.isBurning(Metal.DURALUMIN), "Duralumin still burning");
            helper.assertTrue(data.getStored(Metal.PEWTER) == 0,
                              "Pewter (burning) was not drained by extinguishing Duralumin");
            helper.assertTrue(data.getStored(Metal.STEEL) == 10,
                              "Steel (extinguished) was drained by extinguishing Duralumin");
            helper.assertFalse(data.isBurning(Metal.PEWTER), "Pewter still burning after empty");
        });
    }

    @GameTest
    @EmptyTemplate("1x3x1")
    @TestHolder
    public static void tinClearsBlindness(AllomancyTestHelper helper) {
        var player = helper.makeMistbornPlayer();
        var data = player.getData(AllomancerAttachment.ALLOMANCY_DATA);

        player.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, -1, 0, true, true));

        data.setBurning(Metal.TIN, true);

        helper.succeedOnTickWhen(1, () -> {
            helper.assertMobEffectAbsent(player, MobEffects.BLINDNESS, "Tin didn't remove blindness");
            helper.assertMobEffectPresent(player, MobEffects.NIGHT_VISION, "Tin didn't grant night vision");
        });
    }

    @GameTest
    @EmptyTemplate("1x3x1")
    @TestHolder
    public static void pewterGivesBuffs(AllomancyTestHelper helper) {
        // TODO test pewter health
        var player = helper.makeMistbornPlayer();
        var data = player.getData(AllomancerAttachment.ALLOMANCY_DATA);

        data.setBurning(Metal.PEWTER, true);

        helper.succeedOnTickWhen(1, () -> {
            helper.assertMobEffectPresent(player, MobEffects.MOVEMENT_SPEED, "Pewter didn't grant speed");
            helper.assertMobEffectPresent(player, MobEffects.DIG_SPEED, "Pewter didn't grant haste");
            helper.assertMobEffectPresent(player, MobEffects.JUMP, "Pewter didn't grant jump boost");
        });
    }

    @GameTest(timeoutTicks = 400)
    @EmptyTemplate("1x3x1")
    @TestHolder
    public static void duraluminTinMakesYouIll(AllomancyTestHelper helper) {
        // technically random
        var player = helper.makeMistbornPlayer();
        var data = player.getData(AllomancerAttachment.ALLOMANCY_DATA);

        data.setBurning(Metal.TIN, true);
        data.setBurning(Metal.DURALUMIN, true);

        helper.succeedWhen(() -> {
            helper.assertTrue(data.isEnhanced(), "Duralumin isn't enhancing");
            helper.assertMobEffectPresent(player, MobEffects.CONFUSION, "Player is not confused");
        });
    }

    // TODO test pewter dura invuln
    // TODO test dura gold tp
    // TODO test dura electrum tp

    @GameTest
    @EmptyTemplate("5x3x5")
    @TestHolder
    public static void duraluminChromeWipesOthers(AllomancyTestHelper helper) {
        var player = helper.makeMistbornPlayer();
        var data = player.getData(AllomancerAttachment.ALLOMANCY_DATA);

        data.setBurning(Metal.CHROMIUM, true);
        data.setBurning(Metal.DURALUMIN, true);

        var player2 = helper.makeMistbornPlayer();
        player2.moveToCorner();
        player2.setItemSlot(EquipmentSlot.HEAD, ItemStack.EMPTY);
        var data2 = player2.getData(AllomancerAttachment.ALLOMANCY_DATA);

        var player3 = helper.makeMistbornPlayer();
        player3.moveTo(helper
                               .absoluteVec(
                                       new BlockPos(4, helper.testInfo.getStructureName().endsWith("_floor") ? 2 : 1,
                                                    4).getCenter())
                               .subtract(0, 0.5, 0));
        var data3 = player3.getData(AllomancerAttachment.ALLOMANCY_DATA);

        helper.succeedOnTickWhen(1, () -> {
            helper.assertTrue(data.isEnhanced(), "Duralumin isn't enhancing");
            helper.assertTrue(data2.getStored(Metal.STEEL) == 0, "Player2 wasn't wiped");
            helper.assertTrue(data3.getStored(Metal.STEEL) == 10, "Player3 was wiped");
        });
    }

    @GameTest
    @EmptyTemplate("1x3x1")
    @TestHolder
    public static void duraluminCopperMakesYouInvis(AllomancyTestHelper helper) {
        var player = helper.makeMistbornPlayer();
        var data = player.getData(AllomancerAttachment.ALLOMANCY_DATA);

        data.setBurning(Metal.COPPER, true);
        data.setBurning(Metal.DURALUMIN, true);

        helper.succeedOnTickWhen(1, () -> {
            helper.assertTrue(data.isEnhanced(), "Duralumin isn't enhancing");
            helper.assertMobEffectPresent(player, MobEffects.INVISIBILITY, "Player is not invisible");
        });
    }

    @RegisterStructureTemplate(AllomancyTest.MODID + ":wheat")
    public static final StructureTemplate WHEAT = StructureTemplateBuilder
            .withSize(3, 5, 3)
            .placeSustainedWater(1, 1, 1, Blocks.STONE.defaultBlockState())
            .set(2, 1, 2, Blocks.FARMLAND.defaultBlockState().setValue(FarmBlock.MOISTURE, 7))
            .set(2, 2, 2, Blocks.WHEAT.defaultBlockState())
            .build();

    @GameTest(template = AllomancyTest.MODID + ":wheat")
    @TestHolder
    public static void bendalloyGrowsCrops(AllomancyTestHelper helper) {
        BlockPos wheat = new BlockPos(2, 2, 2);

        var player = helper.makeMistbornPlayer();
        var data = player.getData(AllomancerAttachment.ALLOMANCY_DATA);

        data.setBurning(Metal.BENDALLOY, true);

        helper.succeedWhen(() -> {
            helper.assertTrue(data.isBurning(Metal.BENDALLOY), "Bendalloy went out");
            helper.assertBlockPresent(Blocks.WHEAT, wheat);
            helper.assertBlockProperty(wheat, CropBlock.AGE, CropBlock.MAX_AGE);
        });

    }


    @RegisterStructureTemplate(AllomancyTest.MODID + ":furnace")
    public static final StructureTemplate FURNACE =
            StructureTemplateBuilder.withSize(1, 3, 1).set(0, 0, 0, Blocks.FURNACE.defaultBlockState()).build();

    @GameTest(template = AllomancyTest.MODID + ":furnace")
    @TestHolder
    public static void bendalloyAcceleratesFurnaces(AllomancyTestHelper helper) {
        BlockPos furnace = new BlockPos(0, 0, 0);
        FurnaceBlockEntity furnaceEntity = helper.getBlockEntity(furnace, FurnaceBlockEntity.class);
        furnaceEntity.setItem(0, new ItemStack(MaterialsSetup.RAW_ORE_ITEMS.getFirst().get(), 1));
        furnaceEntity.setItem(1, new ItemStack(Items.STICK, 2));

        var player = helper.makeMistbornPlayer();
        var data = player.getData(AllomancerAttachment.ALLOMANCY_DATA);
        data.setBurning(Metal.BENDALLOY, true);
        helper.succeedOnTickWhen(29, () -> {
            helper.assertTrue(data.isBurning(Metal.BENDALLOY), "Bendalloy went out");
            helper.assertContainerContains(furnace, MaterialsSetup.INGOTS.get(Metal.ALUMINUM.getIndex()).get());

            helper.assertTrue(furnaceEntity.getItem(1).isEmpty(), "Fuel didn't burn out");
            helper.assertTrue(furnaceEntity.getItem(0).isEmpty(), "item didn't smelt");
        });

    }
}
