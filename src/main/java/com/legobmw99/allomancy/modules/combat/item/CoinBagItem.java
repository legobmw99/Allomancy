package com.legobmw99.allomancy.modules.combat.item;

import com.legobmw99.allomancy.api.enums.Metal;
import com.legobmw99.allomancy.modules.combat.entity.ProjectileNuggetEntity;
import com.legobmw99.allomancy.modules.powers.data.AllomancerAttachment;
import com.legobmw99.allomancy.modules.powers.util.Physical;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Predicate;

public class CoinBagItem extends ProjectileWeaponItem {
    private static final Predicate<ItemStack> NUGGETS = (stack) -> {
        Item item = stack.getItem();
        return Physical.doesResourceContainMetal(BuiltInRegistries.ITEM.getKey(item)) &&
               BuiltInRegistries.ITEM.getKey(item).getPath().contains("nugget");
    };

    public CoinBagItem(Item.Properties props) {
        super(props.stacksTo(1).component(DataComponents.ENCHANTABLE, null));
    }

    private static Ammo getAmmoFromItem(Item itemIn) {
        return switch (BuiltInRegistries.ITEM.getKey(itemIn).getPath()) {
            case "iron_nugget", "steel_nugget", "bronze_nugget", "copper_nugget", "nickel_nugget" -> Ammo.HEAVY;
            case "bendalloy_nugget", "nicrosil_nugget", "electrum_nugget", "platinum_nugget" -> Ammo.MAGIC;
            default -> Ammo.LIGHT;
        };
    }

    @Override
    public @NotNull Predicate<ItemStack> getAllSupportedProjectiles() {
        return NUGGETS;
    }

    @Override
    public InteractionResult use(Level world, Player player, InteractionHand hand) {
        ItemStack weapon = player.getItemInHand(hand);
        ItemStack ammo = player.getProjectile(weapon);
        if (ammo.getItem() instanceof ArrowItem) { // the above get function has silly default behavior
            ammo = new ItemStack(Items.GOLD_NUGGET, 1);
        }
        var data = AllomancerAttachment.get(player);
        if (!ammo.isEmpty() && data.isBurning(Metal.STEEL)) {
            if (world instanceof ServerLevel level) {
                Ammo type = getAmmoFromItem(ammo.getItem());

                this.shoot(level, player, hand, weapon, List.of(ammo), type.velocity, type.inaccuracy,
                           data.isEnhanced(), null);

                if (!player.getAbilities().instabuild) {
                    ammo.shrink(1);
                }

                return InteractionResult.SUCCESS;

            }


        }
        return InteractionResult.FAIL;

    }

    @Override
    protected Projectile createProjectile(Level pLevel,
                                          LivingEntity pShooter,
                                          ItemStack pWeapon,
                                          ItemStack pAmmo,
                                          boolean pIsCrit) {

        float dmg = getAmmoFromItem(pAmmo.getItem()).damage;
        if (pIsCrit) {
            dmg *= 2.0F;
        }
        return new ProjectileNuggetEntity(pShooter, pLevel, pAmmo, dmg);
    }


    @Override
    public int getDefaultProjectileRange() {
        return 20;
    }

    @Override
    protected void shootProjectile(LivingEntity pShooter,
                                   Projectile pProjectile,
                                   int pIndex,
                                   float pVelocity,
                                   float pInaccuracy,
                                   float pAngle,
                                   @Nullable LivingEntity pTarget) {
        pProjectile.shootFromRotation(pShooter, pShooter.getXRot(), pShooter.getYHeadRot(), 2.0F, pVelocity,
                                      pInaccuracy);
    }

    private enum Ammo {
        HEAVY(5.0F, 2.25F, 2.5F),
        MAGIC(5.5F, 4.0F, 1.0F),
        LIGHT(4.0F, 4.0F, 1.0F);

        final float damage;
        final float velocity;
        final float inaccuracy;

        @SuppressWarnings("SameParameterValue")
        Ammo(float damage, float velocity, float inaccuracy) {
            this.damage = damage;
            this.velocity = velocity;
            this.inaccuracy = inaccuracy;
        }
    }
}