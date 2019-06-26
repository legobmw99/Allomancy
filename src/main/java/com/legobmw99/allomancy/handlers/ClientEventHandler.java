package com.legobmw99.allomancy.handlers;

import com.legobmw99.allomancy.entities.particles.SoundParticle;
import com.legobmw99.allomancy.gui.MetalSelectScreen;
import com.legobmw99.allomancy.network.NetworkHelper;
import com.legobmw99.allomancy.network.packets.ChangeEmotionPacket;
import com.legobmw99.allomancy.network.packets.GetCapabilitiesPacket;
import com.legobmw99.allomancy.network.packets.TryPushPullBlock;
import com.legobmw99.allomancy.network.packets.TryPushPullEntity;
import com.legobmw99.allomancy.util.AllomancyCapability;
import com.legobmw99.allomancy.util.AllomancyConfig;
import com.legobmw99.allomancy.util.AllomancyUtils;
import com.legobmw99.allomancy.util.Registry;
import net.minecraft.client.MainWindow;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.ISound;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.gui.IngameGui;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.renderer.texture.ITextureObject;
import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.*;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.client.event.sound.PlaySoundEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Stream;

public class ClientEventHandler {

    private static final Point[] Frames = {new Point(72, 0), new Point(72, 4), new Point(72, 8), new Point(72, 12)};
    private static final ResourceLocation meterLoc = new ResourceLocation("allomancy", "textures/gui/overlay/meter.png");

    private Minecraft mc = Minecraft.getInstance();
    private AllomancyCapability cap;
    private ClientPlayerEntity player;

    private int animationCounter = 0;
    private int currentFrame = 0;
    private int max = AllomancyConfig.max_metal_detection;


    private ArrayList<Entity> particleTargets = new ArrayList<Entity>();
    private ArrayList<BlockPos> particleBlockTargets = new ArrayList<BlockPos>();
    private ArrayList<PlayerEntity> metalBurners = new ArrayList<PlayerEntity>();

    private Entity pointedEntity;

    /**
     * Draws the overlay for the metals
     */
    @OnlyIn(Dist.CLIENT)
    private void drawMetalOverlay() {

        player = this.mc.player;
        if (player == null) {
            return;
        }

        cap = AllomancyCapability.forPlayer(player);

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
        MainWindow res = Minecraft.getInstance().mainWindow;

        // Set the offsets of the overlay based on config
        switch (AllomancyConfig.overlay_position) {
            case TOP_LEFT:
                renderX = res.getScaledWidth() - 95;
                renderY = 10;
                break;
            case BOTTOM_RIGHT:
                renderX = res.getScaledWidth() - 95;
                renderY = res.getScaledHeight() - 40;
                break;
            case BOTTOM_LEFT:
                renderX = 5;
                renderY = res.getScaledHeight() - 40;
                break;
            default: //TOP_RIGHT
                renderX = 5;
                renderY = 10;
                break;
        }

        IngameGui gig = new IngameGui(this.mc);
        this.mc.getRenderManager().textureManager.bindTexture(this.meterLoc);
        ITextureObject obj;
        obj = this.mc.getRenderManager().textureManager.getTexture(this.meterLoc);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, obj.getGlTextureId());

