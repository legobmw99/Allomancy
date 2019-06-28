package com.legobmw99.allomancy.item;

import com.legobmw99.allomancy.Allomancy;
import com.legobmw99.allomancy.util.AllomancyCapability;
import com.legobmw99.allomancy.util.Registry;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.LightningBoltEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Food;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Rarity;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

public class LerasiumItem extends Item {
    private static final Food lerasium = new Food.Builder().fastToEat().setAlwaysEdible().saturation(0).hunger(0).build();

    public LerasiumItem() {
        super(new Item.Properties().group(Registry.allomancy_group).rarity(Rarity.EPIC).maxStackSize(1).food(lerasium));
        this.setRegistryName(new ResourceLocation(Allomancy.MODID, "lerasium_nugget"));
    }


    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, PlayerEntity player, Hand hand) {
        AllomancyCapability cap = AllomancyCapability.forPlayer(player);
        ItemStack itemStackIn = player.getHeldItem(hand);
        if (cap.getAllomancyPower() != 8) {
            player.setActiveHand(hand);
            return new ActionResult<>(ActionResultType.SUCCESS, itemStackIn);

        } else {
            return new ActionResult<>(ActionResultType.FAIL, itemStackIn);
        }
    }

    @Override
    public ItemStack onItemUseFinish(ItemStack stack, World world, LivingEntity livingEntity) {
        AllomancyCapability cap = AllomancyCapability.forPlayer((PlayerEntity) livingEntity);
        double x = livingEntity.posX;
        double y = livingEntity.posY + 3;
        double z = livingEntity.posZ;
        if (cap.getAllomancyPower() != 8) {
            cap.setAllomancyPower((byte) 8);
        }
        //Fancy shmancy effects
        world.addEntity(new LightningBoltEntity(world, x, y, z, true));
        livingEntity.addPotionEffect(new EffectInstance(Effects.FIRE_RESISTANCE,
                20, 0, true, false));

        return super.onItemUseFinish(stack, world, livingEntity);
    }


    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        super.addInformation(stack, worldIn, tooltip, flagIn);
        ITextComponent lore = new TranslationTextComponent("item.allomancy.lerasium_nugget.lore");
        lore.setStyle(lore.getStyle().setColor(TextFormatting.LIGHT_PURPLE));
        tooltip.add(lore);

    }

    @Override
    public boolean hasEffect(ItemStack par1ItemStack) {
        //Add enchantment glint
        return true;
    }
}