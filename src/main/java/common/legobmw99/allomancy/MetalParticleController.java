package com.entropicdreams.darva;

import java.util.EnumSet;
import java.util.LinkedList;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.monster.EntityIronGolem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraftforge.oredict.OreDictionary;

import common.legobmw99.allomancy.common.Registry;
import common.legobmw99.allomancy.util.vector3;

public class MetalParticleController{
	public LinkedList<Entity> particleTargets;
	public LinkedList<vector3> particleBlockTargets;
	private LinkedList<Integer> metallist;
	private String[] ores = OreDictionary.getOreNames();

	public void BuildMetalList() {
		this.metallist = new LinkedList<Integer>();
		this.metallist.add(Items.gold_ingot.getIdFromItem(Items.gold_ingot));
		this.metallist.add(Items.iron_ingot.getIdFromItem(Items.iron_ingot));
		this.metallist.add(Items.iron_axe);
		this.metallist.add(Items.golden_axe);
		this.metallist.add(Items.chainmail_boots);
		this.metallist.add(Items.golden_boots);
		this.metallist.add(Items.bootsIron);
		this.metallist.add(Items.bucketEmpty);
		this.metallist.add(Items.bucketLava);
		this.metallist.add(Items.bucketMilk);
		this.metallist.add(Items.bucketWater);
		this.metallist.add(Items.cauldron.getIdFromItem(Items.cauldron));
		this.metallist.add(Items.compass.getIdFromItem(Items.compass));
		this.metallist.add(Items.doorIron.ItemsID);
		this.metallist.add(Items.flintAndSteel.ItemsID);
		this.metallist.add(Items.goldNugget.ItemsID);
		this.metallist.add(Items.helmetChain.ItemsID);
		this.metallist.add(Items.helmetGold.ItemsID);
		this.metallist.add(Items.helmetIron.ItemsID);
		this.metallist.add(Items.hoeGold.ItemsID);
		this.metallist.add(Items.hoeIron.ItemsID);
		this.metallist.add(Items.horseArmorGold.ItemsID);
		this.metallist.add(Items.horseArmorIron.ItemsID);
		this.metallist.add(Items.legsChain.ItemsID);
		this.metallist.add(Items.legsGold.ItemsID);
		this.metallist.add(Items.legsIron.ItemsID);
		this.metallist.add(Items.minecartCrate.ItemsID);
		this.metallist.add(Items.minecartEmpty.ItemsID);
		this.metallist.add(Items.minecartHopper.ItemsID);
		this.metallist.add(Items.minecartPowered.ItemsID);
		this.metallist.add(Items.minecartTnt.ItemsID);
		this.metallist.add(Items.pickaxeIron.ItemsID);
		this.metallist.add(Items.pickaxeGold.ItemsID);
		this.metallist.add(Items.plateIron.ItemsID);
		this.metallist.add(Items.pocketSundial.ItemsID);
		this.metallist.add(Items.shovelGold.ItemsID);
		this.metallist.add(Items.shovelIron.ItemsID);
		this.metallist.add(Items.shears.getIdFromItem(Items.shears));
		this.metallist.add(Items.appleGold.ItemsID);
		this.metallist.add(Items.swordGold.ItemsID);
		this.metallist.add(Items.swordIron.ItemsID);
		this.metallist.add(Registry.ItemsBrassFlakes.ItemsID);
		this.metallist.add(Registry.ItemsBronzeFlakes.ItemsID);
		this.metallist.add(Registry.ItemsCopperFlakes.ItemsID);
		this.metallist.add(Registry.ItemsCopperIngot.ItemsID);
		this.metallist.add(Registry.ItemsIronFlakes.ItemsID);
		this.metallist.add(Registry.ItemsLeadFlakes.ItemsID);
		this.metallist.add(Registry.ItemsLeadIngot.ItemsID);
		this.metallist.add(Registry.ItemsPewterFlakes.ItemsID);
		this.metallist.add(Registry.ItemsSteelFlakes.ItemsID);
		this.metallist.add(Registry.ItemsTinFlakes.ItemsID);
		this.metallist.add(Registry.ItemsTinIngot.ItemsID);
		this.metallist.add(Registry.nuggetLerasium.getIdFromItem(Registry.nuggetLerasium));
		this.metallist.add(Blocks.anvil.getStateId(Blocks.anvil.getDefaultState()));
		this.metallist.add(Blocks.cauldron);
		this.metallist.add(Blocks.blockGold);
		this.metallist.add(Blocks.blockIron);
		this.metallist.add(Blocks.fenceIron);
		this.metallist.add(Blocks.hopperBlock);
		this.metallist.add(Blocks.gold_ore);
		this.metallist.add(Blocks.oreIron);
		this.metallist.add(Blocks.pistonBase);
		this.metallist.add(Blocks.pistonExtension);
		this.metallist.add(Blocks.pistonMoving);
		this.metallist.add(Blocks.pistonStickyBase);
		this.metallist.add(Blocks.pressurePlateGold);
		this.metallist.add(Blocks.pressurePlateIron);
		this.metallist.add(Blocks.rail);
		this.metallist.add(Blocks.activator_rail);
		this.metallist.add(Blocks.detector_rail);
		this.metallist.add(Blocks.golden_rail);
		this.metallist.add(Registry.oreCopper);
		this.metallist.add(Registry.oreTin);
		this.metallist.add(Registry.oreZinc);
		this.metallist.add(Registry.oreLead);
		this.metallist.add(Registry.itemVial);
		this.metallist.add(Registry.itemZincFlakes);
		this.metallist.add(Registry.itemZincIngot);

		/*
		 * for (String s : ores){ if (s.contains("ingot") || s.contains("metal")
		 * || s.contains("ore")){ metallist.add(OreDictionary.getOreID(s)); } }
		 */
	}

