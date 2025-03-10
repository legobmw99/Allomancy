package com.legobmw99.allomancy.modules.combat.entity;

import com.legobmw99.allomancy.modules.combat.CombatSetup;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ItemSupplier;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;

public class ProjectileNuggetEntity extends ThrowableItemProjectile implements ItemSupplier {
    private static final EntityDataAccessor<ItemStack> ITEM =
            SynchedEntityData.defineId(ProjectileNuggetEntity.class, EntityDataSerializers.ITEM_STACK);

    private final float damage;
    private boolean dropItem = true;


    public ProjectileNuggetEntity(LivingEntity livingEntityIn, Level worldIn, ItemStack itemIn, float damageIn) {
        super(CombatSetup.NUGGET_PROJECTILE.get(), livingEntityIn, worldIn, itemIn);
        if (livingEntityIn instanceof Player player && player.getAbilities().instabuild) {
            this.dropItem = false;
        }
        if (!itemIn.isEmpty()) {
            this.entityData.set(ITEM, itemIn.copy());
        }
        this.damage = damageIn;
    }

    public ProjectileNuggetEntity(EntityType<ProjectileNuggetEntity> entityEntityType, Level world) {
        super(entityEntityType, world);
        this.damage = 0;
    }


    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(ITEM, ItemStack.EMPTY);
    }


    @Override
    protected void onHitEntity(EntityHitResult result) {
        if (result.getEntity() == this.getOwner()) {
            return;
        }
        result.getEntity().hurtOrSimulate(this.makeDamage(), this.damage);
        if (this.level() instanceof ServerLevel level) {
            this.kill(level);
        }
    }

    @Override
    protected void onHitBlock(BlockHitResult result) {
        super.onHitBlock(result);
        if (this.level() instanceof ServerLevel level) {
            ItemStack ammo = new ItemStack(this.entityData.get(ITEM).getItem(), 1);
            if (level.getGameRules().getBoolean(GameRules.RULE_DOBLOCKDROPS) && this.dropItem) {
                level.addFreshEntity(
                        new ItemEntity(this.level(), this.position().x(), this.position().y(), this.position().z(),
                                       ammo));
            }

            this.kill(level);
        }
    }

    private DamageSource makeDamage() {
        return new DamageSource(this
                                        .level()
                                        .registryAccess()
                                        .lookupOrThrow(Registries.DAMAGE_TYPE)
                                        .getOrThrow(CombatSetup.COIN_DAMAGE), this, this.getOwner());
    }

    public ItemStack getItem() {
        return this.entityData.get(ITEM).isEmpty() ? new ItemStack(this.getDefaultItem()) : this.entityData.get(ITEM);
    }


    @Override
    protected Item getDefaultItem() {
        return Items.GOLD_NUGGET;
    }

}
