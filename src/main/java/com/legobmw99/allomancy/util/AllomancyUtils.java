package com.legobmw99.allomancy.util;

/**
 * Contains all static, common methods in one place
 * 
 */

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.opengl.GL11;

import com.legobmw99.allomancy.network.packets.MoveEntityPacket;
import com.legobmw99.allomancy.network.packets.StopFallPacket;
import com.legobmw99.allomancy.network.packets.UpdateBurnPacket;

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
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.oredict.OreDictionary;

public class AllomancyUtils {
    private static final ArrayList<String> metallist = new ArrayList<String>();

    /**
     * Builds a list of the unlocalized names of every metal item in vanilla and the ore dictionary
     */
    static {
        metallist.add(Items.IRON_AXE.getUnlocalizedName());
        metallist.add(Items.GOLDEN_AXE.getUnlocalizedName());
        metallist.add(Items.CHAINMAIL_BOOTS.getUnlocalizedName());
        metallist.add(Items.GOLDEN_BOOTS.getUnlocalizedName());
        metallist.add(Items.IRON_BOOTS.getUnlocalizedName());
        metallist.add(Items.BUCKET.getUnlocalizedName());
        metallist.add(Items.LAVA_BUCKET.getUnlocalizedName());
        metallist.add(Items.MILK_BUCKET.getUnlocalizedName());
        metallist.add(Items.WATER_BUCKET.getUnlocalizedName());
        metallist.add(Items.CAULDRON.getUnlocalizedName());
        metallist.add(Items.COMPASS.getUnlocalizedName());
        metallist.add(Items.FLINT_AND_STEEL.getUnlocalizedName());
        metallist.add(Items.GOLD_NUGGET.getUnlocalizedName());
        metallist.add(Items.IRON_NUGGET.getUnlocalizedName()); 
        metallist.add(Items.CHAINMAIL_HELMET.getUnlocalizedName());
        metallist.add(Items.GOLDEN_HELMET.getUnlocalizedName());
        metallist.add(Items.IRON_HELMET.getUnlocalizedName());
        metallist.add(Items.GOLDEN_HOE.getUnlocalizedName());
        metallist.add(Items.IRON_HOE.getUnlocalizedName());
        metallist.add(Items.GOLDEN_HORSE_ARMOR.getUnlocalizedName());
        metallist.add(Items.IRON_HORSE_ARMOR.getUnlocalizedName());
        metallist.add(Items.CHAINMAIL_LEGGINGS.getUnlocalizedName());
        metallist.add(Items.GOLDEN_LEGGINGS.getUnlocalizedName());
        metallist.add(Items.IRON_LEGGINGS.getUnlocalizedName());
        metallist.add(Items.MINECART.getUnlocalizedName());
        metallist.add(Items.CHEST_MINECART.getUnlocalizedName());
        metallist.add(Items.HOPPER_MINECART.getUnlocalizedName());
        metallist.add(Items.FURNACE_MINECART.getUnlocalizedName());
        metallist.add(Items.TNT_MINECART.getUnlocalizedName());
        metallist.add(Items.IRON_PICKAXE.getUnlocalizedName());
        metallist.add(Items.GOLDEN_PICKAXE.getUnlocalizedName());
        metallist.add(Items.IRON_CHESTPLATE.getUnlocalizedName());
        metallist.add(Items.CHAINMAIL_CHESTPLATE.getUnlocalizedName());
        metallist.add(Items.GOLDEN_CHESTPLATE.getUnlocalizedName());
        metallist.add(Items.CLOCK.getUnlocalizedName());
        metallist.add(Items.GOLDEN_SHOVEL.getUnlocalizedName());
        metallist.add(Items.IRON_SHOVEL.getUnlocalizedName());
        metallist.add(Items.SHEARS.getUnlocalizedName());
        metallist.add(Items.GOLDEN_APPLE.getUnlocalizedName());
        metallist.add(Items.GOLDEN_APPLE.getUnlocalizedName());
        metallist.add(Items.GOLDEN_CARROT.getUnlocalizedName());
        metallist.add(Items.IRON_SWORD.getUnlocalizedName());
        metallist.add(Registry.nuggetLerasium.getUnlocalizedName());
        metallist.add(Registry.itemAllomancyGrinder.getUnlocalizedName());
        metallist.add(Registry.itemCoinBag.getUnlocalizedName());
        metallist.add(Blocks.ANVIL.getUnlocalizedName());
        metallist.add(Blocks.IRON_TRAPDOOR.getUnlocalizedName());
        metallist.add(Blocks.IRON_DOOR.getUnlocalizedName());
        metallist.add(Blocks.CAULDRON.getUnlocalizedName());
        metallist.add(Blocks.IRON_BARS.getUnlocalizedName());
        metallist.add(Blocks.HOPPER.getUnlocalizedName());
        metallist.add(Blocks.PISTON_HEAD.getUnlocalizedName());
        metallist.add(Blocks.PISTON_EXTENSION.getUnlocalizedName());
        metallist.add(Blocks.STICKY_PISTON.getUnlocalizedName());
        metallist.add(Blocks.PISTON.getUnlocalizedName());
        metallist.add(Blocks.LIGHT_WEIGHTED_PRESSURE_PLATE.getUnlocalizedName());
        metallist.add(Blocks.HEAVY_WEIGHTED_PRESSURE_PLATE.getUnlocalizedName());
        metallist.add(Blocks.RAIL.getUnlocalizedName());
        metallist.add(Blocks.ACTIVATOR_RAIL.getUnlocalizedName());
        metallist.add(Blocks.DETECTOR_RAIL.getUnlocalizedName());
        metallist.add(Blocks.GOLDEN_RAIL.getUnlocalizedName());

        metallist.add(Registry.itemVial.getUnlocalizedName());

        for (int i = 0; i < Registry.flakeMetals.length; i++) {
            metallist.add(new Item().getByNameOrId("allomancy:" + "flake" + Registry.flakeMetals[i]).getUnlocalizedName());
        }
        
        String[] ores = OreDictionary.getOreNames();
        for (String s : ores) {
            if (s.contains("Copper") || s.contains("Tin") || s.contains("Gold") || s.contains("Iron") || s.contains("Steel") || s.contains("Lead") || s.contains("Silver") || s.contains("Brass") || s.contains("Bronze") || s.contains("Aluminum")
                    || s.contains("Zinc")) {
                for (ItemStack i : OreDictionary.getOres(s)) {
                    if (i.getItem() != null) {
                        metallist.add(i.getItem().getUnlocalizedName());
                    }
                }
            }
        }
    }

