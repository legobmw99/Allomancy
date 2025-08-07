package com.legobmw99.allomancy.test.modules.powers;

import com.legobmw99.allomancy.Allomancy;
import com.legobmw99.allomancy.api.enums.Metal;
import com.legobmw99.allomancy.modules.powers.data.AllomancerAttachment;
import com.legobmw99.allomancy.modules.world.WorldSetup;
import com.legobmw99.allomancy.test.AllomancyTest;
import com.legobmw99.allomancy.test.util.AllomancyTestHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
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
import net.neoforged.testframework.gametest.GameTest;
import net.neoforged.testframework.gametest.StructureTemplateBuilder;

import java.util.List;

@ForEachTest(groups = "powers")
public class PassivePowerTest {

    @GameTest
    @EmptyTemplate("1x3x1")
    @TestHolder(description = "Tests that burning aluminum drains all metals and removes effects")
    public static void aluminiumDrainsInstantly(AllomancyTestHelper helper) {
        var player = helper.makeMistbornPlayer();
        var data = AllomancerAttachment.get(player);

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
    @TestHolder(description = "Tests that duralumin drains all burning metals when it runs out")
    public static void duraluminDrainsWhenExtinguished(AllomancyTestHelper helper) {
        var player = helper.makeMistbornPlayer();
        var data = AllomancerAttachment.get(player);
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
    @TestHolder(description = "Tests that burning tin removes blindness and grants night vision")
    public static void tinClearsBlindness(AllomancyTestHelper helper) {
        var player = helper.makeMistbornPlayer();
        var data = AllomancerAttachment.get(player);

        player.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, -1, 0, true, true));

        data.setBurning(Metal.TIN, true);

        helper.succeedOnTickWhen(1, () -> {
            helper.assertMobEffectAbsent(player, MobEffects.BLINDNESS,
                                         Component.literal("Tin didn't remove blindness"));
            helper.assertMobEffectPresent(player, MobEffects.NIGHT_VISION,
                                          Component.literal("Tin didn't grant night vision"));
        });
    }

    @GameTest
    @EmptyTemplate("1x3x1")
    @TestHolder(description = "Tests that pewter provides general buff potion effects")
    public static void pewterGivesBuffs(AllomancyTestHelper helper) {
        var player = helper.makeMistbornPlayer();
        var data = AllomancerAttachment.get(player);

        data.setBurning(Metal.PEWTER, true);

        helper.succeedOnTickWhen(1, () -> {
            helper.assertMobEffectPresent(player, MobEffects.SPEED, Component.literal("Pewter didn't grant speed"));
            helper.assertMobEffectPresent(player, MobEffects.HASTE, Component.literal("Pewter didn't grant haste"));
            helper.assertMobEffectPresent(player, MobEffects.JUMP_BOOST,
                                          Component.literal("Pewter didn't grant jump boost"));
        });
    }

    @GameTest(timeoutTicks = 150)
    @EmptyTemplate("1x3x1")
    @TestHolder(description = "Tests that pewter stores some of the damage it prevents")
    public static void pewterPreventsAndStoresSomeDamage(AllomancyTestHelper helper) {
        var player = helper.makeMistbornPlayer();
        var data = AllomancerAttachment.get(player);
        helper
                .startSequence()
                .thenExecute(() -> {
                    player.causeFoodExhaustion(40);
                    data.setBurning(Metal.PEWTER, true);
                })
                .thenExecute(() -> helper.setBlock(0, 0, 0, Blocks.CACTUS))
                .thenIdle(40)

                .thenExecute(() -> helper.assertValueEqual(player.getHealth(), player.getMaxHealth(),
                                                           "Player health after some paper cuts"))
                .thenExecute(() -> {
                    data.setBurning(Metal.PEWTER, false);
                    helper.setBlock(0, 0, 0, Blocks.AIR);
                })
                .thenIdle(80)
                .thenExecute(() -> helper.assertTrue(Math.abs(2.0 - player.getHealth()) < 0.0001,
                                                     "Player not damaged after pewter extinguished: " +
                                                     player.getHealth()))
                .thenSucceed();
    }

    @GameTest(timeoutTicks = 400)
    @EmptyTemplate("1x3x1")
    @TestHolder(description = "Tests that duralumin and tin may give you nausea")
    public static void duraluminTinMakesYouIll(AllomancyTestHelper helper) {
        // technically random
        var player = helper.makeMistbornPlayer();
        var data = AllomancerAttachment.get(player);

        data.setBurning(Metal.TIN, true);
        data.setBurning(Metal.DURALUMIN, true);

        helper.succeedWhen(() -> {
            helper.assertTrue(data.isEnhanced(), "Duralumin isn't enhancing");
            helper.assertMobEffectPresent(player, MobEffects.NAUSEA, Component.literal("Player is not confused"));
        });
    }

