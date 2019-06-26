package com.legobmw99.allomancy.entities;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileItemEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;

public class IronNuggetEntity extends ProjectileItemEntity {
    private boolean dropItem = true;

    public IronNuggetEntity(EntityType<? extends ProjectileItemEntity> type, World world) {
        super(type, world);
    }

    public IronNuggetEntity(EntityType<? extends ProjectileItemEntity> type, LivingEntity livingEntity, World world) {
        super(type, livingEntity, world);
        if (livingEntity instanceof PlayerEntity) {
            PlayerEntity ep = (PlayerEntity) livingEntity;
            if (ep.abilities.isCreativeMode) {
                this.dropItem = false;
            }
        }
    }

    public IronNuggetEntity(EntityType<? extends ProjectileItemEntity> type, double x, double y, double z, World world) {
        super(type, x, y, z, world);
    }


    @Override
    protected void onImpact(RayTraceResult rayTraceResult) {
        if (rayTraceResult.getType() == RayTraceResult.Type.ENTITY && ((EntityRayTraceResult) rayTraceResult).getEntity() != this.getThrower()) {
            ((EntityRayTraceResult) rayTraceResult).getEntity().attackEntityFrom(DamageSource.causeThrownDamage(this, this.getThrower()), (float) 5);
        }

        if (!this.world.isRemote) {
            ItemStack ironAmmo = new ItemStack(Items.IRON_NUGGET, 1);
            if (this.world.getGameRules().getBoolean(GameRules.DO_TILE_DROPS) && rayTraceResult.getType() != RayTraceResult.Type.ENTITY && this.dropItem) {
                this.world.addEntity(new ItemEntity(this.world, this.posX, this.posY, this.posZ, ironAmmo));
            }
            this.remove();
        }
    }


    @Override
    protected Item func_213885_i() {
        return Items.IRON_NUGGET;
    }


}