	public boolean isItemsMetal(ItemsStack Items) {
		if (this.metallist.contains(Items.ItemsID)) {
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
		if (entity instanceof EntityItems) {
			this.tryAddItems((EntityItems) entity);
		}
	}

	private void tryAddLiving(EntityLiving entity) {
		if (entity == null) {
			return;
		}
		if ((entity instanceof EntityIronGolem)
				|| ((entity.getHeldItems() != null) && this.metallist
						.contains(entity.getHeldItems().ItemsID))) {
			this.particleTargets.add(entity);

		}
	}

	private void tryAddItems(EntityItems entity) {
		if (this.isItemsMetal(entity.getEntityItems())) {
			this.particleTargets.add(entity);
		}
	}

	public void tryPushBlock(vector3 vec) {
		double motionX, motionY, motionZ, magnitude;
		EntityPlayer player = Minecraft.getMinecraft().thePlayer;
		magnitude = Math.sqrt(Math.pow((player.posX - vec.X),2) + Math.pow((player.posY - vec.Y),2) + Math.pow((player.posZ - vec.Z),2) );
		motionX = ((player.posX - vec.X) * (1.1)/magnitude);
		motionY = ((player.posY - vec.Y) * (1.1)/magnitude);
		motionZ = ((player.posZ - vec.Z) * (1.1)/magnitude);
		player.motionX = motionX;
		player.motionY = motionY;
		player.motionZ = motionZ;
		PacketDispatcher.sendPacketToServer(PacketHandler
				.stopFall(player.entityId));
	}

	public void tryPullBlock(vector3 vec) {
		double motionX, motionY, motionZ, magnitude;
		EntityPlayer player = Minecraft.getMinecraft().thePlayer;
		magnitude = Math.sqrt(Math.pow((player.posX - vec.X),2) + Math.pow((player.posY - vec.Y),2) + Math.pow((player.posZ - vec.Z),2) );
		motionX = ((player.posX - vec.X) * -(1.1)/magnitude);
		motionY = ((player.posY - vec.Y) * -(1.1)/magnitude);
		motionZ = ((player.posZ - vec.Z) * -(1.1)/magnitude);
		player.motionX = motionX;
		player.motionY = motionY;
		player.motionZ = motionZ;
		PacketDispatcher.sendPacketToServer(PacketHandler
				.stopFall(player.entityId));
	}

	public void tryPushEntity(Entity entity) {

		if (entity instanceof EntityItems) {
			this.tryPushItems((EntityItems) entity);
		}

		if (entity instanceof EntityLiving) {
			this.tryPushMob((EntityLiving) entity);
		}

	}

	public void tryPullEntity(Entity entity) {
		if (entity instanceof EntityItems) {
			this.tryPullItems((EntityItems) entity);
		}
		if (entity instanceof EntityLiving) {
			this.tryPullMob((EntityLiving) entity);
		}

	}

	private void tryPullItems(EntityItems entity) {
		double motionX, motionY, motionZ;
		EntityPlayer player = Minecraft.getMinecraft().thePlayer;
		if (this.metallist.contains(entity.getEntityItems().ItemsID)) {
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

	private void tryPushItems(EntityItems entity) {
		double motionX, motionY, motionZ;
		EntityPlayer player = Minecraft.getMinecraft().thePlayer;
		if (this.metallist.contains(entity.getEntityItems().ItemsID)) {
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

		double motionX, motionY, motionZ,magnitude;
		EntityPlayer player = Minecraft.getMinecraft().thePlayer;
		if (entity instanceof EntityIronGolem) {
			magnitude = Math.sqrt(Math.pow((player.posX - entity.posX),2) + Math.pow((player.posY - entity.posY),2) + Math.pow((player.posZ - entity.posZ),2) );
			motionX = ((player.posX - entity.posX) * -(1.1)/magnitude);
			motionY = ((player.posY - entity.posY) * -(1.1)/magnitude);
			motionZ = ((player.posZ - entity.posZ) * -(1.1)/magnitude);
			player.motionX = motionX;
			player.motionY = motionY;
			player.motionZ = motionZ;
			PacketDispatcher.sendPacketToServer(PacketHandler
					.stopFall(player.entityId));
			// waaaaay too damn heavy to push... you get moved.
		}

		if (entity.getHeldItems() == null) {
			return;
		}

		if (this.isItemsMetal(entity.getHeldItems())) {
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

		double motionX, motionY, motionZ,magnitude;
		EntityPlayer player = Minecraft.getMinecraft().thePlayer;
		if (entity instanceof EntityIronGolem) {
			magnitude = Math.sqrt(Math.pow((player.posX - entity.posX),2) + Math.pow((player.posY - entity.posY),2) + Math.pow((player.posZ - entity.posZ),2) );
			motionX = ((player.posX - entity.posX) * (1.1)/magnitude);
			motionY = ((player.posY - entity.posY) * (1.1)/magnitude);
			motionZ = ((player.posZ - entity.posZ) * (1.1)/magnitude);
			PacketDispatcher.sendPacketToServer(PacketHandler
					.stopFall(player.entityId));
			// waaaaay too damn heavy to push... you get moved.
		}

		if (entity.getHeldItems() == null) {
			return;
		}

		if (this.isItemsMetal(entity.getHeldItems())) {
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
