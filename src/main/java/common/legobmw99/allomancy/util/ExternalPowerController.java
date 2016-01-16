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
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;
import common.legobmw99.allomancy.blocks.OreBlock;
import common.legobmw99.allomancy.common.Registry;
import common.legobmw99.allomancy.network.packets.MoveEntityPacket;
import common.legobmw99.allomancy.network.packets.StopFallPacket;

public class ExternalPowerController{
	public LinkedList<Entity> particleTargets;
	public LinkedList<vector3> particleBlockTargets;
	private LinkedList<Integer> metallist;
	public LinkedList<EntityPlayer> metalBurners;

	public void BuildMetalList() {
		this.metallist = new LinkedList<Integer>();
		this.metallist.add(Items.gold_ingot.getIdFromItem(Items.gold_ingot));
		this.metallist.add(Items.iron_ingot.getIdFromItem(Items.iron_ingot));
		this.metallist.add(Items.iron_axe.getIdFromItem(Items.iron_axe));
		this.metallist.add(Items.golden_axe.getIdFromItem(Items.golden_axe));
		this.metallist.add(Items.chainmail_boots.getIdFromItem(Items.chainmail_boots));
		this.metallist.add(Items.golden_boots.getIdFromItem(Items.golden_boots));
		this.metallist.add(Items.iron_boots.getIdFromItem(Items.iron_boots));
		this.metallist.add(Items.bucket.getIdFromItem(Items.bucket));
		this.metallist.add(Items.lava_bucket.getIdFromItem(Items.lava_bucket));
		this.metallist.add(Items.milk_bucket.getIdFromItem(Items.milk_bucket));
		this.metallist.add(Items.water_bucket.getIdFromItem(Items.water_bucket));
		this.metallist.add(Items.cauldron.getIdFromItem(Items.cauldron));
		this.metallist.add(Items.compass.getIdFromItem(Items.compass));
		this.metallist.add(Items.iron_door.getIdFromItem(Items.iron_door));
		this.metallist.add(Items.flint_and_steel.getIdFromItem(Items.flint_and_steel));
		this.metallist.add(Items.gold_nugget.getIdFromItem(Items.gold_nugget));
		this.metallist.add(Items.chainmail_helmet.getIdFromItem(Items.chainmail_helmet));
		this.metallist.add(Items.golden_helmet.getIdFromItem(Items.golden_helmet));
		this.metallist.add(Items.iron_helmet.getIdFromItem(Items.iron_helmet));
		this.metallist.add(Items.golden_hoe.getIdFromItem(Items.golden_hoe));
		this.metallist.add(Items.iron_hoe.getIdFromItem(Items.iron_hoe));
		this.metallist.add(Items.golden_horse_armor.getIdFromItem(Items.golden_horse_armor));
		this.metallist.add(Items.iron_horse_armor.getIdFromItem(Items.iron_horse_armor));
		this.metallist.add(Items.chainmail_leggings.getIdFromItem(Items.chainmail_leggings));
		this.metallist.add(Items.golden_leggings.getIdFromItem(Items.golden_leggings));
		this.metallist.add(Items.iron_leggings.getIdFromItem(Items.iron_leggings));
		this.metallist.add(Items.minecart.getIdFromItem(Items.minecart));
		this.metallist.add(Items.chest_minecart.getIdFromItem(Items.chest_minecart));
		this.metallist.add(Items.hopper_minecart.getIdFromItem(Items.hopper_minecart));
		this.metallist.add(Items.furnace_minecart.getIdFromItem(Items.furnace_minecart));
		this.metallist.add(Items.tnt_minecart.getIdFromItem(Items.tnt_minecart));
		this.metallist.add(Items.iron_pickaxe.getIdFromItem(Items.iron_pickaxe));
		this.metallist.add(Items.golden_pickaxe.getIdFromItem(Items.golden_pickaxe));
		this.metallist.add(Items.iron_chestplate.getIdFromItem(Items.iron_chestplate));
		this.metallist.add(Items.golden_chestplate.getIdFromItem(Items.golden_chestplate));
		this.metallist.add(Items.clock.getIdFromItem(Items.clock));
		this.metallist.add(Items.golden_shovel.getIdFromItem(Items.golden_shovel));
		this.metallist.add(Items.iron_shovel.getIdFromItem(Items.iron_shovel));
		this.metallist.add(Items.shears.getIdFromItem(Items.shears));
		this.metallist.add(Items.golden_apple.getIdFromItem(Items.golden_apple));
		this.metallist.add(Items.golden_sword.getIdFromItem(Items.golden_sword));
		this.metallist.add(Items.iron_sword.getIdFromItem(Items.iron_sword));
		this.metallist.add(Registry.itemBrassFlakes.getIdFromItem(Registry.itemBrassFlakes));
		this.metallist.add(Registry.itemBronzeFlakes.getIdFromItem(Registry.itemBronzeFlakes));
		this.metallist.add(Registry.itemCopperFlakes.getIdFromItem(Registry.itemCopperFlakes));
		this.metallist.add(Registry.itemCopperIngot.getIdFromItem(Registry.itemCopperIngot));
		this.metallist.add(Registry.itemIronFlakes.getIdFromItem(Registry.itemIronFlakes));
		this.metallist.add(Registry.itemLeadFlakes.getIdFromItem(Registry.itemLeadFlakes));
		this.metallist.add(Registry.itemLeadIngot.getIdFromItem(Registry.itemLeadIngot));
		this.metallist.add(Registry.itemPewterFlakes.getIdFromItem(Registry.itemPewterFlakes));
		this.metallist.add(Registry.itemSteelFlakes.getIdFromItem(Registry.itemSteelFlakes));
		this.metallist.add(Registry.itemTinFlakes.getIdFromItem(Registry.itemTinFlakes));
		this.metallist.add(Registry.itemTinIngot.getIdFromItem(Registry.itemTinIngot));
		this.metallist.add(Registry.nuggetLerasium.getIdFromItem(Registry.nuggetLerasium));
		this.metallist.add(Blocks.anvil.getStateId(Blocks.anvil.getDefaultState()));
		this.metallist.add(Blocks.cauldron.getStateId(Blocks.cauldron.getDefaultState()));
		this.metallist.add(Blocks.gold_block.getStateId(Blocks.gold_block.getDefaultState()));
		this.metallist.add(Blocks.iron_block.getStateId(Blocks.iron_block.getDefaultState()));
		this.metallist.add(Blocks.iron_bars.getStateId(Blocks.iron_bars.getDefaultState()));
		this.metallist.add(Blocks.hopper.getStateId(Blocks.hopper.getDefaultState()));
		this.metallist.add(Blocks.gold_ore.getStateId(Blocks.gold_ore.getDefaultState()));
		this.metallist.add(Blocks.iron_ore.getStateId(Blocks.iron_ore.getDefaultState()));
		this.metallist.add(Blocks.piston_head.getStateId(Blocks.iron_ore.getDefaultState()));
		this.metallist.add(Blocks.piston_extension.getStateId(Blocks.piston_extension.getDefaultState()));
		this.metallist.add(Blocks.sticky_piston.getStateId(Blocks.sticky_piston.getDefaultState()));
		this.metallist.add(Blocks.piston.getStateId(Blocks.piston.getDefaultState()));
		this.metallist.add(Blocks.light_weighted_pressure_plate.getStateId(Blocks.light_weighted_pressure_plate.getDefaultState()));
		this.metallist.add(Blocks.heavy_weighted_pressure_plate.getStateId(Blocks.heavy_weighted_pressure_plate.getDefaultState()));
		this.metallist.add(Blocks.rail.getStateId(Blocks.rail.getDefaultState()));
		this.metallist.add(Blocks.activator_rail.getStateId(Blocks.activator_rail.getDefaultState()));
		this.metallist.add(Blocks.detector_rail.getStateId(Blocks.detector_rail.getDefaultState()));
		this.metallist.add(Blocks.golden_rail.getStateId(Blocks.golden_rail.getDefaultState()));
		this.metallist.add(OreBlock.oreCopper.getStateId(OreBlock.oreCopper.getDefaultState()));
		this.metallist.add(OreBlock.oreTin.getStateId(OreBlock.oreTin.getDefaultState()));
		this.metallist.add(OreBlock.oreZinc.getStateId(OreBlock.oreZinc.getDefaultState()));
		this.metallist.add(OreBlock.oreLead.getStateId(OreBlock.oreLead.getDefaultState()));
		this.metallist.add(Registry.itemVial.getIdFromItem(Registry.itemVial));
		this.metallist.add(Registry.itemZincFlakes.getIdFromItem(Registry.itemZincFlakes));
		this.metallist.add(Registry.itemZincIngot.getIdFromItem(Registry.itemZincIngot));

	}

	public boolean isItemMetal(ItemStack Item) {
		if (this.metallist.contains(Item.getItem().getIdFromItem(Item.getItem())) ) {
			return true;
		} else {
			if (Item.getItem() instanceof ItemBlock){
				if (this.metallist.contains(Block.getBlockFromItem(Item.getItem()).getDefaultState())){
			return true;

		}}
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
		this.metalBurners = new LinkedList<EntityPlayer>();
		this.BuildMetalList();
	}
	public void tryAddBurningPlayer(EntityPlayer player){
		this.metalBurners.add(player);
	}
	public void tryAddMetalEntity(Entity entity) {
		if (this.particleTargets.contains(entity)) {
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
		if (entity == null) {
			return;
		}
		if ((entity instanceof EntityIronGolem)
				|| ((entity.getHeldItem() != null) && this.metallist.contains(entity.getHeldItem().getItem().getIdFromItem(entity.getHeldItem().getItem())))) {
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
			Registry.network.sendToServer(new MoveEntityPacket(motionX,motionY,motionZ,entity.getEntityId()));;
		}
	}

}