        /*
         * Misting overlay
         */
        if (cap.getAllomancyPower() >= 0 && cap.getAllomancyPower() < 8) {

            singleMetalY = 9 - cap.getMetalAmounts(cap.getAllomancyPower());
            gig.blit(renderX + 1, renderY + 5 + singleMetalY, 7 + 6 * cap.getAllomancyPower(), 1 + singleMetalY, 3, 10 - singleMetalY);
            gig.blit(renderX, renderY, 0, 0, 5, 20);
            if (this.cap.getMetalBurning(this.cap.getAllomancyPower())) {
                gig.blit(renderX, renderY + 5 + singleMetalY, Frames[this.currentFrame].x, Frames[this.currentFrame].y, 5, 3);
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

            ironY = 9 - this.cap.getMetalAmounts(AllomancyCapability.IRON);
            gig.blit(renderX + 1, renderY + 5 + ironY, 7, 1 + ironY, 3, 10 - ironY);

            steelY = 9 - this.cap.getMetalAmounts(AllomancyCapability.STEEL);
            gig.blit(renderX + 8, renderY + 5 + steelY, 13, 1 + steelY, 3, 10 - steelY);

            tinY = 9 - this.cap.getMetalAmounts(AllomancyCapability.TIN);
            gig.blit(renderX + 26, renderY + 5 + tinY, 19, 1 + tinY, 3, 10 - tinY);

            pewterY = 9 - this.cap.getMetalAmounts(AllomancyCapability.PEWTER);
            gig.blit(renderX + 33, renderY + 5 + pewterY, 25, 1 + pewterY, 3, 10 - pewterY);

            zincY = 9 - this.cap.getMetalAmounts(AllomancyCapability.ZINC);
            gig.blit(renderX + 51, renderY + 5 + zincY, 31, 1 + zincY, 3, 10 - zincY);

            brassY = 9 - this.cap.getMetalAmounts(AllomancyCapability.BRASS);
            gig.blit(renderX + 58, renderY + 5 + brassY, 37, 1 + brassY, 3, 10 - brassY);

            copperY = 9 - this.cap.getMetalAmounts(AllomancyCapability.COPPER);
            gig.blit(renderX + 76, renderY + 5 + copperY, 43, 1 + copperY, 3, 10 - copperY);

            bronzeY = 9 - this.cap.getMetalAmounts(AllomancyCapability.BRONZE);
            gig.blit(renderX + 83, renderY + 5 + bronzeY, 49, 1 + bronzeY, 3, 10 - bronzeY);

            // Draw the gauges second, so that highlights and decorations show over
            // the bar.
            gig.blit(renderX, renderY, 0, 0, 5, 20);
            gig.blit(renderX + 7, renderY, 0, 0, 5, 20);

            gig.blit(renderX + 25, renderY, 0, 0, 5, 20);
            gig.blit(renderX + 32, renderY, 0, 0, 5, 20);

            gig.blit(renderX + 50, renderY, 0, 0, 5, 20);
            gig.blit(renderX + 57, renderY, 0, 0, 5, 20);

            gig.blit(renderX + 75, renderY, 0, 0, 5, 20);
            gig.blit(renderX + 82, renderY, 0, 0, 5, 20);

            if (this.cap.getMetalBurning(AllomancyCapability.IRON)) {
                gig.blit(renderX, renderY + 5 + ironY, Frames[this.currentFrame].x, Frames[this.currentFrame].y, 5, 3);
            }
            if (this.cap.getMetalBurning(AllomancyCapability.STEEL)) {
                gig.blit(renderX + 7, renderY + 5 + steelY, Frames[this.currentFrame].x, Frames[this.currentFrame].y, 5, 3);
            }
            if (this.cap.getMetalBurning(AllomancyCapability.TIN)) {
                gig.blit(renderX + 25, renderY + 5 + tinY, Frames[this.currentFrame].x, Frames[this.currentFrame].y, 5, 3);
            }
            if (this.cap.getMetalBurning(AllomancyCapability.PEWTER)) {
                gig.blit(renderX + 32, renderY + 5 + pewterY, Frames[this.currentFrame].x, Frames[this.currentFrame].y, 5, 3);
            }
            if (this.cap.getMetalBurning(AllomancyCapability.ZINC)) {
                gig.blit(renderX + 50, renderY + 5 + zincY, Frames[this.currentFrame].x, Frames[this.currentFrame].y, 5, 3);
            }
            if (this.cap.getMetalBurning(AllomancyCapability.BRASS)) {
                gig.blit(renderX + 57, renderY + 5 + brassY, Frames[this.currentFrame].x, Frames[this.currentFrame].y, 5, 3);
            }
            if (this.cap.getMetalBurning(AllomancyCapability.COPPER)) {
                gig.blit(renderX + 75, renderY + 5 + copperY, Frames[this.currentFrame].x, Frames[this.currentFrame].y, 5, 3);
            }
            if (this.cap.getMetalBurning(AllomancyCapability.BRONZE)) {
                gig.blit(renderX + 82, renderY + 5 + bronzeY, Frames[this.currentFrame].x, Frames[this.currentFrame].y, 5, 3);
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

    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    public void onClientTick(final TickEvent.ClientTickEvent event) {
        // Run once per tick, only if in game, and only if there is a player
        if (event.phase == TickEvent.Phase.END && !this.mc.isGamePaused() && this.mc.player != null && this.mc.player.isAlive()) {

            player = this.mc.player;
            cap = AllomancyCapability.forPlayer(player);

            if (cap.getAllomancyPower() >= 0) {
                // Populate the metal lists
                if (cap.getMetalBurning(AllomancyCapability.IRON) || cap.getMetalBurning(AllomancyCapability.STEEL)) {
                    particleBlockTargets.clear();
                    particleTargets.clear();

                    List<Entity> eListMetal;
                    Stream<BlockPos> blocks;

                    int xLoc = (int) player.posX;
                    int yLoc = (int) player.posY;
                    int zLoc = (int) player.posZ;
                    BlockPos negative = new BlockPos(xLoc - max, yLoc - max, zLoc - max);
                    BlockPos positive = new BlockPos(xLoc + max, yLoc + max, zLoc + max);

                    // Add entities to metal list
                    eListMetal = player.world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(negative, positive));
                    for (Entity curEntity : eListMetal) {
                        if (curEntity != null && !particleTargets.contains(curEntity)) {
                            if (AllomancyUtils.isEntityMetal(curEntity)) {
                                particleTargets.add(curEntity);
                            }
                        }
                    }

                    // Add metal blocks to metal list
                    blocks = BlockPos.getAllInBox(negative, positive);
                    blocks.forEach(this::checkBlocks);

                }

                if (this.mc.gameSettings.keyBindAttack.isKeyDown()) {
                    // Ray trace 20 blocks
                    RayTraceResult trace = AllomancyUtils.getMouseOverExtended(20F);
                    // All iron pulling powers
                    if (cap.getMetalBurning(AllomancyCapability.IRON)) {
                        if (trace != null) {
                            if (trace.getType() == RayTraceResult.Type.ENTITY && AllomancyUtils.isEntityMetal(((EntityRayTraceResult) trace).getEntity())) {
                                NetworkHelper.sendToServer(new TryPushPullEntity(((EntityRayTraceResult) trace).getEntity().getEntityId(), AllomancyUtils.PULL));
                            }

                            if (trace.getType() == RayTraceResult.Type.BLOCK) {
                                BlockPos bp = ((BlockRayTraceResult) trace).getPos();
                                if (AllomancyUtils.isBlockMetal(this.mc.world.getBlockState(bp).getBlock())) {
                                    NetworkHelper.sendToServer(new TryPushPullBlock(bp, AllomancyUtils.PULL));
                                }
                            }

                        }

                    }
                    // All zinc powers
                    if (cap.getMetalBurning(AllomancyCapability.ZINC)) {
                        Entity entity;
                        if ((trace != null) && (trace.getType() == RayTraceResult.Type.ENTITY)) {
                            entity = ((EntityRayTraceResult) trace).getEntity();
                            if (entity instanceof CreatureEntity) {
                                NetworkHelper.sendToServer(new ChangeEmotionPacket(entity.getEntityId(), true));
                            }
                        }
                    }

                }
                if (this.mc.gameSettings.keyBindUseItem.isKeyDown()) {
                    // Ray trace 20 blocks
                    RayTraceResult trace = AllomancyUtils.getMouseOverExtended(20F);
                    // All steel pushing powers
                    if (cap.getMetalBurning(AllomancyCapability.STEEL)) {
                        if (trace != null) {
                            if (trace.getType() == RayTraceResult.Type.ENTITY && AllomancyUtils.isEntityMetal(((EntityRayTraceResult) trace).getEntity())) {
                                NetworkHelper.sendToServer(new TryPushPullEntity(((EntityRayTraceResult) trace).getEntity().getEntityId(), AllomancyUtils.PUSH));
                            }

                            if (trace.getType() == RayTraceResult.Type.BLOCK) {
                                BlockPos bp = ((BlockRayTraceResult) trace).getPos();
                                if (AllomancyUtils.isBlockMetal(this.mc.world.getBlockState(bp).getBlock())) {
                                    NetworkHelper.sendToServer(new TryPushPullBlock(bp, AllomancyUtils.PUSH));
                                }
                            }

                        }

                    }
                    // All brass powers
                    if (cap.getMetalBurning(AllomancyCapability.BRASS)) {
                        Entity entity;
                        if ((trace != null) && (trace.getType() == RayTraceResult.Type.ENTITY)) {
                            entity = ((EntityRayTraceResult) trace).getEntity();
                            if (entity instanceof CreatureEntity) {
                                NetworkHelper.sendToServer(new ChangeEmotionPacket(entity.getEntityId(), false));
                            }
                        }
                    }

                }
                // todo replace with a PlayerEvent.StartTracking event and PacketDistributor.TRACKING_ENTITY_AND_SELF
                if (cap.getMetalBurning(AllomancyCapability.BRONZE) && !cap.getMetalBurning(AllomancyCapability.COPPER)) {
                    List<Entity> eListBurners;
                    metalBurners.clear();
                    // Add metal burners to a list
                    int xLoc = (int) player.posX;
                    int yLoc = (int) player.posY;
                    int zLoc = (int) player.posZ;
                    BlockPos negative = new BlockPos(xLoc - 30, yLoc - 30, zLoc - 30);
                    BlockPos positive = new BlockPos(xLoc + 30, yLoc + 30, zLoc + 30);
                    // Add entities to metal list
                    eListBurners = player.world.getEntitiesWithinAABB(PlayerEntity.class, new AxisAlignedBB(negative, positive));

                    for (Entity curEntity : eListBurners) {
                        if (curEntity != null && curEntity != player /* todo test if this is needed: && curEntity.hasCapability(Allomancy.PLAYER_CAP, null)*/) {
                            NetworkHelper.sendToServer(new GetCapabilitiesPacket(curEntity.getEntityId()));
                            AllomancyCapability capOther = AllomancyCapability.forPlayer(curEntity);
                            if (capOther.getMetalBurning(AllomancyCapability.COPPER)) {
                                metalBurners.remove((PlayerEntity) curEntity);
                            } else if (capOther.getMetalBurning(AllomancyCapability.IRON) || capOther.getMetalBurning(AllomancyCapability.STEEL) || capOther.getMetalBurning(AllomancyCapability.TIN)
                                    || capOther.getMetalBurning(AllomancyCapability.PEWTER) || capOther.getMetalBurning(AllomancyCapability.ZINC) || capOther.getMetalBurning(AllomancyCapability.BRASS)
                                    || capOther.getMetalBurning(AllomancyCapability.BRONZE)) {
                                metalBurners.add((PlayerEntity) curEntity);
                            }
                        }
                    }
                } else {
                    metalBurners.clear();
                }

                // Remove items from the metal list
                LinkedList<Entity> toRemoveMetal = new LinkedList<Entity>();
                for (Entity entity : particleTargets) {
                    if (!entity.isAlive()) {
                        toRemoveMetal.add(entity);
                    }
                    if (player == null) {
                        return;
                    }
                    if (player.getDistance(entity) > max) {
                        toRemoveMetal.add(entity);
                    }
                }

                for (Entity entity : toRemoveMetal) {
                    particleTargets.remove(entity);
                }
                toRemoveMetal.clear();

                // Remove items from burners
                LinkedList<PlayerEntity> toRemoveBurners = new LinkedList<PlayerEntity>();
                for (PlayerEntity entity : metalBurners) {
                    NetworkHelper.sendToServer(new GetCapabilitiesPacket(entity.getEntityId()));
                    AllomancyCapability capOther = AllomancyCapability.forPlayer(entity);
                    if (!entity.isAlive()) {
                        toRemoveBurners.add(entity);
                    }
                    if (player != null && player.getDistance(entity) > 30) {
                        toRemoveBurners.add(entity);
                    }
                    if (capOther.getMetalBurning(AllomancyCapability.COPPER) || !(capOther.getMetalBurning(AllomancyCapability.IRON) || capOther.getMetalBurning(AllomancyCapability.STEEL)
                            || capOther.getMetalBurning(AllomancyCapability.TIN) || capOther.getMetalBurning(AllomancyCapability.PEWTER) || capOther.getMetalBurning(AllomancyCapability.ZINC)
                            || capOther.getMetalBurning(AllomancyCapability.BRASS) || capOther.getMetalBurning(AllomancyCapability.BRONZE))) {
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

    private void checkBlocks(BlockPos pos) {
        BlockPos imBlock = pos.toImmutable();
        if (AllomancyUtils.isBlockMetal(this.mc.world.getBlockState(imBlock).getBlock())) {
            particleBlockTargets.add(imBlock);
        }
    }

    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    public void onKeyInput(InputEvent.KeyInputEvent event) {
        if (Registry.burn.isPressed()) {
            player = this.mc.player;
            AllomancyCapability cap;
            if (mc.currentScreen == null) {
                if (player == null || !this.mc.isGameFocused()) {
                    return;
                }
                cap = AllomancyCapability.forPlayer(player);
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
                    mc.displayGuiScreen(new MetalSelectScreen());
                }
            }
        }
    }

    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    public void onRenderGameOverlay(RenderGameOverlayEvent event) {

        if (event.isCancelable() || event.getType() != ElementType.EXPERIENCE) {
            return;
        }
        if (!this.mc.isGameFocused()) {
            return;
        }
        if (this.mc.currentScreen != null && !(this.mc.currentScreen instanceof ChatScreen) && !(this.mc.currentScreen instanceof MetalSelectScreen)) {
            return;
        }

        drawMetalOverlay();

    }

    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    public void onRenderGUIScreen(GuiScreenEvent.DrawScreenEvent event) {
        if (event.getGui() instanceof MetalSelectScreen && !event.isCancelable()) {
            drawMetalOverlay();
        }
    }

    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    public void onRenderWorldLast(RenderWorldLastEvent event) {
        player = this.mc.player;
        if (player == null || !player.isAlive()) {
            return;
        }


        cap = AllomancyCapability.forPlayer(player);

        if (cap.getAllomancyPower() < 0) {
            return;
        }

        double playerX = player.prevPosX + (player.posX - player.prevPosX) * event.getPartialTicks();
        double playerY = player.prevPosY + (player.posY - player.prevPosY) * event.getPartialTicks();
        double playerZ = player.prevPosZ + (player.posZ - player.prevPosZ) * event.getPartialTicks();

        // Iron and Steel lines
        if ((this.cap.getMetalBurning(AllomancyCapability.IRON) || this.cap.getMetalBurning(AllomancyCapability.STEEL))) {

            for (Entity entity : particleTargets) {
                AllomancyUtils.drawMetalLine(playerX, playerY, playerZ, entity.posX, entity.posY - 1.25 + entity.getHeight() / 2.0, entity.posZ, 1.5F, 0F, 0.6F, 1F);
            }

            for (BlockPos v : particleBlockTargets) {
                AllomancyUtils.drawMetalLine(playerX, playerY, playerZ, v.getX() + 0.5, v.getY() - 1.0, v.getZ() + 0.5, 1.5F, 0F, 0.6F, 1F);
            }
        }

        if ((cap.getMetalBurning(AllomancyCapability.BRONZE))) {
            for (PlayerEntity playerEntity : metalBurners) {
                AllomancyUtils.drawMetalLine(playerX, playerY, playerZ, playerEntity.posX, playerEntity.posY + 0.5, playerEntity.posZ, 2.5F, 0.5F, 0.15F, 0.15F);
            }
        }
    }

    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    public void onSound(PlaySoundEvent event) {
        double motionX, motionY, motionZ, magnitude;

        player = this.mc.player;
        ISound sound = event.getSound();
        if ((player == null) || (sound == null) || !player.isAlive()) {
            return;
        }

        magnitude = Math.sqrt(Math.pow((player.posX - sound.getX()), 2) + Math.pow((player.posY - sound.getY()), 2) + Math.pow((player.posZ - sound.getZ()), 2));
        if (((magnitude) > 20) || ((magnitude) < 1)) {
            return;
        }
        AllomancyCapability cap = AllomancyCapability.forPlayer(player);
        // Spawn sound particles
        if (cap.getMetalBurning(AllomancyCapability.TIN)) {
            //todo change how this logic works
            if (sound.getSoundLocation().toString().contains("step") || sound.getSoundLocation().toString().contains("entity") || sound.getSoundLocation().toString().contains("hostile") || sound.getSoundLocation().toString().contains(".big")
                    || sound.getSoundLocation().toString().contains("scream") || sound.getSoundLocation().toString().contains("bow")) {
                motionX = ((player.posX - (event.getSound().getX() + .5)) * -0.7) / magnitude;
                motionY = ((player.posY - (event.getSound().getY() + .2)) * -0.7) / magnitude;
                motionZ = ((player.posZ - (event.getSound().getZ() + .5)) * -0.7) / magnitude;
                Particle particle = new SoundParticle(player.world, player.posX + (Math.sin(Math.toRadians(player.getRotationYawHead())) * -.7d), player.posY + .2, player.posZ + (Math.cos(Math.toRadians(player.getRotationYawHead())) * .7d), motionX,
                        motionY, motionZ, sound);
                this.mc.particles.addEffect(particle);
            }

        }
    }
}
