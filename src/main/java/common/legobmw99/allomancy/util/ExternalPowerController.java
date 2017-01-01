package common.legobmw99.allomancy.util;

import java.util.ArrayList;

import common.legobmw99.allomancy.common.Registry;
import common.legobmw99.allomancy.entity.EntityGoldNugget;
import common.legobmw99.allomancy.network.packets.MoveEntityPacket;
import common.legobmw99.allomancy.network.packets.StopFallPacket;
import net.minecraft.block.Block;
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
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.oredict.OreDictionary;

public class ExternalPowerController{
	public ArrayList<Entity> particleTargets;
	public ArrayList<BlockPos> particleBlockTargets;
	private ArrayList<String> metallist;
	private String[] ores = OreDictionary.getOreNames();
	public ArrayList<EntityPlayer> metalBurners;
	
	public ExternalPowerController() {
		this.particleTargets = new ArrayList<Entity>();
		this.particleBlockTargets = new ArrayList<BlockPos>();
		this.metalBurners = new ArrayList<EntityPlayer>();
		this.BuildMetalList();
	}
	

	/**
	 * Builds a list of the unlocalized names of every metal item in vanilla and the ore dictionary
	 */
	public void BuildMetalList() {

		this.metallist = new ArrayList<String>();

		this.metallist.add(Items.IRON_AXE.getUnlocalizedName());
		this.metallist.add(Items.GOLDEN_AXE.getUnlocalizedName());
		this.metallist.add(Items.CHAINMAIL_BOOTS.getUnlocalizedName());
		this.metallist.add(Items.GOLDEN_BOOTS.getUnlocalizedName());
		this.metallist.add(Items.IRON_BOOTS.getUnlocalizedName());
		this.metallist.add(Items.BUCKET.getUnlocalizedName());
		this.metallist.add(Items.LAVA_BUCKET.getUnlocalizedName());
		this.metallist.add(Items.MILK_BUCKET.getUnlocalizedName());
		this.metallist.add(Items.WATER_BUCKET.getUnlocalizedName());
		this.metallist.add(Items.CAULDRON.getUnlocalizedName());
		this.metallist.add(Items.COMPASS.getUnlocalizedName());
		this.metallist.add(Items.FLINT_AND_STEEL.getUnlocalizedName());
		this.metallist.add(Items.GOLD_NUGGET.getUnlocalizedName());
		this.metallist.add(Items.CHAINMAIL_HELMET.getUnlocalizedName());
		this.metallist.add(Items.GOLDEN_HELMET.getUnlocalizedName());
		this.metallist.add(Items.IRON_HELMET.getUnlocalizedName());
		this.metallist.add(Items.GOLDEN_HOE.getUnlocalizedName());
		this.metallist.add(Items.IRON_HOE.getUnlocalizedName());
		this.metallist.add(Items.GOLDEN_HORSE_ARMOR.getUnlocalizedName());
		this.metallist.add(Items.IRON_HORSE_ARMOR.getUnlocalizedName());
		this.metallist.add(Items.CHAINMAIL_LEGGINGS.getUnlocalizedName());
		this.metallist.add(Items.GOLDEN_LEGGINGS.getUnlocalizedName());
		this.metallist.add(Items.IRON_LEGGINGS.getUnlocalizedName());
		this.metallist.add(Items.MINECART.getUnlocalizedName());
		this.metallist.add(Items.CHEST_MINECART.getUnlocalizedName());
		this.metallist.add(Items.HOPPER_MINECART.getUnlocalizedName());
		this.metallist.add(Items.FURNACE_MINECART.getUnlocalizedName());
		this.metallist.add(Items.TNT_MINECART.getUnlocalizedName());
		this.metallist.add(Items.IRON_PICKAXE.getUnlocalizedName());
		this.metallist.add(Items.GOLDEN_PICKAXE.getUnlocalizedName());
		this.metallist.add(Items.IRON_CHESTPLATE.getUnlocalizedName());
		this.metallist.add(Items.CHAINMAIL_CHESTPLATE.getUnlocalizedName());
		this.metallist.add(Items.GOLDEN_CHESTPLATE.getUnlocalizedName());
		this.metallist.add(Items.CLOCK.getUnlocalizedName());
		this.metallist.add(Items.GOLDEN_SHOVEL.getUnlocalizedName());
		this.metallist.add(Items.IRON_SHOVEL.getUnlocalizedName());
		this.metallist.add(Items.SHEARS.getUnlocalizedName());
		this.metallist.add(Items.GOLDEN_APPLE.getUnlocalizedName());
		this.metallist.add(Items.GOLDEN_APPLE.getUnlocalizedName());
		this.metallist.add(Items.GOLDEN_CARROT.getUnlocalizedName());
		this.metallist.add(Items.IRON_SWORD.getUnlocalizedName());
		this.metallist.add(Registry.nuggetLerasium.getUnlocalizedName());
		this.metallist.add(Blocks.ANVIL.getUnlocalizedName());
		this.metallist.add(Blocks.IRON_TRAPDOOR.getUnlocalizedName());
		this.metallist.add(Blocks.IRON_DOOR.getUnlocalizedName());
		this.metallist.add(Blocks.CAULDRON.getUnlocalizedName());
		this.metallist.add(Blocks.IRON_BARS.getUnlocalizedName());
		this.metallist.add(Blocks.HOPPER.getUnlocalizedName());
		this.metallist.add(Blocks.PISTON_HEAD.getUnlocalizedName());
		this.metallist.add(Blocks.PISTON_EXTENSION.getUnlocalizedName());
		this.metallist.add(Blocks.STICKY_PISTON.getUnlocalizedName());
		this.metallist.add(Blocks.PISTON.getUnlocalizedName());
		this.metallist.add(Blocks.LIGHT_WEIGHTED_PRESSURE_PLATE.getUnlocalizedName());
		this.metallist.add(Blocks.HEAVY_WEIGHTED_PRESSURE_PLATE.getUnlocalizedName());
		this.metallist.add(Blocks.RAIL.getUnlocalizedName());
		this.metallist.add(Blocks.ACTIVATOR_RAIL.getUnlocalizedName());
		this.metallist.add(Blocks.DETECTOR_RAIL.getUnlocalizedName());
		this.metallist.add(Blocks.GOLDEN_RAIL.getUnlocalizedName());

		this.metallist.add(Registry.itemVial.getUnlocalizedName());

		
		for (int i = 0; i < Registry.flakeMetals.length; i++) {
			this.metallist.add(new Item().getByNameOrId("allomancy:" + "flake"+ Registry.flakeMetals[i] ).getUnlocalizedName());
		}
		
		for (String s : ores){ 
			if (s.contains("Copper") || s.contains("Tin") || s.contains("Gold") || s.contains("Iron") || s.contains("Steel") || s.contains("Lead") || s.contains("Silver") || s.contains("Brass")|| s.contains("Bronze")|| s.contains("Aluminum")|| s.contains("Zinc")){ 
				for (ItemStack i : OreDictionary.getOres(s)){
					if(i.getItem() != null){
						this.metallist.add(i.getItem().getUnlocalizedName());
					 }
				 }
			 }
		 }
	}

