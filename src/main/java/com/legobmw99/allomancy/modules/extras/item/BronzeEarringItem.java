package com.legobmw99.allomancy.modules.extras.item;

import com.legobmw99.allomancy.modules.extras.ExtrasSetup;
import com.legobmw99.allomancy.util.AllomancyTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemAttributeModifiers;


public class BronzeEarringItem extends Item {

    public BronzeEarringItem(Properties properties) {
        super(properties);
    }

    @Override
    public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        System.out.println(target);
        if (target.isDeadOrDying() && target.getType().is(AllomancyTags.HEMALURGIC_CHARGERS) &&
            stack.getItem() != ExtrasSetup.CHARGED_BRONZE_EARRING.get()) {
            attacker.setItemInHand(InteractionHand.MAIN_HAND,
                                   stack.transmuteCopy(ExtrasSetup.CHARGED_BRONZE_EARRING.get()));
        }
        return false;
    }

    public static ItemAttributeModifiers createAttributes() {
        return ItemAttributeModifiers
                .builder()
                .add(Attributes.ATTACK_DAMAGE,
                     new AttributeModifier(BASE_ATTACK_DAMAGE_ID, 2, AttributeModifier.Operation.ADD_VALUE),
                     EquipmentSlotGroup.MAINHAND)
                .add(Attributes.ATTACK_SPEED,
                     new AttributeModifier(Item.BASE_ATTACK_SPEED_ID, -2, AttributeModifier.Operation.ADD_VALUE),
                     EquipmentSlotGroup.MAINHAND)
                .build()
                .withTooltip(true);
    }
}
