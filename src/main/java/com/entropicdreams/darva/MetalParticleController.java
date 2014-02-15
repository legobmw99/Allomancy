package com.entropicdreams.darva;

import java.util.EnumSet;
import java.util.LinkedList;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.monster.EntityIronGolem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

import com.entropicdreams.darva.common.Registry;
import com.entropicdreams.darva.handlers.PacketHandler;
import com.entropicdreams.darva.util.vector3;

import cpw.mods.fml.common.ITickHandler;
import cpw.mods.fml.common.TickType;
import cpw.mods.fml.common.network.PacketDispatcher;

public class MetalParticleController implements ITickHandler {
	public LinkedList<Entity> particleTargets;
	public LinkedList<vector3> particleBlockTargets;
	private LinkedList<Integer> metallist;
	private String[] ores = OreDictionary.getOreNames();

	public void BuildMetalList() {
		this.metallist = new LinkedList<Integer>();
		this.metallist.add(Item.ingotGold.itemID);
		this.metallist.add(Item.ingotIron.itemID);
		this.metallist.add(Item.axeIron.itemID);
		this.metallist.add(Item.axeGold.itemID);
		this.metallist.add(Item.bootsChain.itemID);
		this.metallist.add(Item.bootsGold.itemID);
		this.metallist.add(Item.bootsIron.itemID);
		this.metallist.add(Item.bucketEmpty.itemID);
		this.metallist.add(Item.bucketLava.itemID);
		this.metallist.add(Item.bucketMilk.itemID);
		this.metallist.add(Item.bucketWater.itemID);
		this.metallist.add(Item.cauldron.itemID);
		this.metallist.add(Item.compass.itemID);
		this.metallist.add(Item.doorIron.itemID);
		this.metallist.add(Item.flintAndSteel.itemID);
		this.metallist.add(Item.goldNugget.itemID);
		this.metallist.add(Item.helmetChain.itemID);
		this.metallist.add(Item.helmetGold.itemID);
		this.metallist.add(Item.helmetIron.itemID);
		this.metallist.add(Item.hoeGold.itemID);
		this.metallist.add(Item.hoeIron.itemID);
		this.metallist.add(Item.horseArmorGold.itemID);
		this.metallist.add(Item.horseArmorIron.itemID);
		this.metallist.add(Item.legsChain.itemID);
		this.metallist.add(Item.legsGold.itemID);
		this.metallist.add(Item.legsIron.itemID);
		this.metallist.add(Item.minecartCrate.itemID);
		this.metallist.add(Item.minecartEmpty.itemID);
		this.metallist.add(Item.minecartHopper.itemID);
		this.metallist.add(Item.minecartPowered.itemID);
		this.metallist.add(Item.minecartTnt.itemID);
		this.metallist.add(Item.pickaxeIron.itemID);
		this.metallist.add(Item.pickaxeGold.itemID);
		this.metallist.add(Item.plateIron.itemID);
		this.metallist.add(Item.pocketSundial.itemID);
		this.metallist.add(Item.shovelGold.itemID);
		this.metallist.add(Item.shovelIron.itemID);
		this.metallist.add(Item.shears.itemID);
		this.metallist.add(Item.appleGold.itemID);
		this.metallist.add(Item.swordGold.itemID);
		this.metallist.add(Item.swordIron.itemID);
		this.metallist.add(Registry.itemBrassFlakes.itemID);
		this.metallist.add(Registry.itemBronzeFlakes.itemID);
		this.metallist.add(Registry.itemCopperFlakes.itemID);
		this.metallist.add(Registry.itemCopperIngot.itemID);
		this.metallist.add(Registry.itemIronFlakes.itemID);
		this.metallist.add(Registry.itemLeadFlakes.itemID);
		this.metallist.add(Registry.itemLeadIngot.itemID);
		this.metallist.add(Registry.itemPewterFlakes.itemID);
		this.metallist.add(Registry.itemSteelFlakes.itemID);
		this.metallist.add(Registry.itemTinFlakes.itemID);
		this.metallist.add(Registry.itemTinIngot.itemID);
		this.metallist.add(Registry.nuggetLerasium.itemID);
		this.metallist.add(Block.anvil.blockID);
		this.metallist.add(Block.blockGold.blockID);
		this.metallist.add(Block.blockIron.blockID);
		this.metallist.add(Block.fenceIron.blockID);
		this.metallist.add(Block.hopperBlock.blockID);
		this.metallist.add(Block.oreGold.blockID);
		this.metallist.add(Block.oreIron.blockID);
		this.metallist.add(Block.pistonBase.blockID);
		this.metallist.add(Block.pistonExtension.blockID);
		this.metallist.add(Block.pistonMoving.blockID);
		this.metallist.add(Block.pistonStickyBase.blockID);
		this.metallist.add(Block.pressurePlateGold.blockID);
		this.metallist.add(Block.pressurePlateIron.blockID);
		this.metallist.add(Block.rail.blockID);
		this.metallist.add(Block.railActivator.blockID);
		this.metallist.add(Block.railDetector.blockID);
		this.metallist.add(Block.railPowered.blockID);
		this.metallist.add(Registry.oreCopper.blockID);
		this.metallist.add(Registry.oreTin.blockID);
		this.metallist.add(Registry.oreZinc.blockID);
		this.metallist.add(Registry.oreLead.blockID);
		this.metallist.add(Registry.itemVial.itemID);
		this.metallist.add(Registry.itemZincFlakes.itemID);
		this.metallist.add(Registry.itemZincIngot.itemID);

		/*
		 * for (String s : ores){ if (s.contains("ingot") || s.contains("metal")
		 * || s.contains("ore")){ metallist.add(OreDictionary.getOreID(s)); } }
		 */
	}

