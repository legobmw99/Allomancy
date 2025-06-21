package com.legobmw99.allomancy.modules.combat.item;

import com.legobmw99.allomancy.util.AllomancyTags;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.ToolMaterial;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentEffectComponents;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.block.state.BlockState;

public class ObsidianDaggerItem extends Item {

    private static final int ATTACK_DAMAGE = 23;
    private static final float ATTACK_SPEED = 9.2F;

    private static final ToolMaterial OBSIDIAN =
            new ToolMaterial(BlockTags.INCORRECT_FOR_WOODEN_TOOL, 2, ATTACK_SPEED, 1.0F, ATTACK_DAMAGE,
                             AllomancyTags.OBSIDIAN_REPAIR);

    public ObsidianDaggerItem(Item.Properties props) {
        super(OBSIDIAN
                      .applySwordProperties(props, ATTACK_DAMAGE, ATTACK_SPEED)
                      .rarity(Rarity.UNCOMMON)
                      .component(DataComponents.ENCHANTABLE, null));
    }

    // prevent dagger from mining
    @Override
    public boolean isCorrectToolForDrops(ItemStack pStack, BlockState pState) {
        return false;
    }

    // Disable mending on daggers
    @Override
    public boolean supportsEnchantment(ItemStack stack, Holder<Enchantment> enchantment) {
        return !(enchantment.value().effects().has(EnchantmentEffectComponents.REPAIR_WITH_XP) ||
                 enchantment.value().effects().has(EnchantmentEffectComponents.ITEM_DAMAGE)) &&
               super.supportsEnchantment(stack, enchantment);
    }

    @Override
    public boolean isPrimaryItemFor(ItemStack stack, Holder<Enchantment> enchantment) {
        if (enchantment.is(Enchantments.MENDING) || enchantment.is(Enchantments.UNBREAKING)) {
            return false;
        }
        return super.isPrimaryItemFor(stack, enchantment);
    }
}