    /**
     * Draws a line from the player (denoted pX,Y,Z) to the given set of coordinates (oX,Y,Z) in a certain color (r,g,b)
     * 
     * @param width
     *            the width of the line
     */
    @SideOnly(Side.CLIENT)
    public static void drawMetalLine(double pX, double pY, double pZ, double oX, double oY, double oZ, float width, float r, float g, float b) {
        GL11.glPushMatrix();
        GL11.glPushAttrib(GL11.GL_ENABLE_BIT);
        GL11.glTranslated(-pX, -pY, -pZ);
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glDisable(GL11.GL_TEXTURE_2D);

        GL11.glLineWidth(width);
        GL11.glColor3f(r, g, b);

        GL11.glBegin(GL11.GL_LINE_STRIP);

        GL11.glVertex3d(pX, pY + 1.2, pZ);
        GL11.glVertex3d(oX, oY, oZ);

        GL11.glEnd();
        GL11.glPopAttrib();
        GL11.glPopMatrix();
    }
    
    /**
     * Copied mostly from vanilla, this gets what the mouse is over
     * @param dist
     * 				distance
     * @return the result of the raytrace
     */
    @SideOnly(Side.CLIENT)
    public static RayTraceResult getMouseOverExtended(float dist) {
        Minecraft mc = FMLClientHandler.instance().getClient();
        Entity theRenderViewEntity = mc.getRenderViewEntity();
        AxisAlignedBB theViewBoundingBox = new AxisAlignedBB(theRenderViewEntity.posX - 0.5D, theRenderViewEntity.posY - 0.0D, theRenderViewEntity.posZ - 0.5D, theRenderViewEntity.posX + 0.5D, theRenderViewEntity.posY + 1.5D,
                theRenderViewEntity.posZ + 0.5D);
        RayTraceResult returnMOP = null;
        if (mc.world != null) {
            double var2 = dist;
            returnMOP = theRenderViewEntity.rayTrace(var2, 0);
            double calcdist = var2;
            Vec3d pos = theRenderViewEntity.getPositionEyes(0);
            var2 = calcdist;
            if (returnMOP != null) {
                calcdist = returnMOP.hitVec.distanceTo(pos);
            }
            Vec3d lookvec = theRenderViewEntity.getLook(0);
            Vec3d var8 = pos.addVector(lookvec.x * var2, lookvec.y * var2, lookvec.z * var2);
            Entity pointedEntity = null;
            float var9 = 1.0F;
            @SuppressWarnings("unchecked")
            List<Entity> list = mc.world.getEntitiesWithinAABBExcludingEntity(theRenderViewEntity, addCoord(theViewBoundingBox,lookvec.x * dist, lookvec.y * dist, lookvec.z * dist).expand(var9, var9, var9));
            double d = calcdist;
            for (Entity entity : list) {
                float bordersize = entity.getCollisionBorderSize();
                AxisAlignedBB aabb = new AxisAlignedBB(entity.posX - entity.width / 2, entity.posY, entity.posZ - entity.width / 2, entity.posX + entity.width / 2, entity.posY + entity.height, entity.posZ + entity.width / 2);
                aabb.expand(bordersize, bordersize, bordersize);
                RayTraceResult mop0 = aabb.calculateIntercept(pos, var8);
                if (aabb.contains(pos)) {
                    if (0.0D < d || d == 0.0D) {
                        pointedEntity = entity;
                        d = 0.0D;
                    }
                } else if (mop0 != null) {
                    double d1 = pos.distanceTo(mop0.hitVec);
                    if (d1 < d || d == 0.0D) {
                        pointedEntity = entity;
                        d = d1;
                    }
                }
            }
            if (pointedEntity != null && (d < calcdist || returnMOP == null)) {
                returnMOP = new RayTraceResult(pointedEntity);
            }
        }
        return returnMOP;
    }
    
