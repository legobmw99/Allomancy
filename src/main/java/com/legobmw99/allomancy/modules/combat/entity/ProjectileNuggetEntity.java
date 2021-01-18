package com.legobmw99.allomancy.modules.combat.entity;

import com.legobmw99.allomancy.modules.combat.CombatSetup;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.IRendersAsItem;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileItemEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.IPacket;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.network.NetworkHooks;

@OnlyIn(value = Dist.CLIENT, _interface = IRendersAsItem.class)
public class ProjectileNuggetEntity extends ProjectileItemEntity implements IRendersAsItem {
    private static final DataParameter<ItemStack> ITEM = EntityDataManager.createKey(ProjectileNuggetEntity.class, DataSerializers.ITEMSTACK);

    private float damage;
    private boolean dropItem = true;

    public ProjectileNuggetEntity(double x, double y, double z, World worldIn, ItemStack itemIn, float damageIn) {
        super(CombatSetup.NUGGET_PROJECTILE.get(), x, y, z, worldIn);
        this.damage = damageIn;

        if (!itemIn.isEmpty()) {
            this.dataManager.set(ITEM, itemIn.copy());
        }
    }

    public ProjectileNuggetEntity(LivingEntity livingEntityIn, World worldIn, ItemStack itemIn, float damageIn) {
        super(CombatSetup.NUGGET_PROJECTILE.get(), livingEntityIn, worldIn);
        if (livingEntityIn instanceof PlayerEntity) {
            PlayerEntity ep = (PlayerEntity) livingEntityIn;
            if (ep.abilities.isCreativeMode) {
                this.dropItem = false;
            }
        }
        if (!itemIn.isEmpty()) {
            this.dataManager.set(ITEM, itemIn.copy());
        }
        this.damage = damageIn;
    }

    public ProjectileNuggetEntity(EntityType<ProjectileNuggetEntity> entityEntityType, World world) {
        super(CombatSetup.NUGGET_PROJECTILE.get(), world);
        this.damage = 0;
    }

    public ProjectileNuggetEntity(World world, Entity other) {
        this(CombatSetup.NUGGET_PROJECTILE.get(), world);
        if (other instanceof ProjectileNuggetEntity) {
            ProjectileNuggetEntity nugget = (ProjectileNuggetEntity) other;

            this.dataManager.set(ITEM, nugget.getItem().copy());
            this.damage = nugget.getDamage();
        }
    }

    protected void registerData() {
        super.registerData();
        this.dataManager.register(ITEM, ItemStack.EMPTY);
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
            ItemStack ammo = new ItemStack(this.dataManager.get(ITEM).getItem(), 1);
            if (this.world.getGameRules().getBoolean(GameRules.DO_TILE_DROPS) && rayTraceResult.getType() != RayTraceResult.Type.ENTITY && this.dropItem) {
                this.world.addEntity(new ItemEntity(this.world, this.getPositionVec().getX(), this.getPositionVec().getY(), this.getPositionVec().getZ(), ammo));
            }

            this.remove();
        }
    }

    public ItemStack getItem() {
        return this.dataManager.get(ITEM).isEmpty() ? new ItemStack(this.getDefaultItem()) : this.dataManager.get(ITEM);
    }

    public float getDamage() {
        return this.damage;
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
