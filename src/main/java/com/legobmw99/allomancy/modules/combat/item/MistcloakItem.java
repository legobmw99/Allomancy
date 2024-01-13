package com.legobmw99.allomancy.modules.combat.item;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import com.legobmw99.allomancy.modules.combat.CombatSetup;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.Item;

import java.util.UUID;

public class MistcloakItem extends ArmorItem {
    private static final UUID MODIFIER = UUID.fromString("9F3D476D-C118-4544-8365-64846904B48E");

    private final Multimap<Attribute, AttributeModifier> attributes;

    public MistcloakItem() {
        super(CombatSetup.WoolArmor, Type.CHESTPLATE, new Item.Properties());

        ImmutableMultimap.Builder<Attribute, AttributeModifier> builder = ImmutableMultimap.builder();
        builder.put(Attributes.ARMOR, new AttributeModifier(MODIFIER, "Armor modifier", this.getDefense(), AttributeModifier.Operation.ADDITION));
        builder.put(Attributes.ARMOR_TOUGHNESS, new AttributeModifier(MODIFIER, "Armor toughness", this.getToughness(), AttributeModifier.Operation.ADDITION));
        builder.put(Attributes.MOVEMENT_SPEED, new AttributeModifier(MODIFIER, "Speed Modifier", .25, AttributeModifier.Operation.MULTIPLY_TOTAL));
        this.attributes = builder.build();
    }

    @Override
    public Multimap<Attribute, AttributeModifier> getDefaultAttributeModifiers(EquipmentSlot equipmentSlot) {
        return equipmentSlot == this.getEquipmentSlot() ? this.attributes : super.getDefaultAttributeModifiers(equipmentSlot);

    }

}