    /**
     * Replacement for the old addCoord in AxisAlignedBB.class, necessary for getMouseOverExtended
     * @param a 
     * 			the original box
     * @param x 
     * @param y
     * @param z
     * @return Adds a coordinate to the bounding box, extending it if the point lies outside the current ranges.
     */
    public static AxisAlignedBB addCoord(AxisAlignedBB a, double x, double y, double z)
    {
        double d0 = a.minX;
        double d1 = a.minY;
        double d2 = a.minZ;
        double d3 = a.maxX;
        double d4 = a.maxY;
        double d5 = a.maxZ;

        if (x < 0.0D)
        {
            d0 += x;
        }
        else if (x > 0.0D)
        {
            d3 += x;
        }

        if (y < 0.0D)
        {
            d1 += y;
        }
        else if (y > 0.0D)
        {
            d4 += y;
        }

        if (z < 0.0D)
        {
            d2 += z;
        }
        else if (z > 0.0D)
        {
            d5 += z;
        }

        return new AxisAlignedBB(d0, d1, d2, d3, d4, d5);
    }

    /**
     * Determines if a block is metal or not
     * 
     * @param block
     *            to be checked
     * @return Whether or not the item is metal
     */
    public static boolean isBlockMetal(Block block) {
        return metallist.contains(block.getUnlocalizedName());
    }
    
    /**
     * Determines if an item is metal or not
     * 
     * @param item
     *            to be checked
     * @return Whether or not the item is metal
     */
    public static boolean isItemMetal(ItemStack item) {
        return (item != null) && metallist.contains(item.getUnlocalizedName());
    }

    /**
     * Move an entity either toward or away from an anchor point
     * 
     * @param directionScalar
     *            the direction and (possibly) scalar multiple of the magnitude
     * @param toMove
     *            the entity to move
     * @param vec
     *            the point being moved toward or away from
     */
    private static void move(double directionScalar, Entity toMove, BlockPos vec) {

        double motionX, motionY, motionZ, magnitude;
        // Calculate the length of the vector between the entity and anchor
        magnitude = Math.sqrt(Math.pow((toMove.posX - (double) (vec.getX() + .5)), 2) + Math.pow((toMove.posY - (double) (vec.getY() + .5)), 2) + Math.pow((toMove.posZ - (double) (vec.getZ() + .5)), 2));
        // Get a unit(-ish) vector in the direction of motion
        motionX = ((toMove.posX - (double) (vec.getX() + .5)) * directionScalar * (1.1) / magnitude);
        motionY = ((toMove.posY - (double) (vec.getY() + .5)) * directionScalar * (1.1) / magnitude);
        motionZ = ((toMove.posZ - (double) (vec.getZ() + .5)) * directionScalar * (1.1) / magnitude);
        // Move along that vector, additively increasing motion until you max out at the above values
        toMove.motionX = Math.abs(toMove.motionX + motionX) > 0.01 ? MathHelper.clamp(toMove.motionX + motionX, -Math.abs(motionX), motionX) : 0;
        toMove.motionY = Math.abs(toMove.motionY + motionY) > 0.01 ? MathHelper.clamp(toMove.motionY + motionY, -Math.abs(motionY), motionY) : 0;
        toMove.motionZ = Math.abs(toMove.motionZ + motionZ) > 0.01 ? MathHelper.clamp(toMove.motionZ + motionZ, -Math.abs(motionZ), motionZ) : 0;

        if (toMove instanceof EntityPlayer) {
            Registry.network.sendToServer(new StopFallPacket());
        } else {
            Registry.network.sendToServer(new MoveEntityPacket(motionX, motionY, motionZ, toMove.getEntityId()));
        }

    }

