package com.legobmw99.allomancy.modules.combat.item;

import com.legobmw99.allomancy.api.data.IAllomancerData;
import com.legobmw99.allomancy.api.enums.Metal;
import com.legobmw99.allomancy.modules.combat.entity.ProjectileNuggetEntity;
import com.legobmw99.allomancy.modules.powers.PowerUtils;
import com.legobmw99.allomancy.modules.powers.data.AllomancerCapability;
import com.legobmw99.allomancy.util.ItemDisplay;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nonnull;
import java.util.function.Predicate;

public class CoinBagItem extends ProjectileWeaponItem {


    public static final Predicate<ItemStack> NUGGETS = (stack) -> {
        Item item = stack.getItem();
        return PowerUtils.doesResourceContainsMetal(ForgeRegistries.ITEMS.getKey(item)) && ForgeRegistries.ITEMS.getKey(item).getPath().contains("nugget");
    };

    public CoinBagItem() {
        super(ItemDisplay.createStandardItemProperties().stacksTo(1));
    }

    private static Ammo getAmmoFromItem(Item itemIn) {
        return switch (ForgeRegistries.ITEMS.getKey(itemIn).getPath()) {
            case "iron_nugget", "steel_nugget", "bronze_nugget", "copper_nugget", "nickel_nugget" -> Ammo.HEAVY;
            case "bendalloy_nugget", "nicrosil_nugget", "electrum_nugget", "platinum_nugget" -> Ammo.MAGIC;
            default -> Ammo.LIGHT;
        };
    }

    @Nonnull
    public Predicate<ItemStack> getAllSupportedProjectiles() {
        return NUGGETS;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level world, Player player, InteractionHand hand) {
        ItemStack itemstack = player.getProjectile(player.getItemInHand(hand));
        if (itemstack.getItem() instanceof ArrowItem) { // the above get function has silly default behavior
            itemstack = new ItemStack(Items.GOLD_NUGGET, 1);
        }


        if (!itemstack.isEmpty() && player.getCapability(AllomancerCapability.PLAYER_CAP).filter(data -> data.isBurning(Metal.STEEL)).isPresent()) {
            if (!world.isClientSide) {

                Ammo type = getAmmoFromItem(itemstack.getItem());
                float dmg = type.damage;
                if (player.getCapability(AllomancerCapability.PLAYER_CAP).filter(IAllomancerData::isEnhanced).isPresent()) {
                    dmg *= 2.0F;
                }
                ProjectileNuggetEntity nugget_projectile = new ProjectileNuggetEntity(player, world, itemstack, dmg);
                nugget_projectile.shootFromRotation(player, player.getXRot(), player.getYHeadRot(), type.arg1, type.arg2, type.arg3);
                world.addFreshEntity(nugget_projectile);


                if (!player.getAbilities().instabuild) {
                    itemstack.shrink(1);
                }

                return new InteractionResultHolder<>(InteractionResult.SUCCESS, player.getItemInHand(hand));

            }


        }
        return new InteractionResultHolder<>(InteractionResult.FAIL, player.getItemInHand(hand));

    }

    @Override
    public int getEnchantmentValue() {
        return 0;
    }

    @Override
    public int getDefaultProjectileRange() {
        return 20;
    }

    private enum Ammo {
        HEAVY(5.0F, 2.0F, 2.25F, 2.5F),
        MAGIC(5.5F, 2.0F, 4.0F, 1.0F),
        LIGHT(4.0F, 2.0F, 4.0F, 1.0F);

        // TODO consider if should be more granular

        final float damage;
        final float arg1;
        final float arg2;
        final float arg3;

        @SuppressWarnings("SameParameterValue")
        Ammo(float damage, float v1, float v2, float v3) {
            this.damage = damage;
            this.arg1 = v1;
            this.arg2 = v2;
            this.arg3 = v3;
        }
    }
}