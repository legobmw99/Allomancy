package com.legobmw99.allomancy.modules.extras.item;

import com.legobmw99.allomancy.Allomancy;
import com.legobmw99.allomancy.modules.extras.ExtrasSetup;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.levelgen.structure.Structure;


public class BronzeEarringItem extends Item {
    public static TagKey<Structure> SEEKABLE = TagKey.create(Registries.STRUCTURE, Allomancy.rl("seekable"));

    public BronzeEarringItem(Properties properties) {
        super(properties);
    }

    @Override
    public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        if (target.isDeadOrDying() && stack.getItem() != ExtrasSetup.CHARGED_BRONZE_EARRING.get()) {
            stack.transmuteCopy(ExtrasSetup.CHARGED_BRONZE_EARRING.get());
        }
        return super.hurtEnemy(stack, target, attacker);
    }
}
