package com.legobmw99.allomancy.modules.combat.entity;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileItemEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.IPacket;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;

public abstract class AbstractNuggetEntity extends ProjectileItemEntity {

    private final Item defaultItem;
    private final float damage;
    private boolean dropItem = true;

    public AbstractNuggetEntity(EntityType<? extends ProjectileItemEntity> type, World worldIn, Item itemIn, float damageIn) {
        super(type, worldIn);
        this.defaultItem = itemIn;
        this.damage = damageIn;
    }

    public AbstractNuggetEntity(EntityType<? extends ProjectileItemEntity> type, double x, double y, double z, World worldIn, Item itemIn, float damageIn) {
        super(type, x, y, z, worldIn);
        this.defaultItem = itemIn;
        this.damage = damageIn;
    }

    public AbstractNuggetEntity(EntityType<? extends ProjectileItemEntity> type, LivingEntity livingEntityIn, World worldIn, Item itemIn, float damageIn) {
        super(type, livingEntityIn, worldIn);
        if (livingEntityIn instanceof PlayerEntity) {
            PlayerEntity ep = (PlayerEntity) livingEntityIn;
            if (ep.abilities.isCreativeMode) {
                this.dropItem = false;
            }
        }
        this.defaultItem = itemIn;
        this.damage = damageIn;
    }


    @Override
    protected void onImpact(RayTraceResult rayTraceResult) {
        // I think this is .getThrower() or equiv
        if (rayTraceResult.getType() == RayTraceResult.Type.ENTITY && ((EntityRayTraceResult) rayTraceResult).getEntity() == this.func_234616_v_()) {
            return;
        }

        if (rayTraceResult.getType() == RayTraceResult.Type.ENTITY) {
            ((EntityRayTraceResult) rayTraceResult).getEntity().attackEntityFrom(DamageSource.causeThrownDamage(this, this.func_234616_v_()), this.damage);
        }

        if (!this.world.isRemote) {
            ItemStack ammo = new ItemStack(this.defaultItem, 1);
            if (this.world.getGameRules().getBoolean(GameRules.DO_TILE_DROPS) && rayTraceResult.getType() != RayTraceResult.Type.ENTITY && this.dropItem) {
                this.world.addEntity(new ItemEntity(this.world, this.getPositionVec().getX(), this.getPositionVec().getY(), this.getPositionVec().getZ(), ammo));
            }

            this.remove();
        }
    }

    @Override
    protected Item getDefaultItem() {
        return this.defaultItem;
    }


    @Override
    public IPacket<?> createSpawnPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

}
