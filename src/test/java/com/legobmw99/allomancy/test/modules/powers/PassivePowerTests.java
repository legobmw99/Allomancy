package com.legobmw99.allomancy.test.modules.powers;

import com.legobmw99.allomancy.api.enums.Metal;
import com.legobmw99.allomancy.modules.materials.MaterialsSetup;
import com.legobmw99.allomancy.modules.powers.data.AllomancerAttachment;
import com.legobmw99.allomancy.test.AllomancyTest;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.FarmBlock;
import net.minecraft.world.level.block.entity.FurnaceBlockEntity;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.neoforged.testframework.annotation.ForEachTest;
import net.neoforged.testframework.annotation.RegisterStructureTemplate;
import net.neoforged.testframework.annotation.TestHolder;
import net.neoforged.testframework.gametest.EmptyTemplate;
import net.neoforged.testframework.gametest.ExtendedGameTestHelper;
import net.neoforged.testframework.gametest.StructureTemplateBuilder;

@ForEachTest(groups = "passive_powers")
public class PassivePowerTests {

    @GameTest
    @EmptyTemplate
    @TestHolder
    public static void aluminiumDrainsInstantly(ExtendedGameTestHelper helper) {
        var player = helper.makeTickingMockServerPlayerInLevel(GameType.SURVIVAL);
        var data = player.getData(AllomancerAttachment.ALLOMANCY_DATA);
        data.setMistborn();
        for (int i = 0; i < 10; i++) {
            data.incrementStored(Metal.PEWTER);
            data.incrementStored(Metal.STEEL);
        }
        data.setBurning(Metal.PEWTER, true);
        data.incrementStored(Metal.ALUMINUM);
        data.setBurning(Metal.ALUMINUM, true);

        player.addEffect(new MobEffectInstance(MobEffects.FIRE_RESISTANCE, -1, 0, true, true));

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
    @EmptyTemplate
    @TestHolder
    public static void duraluminDrainsWhenExtinguished(ExtendedGameTestHelper helper) {
        var player = helper.makeTickingMockServerPlayerInLevel(GameType.SURVIVAL);
        var data = player.getData(AllomancerAttachment.ALLOMANCY_DATA);
        data.setMistborn();
        for (int i = 0; i < 10; i++) {
            data.incrementStored(Metal.PEWTER);
            data.incrementStored(Metal.STEEL);
        }
        data.setBurning(Metal.PEWTER, true);
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
    @EmptyTemplate
    @TestHolder
    public static void tinClearsBlindness(ExtendedGameTestHelper helper) {
        var player = helper.makeTickingMockServerPlayerInLevel(GameType.SURVIVAL);
        var data = player.getData(AllomancerAttachment.ALLOMANCY_DATA);

        player.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, -1, 0, true, true));
        data.setMistborn();
        data.incrementStored(Metal.TIN);
        data.setBurning(Metal.TIN, true);

        helper.succeedOnTickWhen(1, () -> {
            helper.assertMobEffectAbsent(player, MobEffects.BLINDNESS, "Tin didn't remove blindness");
            helper.assertMobEffectPresent(player, MobEffects.NIGHT_VISION, "Tin didn't grant night vision");
        });
    }

    @GameTest
    @EmptyTemplate
    @TestHolder
    public static void pewterGivesBuffs(ExtendedGameTestHelper helper) {
        var player = helper.makeTickingMockServerPlayerInLevel(GameType.SURVIVAL);
        var data = player.getData(AllomancerAttachment.ALLOMANCY_DATA);

        data.setMistborn();
        data.incrementStored(Metal.PEWTER);
        data.setBurning(Metal.PEWTER, true);

        helper.succeedOnTickWhen(1, () -> {
            helper.assertMobEffectPresent(player, MobEffects.MOVEMENT_SPEED, "Pewter didn't grant speed");
            helper.assertMobEffectPresent(player, MobEffects.DIG_SPEED, "Pewter didn't grant haste");
            helper.assertMobEffectPresent(player, MobEffects.JUMP, "Pewter didn't grant jump boost");
        });
    }

