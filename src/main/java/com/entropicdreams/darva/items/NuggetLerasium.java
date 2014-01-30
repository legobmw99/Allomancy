package com.entropicdreams.darva.items;

import java.util.List;

import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.util.WeightedRandomChestContent;
import net.minecraft.world.World;
import net.minecraftforge.common.ChestGenHooks;

import com.entropicdreams.darva.AllomancyData;
import com.entropicdreams.darva.common.ModRegistry;
import com.entropicdreams.darva.handlers.PacketHandler;

import cpw.mods.fml.common.network.PacketDispatcher;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class NuggetLerasium extends ItemFood{
	public NuggetLerasium(int par1) {
		super(par1, 0, false);
		this.setAlwaysEdible();
		this.setHasSubtypes(false);
		this.setCreativeTab(ModRegistry.tabsAllomancy);
	}
	@Override
	public int getHealAmount() {
		return 0;
	}

	@Override
	public EnumAction getItemUseAction(ItemStack par1ItemStack) {
		return EnumAction.eat;
	}

	@Override
	public int getMaxItemUseDuration(ItemStack par1ItemStack) {
		return 1;
	}

	@Override
	public ItemStack onItemRightClick(ItemStack par1ItemStack, World par2World,
			EntityPlayer par3EntityPlayer) {
		par3EntityPlayer.setItemInUse(par1ItemStack,
				this.getMaxItemUseDuration(par1ItemStack));
		return par1ItemStack;
	}
	public ItemStack onEaten(ItemStack item, World world,
			EntityPlayer player) {
		AllomancyData data;
		data = AllomancyData.forPlayer(player);
	
		if (data.isMistborn == false) {
			data.isMistborn = true;
			PacketDispatcher.sendPacketToServer(PacketHandler.becomeMistborn());
		}
		
		return super.onEaten(item, world, player);
	}
	@Override
	public void addInformation(ItemStack par1ItemStack, EntityPlayer par2EntityPlayer, List par3List, boolean par4)
	{
	par3List.add("\u00A75This item is endowed with strange powers");
	par3List.add("\u00A75Perhaps you should ingest it?");
	}
 @SideOnly(Side.CLIENT)
 	public boolean hasEffect(ItemStack par1ItemStack)
	    {
	        return true;
	    }
}
