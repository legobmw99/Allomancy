package com.legobmw99.allomancy.modules.combat.entity;

import com.legobmw99.allomancy.modules.combat.CombatSetup;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Items;
import net.minecraft.world.World;

public class IronNuggetEntity extends AbstractNuggetEntity {

    public IronNuggetEntity(World world) {
        super(CombatSetup.IRON_NUGGET.get(), world, Items.IRON_NUGGET, 5.0f);
    }

    public IronNuggetEntity(LivingEntity livingEntity, World world) {
        super(CombatSetup.IRON_NUGGET.get(), livingEntity, world, Items.IRON_NUGGET, 5.0f);
    }

    public IronNuggetEntity(double x, double y, double z, World world) {
        super(CombatSetup.IRON_NUGGET.get(), x, y, z, world, Items.IRON_NUGGET, 5.0f);
    }

    public IronNuggetEntity(EntityType<IronNuggetEntity> type, World world) {
        super(CombatSetup.IRON_NUGGET.get(), world, Items.IRON_NUGGET, 5.0f);
    }

}