	/**
	 * Determines if an item is metal or not
	 * @param item to be checked
	 * @return Whether or not the item is metal 
	 */
	public boolean isItemMetal(ItemStack item) {
		return (item != null) && this.metallist.contains(item.getUnlocalizedName());
	}
	
	/**
	 * Determines if a block is metal or not
	 * @param block to be checked
	 * @return Whether or not the item is metal 
	 */
	public boolean isBlockMetal(Block block) {
		return this.metallist.contains(block.getUnlocalizedName());		
	}

	/**
	 * Adds a player to the list of entities burning metals
	 * @param player who is burning metal
	 */
	public void addBurningPlayer(EntityPlayer player){
		this.metalBurners.add(player);
	}
	
	/**
	 * Takes an Entity and determines if it should be added to the list of metallic entities
	 * @param entity the entity to be checked and added
	 */
	public void tryAddMetalEntity(Entity entity) {
		if (entity == null || this.particleTargets.contains(entity)) {
			return;
		}

		if (entity instanceof EntityGoldNugget) {
			this.particleTargets.add(entity);
			return;
		}
		
		if (entity instanceof EntityLiving && (((entity instanceof EntityIronGolem) || (((((EntityLiving) entity).getHeldItem(EnumHand.MAIN_HAND) != null) || ((EntityLiving) entity).getHeldItem(EnumHand.OFF_HAND) == null) && (this.isItemMetal(((EntityLiving) entity).getHeldItem(EnumHand.MAIN_HAND)) || this.isItemMetal(((EntityLiving) entity).getHeldItem(EnumHand.OFF_HAND))))))) {
			this.particleTargets.add(entity);
			return;
		}
		
		if (entity instanceof EntityItem && this.isItemMetal(((EntityItem) entity).getEntityItem())) {
			this.particleTargets.add(entity);
			return;
		}
	}
	
	/**
	 * Move an entity either toward or away from an anchor point
	 * @param directionScalar the direction and (possibly) scalar multiple of the magnitude
	 * @param toMove the entity to move
	 * @param vec the point being moved toward or away from
	 */
	private void move(double directionScalar, Entity toMove, BlockPos vec){
		
		double motionX, motionY, motionZ, magnitude;
		magnitude = Math.sqrt(Math.pow((toMove.posX - (double)(vec.getX() + .5)),2) + Math.pow((toMove.posY - (double)(vec.getY() + .5)),2) + Math.pow((toMove.posZ - (double)(vec.getZ() + .5)),2) );
		motionX = ((toMove.posX - (double)(vec.getX() + .5)) * directionScalar * (1.1)/magnitude);
		motionY = ((toMove.posY - (double)(vec.getY() + .5)) * directionScalar * (1.1)/magnitude);
		motionZ = ((toMove.posZ - (double)(vec.getZ() + .5)) * directionScalar * (1.1)/magnitude);
		toMove.motionX = motionX;
		toMove.motionY = motionY;
		toMove.motionZ = motionZ;
		
		if(toMove instanceof EntityPlayer){
			Registry.network.sendToServer(new StopFallPacket());
		} else {
			Registry.network.sendToServer(new MoveEntityPacket(motionX,motionY,motionZ,toMove.getEntityId()));
		}
	
	}

