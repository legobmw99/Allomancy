package common.legobmw99.allomancy.util;

import java.util.LinkedList;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.monster.EntityIronGolem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.ShapelessOreRecipe;
import common.legobmw99.allomancy.blocks.OreBlock;
import common.legobmw99.allomancy.common.Registry;
import common.legobmw99.allomancy.entity.EntityGoldNugget;
import common.legobmw99.allomancy.network.packets.MoveEntityPacket;
import common.legobmw99.allomancy.network.packets.StopFallPacket;

public class ExternalPowerController{
	public LinkedList<Entity> particleTargets;
	public LinkedList<vector3> particleBlockTargets;
	private LinkedList<Integer> metallist;
	private String[] ores = OreDictionary.getOreNames();
	//public LinkedList<EntityPlayer> metalBurners;

	public void BuildMetalList() {
		/*
		 * Add every single metal object in vanilla and this mod
		 * Add item ids and default block states
		 */
		this.metallist = new LinkedList<Integer>();
		this.metallist.add(Items.GOLD_INGOT.getIdFromItem(Items.GOLD_INGOT));
		this.metallist.add(Items.IRON_INGOT.getIdFromItem(Items.IRON_INGOT));
		this.metallist.add(Items.IRON_AXE.getIdFromItem(Items.IRON_AXE));
		this.metallist.add(Items.GOLDEN_AXE.getIdFromItem(Items.GOLDEN_AXE));
		this.metallist.add(Items.CHAINMAIL_BOOTS.getIdFromItem(Items.CHAINMAIL_BOOTS));
		this.metallist.add(Items.GOLDEN_BOOTS.getIdFromItem(Items.GOLDEN_BOOTS));
		this.metallist.add(Items.IRON_BOOTS.getIdFromItem(Items.IRON_BOOTS));
		this.metallist.add(Items.BUCKET.getIdFromItem(Items.BUCKET));
		this.metallist.add(Items.LAVA_BUCKET.getIdFromItem(Items.LAVA_BUCKET));
		this.metallist.add(Items.MILK_BUCKET.getIdFromItem(Items.MILK_BUCKET));
		this.metallist.add(Items.WATER_BUCKET.getIdFromItem(Items.WATER_BUCKET));
		this.metallist.add(Items.CAULDRON.getIdFromItem(Items.CAULDRON));
		this.metallist.add(Items.COMPASS.getIdFromItem(Items.COMPASS));
		this.metallist.add(Items.FLINT_AND_STEEL.getIdFromItem(Items.FLINT_AND_STEEL));
		this.metallist.add(Items.GOLD_NUGGET.getIdFromItem(Items.GOLD_NUGGET));
		this.metallist.add(Items.CHAINMAIL_HELMET.getIdFromItem(Items.CHAINMAIL_HELMET));
		this.metallist.add(Items.GOLDEN_HELMET.getIdFromItem(Items.GOLDEN_HELMET));
		this.metallist.add(Items.IRON_HELMET.getIdFromItem(Items.IRON_HELMET));
		this.metallist.add(Items.GOLDEN_HOE.getIdFromItem(Items.GOLDEN_HOE));
		this.metallist.add(Items.IRON_HOE.getIdFromItem(Items.IRON_HOE));
		this.metallist.add(Items.GOLDEN_HORSE_ARMOR.getIdFromItem(Items.GOLDEN_HORSE_ARMOR));
		this.metallist.add(Items.IRON_HORSE_ARMOR.getIdFromItem(Items.IRON_HORSE_ARMOR));
		this.metallist.add(Items.CHAINMAIL_LEGGINGS.getIdFromItem(Items.CHAINMAIL_LEGGINGS));
		this.metallist.add(Items.GOLDEN_LEGGINGS.getIdFromItem(Items.GOLDEN_LEGGINGS));
		this.metallist.add(Items.IRON_LEGGINGS.getIdFromItem(Items.IRON_LEGGINGS));
		this.metallist.add(Items.MINECART.getIdFromItem(Items.MINECART));
		this.metallist.add(Items.CHEST_MINECART.getIdFromItem(Items.CHEST_MINECART));
		this.metallist.add(Items.HOPPER_MINECART.getIdFromItem(Items.HOPPER_MINECART));
		this.metallist.add(Items.FURNACE_MINECART.getIdFromItem(Items.FURNACE_MINECART));
		this.metallist.add(Items.TNT_MINECART.getIdFromItem(Items.TNT_MINECART));
		this.metallist.add(Items.IRON_PICKAXE.getIdFromItem(Items.IRON_PICKAXE));
		this.metallist.add(Items.GOLDEN_PICKAXE.getIdFromItem(Items.GOLDEN_PICKAXE));
		this.metallist.add(Items.IRON_CHESTPLATE.getIdFromItem(Items.IRON_CHESTPLATE));
		this.metallist.add(Items.CHAINMAIL_CHESTPLATE.getIdFromItem(Items.CHAINMAIL_CHESTPLATE));
		this.metallist.add(Items.GOLDEN_CHESTPLATE.getIdFromItem(Items.GOLDEN_CHESTPLATE));
		this.metallist.add(Items.CLOCK.getIdFromItem(Items.CLOCK));
		this.metallist.add(Items.GOLDEN_SHOVEL.getIdFromItem(Items.GOLDEN_SHOVEL));
		this.metallist.add(Items.IRON_SHOVEL.getIdFromItem(Items.IRON_SHOVEL));
		this.metallist.add(Items.SHEARS.getIdFromItem(Items.SHEARS));
		this.metallist.add(Items.GOLDEN_APPLE.getIdFromItem(Items.GOLDEN_APPLE));
		this.metallist.add(Items.GOLDEN_APPLE.getIdFromItem(Items.GOLDEN_APPLE));
		this.metallist.add(Items.GOLDEN_CARROT.getIdFromItem(Items.GOLDEN_CARROT));
		this.metallist.add(Items.IRON_SWORD.getIdFromItem(Items.IRON_SWORD));
		this.metallist.add(Registry.itemCopperIngot.getIdFromItem(Registry.itemCopperIngot));
		this.metallist.add(Registry.itemLeadIngot.getIdFromItem(Registry.itemLeadIngot));
		this.metallist.add(Registry.itemTinIngot.getIdFromItem(Registry.itemTinIngot));
		this.metallist.add(Registry.nuggetLerasium.getIdFromItem(Registry.nuggetLerasium));
		this.metallist.add(Blocks.ANVIL.getStateId(Blocks.ANVIL.getDefaultState()));
		this.metallist.add(Blocks.IRON_TRAPDOOR.getStateId(Blocks.IRON_TRAPDOOR.getDefaultState()));
		this.metallist.add(Blocks.IRON_DOOR.getStateId(Blocks.IRON_DOOR.getDefaultState()));
		this.metallist.add(Blocks.CAULDRON.getStateId(Blocks.CAULDRON.getDefaultState()));
		this.metallist.add(Blocks.GOLD_BLOCK.getStateId(Blocks.GOLD_BLOCK.getDefaultState()));
		this.metallist.add(Blocks.IRON_BLOCK.getStateId(Blocks.IRON_BLOCK.getDefaultState()));
		this.metallist.add(Blocks.IRON_BARS.getStateId(Blocks.IRON_BARS.getDefaultState()));
		this.metallist.add(Blocks.HOPPER.getStateId(Blocks.HOPPER.getDefaultState()));
		this.metallist.add(Blocks.GOLD_ORE.getStateId(Blocks.GOLD_ORE.getDefaultState()));
		this.metallist.add(Blocks.IRON_ORE.getStateId(Blocks.IRON_ORE.getDefaultState()));
		this.metallist.add(Blocks.PISTON_HEAD.getStateId(Blocks.IRON_ORE.getDefaultState()));
		this.metallist.add(Blocks.PISTON_EXTENSION.getStateId(Blocks.PISTON_EXTENSION.getDefaultState()));
		this.metallist.add(Blocks.STICKY_PISTON.getStateId(Blocks.STICKY_PISTON.getDefaultState()));
		this.metallist.add(Blocks.PISTON.getStateId(Blocks.PISTON.getDefaultState()));
		this.metallist.add(Blocks.LIGHT_WEIGHTED_PRESSURE_PLATE.getStateId(Blocks.LIGHT_WEIGHTED_PRESSURE_PLATE.getDefaultState()));
		this.metallist.add(Blocks.HEAVY_WEIGHTED_PRESSURE_PLATE.getStateId(Blocks.HEAVY_WEIGHTED_PRESSURE_PLATE.getDefaultState()));
		this.metallist.add(Blocks.RAIL.getStateId(Blocks.RAIL.getDefaultState()));
		this.metallist.add(Blocks.ACTIVATOR_RAIL.getStateId(Blocks.ACTIVATOR_RAIL.getDefaultState()));
		this.metallist.add(Blocks.DETECTOR_RAIL.getStateId(Blocks.DETECTOR_RAIL.getDefaultState()));
		this.metallist.add(Blocks.GOLDEN_RAIL.getStateId(Blocks.GOLDEN_RAIL.getDefaultState()));
		this.metallist.add(OreBlock.oreCopper.getStateId(OreBlock.oreCopper.getDefaultState()));
		this.metallist.add(OreBlock.oreTin.getStateId(OreBlock.oreTin.getDefaultState()));
		this.metallist.add(OreBlock.oreZinc.getStateId(OreBlock.oreZinc.getDefaultState()));
		this.metallist.add(OreBlock.oreLead.getStateId(OreBlock.oreLead.getDefaultState()));
		this.metallist.add(Registry.itemVial.getIdFromItem(Registry.itemVial));
		this.metallist.add(Registry.itemZincIngot.getIdFromItem(Registry.itemZincIngot));
		
		for (int i = 0; i < Registry.flakeMetals.length; i++) {
			this.metallist.add(new Item().getByNameOrId("allomancy:" + "flake"+ Registry.flakeMetals[i] ).getIdFromItem(new Item().getByNameOrId("allomancy:" + "flake"+ Registry.flakeMetals[i] )));
		}
		
		for (String s : ores){ 
			if (s.contains("Copper") || s.contains("Tin") || s.contains("Gold") || s.contains("Iron") || s.contains("Steel") || s.contains("Lead") || s.contains("Silver") || s.contains("Brass")|| s.contains("Bronze")|| s.contains("Aluminum")){ 
				for (ItemStack i : OreDictionary.getOres(s)){
					if(i.getItem() != null){
						if(i.getItem() instanceof ItemBlock){
							this.metallist.add(Block.getBlockFromItem(i.getItem()).getStateId(Block.getBlockFromItem(i.getItem()).getDefaultState()));
						} 
						this.metallist.add(i.getItem().getIdFromItem(i.getItem()));
					 }
				 }
			 }
		 }
	}

