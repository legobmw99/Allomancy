package com.legobmw99.allomancy.modules.combat.item;

import com.legobmw99.allomancy.Allomancy;
import com.legobmw99.allomancy.modules.combat.entity.ProjectileNuggetEntity;
import com.legobmw99.allomancy.modules.powers.PowerUtils;
import com.legobmw99.allomancy.modules.powers.data.AllomancerCapability;
import com.legobmw99.allomancy.util.Metal;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import java.util.function.Predicate;

public class CoinBagItem extends ShootableItem {


    public static final Predicate<ItemStack> NUGGETS = (stack) -> {
        Item item = stack.getItem();
        return PowerUtils.doesResourceContainsMetal(item.getRegistryName()) && item.getRegistryName().getPath().contains("nugget");
    };

    public CoinBagItem() {
        super(Allomancy.createStandardItemProperties().stacksTo(1));
    }

    private static Ammo getAmmoFromItem(Item itemIn) {
        switch (itemIn.getRegistryName().getPath()) {
            case "iron_nugget":
            case "steel_nugget":
            case "bronze_nugget":
            case "copper_nugget":
            case "nickel_nugget":
                return Ammo.HEAVY;
            case "bendalloy_nugget":
            case "nicrosil_nugget":
            case "electrum_nugget":
            case "platinum_nugget":
                return Ammo.MAGIC;
            default:
                return Ammo.LIGHT;
        }
    }

    @Nonnull
    public Predicate<ItemStack> getAllSupportedProjectiles() {
        return NUGGETS;
    }

    @Override
    public ActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
        ItemStack itemstack = player.getProjectile(player.getItemInHand(hand));
        if (itemstack.getItem() instanceof ArrowItem) { // the above get function has silly default behavior
            itemstack = new ItemStack(Items.GOLD_NUGGET, 1);
        }


        if (player.getCapability(AllomancerCapability.PLAYER_CAP).filter(d -> d.isBurning(Metal.STEEL)).isPresent()) {
            if (!world.isClientSide) {

                Ammo type = getAmmoFromItem(itemstack.getItem());
                float dmg=type.damage; 
                if (player.getCapability(AllomancerCapability.PLAYER_CAP).filter(d -> d.isEnhanced()).isPresent()) {
                    dmg*=2.0F;
                }
                ProjectileNuggetEntity nugget_projectile = new ProjectileNuggetEntity(player, world, itemstack, dmg);
                //          formerly called .shoot()
                nugget_projectile.shootFromRotation(player, player.xRot, player.yHeadRot, type.arg1, type.arg2, type.arg3);
                world.addFreshEntity(nugget_projectile);


                if (!player.abilities.instabuild) {
                    itemstack.shrink(1);
                }

                return new ActionResult<>(ActionResultType.SUCCESS, player.getItemInHand(hand));

            }


        }
        return new ActionResult<>(ActionResultType.FAIL, player.getItemInHand(hand));

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