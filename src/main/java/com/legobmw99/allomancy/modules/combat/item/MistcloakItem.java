package com.legobmw99.allomancy.modules.combat.item;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import com.legobmw99.allomancy.modules.combat.CombatSetup;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;

import java.util.UUID;

public class MistcloakItem extends ArmorItem {
    private static final UUID[] ARMOR_MODIFIERS = new UUID[]{UUID.fromString("845DB27C-C624-495F-8C9F-6020A9A58B6B"), UUID.fromString("D8499B04-0E66-4726-AB29-64469D734E0D"),
                                                             UUID.fromString("9F3D476D-C118-4544-8365-64846904B48E"), UUID.fromString("2AD3F246-FEE1-4E67-B886-69FD380BB150")};
    private final Multimap<Attribute, AttributeModifier> attributes;

    public MistcloakItem() {
        super(CombatSetup.WoolArmor, EquipmentSlotType.CHEST, new Item.Properties().group(ItemGroup.COMBAT));

        ImmutableMultimap.Builder<Attribute, AttributeModifier> builder = ImmutableMultimap.builder();
        UUID uuid = ARMOR_MODIFIERS[this.slot.getIndex()];
        builder.put(Attributes.ARMOR, new AttributeModifier(uuid, "Armor modifier", this.getDamageReduceAmount(), AttributeModifier.Operation.ADDITION));
        builder.put(Attributes.ARMOR_TOUGHNESS, new AttributeModifier(uuid, "Armor toughness", this.getToughness(), AttributeModifier.Operation.ADDITION));
        builder.put(Attributes.MOVEMENT_SPEED, new AttributeModifier(uuid, "Speed Modifier", .25, AttributeModifier.Operation.MULTIPLY_TOTAL));
        this.attributes = builder.build();
    }

    @Override
    public Multimap<Attribute, AttributeModifier> getAttributeModifiers(EquipmentSlotType equipmentSlot) {
        return equipmentSlot == this.slot ? this.attributes : super.getAttributeModifiers(equipmentSlot);

    }

}