	public boolean isItemMetal(ItemStack item) {
		if(item == null){
			return false;
		}
		if (this.metallist.contains(item.getItem().getIdFromItem(item.getItem())) ) {
			return true;
		} else {
			if (item.getItem() instanceof ItemBlock){
				if (this.metallist.contains(Block.getBlockFromItem(item.getItem()).getDefaultState())){
					return true;

				}
			}
			return false;
		}
	}

	public boolean isBlockMetal(IBlockState state) {
		if (this.metallist.contains(state.getBlock().getStateId(state.getBlock().getDefaultState()))) {
			return true;
		} else {
			return false;
		}
	}

	public ExternalPowerController() {
		this.particleTargets = new LinkedList<Entity>();
		this.particleBlockTargets = new LinkedList<vector3>();
		//this.metalBurners = new LinkedList<EntityPlayer>();
		this.BuildMetalList();
	}
	/* Come back in 1.9
	 * public void tryAddBurningPlayer(EntityPlayer player){
		this.metalBurners.add(player);
	}*/
	public void tryAddMetalEntity(Entity entity) {
		if (entity == null) {
			return;
		}
		if (this.particleTargets.contains(entity)) {
			return;
		}
		if (entity instanceof EntityGoldNugget) {
			this.particleTargets.add(entity);

			return;
		}
		if (entity instanceof EntityLiving) {
			this.tryAddMetalLiving((EntityLiving) entity);
			return;
		}
		if (entity instanceof EntityItem) {
			this.tryAddMetalItem((EntityItem) entity);
		}
	}