	public boolean isItemMetal(ItemStack item) {
		if (this.metallist.contains(item.itemID)) {
			return true;
		} else {
			return false;
		}
	}

	public boolean isBlockMetal(int blockID) {
		if (this.metallist.contains(blockID)) {
			return true;
		} else {
			return false;
		}
	}

	public MetalParticleController() {
		this.particleTargets = new LinkedList<Entity>();
		this.BuildMetalList();
		this.particleBlockTargets = new LinkedList<vector3>();
	}

	public void tryAdd(Entity entity) {
		if (this.particleTargets.contains(entity)) {
			return;
		}
		if (entity instanceof EntityLiving) {
			this.tryAddLiving((EntityLiving) entity);
			return;
		}
		if (entity instanceof EntityItem) {
			this.tryAddItem((EntityItem) entity);
		}
	}

	private void tryAddLiving(EntityLiving entity) {
		if (entity == null) {
			return;
		}
		if ((entity instanceof EntityIronGolem)
				|| ((entity.getHeldItem() != null) && this.metallist
						.contains(entity.getHeldItem().itemID))) {
			this.particleTargets.add(entity);

		}
	}

	private void tryAddItem(EntityItem entity) {
		if (this.isItemMetal(entity.getEntityItem())) {
			this.particleTargets.add(entity);
		}
	}

	public void tryPushBlock(vector3 vec) {
		double motionX, motionY, motionZ;
		EntityPlayer player = Minecraft.getMinecraft().thePlayer;
		motionX = ((player.posX - vec.X) * .1);
		motionY = ((player.posY - vec.Y) * .1);
		motionZ = ((player.posZ - vec.Z) * .1);
		player.motionX = motionX;
		player.motionY = motionY;
		player.motionZ = motionZ;
		PacketDispatcher.sendPacketToServer(PacketHandler
				.stopFall(player.entityId));
	}

	public void tryPullBlock(vector3 vec) {
		double motionX, motionY, motionZ;
		EntityPlayer player = Minecraft.getMinecraft().thePlayer;
		motionX = ((player.posX - vec.X) * .1) * -1;
		motionY = ((player.posY - vec.Y) * .1) * -1;
		motionZ = ((player.posZ - vec.Z) * .1) * -1;
		player.motionX = motionX;
		player.motionY = motionY;
		player.motionZ = motionZ;
		PacketDispatcher.sendPacketToServer(PacketHandler
				.stopFall(player.entityId));
	}

	public void tryPushEntity(Entity entity) {

		if (entity instanceof EntityItem) {
			this.tryPushItem((EntityItem) entity);
		}

		if (entity instanceof EntityLiving) {
			this.tryPushMob((EntityLiving) entity);
		}

	}

	public void tryPullEntity(Entity entity) {
		if (entity instanceof EntityItem) {
			this.tryPullItem((EntityItem) entity);
		}
		if (entity instanceof EntityLiving) {
			this.tryPullMob((EntityLiving) entity);
		}

	}

	private void tryPullItem(EntityItem entity) {
		double motionX, motionY, motionZ;
		EntityPlayer player = Minecraft.getMinecraft().thePlayer;
		if (this.metallist.contains(entity.getEntityItem().itemID)) {
			motionX = (player.posX - entity.posX) * .1;
			motionY = (player.posY - entity.posY) * .1;
			motionZ = (player.posZ - entity.posZ) * .1;
			entity.motionX = motionX;
			entity.motionY = motionY;
			entity.motionZ = motionZ;

			PacketDispatcher.sendPacketToServer(PacketHandler.moveEntity(
					motionX, motionY, motionZ, entity.entityId));
		}
	}

