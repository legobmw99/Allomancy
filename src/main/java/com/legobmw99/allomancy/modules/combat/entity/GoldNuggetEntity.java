package com.legobmw99.allomancy.modules.combat.entity;

import com.legobmw99.allomancy.modules.combat.CombatSetup;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Items;
import net.minecraft.world.World;

public class GoldNuggetEntity extends AbstractNuggetEntity {

    public GoldNuggetEntity(World world) {
        super(CombatSetup.GOLD_NUGGET.get(), world, Items.GOLD_NUGGET, 4.0f);
    }

    public GoldNuggetEntity(LivingEntity livingEntity, World world) {
        super(CombatSetup.GOLD_NUGGET.get(), livingEntity, world, Items.GOLD_NUGGET, 4.0f);
    }

    public GoldNuggetEntity(double x, double y, double z, World world) {
        super(CombatSetup.GOLD_NUGGET.get(), x, y, z, world, Items.GOLD_NUGGET, 4.0f);
    }

    public GoldNuggetEntity(EntityType<GoldNuggetEntity> type, World world) {
        super(CombatSetup.GOLD_NUGGET.get(), world, Items.GOLD_NUGGET, 4.0f);
    }

}