	private void tryAddMetalLiving(EntityLiving entity) {
		if ((entity instanceof EntityIronGolem)
				|| (((entity.getHeldItem(EnumHand.MAIN_HAND) != null) || entity.getHeldItem(EnumHand.OFF_HAND) == null) && (this.isItemMetal(entity.getHeldItem(EnumHand.MAIN_HAND)) || this.isItemMetal(entity.getHeldItem(EnumHand.OFF_HAND))))) {
			this.particleTargets.add(entity);

		}
	}

	private void tryAddMetalItem(EntityItem entity) {
		if (this.isItemMetal(entity.getEntityItem())) {
			this.particleTargets.add(entity);
		}
	}

	public void tryPushBlock(vector3 vec) {
		double motionX, motionY, motionZ, magnitude;
		EntityPlayer player = Minecraft.getMinecraft().thePlayer;
		magnitude = Math.sqrt(Math.pow((player.posX - (double)(vec.X + .5)),2) + Math.pow((player.posY - (double)(vec.Y + .5)),2) + Math.pow((player.posZ - (double)(vec.Z + .5)),2) );
		motionX = ((player.posX - (double)(vec.X + .5)) * (1.1)/magnitude);
		motionY = ((player.posY - (double)(vec.Y + .5)) * (1.1)/magnitude);
		motionZ = ((player.posZ - (double)(vec.Z + .5)) * (1.1)/magnitude);
		player.motionX = motionX;
		player.motionY = motionY;
		player.motionZ = motionZ;
		Registry.network.sendToServer(new StopFallPacket());

	}