	private void tryPushItem(EntityItem entity) {
		double motionX, motionY, motionZ;
		EntityPlayer player = Minecraft.getMinecraft().thePlayer;
		if (this.metallist.contains(entity.getEntityItem().itemID)) {
			motionX = ((player.posX - entity.posX) * .1) * -1;
			motionY = ((player.posY - entity.posY) * .1);
			motionZ = ((player.posZ - entity.posZ) * .1) * -1;
			entity.motionX = motionX;
			entity.motionY = motionY;
			entity.motionZ = motionZ;
			PacketDispatcher.sendPacketToServer(PacketHandler.moveEntity(
					motionX, motionY, motionZ, entity.entityId));
		}
	}

	private void tryPullMob(EntityLiving entity) {

		double motionX, motionY, motionZ;
		EntityPlayer player = Minecraft.getMinecraft().thePlayer;
		if (entity instanceof EntityIronGolem) {
			motionX = ((player.posX - entity.posX) * .1) * -1;
			motionY = ((player.posY - entity.posY) * .1) * -1;
			motionZ = ((player.posZ - entity.posZ) * .1) * -1;
			player.motionX = motionX;
			player.motionY = motionY;
			player.motionZ = motionZ;
			PacketDispatcher.sendPacketToServer(PacketHandler
					.stopFall(player.entityId));
			// waaaaay too damn heavy to push... you get moved.
		}

		if (entity.getHeldItem() == null) {
			return;
		}

		if (this.isItemMetal(entity.getHeldItem())) {
			// Pull em towards you.
			motionX = ((player.posX - entity.posX) * .1);
			motionY = ((player.posY - entity.posY) * .1);
			motionZ = ((player.posZ - entity.posZ) * .1);
			entity.motionX = motionX;
			entity.motionY = motionY;
			entity.motionZ = motionZ;
			PacketDispatcher.sendPacketToServer(PacketHandler.moveEntity(
					motionX, motionY, motionZ, entity.entityId));
			PacketDispatcher.sendPacketToServer(PacketHandler
					.stopFall(entity.entityId));
		}
	}

	private void tryPushMob(EntityLiving entity) {

		double motionX, motionY, motionZ;
		EntityPlayer player = Minecraft.getMinecraft().thePlayer;
		if (entity instanceof EntityIronGolem) {
			motionX = ((player.posX - entity.posX) * .1);
			motionY = ((player.posY - entity.posY) * .1);
			motionZ = ((player.posZ - entity.posZ) * .1);
			player.motionX = motionX;
			player.motionY = motionY;
			player.motionZ = motionZ;
			PacketDispatcher.sendPacketToServer(PacketHandler
					.stopFall(player.entityId));
			// waaaaay too damn heavy to push... you get moved.
		}

		if (entity.getHeldItem() == null) {
			return;
		}

		if (this.isItemMetal(entity.getHeldItem())) {
			// Pull em towards you.
			motionX = ((player.posX - entity.posX) * .1) * -1;
			motionY = (player.posY - entity.posY) * .1;
			motionZ = ((player.posZ - entity.posZ) * .1) * -1;
			entity.motionX = motionX;
			entity.motionY = motionY;
			entity.motionZ = motionZ;
			PacketDispatcher.sendPacketToServer(PacketHandler.moveEntity(
					motionX, motionY, motionZ, entity.entityId));
			PacketDispatcher.sendPacketToServer(PacketHandler
					.stopFall(entity.entityId));
		}
	}

	@Override
	public void tickStart(EnumSet<TickType> type, Object... tickData) {
		// TODO Auto-generated method stub
	}

	@Override
	public void tickEnd(EnumSet<TickType> type, Object... tickData) {
		// TODO Auto-generated method stub
		EntityPlayer player = Minecraft.getMinecraft().thePlayer;
		LinkedList<Entity> toRemove = new LinkedList<Entity>();
		new LinkedList<vector3>();
		if (player == null) {
			return;
		}

		for (Entity entity : this.particleTargets) {

			if (entity.isDead == true) {
				toRemove.add(entity);
			}

			if (player.getDistanceToEntity(entity) > 10) {
				toRemove.add(entity);
			}
		}
		for (Entity entity : toRemove) {
			this.particleTargets.remove(entity);
		}
		this.particleBlockTargets.clear();
		toRemove.clear();
	}

	@Override
	public EnumSet<TickType> ticks() {
		// TODO Auto-generated method stub
		return EnumSet.of(TickType.RENDER);
	}

	@Override
	public String getLabel() {
		// TODO Auto-generated method stub
		return null;
	}
}
