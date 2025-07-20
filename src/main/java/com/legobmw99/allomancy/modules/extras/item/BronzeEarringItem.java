package com.legobmw99.allomancy.modules.extras.item;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import com.legobmw99.allomancy.modules.extras.ExtrasSetup;
import com.legobmw99.allomancy.util.AllomancyTags;
import com.legobmw99.allomancy.util.ItemDisplay;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;


public class BronzeEarringItem extends ArmorItem {


    private final Multimap<Attribute, AttributeModifier> defaultModifiers;

    public BronzeEarringItem() {
        super(new BronzeMaterial(), Type.HELMET, new Item.Properties().stacksTo(1).durability());
        ImmutableMultimap.Builder<Attribute, AttributeModifier> builder = ImmutableMultimap.builder();
        builder.put(Attributes.ATTACK_DAMAGE, new AttributeModifier(BASE_ATTACK_DAMAGE_UUID, "Weapon modifier", 2,
                                                                    AttributeModifier.Operation.ADDITION));
        builder.put(Attributes.ATTACK_SPEED, new AttributeModifier(BASE_ATTACK_SPEED_UUID, "Weapon modifier", -2,
                                                                   AttributeModifier.Operation.ADDITION));
        this.defaultModifiers = builder.build();
    }

    @Override
    public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        if (target.isDeadOrDying() && target.getType().is(AllomancyTags.HEMALURGIC_CHARGERS) &&
            stack.getItem() != ExtrasSetup.CHARGED_BRONZE_EARRING.get()) {
            ItemStack itemstack = new ItemStack(ExtrasSetup.CHARGED_BRONZE_EARRING.get());

            CompoundTag compoundtag = stack.getTag();
            if (compoundtag != null) {
                itemstack.setTag(compoundtag.copy());
            }
            attacker.setItemInHand(InteractionHand.MAIN_HAND, itemstack);
        }
        return false;
    }

    @Override
    public void appendHoverText(ItemStack pStack,
                                @org.jetbrains.annotations.Nullable Level pLevel,
                                List<Component> pTooltipComponents,
                                TooltipFlag pIsAdvanced) {
        super.appendHoverText(pStack, pLevel, pTooltipComponents, pIsAdvanced);

        Component lore = pStack.getItem() == ExtrasSetup.BRONZE_EARRING.get() ?
                         ItemDisplay.addColorToText("item.allomancy.bronze_earring.lore", ChatFormatting.GRAY) :
                         ItemDisplay.addColorToText("item.allomancy.charged_bronze_earring.lore",
                                                    ChatFormatting.BLUE);
        pTooltipComponents.add(lore);
    }

    @Override
    public boolean canEquip(ItemStack stack, EquipmentSlot armorType, Entity entity) {
        return entity instanceof Player && armorType == EquipmentSlot.HEAD &&
               stack.getItem() == ExtrasSetup.CHARGED_BRONZE_EARRING.get();
    }


    @Override
    public @Nullable EquipmentSlot getEquipmentSlot(ItemStack stack) {
        if (stack.getItem() == ExtrasSetup.CHARGED_BRONZE_EARRING.get()) {
            return EquipmentSlot.HEAD;
        }
        return null;
    }

    @Override
    public Rarity getRarity(ItemStack pStack) {
        if (pStack.getItem() == ExtrasSetup.CHARGED_BRONZE_EARRING.get()) {
            return Rarity.RARE;
        }
        return Rarity.COMMON;
    }

    @Override
    public Multimap<Attribute, AttributeModifier> getAttributeModifiers(EquipmentSlot slot, ItemStack stack) {
        return (slot == EquipmentSlot.MAINHAND && stack.getItem() == ExtrasSetup.BRONZE_EARRING.get()) ?
               this.defaultModifiers : super.getAttributeModifiers(slot, stack);
    }

    @Override
    public boolean canPerformAction(ItemStack stack, net.minecraftforge.common.ToolAction toolAction) {
        return stack.getItem() == ExtrasSetup.BRONZE_EARRING.get() &&
               net.minecraftforge.common.ToolActions.DEFAULT_SWORD_ACTIONS.contains(toolAction);
    }

    private static class BronzeMaterial implements ArmorMaterial {
        @Override
        public int getDurabilityForType(Type type) {
            return 0;
        }

        @Override
        public int getDefenseForType(Type type) {
            return 0;
        }

        @Override
        public int getEnchantmentValue() {
            return 0;
        }

        @Override
        public SoundEvent getEquipSound() {
            return SoundEvents.ARMOR_EQUIP_CHAIN;
        }

        @Override
        public Ingredient getRepairIngredient() {
            return Ingredient.EMPTY;
        }

        @Override
        public String getName() {
            return "allomancy:bronze";
        }

        @Override
        public float getToughness() {
            return 0;
        }

        @Override
        public float getKnockbackResistance() {
            return 0;
        }
    }
}