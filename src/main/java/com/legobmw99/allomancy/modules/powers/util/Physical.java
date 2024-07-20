package com.legobmw99.allomancy.modules.powers.util;

import com.legobmw99.allomancy.Allomancy;
import com.legobmw99.allomancy.modules.combat.CombatSetup;
import com.legobmw99.allomancy.modules.combat.entity.ProjectileNuggetEntity;
import com.legobmw99.allomancy.modules.consumables.ConsumeSetup;
import com.legobmw99.allomancy.modules.powers.PowersConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.decoration.ItemFrame;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.vehicle.AbstractMinecart;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.WoodType;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;

public final class Physical {


    private Physical() {}

    /**
     * Block state wrapper on {@link Physical#isBlockMetallic}
     *
     * @param state BlockState to check
     * @return whether the block state is metal
     */
    public static boolean isBlockStateMetallic(BlockState state) {
        return isBlockMetallic(state.getBlock());
    }

    /**
     * Determines if a block is metal or not
     *
     * @param block to be checked
     * @return Whether the block is metal
     */
    public static boolean isBlockMetallic(Block block) {
        return isOnWhitelist(BuiltInRegistries.BLOCK.getKey(block).toString());
    }

    /**
     * Determines if an item is metal or not
     *
     * @param item to be checked
     * @return Whether the item is metal
     */
    public static boolean isItemMetallic(ItemStack item) {
        return isOnWhitelist(BuiltInRegistries.ITEM.getKey(item.getItem()).toString());
    }

    private static boolean isOnWhitelist(String s) {
        return PowersConfig.whitelist.contains(s);
    }

    /**
     * Determines if an entity is metal or not
     *
     * @param entity to be checked
     * @return Whether the entity is metallic
     */
    public static boolean isEntityMetallic(Entity entity) {
        return switch (entity) {
            case ItemEntity item -> isItemMetallic(item.getItem());
            case ItemFrame itemFrame -> isItemMetallic(itemFrame.getItem());
            case FallingBlockEntity fbe -> isBlockStateMetallic(fbe.getBlockState());
            case ProjectileNuggetEntity ignored -> true;
            case AbstractMinecart ignored -> true;
            case IronGolem ignored -> true;
            case LivingEntity ent -> {
                for (ItemStack itemStack : ent.getHandSlots()) {
                    if (isItemMetallic(itemStack)) {
                        yield true;
                    }
                }
                for (ItemStack itemStack : ent.getArmorAndBodyArmorSlots()) {
                    if (isItemMetallic(itemStack)) {
                        yield true;
                    }
                }
                yield false;
            }
            default -> false;
        };

    }

    private static final Pattern ACTIVE_METAL_REGEX = Pattern.compile(
            ".*(iron|steel|tin_|pewter|zinc|brass|copper|bronze|duralumin|chromium|nicrosil|gold|electrum|cadmium" +
            "|bendalloy|lead_|silver|platinum|nickle).*");

    public static boolean doesResourceContainMetal(ResourceLocation input) {
        return ACTIVE_METAL_REGEX.matcher(input.getPath()).matches();
    }


    /**
     * Move an entity either toward or away from an anchor point
     *
     * @param directionScalar the direction and (possibly) scalar multiple of the magnitude
     * @param toMove          the entity to move
     * @param block           the point being moved toward or away from
     */
    public static void lurch(double directionScalar, Entity toMove, BlockPos block) {

        if (toMove.isPassenger()) {
            toMove = toMove.getVehicle();
        }

        Vec3 motion = toMove.position().subtract(Vec3.atCenterOf(block)).normalize().scale(directionScalar * 1.1);
        Vec3 mod = clamp(cutoff(motion.add(toMove.getDeltaMovement()), 0.1), abs(motion).reverse(), abs(motion));
        toMove.setDeltaMovement(mod);
        toMove.hurtMarked = true;

        // Only save players from fall damage
        if (toMove instanceof ServerPlayer) {
            toMove.fallDistance = 0;
        }
    }

    /*
     * Three helper functions for working with Vec3s
     */
    private static Vec3 clamp(Vec3 value, Vec3 min, Vec3 max) {
        return new Vec3(Mth.clamp(value.x, min.x, max.x), Mth.clamp(value.y, min.y, max.y),
                        Mth.clamp(value.z, min.z, max.z));
    }

    private static Vec3 abs(Vec3 vec) {
        return new Vec3(Math.abs(vec.x), Math.abs(vec.y), Math.abs(vec.z));
    }

    private static Vec3 cutoff(Vec3 value, double e) {
        Vec3 mag = abs(value);
        return new Vec3(mag.x < e ? 0 : value.x, mag.y < e ? 0 : value.y, mag.z < e ? 0 : value.z);
    }

    private static HashSet<String> defaultSet;
    private static List<String> defaultList = null;

