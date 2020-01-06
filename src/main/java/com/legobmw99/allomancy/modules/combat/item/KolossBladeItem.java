package com.legobmw99.allomancy.modules.combat.item;

import com.legobmw99.allomancy.Allomancy;
import com.legobmw99.allomancy.modules.powers.util.AllomancyCapability;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.world.World;

public class KolossBladeItem extends SwordItem {
    public KolossBladeItem() {
        super(ItemTier.STONE, 8, -2.4F,  new Item.Properties().group(ItemGroup.COMBAT));
    }

    @Override
    public void inventoryTick(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
        super.inventoryTick(stack, worldIn, entityIn, itemSlot, isSelected);
        if(isSelected){
            if(entityIn != null && entityIn instanceof PlayerEntity){
                PlayerEntity player = (PlayerEntity) entityIn;
                AllomancyCapability cap = AllomancyCapability.forPlayer(player);
                if(!cap.getMetalBurning(Allomancy.PEWTER)){
                    player.addPotionEffect(new EffectInstance(Effects.MINING_FATIGUE, 10, 10, true, false));
                    player.addPotionEffect(new EffectInstance(Effects.WEAKNESS, 10, 10, true, false));
                    player.addPotionEffect(new EffectInstance(Effects.SLOWNESS, 10, 0, true, false));
                }
            }
        }
    }

    @Override
    public boolean canHarvestBlock(BlockState blockIn) {
        return false;
    }
}
