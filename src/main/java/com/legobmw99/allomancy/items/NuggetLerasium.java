package com.legobmw99.allomancy.items;

import java.util.List;

import javax.annotation.Nullable;

import com.legobmw99.allomancy.network.packets.AllomancyPowerPacket;
import com.legobmw99.allomancy.util.AllomancyCapabilities;
import com.legobmw99.allomancy.util.Registry;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.EnumAction;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;

public class NuggetLerasium extends ItemFood {
	public NuggetLerasium() {
		super(0, false);
		this.setAlwaysEdible();
		this.setHasSubtypes(false);
		this.setUnlocalizedName("nuggetLerasium");
		this.setCreativeTab(Registry.tabsAllomancy);
		this.maxStackSize = 1;
	}

	@Override
	public EnumAction getItemUseAction(ItemStack par1ItemStack) {
		return EnumAction.EAT;
	}

	@Override
	public int getMaxItemUseDuration(ItemStack par1ItemStack) {
		return 1;
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand hand){
		AllomancyCapabilities cap = AllomancyCapabilities.forPlayer(playerIn);
		ItemStack itemStackIn = playerIn.getHeldItem(hand);
		if (cap.getAllomancyPower() != 8) {
	        playerIn.setActiveHand(hand);
	        return new ActionResult(EnumActionResult.SUCCESS, itemStackIn);	

		} else {
        return new ActionResult(EnumActionResult.FAIL, itemStackIn);	
		}
	}
	
	@Override
    public ItemStack onItemUseFinish(ItemStack stack, World worldIn, EntityLivingBase entityLiving){
		AllomancyCapabilities cap = AllomancyCapabilities.forPlayer((EntityPlayer)entityLiving);
		double x = entityLiving.posX;
		double y = entityLiving.posY + 3;
		double z = entityLiving.posZ;
		if (cap.getAllomancyPower() != 8) {
			cap.setAllomancyPower(8);
        }
		//Fancy shmancy effects
		worldIn.spawnEntity(new EntityLightningBolt(worldIn, x, y, z, true));
		entityLiving.addPotionEffect(new PotionEffect(Potion.getPotionById(12),
				20, 0, true, false));

		return super.onItemUseFinish(stack, worldIn, entityLiving);
	}

	@Override
    public void addInformation(ItemStack stack, @Nullable World playerIn, List<String> tooltip, ITooltipFlag advanced){

		tooltip.add("\u00A75This item is endowed with strange powers");
		tooltip.add("\u00A75Perhaps you should ingest it?");
	}
	
	@Override
    public EnumRarity getRarity(ItemStack stack)
    {
        return EnumRarity.EPIC;
    }

    @Override
    public boolean hasEffect(ItemStack par1ItemStack) {
        //Add enchantment glint
        return true;
    }
}