    /**
     * Used to toggle a metal's burn state and play a sound effect
     * 
     * @param metal
     *            the index of the metal to toggle
     * @param capability
     *            the capability being handled
     */
    public static void toggleMetalBurn(int metal, AllomancyCapabilities capability) {
        Registry.network.sendToServer(new UpdateBurnPacket(metal, !capability.getMetalBurning(metal)));

        if (capability.getMetalAmounts(metal) > 0) {
            capability.setMetalBurning(metal, !capability.getMetalBurning(metal));
        }
        // play a sound effect
        if (capability.getMetalBurning(metal)) {
            Minecraft.getMinecraft().player.playSound(new SoundEvent(new ResourceLocation("item.flintandsteel.use")), 1, 5);
        } else {
            Minecraft.getMinecraft().player.playSound(new SoundEvent(new ResourceLocation("block.fire.extinguish")), 1, 4);
        }
    }

    /**
     * Player tries to Pull a block
     * 
     * @param vec
     *            the location of the block
     */
    public static void tryPullBlock(BlockPos vec) {
        EntityPlayer player = Minecraft.getMinecraft().player;
        move(-1, player, vec);
    }

    /**
     * Player tries to Pull an entity, is sorted into item or creature
     * 
     * @param entity
     *            the entity to try to Pull
     */
    public static void tryPullEntity(Entity entity) {
        if (entity instanceof EntityItem) {
            tryPullItem((EntityItem) entity);
        }
        if (entity instanceof EntityLiving) {
            tryPullMob((EntityLiving) entity);
        }

    }

    /**
     * The player has tried to Pull an item
     * 
     * @param entity
     *            the EntityItem to Pull
     */
    private static void tryPullItem(EntityItem entity) {
        if (metallist.contains(entity.getItem().getItem().getUnlocalizedName())) {
            EntityPlayer player = Minecraft.getMinecraft().player;
            BlockPos anchor = new BlockPos((int) player.posX, (int) player.posY - 1, (int) player.posZ);
            move(-0.5, entity, anchor);
        }
    }

    /**
     * The player has tried to Pull a mob
     * 
     * @param entity
     *            the mob to Pull
     */
    private static void tryPullMob(EntityLiving entity) {

        EntityPlayer player = Minecraft.getMinecraft().player;

        if (entity instanceof EntityIronGolem) {
            // Pull you toward the entity
            BlockPos anchor = new BlockPos((int) entity.posX, (int) entity.posY, (int) entity.posZ);
            move(-1, player, anchor);
        }

        if ((entity.getHeldItem(EnumHand.OFF_HAND) != null && isItemMetal(entity.getHeldItem(EnumHand.MAIN_HAND))) || (entity.getHeldItem(EnumHand.MAIN_HAND) != null && isItemMetal(entity.getHeldItem(EnumHand.OFF_HAND)))) {
            // Pull the entity toward you
            BlockPos anchor = new BlockPos((int) player.posX, (int) player.posY, (int) player.posZ);
            move(-1, entity, anchor);
        }
    }

    /**
     * Player tries to Push a block
     * 
     * @param vec
     *            the location of the block
     */
    public static void tryPushBlock(BlockPos vec) {
        EntityPlayer player = Minecraft.getMinecraft().player;
        move(1, player, vec);
    }

    /**
     * Player tries to Push an entity, is sorted into item or creature
     * 
     * @param entity
     *            the entity to try to Push
     */
    public static void tryPushEntity(Entity entity) {

        if (entity instanceof EntityItem) {
            tryPushItem((EntityItem) entity);
        }

        if (entity instanceof EntityCreature) {
            tryPushMob((EntityCreature) entity);
        }

    }
    
    /**
     * The player has tried to Push an item
     * 
     * @param entity
     *            the EntityItem to Push
     */
    private static void tryPushItem(EntityItem entity) {
        if (metallist.contains(entity.getItem().getItem().getUnlocalizedName())) {
            EntityPlayer player = Minecraft.getMinecraft().player;
            BlockPos anchor = new BlockPos((int) player.posX, (int) player.posY - 1, (int) player.posZ);
            move(0.5, entity, anchor);
        }
    }
    

    /**
     * The player has tried to Push a mob
     * 
     * @param entity
     *            the mob to Push
     */
    private static void tryPushMob(EntityLiving entity) {

        EntityPlayer player = Minecraft.getMinecraft().player;

        if (entity instanceof EntityIronGolem) {
            // Pull you toward the entity
            BlockPos anchor = new BlockPos((int) entity.posX, (int) entity.posY, (int) entity.posZ);
            move(1, player, anchor);
        }

        if ((entity.getHeldItem(EnumHand.OFF_HAND) != null && isItemMetal(entity.getHeldItem(EnumHand.MAIN_HAND))) || (entity.getHeldItem(EnumHand.MAIN_HAND) != null && isItemMetal(entity.getHeldItem(EnumHand.OFF_HAND)))) {
            // Pull the entity toward you
            BlockPos anchor = new BlockPos((int) player.posX, (int) player.posY, (int) player.posZ);
            move(1, entity, anchor);
        }
    }
}