    @GameTest
    @EmptyTemplate("1x3x1")
    @TestHolder(description = "Tests that duralumin and pewter cancel damage")
    public static void duraluminPewterMakesInvuln(AllomancyTestHelper helper) {
        var player = helper.makeMistbornPlayer();
        var data = AllomancerAttachment.get(player);
        helper
                .startSequence()
                .thenExecute(() -> {
                    data.setBurning(Metal.PEWTER, true);
                    data.setBurning(Metal.DURALUMIN, true);
                })
                .thenIdle(1)
                .thenMap(() -> player.hurtServer(player.level(), player.damageSources().wither(), 100))
                .thenExecute(res -> helper.assertFalse(res, "Player still injured"))
                .thenExecute(() -> helper.assertValueEqual(player.getHealth(), player.getMaxHealth(),
                                                           "Player health changed"))
                .thenSucceed();
    }


    @GameTest(batch = "chrome_wipe")
    @EmptyTemplate(value = "5x3x5", floor = true)
    @TestHolder(description = "Tests that duralumin and chrome wipes nearby players")
    public static void duraluminChromeWipesOthers(AllomancyTestHelper helper) {
        var player = helper.makeMistbornPlayer();
        var data = AllomancerAttachment.get(player);

        var player2 = helper.makeMistbornPlayer();
        player2.moveToCorner();
        player2.setItemSlot(EquipmentSlot.HEAD, ItemStack.EMPTY);
        var data2 = AllomancerAttachment.get(player2);

        var player3 = helper.makeMistbornPlayer();
        player3.snapTo(helper.absoluteVec(new BlockPos(4, 1, 4).getCenter()).subtract(0, 0.5, 0));
        var data3 = AllomancerAttachment.get(player3);
        helper.startSequence().thenExecute(() -> {
            data.setBurning(Metal.CHROMIUM, true);
            data.setBurning(Metal.DURALUMIN, true);
        }).thenExecuteAfter(1, () -> {
            helper.assertTrue(data.isEnhanced(), "Duralumin isn't enhancing");
            helper.assertTrue(data2.getStored(Metal.STEEL) == 0, "Player2 wasn't wiped");
            helper.assertTrue(data3.getStored(Metal.STEEL) == 10, "Player3 was wiped");
            helper.assertPlayerHasAdvancement(player3, Allomancy.rl("main/tin_foil_hat"));
        }).thenSucceed();
    }

    @GameTest
    @EmptyTemplate("1x3x1")
    @TestHolder(description = "Tests that duralumin and copper grants invisibility")
    public static void duraluminCopperMakesYouInvis(AllomancyTestHelper helper) {
        var player = helper.makeMistbornPlayer();
        var data = AllomancerAttachment.get(player);

        data.setBurning(Metal.COPPER, true);
        data.setBurning(Metal.DURALUMIN, true);

        helper.succeedOnTickWhen(1, () -> {
            helper.assertTrue(data.isEnhanced(), "Duralumin isn't enhancing");
            helper.assertMobEffectPresent(player, MobEffects.INVISIBILITY,
                                          Component.literal("Player is not invisible"));
        });
    }

    @GameTest(batch = "cadmium_self")
    @EmptyTemplate("1x3x1")
    @TestHolder(description = "Tests that cadmium grants slow falling")
    public static void cadmiumGrantsSlowFall(AllomancyTestHelper helper) {
        var player = helper.makeMistbornPlayer();
        var data = AllomancerAttachment.get(player);

        data.setBurning(Metal.CADMIUM, true);

        helper.succeedOnTickWhen(1, () -> {
            helper.assertMobEffectPresent(player, MobEffects.SLOW_FALLING,
                                          Component.literal("Cadmium didn't grant slow fall"));
            helper.assertMobEffectAbsent(player, MobEffects.SLOWNESS,
                                         Component.literal("Cadmium also slowed player"));
        });
    }


    @GameTest(batch = "cadmium_other")
    @EmptyTemplate(value = "5x3x5", floor = true)
    @TestHolder(description = "Tests that cadmium slows nearby entities falling")
    public static void cadmiumSlowsNearby(AllomancyTestHelper helper) {
        var player = helper.makeMistbornPlayer();
        var data = AllomancerAttachment.get(player);

        data.setBurning(Metal.CADMIUM, true);

        var zombie = helper.spawnWithNoFreeWill(EntityType.ZOMBIE, new BlockPos(1, 1, 1));

        helper.succeedOnTickWhen(1, () -> {
            helper.assertMobEffectPresent(zombie, MobEffects.SLOW_FALLING,
                                          Component.literal("Cadmium didn't grant slow fall"));
            helper.assertMobEffectPresent(zombie, MobEffects.SLOWNESS,
                                          Component.literal("Cadmium didn't grant slow fall"));
        });
    }