	/**
	 * Player tries to Push a block
	 * @param vec the location of the block
	 */
	public void tryPushBlock(BlockPos vec) {
		EntityPlayer player = Minecraft.getMinecraft().player;
		this.move(1, player, vec);
	}

	/**
	 * Player tries to Pull a block
	 * @param vec the location of the block
	 */
	public void tryPullBlock(BlockPos vec) {
		EntityPlayer player = Minecraft.getMinecraft().player;
		this.move(-1, player, vec);
	}

	/**
	 * Player tries to Push an entity, is sorted into item or creature
	 * @param entity the entity to try to Push
	 */
	public void tryPushEntity(Entity entity) {

		if (entity instanceof EntityItem) {
			this.tryPushItem((EntityItem) entity);
		}

		if (entity instanceof EntityCreature) {

			this.tryPushMob((EntityCreature) entity);
		}

	}

	/**
	 * Player tries to Pull an entity, is sorted into item or creature
	 * @param entity the entity to try to Pull
	 */
	public void tryPullEntity(Entity entity) {
		if (entity instanceof EntityItem) {
			this.tryPullItem((EntityItem) entity);
		}
		if (entity instanceof EntityLiving) {
			this.tryPullMob((EntityLiving) entity);
		}

	}

	/**
	 * The player has tried to Pull an item
	 * @param entity the EntityItem to Pull
	 */
	private void tryPullItem(EntityItem entity) {
		if (this.metallist.contains(entity.getEntityItem().getItem().getUnlocalizedName())) {
			EntityPlayer player = Minecraft.getMinecraft().player;
			BlockPos anchor = new BlockPos((int)player.posX,(int)player.posY - 1,(int)player.posZ);
			this.move(-0.5, entity, anchor);			
        }
	}

	/**
	 * The player has tried to Push an item
	 * @param entity the EntityItem to Push
	 */
	private void tryPushItem(EntityItem entity) {
		if (this.metallist.contains(entity.getEntityItem().getItem().getUnlocalizedName())) {
			EntityPlayer player = Minecraft.getMinecraft().player;
			BlockPos anchor = new BlockPos((int)player.posX,(int)player.posY - 1,(int)player.posZ);
			this.move(0.5, entity, anchor);			
        }
	}

	/**
	 * The player has tried to Pull a mob
	 * @param entity the mob to Pull
	 */
	private void tryPullMob(EntityLiving entity) {

		EntityPlayer player = Minecraft.getMinecraft().player;
		
		if (entity instanceof EntityIronGolem) {
			//Pull you toward the entity
			BlockPos anchor = new BlockPos((int)entity.posX,(int)entity.posY,(int)entity.posZ);
			this.move(-1, player, anchor);
		}

		if ((entity.getHeldItem(EnumHand.OFF_HAND) != null && this.isItemMetal(entity.getHeldItem(EnumHand.MAIN_HAND))) || (entity.getHeldItem(EnumHand.MAIN_HAND) != null && this.isItemMetal(entity.getHeldItem(EnumHand.OFF_HAND)))) {
			//Pull the entity toward you
			BlockPos anchor = new BlockPos((int)player.posX,(int)player.posY,(int)player.posZ);
			this.move(-1, entity, anchor);		
        }
	}
	
	/**
	 * The player has tried to Push a mob
	 * @param entity the mob to Push
	 */
	private void tryPushMob(EntityLiving entity) {

		EntityPlayer player = Minecraft.getMinecraft().player;
		
		if (entity instanceof EntityIronGolem) {
			//Pull you toward the entity
			BlockPos anchor = new BlockPos((int)entity.posX,(int)entity.posY,(int)entity.posZ);
			this.move(1, player, anchor);
		}

		if ((entity.getHeldItem(EnumHand.OFF_HAND) != null && this.isItemMetal(entity.getHeldItem(EnumHand.MAIN_HAND))) || (entity.getHeldItem(EnumHand.MAIN_HAND) != null && this.isItemMetal(entity.getHeldItem(EnumHand.OFF_HAND)))) {
			//Pull the entity toward you
			BlockPos anchor = new BlockPos((int)player.posX,(int)player.posY,(int)player.posZ);
			this.move(1, entity, anchor);		
        }
	}

}
