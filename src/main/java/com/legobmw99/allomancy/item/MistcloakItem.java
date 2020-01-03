package com.legobmw99.allomancy.item;

import com.google.common.collect.Multimap;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.*;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;

import java.util.UUID;

public class MistcloakItem extends ArmorItem {
    private static final UUID[] ARMOR_MODIFIERS = new UUID[]{UUID.fromString("845DB27C-C624-495F-8C9F-6020A9A58B6B"), UUID.fromString("D8499B04-0E66-4726-AB29-64469D734E0D"), UUID.fromString("9F3D476D-C118-4544-8365-64846904B48E"),
            UUID.fromString("2AD3F246-FEE1-4E67-B886-69FD380BB150")};

    private static final IArmorMaterial WoolArmor = new IArmorMaterial() {
        @Override
        public int getDurability(EquipmentSlotType slotIn) {
            return 50;
        }

        @Override
        public int getDamageReductionAmount(EquipmentSlotType slotIn) {
            return slotIn == EquipmentSlotType.CHEST ? 4 : 0;
        }

        @Override
        public int getEnchantability() {
            return 15;
        }

        @Override
        public SoundEvent getSoundEvent() {
            return SoundEvents.ITEM_ARMOR_EQUIP_LEATHER;
        }

        @Override
        public Ingredient getRepairMaterial() {
            return Ingredient.fromItems(Items.GRAY_WOOL);
        }

        @Override
        public String getName() {
            return "allomancy:wool";
        }

        @Override
        public float getToughness() {
            return 0;
        }
    };

    public MistcloakItem() {
        super(WoolArmor, EquipmentSlotType.CHEST, new Item.Properties().group(ItemGroup.COMBAT));
    }

    @Override
    public Multimap<String, AttributeModifier> getAttributeModifiers(EquipmentSlotType equipmentSlot) {
        Multimap<String, AttributeModifier> multimap = super.getAttributeModifiers(equipmentSlot);

        if (equipmentSlot == EquipmentSlotType.CHEST) {
            multimap.put(SharedMonsterAttributes.ARMOR.getName(), new AttributeModifier(ARMOR_MODIFIERS[equipmentSlot.getIndex()], "Armor modifier", (double) this.damageReduceAmount, AttributeModifier.Operation.ADDITION));
            multimap.put(SharedMonsterAttributes.ARMOR_TOUGHNESS.getName(), new AttributeModifier(ARMOR_MODIFIERS[equipmentSlot.getIndex()], "Armor toughness", (double) this.toughness, AttributeModifier.Operation.ADDITION));
            multimap.put(SharedMonsterAttributes.MOVEMENT_SPEED.getName(), new AttributeModifier(ARMOR_MODIFIERS[equipmentSlot.getIndex()], "Speed Modifier", .25, AttributeModifier.Operation.MULTIPLY_TOTAL));
        }
        return multimap;
    }

}