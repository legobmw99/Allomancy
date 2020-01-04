package com.legobmw99.allomancy.modules.combat.entity;

import com.legobmw99.allomancy.modules.combat.CombatSetup;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileItemEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.IPacket;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;

public class GoldNuggetEntity extends ProjectileItemEntity {
    private boolean dropItem = true;

    public GoldNuggetEntity(World world) {
        super((EntityType<? extends ProjectileItemEntity>) CombatSetup.GOLD_NUGGET.get(), world);
    }

    public GoldNuggetEntity(LivingEntity livingEntity, World world) {
        super((EntityType<? extends ProjectileItemEntity>) CombatSetup.GOLD_NUGGET.get(), livingEntity, world);
        if (livingEntity instanceof PlayerEntity) {
            PlayerEntity ep = (PlayerEntity) livingEntity;
            if (ep.abilities.isCreativeMode) {
                this.dropItem = false;
            }
        }
    }

    public GoldNuggetEntity(double x, double y, double z, World world) {
        super((EntityType<? extends ProjectileItemEntity>) CombatSetup.GOLD_NUGGET.get(), x, y, z, world);
    }

    public GoldNuggetEntity(EntityType<GoldNuggetEntity> type, World world) {
        super((EntityType<? extends ProjectileItemEntity>) CombatSetup.GOLD_NUGGET.get(), world);
    }


    @Override
    protected void onImpact(RayTraceResult rayTraceResult) {
        if (rayTraceResult.getType() == RayTraceResult.Type.ENTITY && ((EntityRayTraceResult) rayTraceResult).getEntity() == this.getThrower()) {
            return;
        }

        if (rayTraceResult.getType() == RayTraceResult.Type.ENTITY) {
            ((EntityRayTraceResult) rayTraceResult).getEntity().attackEntityFrom(DamageSource.causeThrownDamage(this, this.getThrower()), (float) 4);
        }

        if (!this.world.isRemote) {
            ItemStack goldAmmo = new ItemStack(Items.GOLD_NUGGET);
            if (this.world.getGameRules().getBoolean(GameRules.DO_TILE_DROPS) && rayTraceResult.getType() != RayTraceResult.Type.ENTITY && this.dropItem) {
                this.world.addEntity(new ItemEntity(this.world, this.getPositionVec().getX(), this.getPositionVec().getY(), this.getPositionVec().getZ(), goldAmmo));
            }

            this.remove();
        }
    }


    @Override
    protected Item getDefaultItem() {
        return Items.GOLD_NUGGET;
    }

    @Override
    public IPacket<?> createSpawnPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }
}
