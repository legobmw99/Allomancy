package com.legobmw99.allomancy.modules.combat.item;

import com.legobmw99.allomancy.api.enums.Metal;
import com.legobmw99.allomancy.modules.powers.data.AllomancerAttachment;
import com.legobmw99.allomancy.util.ItemDisplay;
import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ToolMaterial;
import net.minecraft.world.item.component.ItemLore;
import net.minecraft.world.item.component.Weapon;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;

public class KolossBladeItem extends Item {

    private static final int ATTACK_DAMAGE = 17;
    private static final float ATTACK_SPEED = -2.6F;

    private static final ToolMaterial SLOW_STONE =
            new ToolMaterial(BlockTags.INCORRECT_FOR_STONE_TOOL, 131, ATTACK_SPEED, 1.0F, ATTACK_DAMAGE,
                             ItemTags.STONE_TOOL_MATERIALS);

    public KolossBladeItem(Item.Properties props) {
        super(SLOW_STONE
                      .applySwordProperties(props, ATTACK_DAMAGE, ATTACK_SPEED)
                      .component(DataComponents.WEAPON, new Weapon(1, Weapon.AXE_DISABLES_BLOCKING_FOR_SECONDS))
                      .component(DataComponents.LORE, new ItemLore(
                              List.of(ItemDisplay.addColorToText("item.allomancy.koloss_blade.lore",
                                                                 ChatFormatting.GRAY)))));
    }

    @Override
    public void inventoryTick(ItemStack stack, ServerLevel worldIn, Entity entityIn, EquipmentSlot slot) {
        super.inventoryTick(stack, worldIn, entityIn, slot);
        if (entityIn instanceof Player player) {
            if (player.getMainHandItem() == stack) {
                if (!(player.hasEffect(MobEffects.STRENGTH) &&
                      player.getEffect(MobEffects.STRENGTH).getAmplifier() >= 2) &&
                    !player.getData(AllomancerAttachment.ALLOMANCY_DATA).isBurning(Metal.PEWTER)) {
                    player.addEffect(new MobEffectInstance(MobEffects.MINING_FATIGUE, 10, 10, true, false));
                    player.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 10, 10, true, false));
                    player.addEffect(new MobEffectInstance(MobEffects.SLOWNESS, 10, 0, true, false));
                }
            }
        }
    }

    @Override
    public boolean isCorrectToolForDrops(ItemStack pStack, BlockState pState) {
        return false;
    }


}
