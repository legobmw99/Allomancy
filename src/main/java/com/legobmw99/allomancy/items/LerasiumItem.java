package com.legobmw99.allomancy.items;

import com.legobmw99.allomancy.Allomancy;
import com.legobmw99.allomancy.util.AllomancyCapability;
import com.legobmw99.allomancy.util.Registry;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.effect.LightningBoltEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
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
	public ActionResult<ItemStack> onItemRightClick(World world, PlayerEntity player, Hand hand){
		AllomancyCapability cap = AllomancyCapability.forPlayer(player);
		ItemStack itemStackIn = player.getHeldItem(hand);
		if (cap.getAllomancyPower() != 8) {
	        player.setActiveHand(hand);
	        return new ActionResult<ItemStack> (ActionResultType.SUCCESS, itemStackIn);

		} else {
        return new ActionResult<ItemStack> (ActionResultType.FAIL, itemStackIn);
		}
	}

	@Override
	public ActionResultType onItemUse(ItemUseContext context) {
		PlayerEntity player = context.getPlayer();
		if(player == null){
			return ActionResultType.FAIL;
		}

		AllomancyCapability cap = AllomancyCapability.forPlayer(player);
		double x = player.posX;
		double y = player.posY + 3;
		double z = player.posZ;
		if (cap.getAllomancyPower() != 8) {
			cap.setAllomancyPower(8);
		}
		//Fancy shmancy effects
		context.getWorld().addEntity(new LightningBoltEntity(context.getWorld(), x, y, z, true));
		player.addPotionEffect(new EffectInstance(Effects.FIRE_RESISTANCE,
				20, 0, true, false));

		return super.onItemUse(context);
	}

	@Override
	public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
		super.addInformation(stack, worldIn, tooltip, flagIn);
		tooltip.add(new StringTextComponent("item.lerasium_nugget.lore"));

	}

    @Override
    public boolean hasEffect(ItemStack par1ItemStack) {
        //Add enchantment glint
        return true;
    }
}