    @GameTest(batch = "cadmium_bendalloy_advancement")
    @EmptyTemplate(value = "5x3x5", floor = true)
    @TestHolder(description = "Tests that getting sped and slowed grants an advancement")
    public static void cadmiumBendalloyAdvancement(AllomancyTestHelper helper) {
        var player = helper.makeMistbornPlayer();
        var data = AllomancerAttachment.get(player);


        var player2 = helper.makeTickingPlayer();

        helper.startSequence().thenExecute(() -> {
            data.setBurning(Metal.CADMIUM, true);

        }).thenExecuteAfter(1, () -> {
            data.setBurning(Metal.CADMIUM, false);

        }).thenExecuteAfter(1, () -> {
            data.setBurning(Metal.BENDALLOY, true);

        }).thenExecuteAfter(1, () -> {
            helper.assertPlayerHasAdvancement(player2, Allomancy.rl("main/time_warp"));
        }).thenSucceed();
    }

    @GameTest(batch = "bendalloy_self")
    @EmptyTemplate("1x3x1")
    @TestHolder(description = "Tests that bendalloy grants haste")
    public static void bendalloyGrantsHaste(AllomancyTestHelper helper) {
        var player = helper.makeMistbornPlayer();
        var data = AllomancerAttachment.get(player);

        data.setBurning(Metal.BENDALLOY, true);

        helper.succeedOnTickWhen(1, () -> {
            helper.assertMobEffectPresent(player, MobEffects.HASTE,
                                          Component.literal("Bendalloy didn't grant haste"));
        });
    }

    @GameTest(batch = "cadmium_bendalloy")
    @EmptyTemplate(value = "5x3x5", floor = true)
    @TestHolder(description = "Tests that bendalloy grants haste")
    public static void bendalloyAndCadmiumCancel(AllomancyTestHelper helper) {
        var player = helper.makeMistbornPlayer();
        var data = AllomancerAttachment.get(player);

        var zombie = helper.spawnWithNoFreeWill(EntityType.ZOMBIE, new BlockPos(1, 1, 1));

        data.setBurning(Metal.BENDALLOY, true);
        data.setBurning(Metal.CADMIUM, true);
        helper.startSequence().thenIdle(10).thenExecute(() -> {
            helper.assertMobEffectAbsent(player, MobEffects.HASTE, Component.literal("Bendalloy still grant haste"));
            helper.assertMobEffectAbsent(player, MobEffects.SLOW_FALLING,
                                         Component.literal("Cadmium still grant slow fall"));
            helper.assertMobEffectAbsent(zombie, MobEffects.SLOW_FALLING,
                                         Component.literal("Cadmium still grant slow fall"));
            helper.assertMobEffectAbsent(zombie, MobEffects.SLOWNESS,
                                         Component.literal("Cadmium still grant slow fall"));
        }).thenSucceed();
    }


    @RegisterStructureTemplate(AllomancyTest.MODID + ":wheat")
    public static final StructureTemplate WHEAT = StructureTemplateBuilder
            .withSize(3, 5, 3)
            .placeSustainedWater(1, 1, 1, Blocks.STONE.defaultBlockState())
            .set(2, 1, 2, Blocks.FARMLAND.defaultBlockState().setValue(FarmBlock.MOISTURE, 7))
            .set(2, 2, 2, Blocks.WHEAT.defaultBlockState())
            .build();

    @GameTest(batch = "bendalloy_crop", template = AllomancyTest.MODID + ":wheat")
    @TestHolder(description = "Tests that bendalloy speeds up random ticks")
    public static void bendalloyGrowsCrops(AllomancyTestHelper helper) {
        BlockPos wheat = new BlockPos(2, 2, 2);

        var player = helper.makeMistbornPlayer();
        var data = AllomancerAttachment.get(player);

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

    @GameTest(batch = "bendalloy_blockentity", template = AllomancyTest.MODID + ":furnace")
    @TestHolder(description = "Tests that bendalloy accelerates ticking blocks")
    public static void bendalloyAcceleratesFurnaces(AllomancyTestHelper helper) {
        BlockPos furnace = new BlockPos(0, 0, 0);
        FurnaceBlockEntity furnaceEntity = helper.getBlockEntity(furnace, FurnaceBlockEntity.class);
        furnaceEntity.setItem(0, WorldSetup.RAW_ORE_ITEMS.get(Metal.ALUMINUM.getIndex()).toStack());
        furnaceEntity.setItem(1, new ItemStack(Items.STICK, 2));

        var player = helper.makeMistbornPlayer();
        var data = AllomancerAttachment.get(player);
        data.setBurning(Metal.BENDALLOY, true);
        helper.succeedOnTickWhen(29, () -> {
            helper.assertTrue(data.isBurning(Metal.BENDALLOY), "Bendalloy went out");
            helper.assertContainerContains(furnace, WorldSetup.INGOTS.get(Metal.ALUMINUM.getIndex()).get());

            helper.assertTrue(furnaceEntity.getItem(1).isEmpty(), "Fuel didn't burn out");
            helper.assertTrue(furnaceEntity.getItem(0).isEmpty(), "item didn't smelt");
        });

    }
}