    // TODO? this could be a Tag one day
    public static List<String> default_whitelist() {
        if (defaultList != null) {
            return defaultList;
        }

        defaultSet = new HashSet<>();

        add(Items.BUCKET);
        add(Items.LAVA_BUCKET);
        add(Items.MILK_BUCKET);
        add(Items.COD_BUCKET);
        add(Items.PUFFERFISH_BUCKET);
        add(Items.SALMON_BUCKET);
        add(Items.TROPICAL_FISH_BUCKET);
        add(Items.WATER_BUCKET);
        add(Items.TADPOLE_BUCKET);
        add(Items.AXOLOTL_BUCKET);
        add(Items.POWDER_SNOW_BUCKET);
        add(Items.COMPASS);
        add(Items.RECOVERY_COMPASS);
        add(Items.CHAINMAIL_HELMET);
        add(Items.CHAINMAIL_CHESTPLATE);
        add(Items.CHAINMAIL_LEGGINGS);
        add(Items.CHAINMAIL_BOOTS);
        add(Items.MINECART);
        add(Items.CHEST_MINECART);
        add(Items.HOPPER_MINECART);
        add(Items.FURNACE_MINECART);
        add(Items.TNT_MINECART);
        add(Items.CLOCK);
        add(Items.SHEARS);
        add(Items.SHIELD);
        add(Items.NETHERITE_INGOT);
        add(Items.NETHERITE_HELMET);
        add(Items.NETHERITE_CHESTPLATE);
        add(Items.NETHERITE_LEGGINGS);
        add(Items.NETHERITE_BOOTS);
        add(Items.NETHERITE_HOE);
        add(Items.NETHERITE_PICKAXE);
        add(Items.NETHERITE_SHOVEL);
        add(Items.NETHERITE_SWORD);
        add(Items.NETHERITE_AXE);
        add(Items.NETHERITE_UPGRADE_SMITHING_TEMPLATE);
        add(Items.CROSSBOW);
        add(Items.BRUSH);
        add(Items.MUSIC_DISC_CREATOR);
        add(Items.MUSIC_DISC_CREATOR_MUSIC_BOX);
        add(Items.TRIAL_KEY);
        add(Items.OMINOUS_TRIAL_KEY);

        add(Blocks.ANVIL);
        add(Blocks.CHIPPED_ANVIL);
        add(Blocks.DAMAGED_ANVIL);
        add(Blocks.CAULDRON);
        add(Blocks.POWDER_SNOW_CAULDRON);
        add(Blocks.LAVA_CAULDRON);
        add(Blocks.WATER_CAULDRON);
        add(Blocks.SMITHING_TABLE);
        add(Blocks.STONECUTTER);
        add(Blocks.CHAIN);
        add(Blocks.HOPPER);
        add(Blocks.PISTON_HEAD);
        add(Blocks.MOVING_PISTON);
        add(Blocks.STICKY_PISTON);
        add(Blocks.BLAST_FURNACE);
        add(Blocks.BELL);
        add(Blocks.PISTON);
        add(Blocks.LIGHT_WEIGHTED_PRESSURE_PLATE);
        add(Blocks.HEAVY_WEIGHTED_PRESSURE_PLATE);
        add(Blocks.RAIL);
        add(Blocks.ACTIVATOR_RAIL);
        add(Blocks.DETECTOR_RAIL);
        add(Blocks.POWERED_RAIL);
        add(Blocks.LANTERN);
        add(Blocks.TRAPPED_CHEST);
        add(Blocks.TRIPWIRE_HOOK);
        add(Blocks.SOUL_LANTERN);
        add(Blocks.NETHERITE_BLOCK);
        add(Blocks.LODESTONE);
        add(Blocks.GILDED_BLACKSTONE);
        add(Blocks.LIGHTNING_ROD);
        add(Blocks.CRAFTER);
        add(Blocks.HEAVY_CORE);


        WoodType.values().forEach(wt -> {
            add("minecraft:" + wt.name() + "_hanging_sign");
            add("minecraft:" + wt.name() + "_wall_hanging_sign");
        });


        add(ConsumeSetup.VIAL.get());
        add(ConsumeSetup.LERASIUM_NUGGET.get());
        add(ConsumeSetup.ALLOMANTIC_GRINDER.get());
        add(CombatSetup.COIN_BAG.get());


        BuiltInRegistries.ITEM
                .keySet()
                .stream()
                .filter(Objects::nonNull)
                .filter(Physical::doesResourceContainMetal)
                .forEach(Physical::add);

        BuiltInRegistries.BLOCK
                .keySet()
                .stream()
                .filter(Objects::nonNull)
                .filter(Physical::doesResourceContainMetal)
                .forEach(Physical::add);


        defaultList = new ArrayList<>(defaultSet);
        defaultList.sort(String::compareTo);
        return defaultList;

    }

    private static void add(String s) {
        Allomancy.LOGGER.info("Adding {} to the default whitelist!", s);
        defaultSet.add(s);
    }

    private static void add(ResourceLocation r) {
        add(r.toString());
    }

    private static void add(Block block) {
        add(BuiltInRegistries.BLOCK.getKey(block));
    }

    private static void add(Item item) {
        add(BuiltInRegistries.ITEM.getKey(item));
    }

}