    @GameTest(timeoutTicks = 400)
    @EmptyTemplate
    @TestHolder
    public static void duraluminTinMakesYouIll(ExtendedGameTestHelper helper) {
        // technically random
        var player = helper.makeTickingMockServerPlayerInLevel(GameType.SURVIVAL);
        var data = player.getData(AllomancerAttachment.ALLOMANCY_DATA);
        data.setMistborn();
        for (int i = 0; i < 10; i++) {
            data.incrementStored(Metal.TIN);
            data.incrementStored(Metal.DURALUMIN);
        }

        data.setBurning(Metal.TIN, true);
        data.setBurning(Metal.DURALUMIN, true);

        helper.succeedWhen(() -> {
            helper.assertTrue(data.isEnhanced(), "Duralumin isn't enhancing");
            helper.assertMobEffectPresent(player, MobEffects.CONFUSION, "Player is not confused");
        });
    }

    @GameTest
    @EmptyTemplate
    @TestHolder
    public static void duraluminCopperMakesYouInvis(ExtendedGameTestHelper helper) {
        var player = helper.makeTickingMockServerPlayerInLevel(GameType.SURVIVAL);
        var data = player.getData(AllomancerAttachment.ALLOMANCY_DATA);
        data.setMistborn();
        data.incrementStored(Metal.COPPER);
        data.incrementStored(Metal.DURALUMIN);

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
    public static void bendalloyGrowsCrops(ExtendedGameTestHelper helper) {
        BlockPos wheat = new BlockPos(2, 2, 2);

        var player = helper.makeTickingMockServerPlayerInLevel(GameType.SURVIVAL);
        player.moveToCentre();
        var data = player.getData(AllomancerAttachment.ALLOMANCY_DATA);
        data.setMistborn();
        for (int i = 0; i < 10; i++) {
            data.incrementStored(Metal.BENDALLOY);
        }
        data.setBurning(Metal.BENDALLOY, true);
        helper.succeedWhen(() -> {
            helper.assertTrue(data.isBurning(Metal.BENDALLOY), "Bendalloy went out");
            helper.assertTrue(helper.getBlockState(wheat).is(Blocks.WHEAT), "Wheat isn't wheat");

            helper.assertTrue(((CropBlock) Blocks.WHEAT).isMaxAge(helper.getBlockState(wheat)),
                              "Wheat didn't grow up");

        });

    }


    @RegisterStructureTemplate(AllomancyTest.MODID + ":furnace")
    public static final StructureTemplate FURNACE =
            StructureTemplateBuilder.withSize(1, 3, 1).set(0, 0, 0, Blocks.FURNACE.defaultBlockState()).build();

    @GameTest(template = AllomancyTest.MODID + ":furnace")
    @TestHolder
    public static void bendalloyAcceleratesFurnaces(ExtendedGameTestHelper helper) {
        BlockPos furnace = new BlockPos(0, 0, 0);
        FurnaceBlockEntity furnaceEntity = helper.getBlockEntity(furnace, FurnaceBlockEntity.class);
        furnaceEntity.setItem(0, new ItemStack(MaterialsSetup.RAW_ORE_ITEMS.getFirst().get(), 1));
        furnaceEntity.setItem(1, new ItemStack(Items.STICK, 2));

        var player = helper.makeTickingMockServerPlayerInLevel(GameType.SURVIVAL);
        player.moveToCentre();
        var data = player.getData(AllomancerAttachment.ALLOMANCY_DATA);
        data.setMistborn();
        data.incrementStored(Metal.BENDALLOY);
        data.setBurning(Metal.BENDALLOY, true);
        helper.succeedOnTickWhen(29, () -> {
            helper.assertTrue(data.isBurning(Metal.BENDALLOY), "Bendalloy went out");
            helper.assertTrue(furnaceEntity.getItem(1).isEmpty(), "Fuel didn't burn out");
            helper.assertTrue(furnaceEntity.getItem(0).isEmpty(), "item didn't smelt");
            helper.assertTrue(furnaceEntity.getItem(2).is(MaterialsSetup.INGOTS.get(Metal.ALUMINUM.getIndex()).get()),
                              "item didn't smelt");

        });

    }
}
