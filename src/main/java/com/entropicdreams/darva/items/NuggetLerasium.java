package com.entropicdreams.darva.items;

import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import com.entropicdreams.darva.AllomancyData;
import com.entropicdreams.darva.common.ModRegistry;
import com.entropicdreams.darva.handlers.PacketHandler;

import cpw.mods.fml.common.network.PacketDispatcher;

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
}
