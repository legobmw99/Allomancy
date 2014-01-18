package com.entropicdreams.darva;

import java.util.EnumSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;

import com.entropicdreams.darva.common.ModRegistry;
import com.entropicdreams.darva.handlers.PacketHandler;

import cpw.mods.fml.common.ITickHandler;
import cpw.mods.fml.common.TickType;
import cpw.mods.fml.common.network.PacketDispatcher;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.monster.EntityIronGolem;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.ForgeSubscribe;
import net.minecraftforge.event.world.BlockEvent.BreakEvent;

public class MetalParticleController implements ITickHandler {
	public LinkedList<Entity> particleTargets;
	public LinkedList<vector3> particleBlockTargets;
	private LinkedList<Integer> metallist;

	public void BuildMetalList() {
		metallist = new LinkedList<Integer>();
		metallist.add(Item.ingotGold.itemID);
		metallist.add(Item.ingotIron.itemID);
		metallist.add(Item.axeIron.itemID);
		metallist.add(Item.axeGold.itemID);
		metallist.add(Item.bootsChain.itemID);
		metallist.add(Item.bootsGold.itemID);
		metallist.add(Item.bootsIron.itemID);
		metallist.add(Item.bucketEmpty.itemID);
		metallist.add(Item.bucketLava.itemID);
		metallist.add(Item.bucketMilk.itemID);
		metallist.add(Item.bucketWater.itemID);
		metallist.add(Item.cauldron.itemID);
		metallist.add(Item.compass.itemID);
		metallist.add(Item.doorIron.itemID);
		metallist.add(Item.flintAndSteel.itemID);
		metallist.add(Item.goldNugget.itemID);
		metallist.add(Item.helmetChain.itemID);
		metallist.add(Item.helmetGold.itemID);
		metallist.add(Item.helmetIron.itemID);
		metallist.add(Item.hoeGold.itemID);
		metallist.add(Item.hoeIron.itemID);
		metallist.add(Item.horseArmorGold.itemID);
		metallist.add(Item.horseArmorIron.itemID);
		metallist.add(Item.legsChain.itemID);
		metallist.add(Item.legsGold.itemID);
		metallist.add(Item.legsIron.itemID);
		metallist.add(Item.minecartCrate.itemID);
		metallist.add(Item.minecartEmpty.itemID);
		metallist.add(Item.minecartHopper.itemID);
		metallist.add(Item.minecartPowered.itemID);
		metallist.add(Item.minecartTnt.itemID);
		metallist.add(Item.pickaxeIron.itemID);
		metallist.add(Item.pickaxeGold.itemID);
		metallist.add(Item.plateIron.itemID);
		metallist.add(Item.pocketSundial.itemID);
		metallist.add(Item.shovelGold.itemID);
		metallist.add(Item.shovelIron.itemID);
		metallist.add(Item.shears.itemID);
		metallist.add(Item.appleGold.itemID);
		metallist.add(Item.swordGold.itemID);
		metallist.add(Item.swordIron.itemID);
		metallist.add(ModRegistry.itemBrassFlakes.itemID);
		metallist.add(ModRegistry.itemBronzeFlakes.itemID);
		metallist.add(ModRegistry.itemCopperFlakes.itemID);
		metallist.add(ModRegistry.itemCopperIngot.itemID);
		metallist.add(ModRegistry.itemIronFlakes.itemID);
		metallist.add(ModRegistry.itemLeadFlakes.itemID);
		metallist.add(ModRegistry.itemLeadIngot.itemID);
		metallist.add(ModRegistry.itemPewterFlakes.itemID);
		metallist.add(ModRegistry.itemSteelFlakes.itemID);
		metallist.add(ModRegistry.itemTinFlakes.itemID);
		metallist.add(ModRegistry.itemTinIngot.itemID);
		metallist.add(Block.anvil.blockID);
		metallist.add(Block.blockGold.blockID);
		metallist.add(Block.blockIron.blockID);
		metallist.add(Block.fenceIron.blockID);
		metallist.add(Block.hopperBlock.blockID);
		metallist.add(Block.oreGold.blockID);
		metallist.add(Block.oreIron.blockID);
		metallist.add(Block.pistonBase.blockID);
		metallist.add(Block.pistonExtension.blockID);
		metallist.add(Block.pistonMoving.blockID);
		metallist.add(Block.pistonStickyBase.blockID);
		metallist.add(Block.pressurePlateGold.blockID);
		metallist.add(Block.pressurePlateIron.blockID);
		metallist.add(Block.rail.blockID);
		metallist.add(Block.railActivator.blockID);
		metallist.add(Block.railDetector.blockID);
		metallist.add(Block.railPowered.blockID);
		metallist.add(ModRegistry.oreCopper.blockID);
		metallist.add(ModRegistry.oreTin.blockID);
		metallist.add(ModRegistry.oreZinc.blockID);
		metallist.add(ModRegistry.oreLead.blockID);
		metallist.add(ModRegistry.itemVial.itemID);
		metallist.add(ModRegistry.itemZincFlakes.itemID);
		metallist.add(ModRegistry.itemZincIngot.itemID);
	}

	public boolean isItemMetal(ItemStack item) {
		if (metallist.contains(item.itemID))
			return true;
		else
			return false;
	}

	public boolean isBlockMetal(int blockID) {
		if (metallist.contains(blockID))
			return true;
		else
			return false;
	}

	public MetalParticleController() {
		particleTargets = new LinkedList<Entity>();
		BuildMetalList();
		particleBlockTargets = new LinkedList<vector3>();
	}

	public void tryAdd(Entity entity) {
		if (particleTargets.contains(entity))
			return;
		if (entity instanceof EntityLiving) {
			tryAddLiving((EntityLiving) entity);
			return;
		}
		if (entity instanceof EntityItem) {
			tryAddItem((EntityItem) entity);
		}
	}

	private void tryAddLiving(EntityLiving entity) {
		if (entity == null)
			return;
		if (entity instanceof EntityIronGolem
				|| (entity.getHeldItem() != null && metallist.contains(entity
						.getHeldItem().itemID))) {
			particleTargets.add(entity);
		}

	}

	private void tryAddItem(EntityItem entity) {
		if (isItemMetal(entity.getEntityItem())) {
			particleTargets.add(entity);
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
			tryPushItem((EntityItem) entity);
		}

		if (entity instanceof EntityLiving) {
			tryPushMob((EntityLiving) entity);
		}

	}

	public void tryPullEntity(Entity entity) {
		if (entity instanceof EntityItem) {
			tryPullItem((EntityItem) entity);
		}
		if (entity instanceof EntityLiving) {
			tryPullMob((EntityLiving) entity);
		}

	}

	private void tryPullItem(EntityItem entity) {
		double motionX, motionY, motionZ;
		EntityPlayer player = Minecraft.getMinecraft().thePlayer;
		if (metallist.contains(entity.getEntityItem().itemID)) {
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
		if (metallist.contains(entity.getEntityItem().itemID)) {
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
			motionY = ((player.posY - entity.posY) * .1);
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

		if (isItemMetal(entity.getHeldItem())) {
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

		if (isItemMetal(entity.getHeldItem())) {
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
		LinkedList<vector3> toRemove2 = new LinkedList<vector3>();
		if (player == null)
			return;

		for (Entity entity : particleTargets) {

			if (entity.isDead == true)
				toRemove.add(entity);

			if (player.getDistanceToEntity(entity) > 10)
				toRemove.add(entity);
		}
		for (Entity entity : toRemove) {
			particleTargets.remove(entity);
		}
		particleBlockTargets.clear();
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
