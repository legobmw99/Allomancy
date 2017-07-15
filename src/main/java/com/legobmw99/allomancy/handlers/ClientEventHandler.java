package com.legobmw99.allomancy.handlers;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.lwjgl.opengl.GL11;
import org.lwjgl.util.Point;

import com.legobmw99.allomancy.entities.EntityGoldNugget;
import com.legobmw99.allomancy.entities.EntityIronNugget;
import com.legobmw99.allomancy.entities.particles.ParticlePointer;
import com.legobmw99.allomancy.entities.particles.ParticleSound;
import com.legobmw99.allomancy.gui.GUIMetalSelect;
import com.legobmw99.allomancy.network.packets.ChangeEmotionPacket;
import com.legobmw99.allomancy.network.packets.GetCapabilitiesPacket;
import com.legobmw99.allomancy.util.AllomancyCapabilities;
import com.legobmw99.allomancy.util.AllomancyConfig;
import com.legobmw99.allomancy.util.AllomancyUtils;
import com.legobmw99.allomancy.util.Registry;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.ISound;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.GuiIngame;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.renderer.texture.ITextureObject;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityItemFrame;
import net.minecraft.entity.monster.EntityIronGolem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.client.event.sound.PlaySoundEvent;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ClientEventHandler {
	
    private static final Point[] Frames = { new Point(72, 0), new Point(72, 4), new Point(72, 8), new Point(72, 12) };
    private static final ResourceLocation meterLoc  = new ResourceLocation("allomancy", "textures/gui/overlay/meter.png");

    private Minecraft mc;
    private AllomancyCapabilities cap;
    private EntityPlayerSP player;
    
	private int animationCounter = 0;
    private int currentFrame = 0;
		
    private ArrayList<Entity> particleTargets = new ArrayList<Entity>();
    private ArrayList<BlockPos> particleBlockTargets = new ArrayList<BlockPos>();
    private ArrayList<EntityPlayer> metalBurners = new ArrayList<EntityPlayer>();

    private Entity pointedEntity;
    
	/**
     * Draws the overlay for the metals
     */
    @SideOnly(Side.CLIENT)
    private void drawMetalOverlay() {
        this.mc = Minecraft.getMinecraft();

        player = this.mc.player;
        if (player == null) {
            return;
        }

        cap = AllomancyCapabilities.forPlayer(player);

        if (cap.getAllomancyPower() < 0) {
            return;
        }

        this.animationCounter++;
        // left hand side.
        int ironY, steelY, tinY, pewterY;
        // right hand side
        int copperY, bronzeY, zincY, brassY;
        // single metal
        int singleMetalY;
        int renderX, renderY = 0;
        ScaledResolution res = new ScaledResolution(this.mc);

        // Set the offsets of the overlay based on config
        switch (AllomancyConfig.overlayPosition % 4) {
            case 0:
                renderX = 5;
                renderY = 10;
                break;
            case 1:
                renderX = res.getScaledWidth() - 95;
                renderY = 10;
                break;
            case 2:
                renderX = res.getScaledWidth() - 95;
                renderY = res.getScaledHeight() - 40;
                break;
            case 3:
                renderX = 5;
                renderY = res.getScaledHeight() - 40;
                break;
            default:
                renderX = 5;
                renderY = 10;
                break;
        }

        GuiIngame gig = new GuiIngame(Minecraft.getMinecraft());
        Minecraft.getMinecraft().renderEngine.bindTexture(this.meterLoc);
        ITextureObject obj;
        obj = Minecraft.getMinecraft().renderEngine.getTexture(this.meterLoc);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, obj.getGlTextureId());

        /*
         * Misting overlay
         */
        if (cap.getAllomancyPower() >= 0 && cap.getAllomancyPower() < 8) {

            singleMetalY = 9 - cap.getMetalAmounts(cap.getAllomancyPower());
            gig.drawTexturedModalRect(renderX + 1, renderY + 5 + singleMetalY, 7 + 6 * cap.getAllomancyPower(), 1 + singleMetalY, 3, 10 - singleMetalY);
            gig.drawTexturedModalRect(renderX, renderY, 0, 0, 5, 20);
            if (this.cap.getMetalBurning(this.cap.getAllomancyPower())) {
                gig.drawTexturedModalRect(renderX, renderY + 5 + singleMetalY, Frames[this.currentFrame].getX(), Frames[this.currentFrame].getY(), 5, 3);
            }
            if (this.animationCounter > 6) // Draw the burning symbols...
            {
                this.animationCounter = 0;
                this.currentFrame++;
                if (this.currentFrame > 3) {
                    this.currentFrame = 0;
                }
            }

        }

        /*
         * The rendering for a the overlay of a full Mistborn
         */
        if (cap.getAllomancyPower() == 8) {

            ironY = 9 - this.cap.getMetalAmounts(AllomancyCapabilities.matIron);
            gig.drawTexturedModalRect(renderX + 1, renderY + 5 + ironY, 7, 1 + ironY, 3, 10 - ironY);

            steelY = 9 - this.cap.getMetalAmounts(AllomancyCapabilities.matSteel);
            gig.drawTexturedModalRect(renderX + 8, renderY + 5 + steelY, 13, 1 + steelY, 3, 10 - steelY);

            tinY = 9 - this.cap.getMetalAmounts(AllomancyCapabilities.matTin);
            gig.drawTexturedModalRect(renderX + 26, renderY + 5 + tinY, 19, 1 + tinY, 3, 10 - tinY);

            pewterY = 9 - this.cap.getMetalAmounts(AllomancyCapabilities.matPewter);
            gig.drawTexturedModalRect(renderX + 33, renderY + 5 + pewterY, 25, 1 + pewterY, 3, 10 - pewterY);

            zincY = 9 - this.cap.getMetalAmounts(AllomancyCapabilities.matZinc);
            gig.drawTexturedModalRect(renderX + 51, renderY + 5 + zincY, 31, 1 + zincY, 3, 10 - zincY);

            brassY = 9 - this.cap.getMetalAmounts(AllomancyCapabilities.matBrass);
            gig.drawTexturedModalRect(renderX + 58, renderY + 5 + brassY, 37, 1 + brassY, 3, 10 - brassY);

            copperY = 9 - this.cap.getMetalAmounts(AllomancyCapabilities.matCopper);
            gig.drawTexturedModalRect(renderX + 76, renderY + 5 + copperY, 43, 1 + copperY, 3, 10 - copperY);

            bronzeY = 9 - this.cap.getMetalAmounts(AllomancyCapabilities.matBronze);
            gig.drawTexturedModalRect(renderX + 83, renderY + 5 + bronzeY, 49, 1 + bronzeY, 3, 10 - bronzeY);

            // Draw the gauges second, so that highlights and decorations show over
            // the bar.
            gig.drawTexturedModalRect(renderX, renderY, 0, 0, 5, 20);
            gig.drawTexturedModalRect(renderX + 7, renderY, 0, 0, 5, 20);

            gig.drawTexturedModalRect(renderX + 25, renderY, 0, 0, 5, 20);
            gig.drawTexturedModalRect(renderX + 32, renderY, 0, 0, 5, 20);

            gig.drawTexturedModalRect(renderX + 50, renderY, 0, 0, 5, 20);
            gig.drawTexturedModalRect(renderX + 57, renderY, 0, 0, 5, 20);

            gig.drawTexturedModalRect(renderX + 75, renderY, 0, 0, 5, 20);
            gig.drawTexturedModalRect(renderX + 82, renderY, 0, 0, 5, 20);

            if (this.cap.getMetalBurning(AllomancyCapabilities.matIron)) {
                gig.drawTexturedModalRect(renderX, renderY + 5 + ironY, Frames[this.currentFrame].getX(), Frames[this.currentFrame].getY(), 5, 3);
            }
            if (this.cap.getMetalBurning(AllomancyCapabilities.matSteel)) {
                gig.drawTexturedModalRect(renderX + 7, renderY + 5 + steelY, Frames[this.currentFrame].getX(), Frames[this.currentFrame].getY(), 5, 3);
            }
            if (this.cap.getMetalBurning(AllomancyCapabilities.matTin)) {
                gig.drawTexturedModalRect(renderX + 25, renderY + 5 + tinY, Frames[this.currentFrame].getX(), Frames[this.currentFrame].getY(), 5, 3);
            }
            if (this.cap.getMetalBurning(AllomancyCapabilities.matPewter)) {
                gig.drawTexturedModalRect(renderX + 32, renderY + 5 + pewterY, Frames[this.currentFrame].getX(), Frames[this.currentFrame].getY(), 5, 3);
            }
            if (this.cap.getMetalBurning(AllomancyCapabilities.matZinc)) {
                gig.drawTexturedModalRect(renderX + 50, renderY + 5 + zincY, Frames[this.currentFrame].getX(), Frames[this.currentFrame].getY(), 5, 3);
            }
            if (this.cap.getMetalBurning(AllomancyCapabilities.matBrass)) {
                gig.drawTexturedModalRect(renderX + 57, renderY + 5 + brassY, Frames[this.currentFrame].getX(), Frames[this.currentFrame].getY(), 5, 3);
            }
            if (this.cap.getMetalBurning(AllomancyCapabilities.matCopper)) {
                gig.drawTexturedModalRect(renderX + 75, renderY + 5 + copperY, Frames[this.currentFrame].getX(), Frames[this.currentFrame].getY(), 5, 3);
            }
            if (this.cap.getMetalBurning(AllomancyCapabilities.matBronze)) {
                gig.drawTexturedModalRect(renderX + 82, renderY + 5 + bronzeY, Frames[this.currentFrame].getX(), Frames[this.currentFrame].getY(), 5, 3);
            }

            if (this.animationCounter > 6) // Draw the burning symbols...
            {
                this.animationCounter = 0;
                this.currentFrame++;
                if (this.currentFrame > 3) {
                    this.currentFrame = 0;
                }
            }
        }
    }
    
    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        // Run once per tick, only if in game, and only if there is a player
        if (event.phase == TickEvent.Phase.END && (!Minecraft.getMinecraft().isGamePaused() && Minecraft.getMinecraft().player != null)) {

            player = Minecraft.getMinecraft().player;
            AllomancyCapabilities cap = AllomancyCapabilities.forPlayer(player);

            int max = AllomancyConfig.maxDrawLine;

            if (cap.getAllomancyPower() >= 0) {
                // Populate the metal lists
                if (cap.getMetalBurning(AllomancyCapabilities.matIron) || cap.getMetalBurning(AllomancyCapabilities.matSteel)) {
                    particleBlockTargets.clear();
                    particleTargets.clear();

                    List<Entity> eListMetal;
                    Iterable<BlockPos> blocks;

                    int xLoc = (int) player.posX;
                    int yLoc = (int) player.posY;
                    int zLoc = (int) player.posZ;
                    BlockPos negative = new BlockPos(xLoc - max, yLoc - max, zLoc - max);
                    BlockPos positive = new BlockPos(xLoc + max, yLoc + max, zLoc + max);

                    // Add entities to metal list
                    eListMetal = player.world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(negative, positive));
                    for (Entity curEntity : eListMetal) {
                        if (curEntity != null && !particleTargets.contains(curEntity)) {
                            if (curEntity instanceof EntityGoldNugget || curEntity instanceof EntityIronNugget) {
                                particleTargets.add(curEntity);
                            } else if (curEntity instanceof EntityLiving
                                    && (((curEntity instanceof EntityIronGolem) || (((((EntityLiving) curEntity).getHeldItem(EnumHand.MAIN_HAND) != null) || ((EntityLiving) curEntity).getHeldItem(EnumHand.OFF_HAND) == null)
                                            && (AllomancyUtils.isItemMetal(((EntityLiving) curEntity).getHeldItem(EnumHand.MAIN_HAND)) || AllomancyUtils.isItemMetal(((EntityLiving) curEntity).getHeldItem(EnumHand.OFF_HAND))))))) {
                                particleTargets.add(curEntity);
                            } else if (curEntity instanceof EntityItem && AllomancyUtils.isItemMetal(((EntityItem) curEntity).getItem())) {
                                particleTargets.add(curEntity);
                            } else if (curEntity instanceof EntityItemFrame && AllomancyUtils.isItemMetal(((EntityItemFrame) curEntity).getDisplayedItem())) {
                                particleTargets.add(curEntity);
                            }
                        }
                    }

                    // Add metal blocks to metal list
                    blocks = BlockPos.getAllInBox(negative, positive);
                    for (BlockPos block : blocks) {
                        BlockPos imBlock = block.toImmutable();
                        if (AllomancyUtils.isBlockMetal(Minecraft.getMinecraft().world.getBlockState(imBlock).getBlock())) {
                            particleBlockTargets.add(imBlock);
                        }
                    }

                }

                if ((player.getHeldItemMainhand().isEmpty()) && (Minecraft.getMinecraft().gameSettings.keyBindAttack.isKeyDown())) {
                    // Ray trace 20 blocks
                    RayTraceResult mov = AllomancyUtils.getMouseOverExtended(20.0F);
                    // All iron pulling powers
                    if (cap.getMetalBurning(AllomancyCapabilities.matIron)) {
                        if (mov != null) {
                            if (mov.entityHit != null) {
                                AllomancyUtils.tryMoveOffEntity(mov.entityHit, AllomancyUtils.PULL);
                            }
                            if (mov.typeOfHit == RayTraceResult.Type.BLOCK || mov.typeOfHit == RayTraceResult.Type.MISS) {
                                BlockPos bp = mov.getBlockPos();
                                if (AllomancyUtils.isBlockMetal(Minecraft.getMinecraft().world.getBlockState(bp).getBlock())) {
                                    AllomancyUtils.tryMoveOffBlock(bp, AllomancyUtils.PULL);
                                }
                            }

                        }

                    }
                    // All zinc powers
                    if (cap.getMetalBurning(AllomancyCapabilities.matZinc)) {
                        Entity entity;
                        if ((mov != null) && (mov.entityHit != null) && (mov.entityHit instanceof EntityCreature) && !(mov.entityHit instanceof EntityPlayer)) {

                            entity = mov.entityHit;
                            Registry.network.sendToServer(new ChangeEmotionPacket(entity.getEntityId(), true));

                        }
                    }

                }
                if ((player.getHeldItemMainhand()).isEmpty() && (Minecraft.getMinecraft().gameSettings.keyBindUseItem.isKeyDown())) {
                    // Ray trace 20 blocks
                    RayTraceResult mov = AllomancyUtils.getMouseOverExtended(20.0F);
                    // All steel pushing powers
                    if (cap.getMetalBurning(AllomancyCapabilities.matSteel)) {
                        if (mov != null) {
                            if (mov.entityHit != null) {
                                AllomancyUtils.tryMoveOffEntity(mov.entityHit,AllomancyUtils.PUSH);

                            }
                            if (mov.typeOfHit == RayTraceResult.Type.BLOCK || mov.typeOfHit == RayTraceResult.Type.MISS) {

                                BlockPos bp = mov.getBlockPos();
                                if (AllomancyUtils.isBlockMetal(Minecraft.getMinecraft().world.getBlockState(bp).getBlock())) {
                                    AllomancyUtils.tryMoveOffBlock(bp, AllomancyUtils.PUSH);
                                }
                            }

                        }

                    }
                    // All brass powers
                    if (cap.getMetalBurning(AllomancyCapabilities.matBrass)) {
                        Entity entity;
                        if ((mov != null) && (mov.entityHit != null) && (mov.entityHit instanceof EntityCreature) && !(mov.entityHit instanceof EntityPlayer)) {
                            entity = mov.entityHit;
                            Registry.network.sendToServer(new ChangeEmotionPacket(entity.getEntityId(), false));

                        }
                    }

                }

                // Pewter's speed powers
                if (cap.getMetalBurning(AllomancyCapabilities.matPewter)) {
                    if ((player.onGround) && (!player.isInWater()) && (Minecraft.getMinecraft().gameSettings.keyBindForward.isKeyDown())) {
                        player.motionX *= 1.2;
                        player.motionZ *= 1.2;

                        // Don't allow motion values to get too out of the norm
                        player.motionX = MathHelper.clamp((float) player.motionX, -2, 2);
                        player.motionZ = MathHelper.clamp((float) player.motionZ, -2, 2);
                    }
                }

                if (cap.getMetalBurning(AllomancyCapabilities.matBronze) && !cap.getMetalBurning(AllomancyCapabilities.matCopper)) {
                    AxisAlignedBB boxBurners;
                    List<Entity> eListBurners;
                    metalBurners.clear();
                    // Add metal burners to a list
                    boxBurners = new AxisAlignedBB((player.posX - 30), (player.posY - 30), (player.posZ - 30), (player.posX + 30), (player.posY + 30), (player.posZ + 30));
                    eListBurners = player.world.getEntitiesWithinAABB(Entity.class, boxBurners);
                    for (Entity curEntity : eListBurners) {
                        if (curEntity != null && (curEntity instanceof EntityPlayer) && curEntity != player) {
                            Registry.network.sendToServer(new GetCapabilitiesPacket(curEntity.getEntityId(), player.getEntityId()));
                            AllomancyCapabilities capOther = AllomancyCapabilities.forPlayer(curEntity);

                            if (capOther.getMetalBurning(AllomancyCapabilities.matCopper)) {
                                metalBurners.remove((EntityPlayer) curEntity);
                            } else {
                                if (capOther.getMetalBurning(AllomancyCapabilities.matIron) || capOther.getMetalBurning(AllomancyCapabilities.matSteel) || capOther.getMetalBurning(AllomancyCapabilities.matTin)
                                        || capOther.getMetalBurning(AllomancyCapabilities.matPewter) || capOther.getMetalBurning(AllomancyCapabilities.matZinc) || capOther.getMetalBurning(AllomancyCapabilities.matBrass)
                                        || capOther.getMetalBurning(AllomancyCapabilities.matBronze)) {
                                    metalBurners.add((EntityPlayer) curEntity);
                                }
                            }
                        }
                    }
                } else {
                    metalBurners.clear();
                }

                // Remove items from the metal list
                LinkedList<Entity> toRemoveMetal = new LinkedList<Entity>();

                for (Entity entity : particleTargets) {

                    if (entity.isDead) {
                        toRemoveMetal.add(entity);
                    }
                    if (player == null) {
                        return;
                    }
                    if (player.getDistanceToEntity(entity) > 10) {
                        toRemoveMetal.add(entity);
                    }
                }

                for (Entity entity : toRemoveMetal) {
                    particleTargets.remove(entity);
                }
                toRemoveMetal.clear();

                // Remove items from burners
                LinkedList<EntityPlayer> toRemoveBurners = new LinkedList<EntityPlayer>();

                for (EntityPlayer entity : metalBurners) {
                    AllomancyCapabilities capOther = AllomancyCapabilities.forPlayer(entity);
                    Registry.network.sendToServer(new GetCapabilitiesPacket(entity.getEntityId(), player.getEntityId()));
                    if (entity.isDead) {
                        toRemoveBurners.add(entity);
                    }

                    if (player != null && player.getDistanceToEntity(entity) > 10) {
                        toRemoveBurners.add(entity);
                    }
                    if (capOther.getMetalBurning(AllomancyCapabilities.matCopper) || !(capOther.getMetalBurning(AllomancyCapabilities.matIron) || capOther.getMetalBurning(AllomancyCapabilities.matSteel)
                            || capOther.getMetalBurning(AllomancyCapabilities.matTin) || capOther.getMetalBurning(AllomancyCapabilities.matPewter) || capOther.getMetalBurning(AllomancyCapabilities.matZinc)
                            || capOther.getMetalBurning(AllomancyCapabilities.matBrass) || capOther.getMetalBurning(AllomancyCapabilities.matBronze))) {
                        toRemoveBurners.add(entity);
                    }
                }

                for (Entity entity : toRemoveBurners) {
                    metalBurners.remove(entity);
                }
                toRemoveBurners.clear();
            }
        }
    }
    
    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public void onKeyInput(InputEvent.KeyInputEvent event) {
        if (Registry.burn.isPressed()) {
            player = Minecraft.getMinecraft().player;
            AllomancyCapabilities cap;
            Minecraft mc = FMLClientHandler.instance().getClient();
            if (mc.currentScreen == null) {
                if (player == null || !Minecraft.getMinecraft().inGameHasFocus) {
                    return;
                }
                cap = AllomancyCapabilities.forPlayer(player);
                /*
                 * Mistings only have one metal, so toggle that one
                 */
                if (cap.getAllomancyPower() >= 0 && cap.getAllomancyPower() < 8) {
                    AllomancyUtils.toggleMetalBurn(cap.getAllomancyPower(), cap);
                }

                /*
                 * If the player is a full Mistborn, display the GUI
                 */
                if (cap.getAllomancyPower() == 8) {
                    mc.displayGuiScreen(new GUIMetalSelect());

                }
            }
        }
    }
    
    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public void onRenderGameOverlay(RenderGameOverlayEvent event) {

        if (event.isCancelable() || event.getType() != ElementType.EXPERIENCE) {
            return;
        }
        if (!Minecraft.getMinecraft().inGameHasFocus) {
            return;
        }
        if (FMLClientHandler.instance().getClient().currentScreen != null ) {
            return;
        }

        drawMetalOverlay();

    }

    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public void onRenderGUIScreen(GuiScreenEvent.DrawScreenEvent event) {
        if (event.getGui() instanceof GUIMetalSelect && !event.isCancelable()) {
            drawMetalOverlay();
        }
    }

    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public void onRenderWorldLast(RenderWorldLastEvent event) {
        this.mc = Minecraft.getMinecraft();
        player = this.mc.player;
        if (player == null) {
            return;
        }

        cap = AllomancyCapabilities.forPlayer(player);

        if (cap.getAllomancyPower() < 0) {
            return;
        }

        double playerX = player.prevPosX + (player.posX - player.prevPosX) * event.getPartialTicks();
        double playerY = player.prevPosY + (player.posY - player.prevPosY) * event.getPartialTicks();
        double playerZ = player.prevPosZ + (player.posZ - player.prevPosZ) * event.getPartialTicks();

        // Iron and Steel lines
        if ((this.cap.getMetalBurning(AllomancyCapabilities.matIron) || this.cap.getMetalBurning(AllomancyCapabilities.matSteel))) {

            for (Entity entity : particleTargets) {
                AllomancyUtils.drawMetalLine(playerX, playerY, playerZ, entity.posX, entity.posY, entity.posZ, 1F, 0F, 0.6F, 1F);
            }

            for (BlockPos v : particleBlockTargets) {
                AllomancyUtils.drawMetalLine(playerX, playerY, playerZ, v.getX() + 0.5, v.getY() + 0.5, v.getZ() + 0.5, 1F, 0F, 0.6F, 1F);
            }
        }

        if ((cap.getMetalBurning(AllomancyCapabilities.matBronze))) {
            for (EntityPlayer entityplayer : metalBurners) {

                // drawMetalLine(playerX, playerY, playerZ, entityplayer.posX, entityplayer.posY, entityplayer.posZ, 1, 1F, 0.15F, 0.15F);
                double x = ((player.posX - entityplayer.posX) * -1) * .03;
                double y = (((player.posY - entityplayer.posY + 1.4) * -1) * .03) + .021;
                double z = ((player.posZ - entityplayer.posZ) * -1) * .03;
                ParticlePointer particle = new ParticlePointer(player.world, player.posX - (Math.sin(Math.toRadians(player.getRotationYawHead())) * .1d), player.posY + .1, player.posZ + (Math.cos(Math.toRadians(player.getRotationYawHead())) * .1d), x, y,
                        z);
                Minecraft.getMinecraft().effectRenderer.addEffect(particle);
            }
        }
    }

    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public void onSound(PlaySoundEvent event) {
        double motionX, motionY, motionZ, magnitude;

        player = Minecraft.getMinecraft().player;
        ISound sound = event.getSound();
        if ((player == null) || (sound == null)) {
            return;
        }

        magnitude = Math.sqrt(Math.pow((player.posX - sound.getXPosF()), 2) + Math.pow((player.posY - sound.getYPosF()), 2) + Math.pow((player.posZ - sound.getZPosF()), 2));
        if (((magnitude) > 20) || ((magnitude) < .5)) {
            return;
        }
        AllomancyCapabilities cap = AllomancyCapabilities.forPlayer(player);
        // Spawn sound particles
        if (cap.getMetalBurning(AllomancyCapabilities.matTin)) {
            if (sound.getSoundLocation().toString().contains("step") || sound.getSoundLocation().toString().contains("entity") || sound.getSoundLocation().toString().contains("hostile") || sound.getSoundLocation().toString().contains(".big")
                    || sound.getSoundLocation().toString().contains("scream") || sound.getSoundLocation().toString().contains("bow")) {
                motionX = ((player.posX - (event.getSound().getXPosF() + .5)) * -0.7) / magnitude;
                motionY = ((player.posY - (event.getSound().getYPosF() + .2)) * -0.7) / magnitude;
                motionZ = ((player.posZ - (event.getSound().getZPosF() + .5)) * -0.7) / magnitude;
                Particle particle = new ParticleSound(player.world, player.posX + (Math.sin(Math.toRadians(player.getRotationYawHead())) * -.7d), player.posY + .2, player.posZ + (Math.cos(Math.toRadians(player.getRotationYawHead())) * .7d), motionX,
                        motionY, motionZ, sound);
                Minecraft.getMinecraft().effectRenderer.addEffect(particle);
            }

        }
    }
}