	public void tryPullBlock(vector3 vec) {
		double motionX, motionY, motionZ, magnitude;
		EntityPlayer player = Minecraft.getMinecraft().thePlayer;
		magnitude = Math.sqrt(Math.pow((player.posX - (double)(vec.X + .5)),2) + Math.pow((player.posY - (double)(vec.Y + .5)),2) + Math.pow((player.posZ - (double)(vec.Z + .5)),2) );
		motionX = ((player.posX - (double)(vec.X + .5)) * -(1.1)/magnitude);
		motionY = ((player.posY - (double)(vec.Y + .5)) * -(1.1)/magnitude);
		motionZ = ((player.posZ - (double)(vec.Z + .5)) * -(1.1)/magnitude);
		player.motionX = motionX;
		player.motionY = motionY;
		player.motionZ = motionZ;
		Registry.network.sendToServer(new StopFallPacket());
	}

	public void tryPushEntity(Entity entity) {

		if (entity instanceof EntityItem) {
			this.tryPushItem((EntityItem) entity);
		}

		if (entity instanceof EntityCreature) {

			this.tryPushMob((EntityCreature) entity);
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
		if (this.metallist.contains(entity.getEntityItem().getItem().getIdFromItem(entity.getEntityItem().getItem()))) {
			motionX = (player.posX - entity.posX) * .1;
			motionY = (player.posY - entity.posY) * .1;
			motionZ = (player.posZ - entity.posZ) * .1;
			entity.motionX = motionX;
			entity.motionY = motionY;
			entity.motionZ = motionZ;

			Registry.network.sendToServer(new MoveEntityPacket(motionX,motionY,motionZ,entity.getEntityId()));;

		}
	}

	private void tryPushItem(EntityItem entity) {
		double motionX, motionY, motionZ;
		EntityPlayer player = Minecraft.getMinecraft().thePlayer;
		if (this.metallist.contains(entity.getEntityItem().getItem().getIdFromItem(entity.getEntityItem().getItem()))) {
			motionX = ((player.posX - entity.posX) * .1) * -1;
			motionY = ((player.posY - entity.posY) * .1);
			motionZ = ((player.posZ - entity.posZ) * .1) * -1;
			entity.motionX = motionX;
			entity.motionY = motionY;
			entity.motionZ = motionZ;
			Registry.network.sendToServer(new MoveEntityPacket(motionX,motionY,motionZ,entity.getEntityId()));;

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
			Registry.network.sendToServer(new StopFallPacket());

			// waaaaay too damn heavy to push... you get moved.
		}

		if (entity.getHeldItem(EnumHand.OFF_HAND) == null || entity.getHeldItem(EnumHand.MAIN_HAND) == null) {
			return;
		}

		if (this.isItemMetal(entity.getHeldItem(EnumHand.MAIN_HAND)) || this.isItemMetal(entity.getHeldItem(EnumHand.OFF_HAND))) {
			// Pull em towards you.
			magnitude = Math.sqrt(Math.pow((player.posX - entity.posX),2) + Math.pow((player.posY - entity.posY),2) + Math.pow((player.posZ - entity.posZ),2) );
			motionX = ((player.posX - entity.posX) * (1.1)/magnitude);
			motionY = ((player.posY - entity.posY) * (1.1)/magnitude);
			motionZ = ((player.posZ - entity.posZ) * (1.1)/magnitude);
			entity.motionX = motionX;
			entity.motionY = motionY;
			entity.motionZ = motionZ;
			Registry.network.sendToServer(new MoveEntityPacket(motionX,motionY,motionZ,entity.getEntityId()));;
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
			player.motionX = motionX;
			player.motionY = motionY;
			player.motionZ = motionZ;
			Registry.network.sendToServer(new StopFallPacket());

			// waaaaay too damn heavy to push... you get moved.
		}

		if (entity.getHeldItem(EnumHand.OFF_HAND) == null || entity.getHeldItem(EnumHand.MAIN_HAND) == null) {
			return;
		}

		if (this.isItemMetal(entity.getHeldItem(EnumHand.MAIN_HAND)) || this.isItemMetal(entity.getHeldItem(EnumHand.OFF_HAND))) {
			// Pull em towards you.
			magnitude = Math.sqrt(Math.pow((player.posX - entity.posX),2) + Math.pow((player.posY - entity.posY),2) + Math.pow((player.posZ - entity.posZ),2) );
			motionX = ((player.posX - entity.posX) * -(1.1)/magnitude);
			motionY = ((player.posY - entity.posY) * (1.1)/magnitude);
			motionZ = ((player.posZ - entity.posZ) * -(1.1)/magnitude);
			entity.motionX = motionX;
			entity.motionY = motionY;
			entity.motionZ = motionZ;
			Registry.network.sendToServer(new MoveEntityPacket(motionX,motionY,motionZ,entity.getEntityId()));;
		}
	}

}
