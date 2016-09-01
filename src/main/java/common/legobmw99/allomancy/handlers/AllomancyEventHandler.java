package common.legobmw99.allomancy.handlers;

import java.util.LinkedList;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.GuiIngame;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.renderer.texture.ITextureObject;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.PlaySoundAtEntityEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.world.BlockEvent.BreakEvent;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerRespawnEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import org.lwjgl.opengl.GL11;
import org.lwjgl.util.Point;

import common.legobmw99.allomancy.Allomancy;
import common.legobmw99.allomancy.common.AllomancyCapabilities;
import common.legobmw99.allomancy.common.Registry;
import common.legobmw99.allomancy.entity.EntityGoldNugget;
import common.legobmw99.allomancy.network.packets.AllomancyCapabiltiesPacket;
import common.legobmw99.allomancy.network.packets.BecomeMistbornPacket;
import common.legobmw99.allomancy.network.packets.ChangeEmotionPacket;
import common.legobmw99.allomancy.network.packets.SelectMetalPacket;
import common.legobmw99.allomancy.network.packets.UpdateBurnPacket;
import common.legobmw99.allomancy.particle.ParticleMetal;
import common.legobmw99.allomancy.particle.ParticleSound;
import common.legobmw99.allomancy.util.AllomancyConfig;
import common.legobmw99.allomancy.util.vector3;

public class AllomancyEventHandler {

    private Entity pointedEntity;
    private Minecraft mc;
    private ResourceLocation meterLoc;
    private AllomancyCapabilities cap;
    private int animationCounter = 0;
    private int currentFrame = 0;


    private Point[] Frames = { new Point(72, 0), new Point(72, 4),
            new Point(72, 8), new Point(72, 12) };

    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public void onBlockBreak(BreakEvent event) {
        if(Allomancy.XPC.isBlockMetal(event.getState())){
            Allomancy.XPC.particleBlockTargets.clear();
        }
    }
    

    @SubscribeEvent
    public void onDamage(LivingHurtEvent event) {
        //Increase outgoing damage for pewter burners
        if (event.getSource().getSourceOfDamage() instanceof EntityPlayerMP) {
            EntityPlayerMP source = (EntityPlayerMP) event.getSource()
                    .getSourceOfDamage();
            AllomancyCapabilities cap = AllomancyCapabilities.forPlayer(source);

            if (cap.MetalBurning[AllomancyCapabilities.matPewter]) {
                event.setAmount(event.getAmount() + 2);
            }
        }
        //Reduce incoming damage for pewter burners
        if (event.getEntityLiving() instanceof EntityPlayerMP) {
            AllomancyCapabilities cap = AllomancyCapabilities.forPlayer(event.getEntityLiving());
            if (cap.MetalBurning[AllomancyCapabilities.matPewter]) {
                event.setAmount(event.getAmount() - 2);
                //Note that they took damage, will come in to play if they stop burning
                cap.damageStored++;
            }
        }
    }

    
     @SubscribeEvent
        public void onAttachCapability(AttachCapabilitiesEvent.Entity event)
        {
            if(event.getEntity() instanceof EntityPlayer)
            {
                event.addCapability(new ResourceLocation(Allomancy.MODID, "Allomancy_Data"), new AllomancyCapabilities(((EntityPlayer) event.getEntity())));
            }
        }
        
    


    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public void onKeyInput(InputEvent.KeyInputEvent event) {
        if (Registry.changeGroup.isPressed()) {

            EntityPlayerSP player;
            player = Minecraft.getMinecraft().thePlayer;
            Minecraft mc = FMLClientHandler.instance().getClient();
            if (mc.currentScreen == null) {
                if ((player == null)
                        || !Minecraft.getMinecraft().inGameHasFocus) {
                    return;
                }
                AllomancyCapabilities cap = AllomancyCapabilities.forPlayer(player);
                Registry.network.sendToServer(new SelectMetalPacket(cap
                        .getSelected() + 1));
                cap.selected = (cap.getSelected() + 1);
            }
        }
        if (Registry.burnFirst.isPressed()) {
            EntityPlayerSP player;
            player = Minecraft.getMinecraft().thePlayer;
            AllomancyCapabilities cap;
            Minecraft mc = FMLClientHandler.instance().getClient();
            if (mc.currentScreen == null) {
                if (player == null) {
                    return;
                }
                cap = AllomancyCapabilities.forPlayer(player);
                switch (cap.getSelected()) {
                case 1:
                    // toggle iron.
                    Registry.network.sendToServer(new UpdateBurnPacket(
                            AllomancyCapabilities.matIron,
                            !cap.MetalBurning[AllomancyCapabilities.matIron]));

                    if (cap.MetalAmounts[AllomancyCapabilities.matIron] > 0) {
                        cap.MetalBurning[AllomancyCapabilities.matIron] = !cap.MetalBurning[AllomancyCapabilities.matIron];
                    }
                    //play a sound effect
                    if(cap.MetalBurning[AllomancyCapabilities.matIron]){
                        //Minecraft.getMinecraft().thePlayer.playSound("fire.ignite", 1, 5);
                    }else{
                        //Minecraft.getMinecraft().thePlayer.playSound("random.fizz", 1, 4);
                    }
                    break;
                case 2:
                    // toggle Tin.

                    Registry.network.sendToServer(new UpdateBurnPacket(
                            AllomancyCapabilities.matTin,
                            !cap.MetalBurning[AllomancyCapabilities.matTin]));
                    if (cap.MetalAmounts[AllomancyCapabilities.matTin] > 0) {
                        cap.MetalBurning[AllomancyCapabilities.matTin] = !cap.MetalBurning[AllomancyCapabilities.matTin];
                    }
                    //play a sound effect
                    if(cap.MetalBurning[AllomancyCapabilities.matTin]){
                        //Minecraft.getMinecraft().thePlayer.playSound("fire.ignite", 1, 5);
                    }else{
                        //Minecraft.getMinecraft().thePlayer.playSound("random.fizz", 1, 4);
                    }
                    break;
                case 3:
                    // toggle Zinc.

                    Registry.network.sendToServer(new UpdateBurnPacket(
                            AllomancyCapabilities.matZinc,
                            !cap.MetalBurning[AllomancyCapabilities.matZinc]));
                    if (cap.MetalAmounts[AllomancyCapabilities.matZinc] > 0) {
                        cap.MetalBurning[AllomancyCapabilities.matZinc] = !cap.MetalBurning[AllomancyCapabilities.matZinc];
                    }
                    //play a sound effect
                    if(cap.MetalBurning[AllomancyCapabilities.matZinc]){
                        //Minecraft.getMinecraft().thePlayer.playSound("fire.ignite", 1, 5);
                    }else{
                        //Minecraft.getMinecraft().thePlayer.playSound("random.fizz", 1, 4);
                    }
                    break;
                case 4:
                    // toggle Copper.

                    Registry.network.sendToServer(new UpdateBurnPacket(
                            AllomancyCapabilities.matCopper,
                            !cap.MetalBurning[AllomancyCapabilities.matCopper]));
                    if (cap.MetalAmounts[AllomancyCapabilities.matCopper] > 0) {
                        cap.MetalBurning[AllomancyCapabilities.matCopper] = !cap.MetalBurning[AllomancyCapabilities.matCopper];
                    }
                    //play a sound effect
                    if(cap.MetalBurning[AllomancyCapabilities.matCopper]){
                        //Minecraft.getMinecraft().thePlayer.playSound("fire.ignite", 1, 5);
                    }else{
                        //Minecraft.getMinecraft().thePlayer.playSound("random.fizz", 1, 4);
                    }
                    break;
                default:
                    break;
                }
            }
        }
        if (Registry.burnSecond.isPressed()) {
            EntityPlayerSP player;
            player = Minecraft.getMinecraft().thePlayer;
            AllomancyCapabilities cap;
            Minecraft mc = FMLClientHandler.instance().getClient();
            if (mc.currentScreen == null) {
                if (player == null) {
                    return;
                }

                cap = AllomancyCapabilities.forPlayer(player);
                switch (cap.getSelected()) {
                case 1:
                    // toggle Steel.

                    Registry.network.sendToServer(new UpdateBurnPacket(
                            AllomancyCapabilities.matSteel,
                            !cap.MetalBurning[AllomancyCapabilities.matSteel]));
                    if (cap.MetalAmounts[AllomancyCapabilities.matSteel] > 0) {
                        cap.MetalBurning[AllomancyCapabilities.matSteel] = !cap.MetalBurning[AllomancyCapabilities.matSteel];
                    }
                    //play a sound effect
                    if(cap.MetalBurning[AllomancyCapabilities.matSteel]){
                        //Minecraft.getMinecraft().thePlayer.playSound("fire.ignite", 1, 5);
                    }else{
                        //Minecraft.getMinecraft().thePlayer.playSound("random.fizz", 1, 4);
                    }
                    break;
                case 2:
                    // toggle Pewter.

                    Registry.network.sendToServer(new UpdateBurnPacket(
                            AllomancyCapabilities.matPewter,
                            !cap.MetalBurning[AllomancyCapabilities.matPewter]));
                    if (cap.MetalAmounts[AllomancyCapabilities.matPewter] > 0) {
                        cap.MetalBurning[AllomancyCapabilities.matPewter] = !cap.MetalBurning[AllomancyCapabilities.matPewter];
                    }
                    //play a sound effect
                    if(cap.MetalBurning[AllomancyCapabilities.matPewter]){
                        //Minecraft.getMinecraft().thePlayer.playSound("fire.ignite", 1, 5);
                    }else{
                        //Minecraft.getMinecraft().thePlayer.playSound("random.fizz", 1, 4);
                    }
                    break;
                case 3:
                    // toggle Brass.

                    Registry.network.sendToServer(new UpdateBurnPacket(
                            AllomancyCapabilities.matBrass,
                            !cap.MetalBurning[AllomancyCapabilities.matBrass]));
                    if (cap.MetalAmounts[AllomancyCapabilities.matBrass] > 0) {
                        cap.MetalBurning[AllomancyCapabilities.matBrass] = !cap.MetalBurning[AllomancyCapabilities.matBrass];
                    }
                    //play a sound effect
                    if(cap.MetalBurning[AllomancyCapabilities.matBrass]){
                        //Minecraft.getMinecraft().thePlayer.playSound("fire.ignite", 1, 5);
                    }else{
                        //Minecraft.getMinecraft().thePlayer.playSound("random.fizz", 1, 4);
                    }
                    break;
                case 4:
                    // toggle Bronze.

                    Registry.network.sendToServer(new UpdateBurnPacket(
                            AllomancyCapabilities.matBronze,
                            !cap.MetalBurning[AllomancyCapabilities.matBronze]));
                    if (cap.MetalAmounts[AllomancyCapabilities.matBronze] > 0) {
                        cap.MetalBurning[AllomancyCapabilities.matBronze] = !cap.MetalBurning[AllomancyCapabilities.matBronze];
                    }
                    //play a sound effect
                    if(cap.MetalBurning[AllomancyCapabilities.matBronze]){
                        //Minecraft.getMinecraft().thePlayer.playSound("fire.ignite", 1, 5);
                    }else{
                        //.getMinecraft().thePlayer.playSound("random.fizz", 1, 4);
                    }
                    break;
                default:
                    break;
                }
            }
        }
    }

    @SubscribeEvent
    public void onPlayerLogin(PlayerLoggedInEvent event) {
        if (event.player instanceof EntityPlayerMP) {
            AllomancyCapabilities cap = AllomancyCapabilities.forPlayer(event.player);
            for (int i = 0; i < 7; i++) {
                cap.MetalBurning[i] = false;
            }
            Registry.network.sendTo(new AllomancyCapabiltiesPacket(cap),
                    (EntityPlayerMP) event.player);
            if (cap.isMistborn == true) {
                Registry.network.sendTo(new BecomeMistbornPacket(),(EntityPlayerMP) event.player);

                if (event.player.worldObj.isRemote) {
                    cap.isMistborn = true;
                }
            }
        }
    }

    @SubscribeEvent
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        AllomancyCapabilities cap = AllomancyCapabilities.forPlayer(event.player);
        for (int i = 0; i < 8; i++) {
            cap.MetalAmounts[i] = 0;
        }
        NBTTagCompound old = event.player.getEntityData();
        if (old.hasKey("Allomancy_Data")) {
            event.player.getEntityData().setTag("Allomancy_Data",
                    old.getCompoundTag("Allomancy_Data"));
        }
    }

    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public void onRenderGameOverlay(RenderGameOverlayEvent event) {
        if (event.isCancelable() || event.getType() != ElementType.EXPERIENCE) {
            return;
        }

        this.mc = Minecraft.getMinecraft();
        this.meterLoc = new ResourceLocation("allomancy",
                "textures/overlay/meter.png");

        ParticleMetal particle;
        if (!Minecraft.getMinecraft().inGameHasFocus) {
            return;
        }
        if (FMLClientHandler.instance().getClient().currentScreen != null) {
            return;
        }
        EntityPlayerSP player;
        player = this.mc.thePlayer;
        if (player == null) {
            return;
        }

        this.animationCounter++;

        cap = AllomancyCapabilities.forPlayer(player);
        // left hand side.
        int ironY, steelY, tinY, pewterY;
        // right hand side
        int copperY, bronzeY, zincY, brassY;
        int renderX,renderY = 0;
        ScaledResolution res = new ScaledResolution(this.mc);

        //Set the offsets of the overlay based on config
        switch (AllomancyConfig.overlayPosition){
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
            renderY = res.getScaledHeight() - 30;
            break;
        case 3:
            renderX = 5;
            renderY = res.getScaledHeight() - 30;
            break;
        default:
             renderX = 5;
             renderY = 10;
             break;
        
        }
        if (!cap.isMistborn) {
            return;
        }
        GuiIngame gig = new GuiIngame(Minecraft.getMinecraft());
        Minecraft.getMinecraft().renderEngine.bindTexture(this.meterLoc);
        ITextureObject obj;
        obj = Minecraft.getMinecraft().renderEngine.getTexture(this.meterLoc);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, obj.getGlTextureId());
        
        switch (cap.getSelected()) {
        case 0:
            break;
        case 1:
            gig.drawTexturedModalRect(renderX-2, renderY-2, 54, 0, 16, 24);
            break;
        case 2:
            gig.drawTexturedModalRect(renderX+23, renderY-2, 54, 0, 16, 24);
            break;
        case 3:
            gig.drawTexturedModalRect(renderX+48, renderY-2, 54, 0, 16, 24);
            break;
        case 4:
            gig.drawTexturedModalRect(renderX+73, renderY-2, 54, 0, 16, 24);
            break;

        }

        ironY = 9 - this.cap.MetalAmounts[AllomancyCapabilities.matIron];
        gig.drawTexturedModalRect(renderX+1, renderY + 5 + ironY, 7, 1 + ironY, 3, 10 - ironY);

        steelY = 9 - this.cap.MetalAmounts[AllomancyCapabilities.matSteel];
        gig.drawTexturedModalRect(renderX+8, renderY + 5 + steelY, 13, 1 + steelY, 3,
                10 - steelY);

        tinY = 9 - this.cap.MetalAmounts[AllomancyCapabilities.matTin];
        gig.drawTexturedModalRect(renderX+26, renderY + 5 + tinY, 19, 1 + tinY, 3, 10 - tinY);

        pewterY = 9 - this.cap.MetalAmounts[AllomancyCapabilities.matPewter];
        gig.drawTexturedModalRect(renderX+33, renderY + 5 + pewterY, 25, 1 + pewterY, 3,
                10 - pewterY);
        
        zincY = 9 - this.cap.MetalAmounts[AllomancyCapabilities.matZinc];
        gig.drawTexturedModalRect(renderX+51, renderY + 5 + zincY, 43, 1 + zincY, 3, 10 - zincY);

        brassY = 9 - this.cap.MetalAmounts[AllomancyCapabilities.matBrass];
        gig.drawTexturedModalRect(renderX+58, renderY + 5 + brassY, 49, 1 + brassY, 3,
                10 - brassY);
        
        copperY = 9 - this.cap.MetalAmounts[AllomancyCapabilities.matCopper];
        gig.drawTexturedModalRect(renderX+76, renderY + 5 + copperY, 31, 1 + copperY, 3,
                10 - copperY);

        bronzeY = 9 - this.cap.MetalAmounts[AllomancyCapabilities.matBronze];
        gig.drawTexturedModalRect(renderX+83, renderY + 5 + bronzeY, 37, 1 + bronzeY, 3,
                10 - bronzeY);


        // Draw the gauges second, so that highlights and decorations show over the bar.
        gig.drawTexturedModalRect(renderX, renderY, 0, 0, 5, 20);
        gig.drawTexturedModalRect(renderX+7, renderY, 0, 0, 5, 20);

        gig.drawTexturedModalRect(renderX+25, renderY, 0, 0, 5, 20);
        gig.drawTexturedModalRect(renderX+32, renderY, 0, 0, 5, 20);

        gig.drawTexturedModalRect(renderX+50, renderY, 0, 0, 5, 20);
        gig.drawTexturedModalRect(renderX+57, renderY, 0, 0, 5, 20);

        gig.drawTexturedModalRect(renderX+75, renderY, 0, 0, 5, 20);
        gig.drawTexturedModalRect(renderX+82, renderY, 0, 0, 5, 20);

        if (this.cap.MetalBurning[AllomancyCapabilities.matIron]) {
            gig.drawTexturedModalRect(renderX, renderY + 5 + ironY,
                    this.Frames[this.currentFrame].getX(),
                    this.Frames[this.currentFrame].getY(), 5, 3);
        }
        if (this.cap.MetalBurning[AllomancyCapabilities.matSteel]) {
            gig.drawTexturedModalRect(renderX+7, renderY + 5 + steelY,
                    this.Frames[this.currentFrame].getX(),
                    this.Frames[this.currentFrame].getY(), 5, 3);
        }
        if (this.cap.MetalBurning[AllomancyCapabilities.matTin]) {
            gig.drawTexturedModalRect(renderX+25, renderY + 5 + tinY,
                    this.Frames[this.currentFrame].getX(),
                    this.Frames[this.currentFrame].getY(), 5, 3);
        }
        if (this.cap.MetalBurning[AllomancyCapabilities.matPewter]) {
            gig.drawTexturedModalRect(renderX+32, renderY + 5 + pewterY,
                    this.Frames[this.currentFrame].getX(),
                    this.Frames[this.currentFrame].getY(), 5, 3);
        }
        if (this.cap.MetalBurning[AllomancyCapabilities.matZinc]) {
            gig.drawTexturedModalRect(renderX+50, renderY + 5 + zincY,
                    this.Frames[this.currentFrame].getX(),
                    this.Frames[this.currentFrame].getY(), 5, 3);
        }
        if (this.cap.MetalBurning[AllomancyCapabilities.matBrass]) {
            gig.drawTexturedModalRect(renderX+57, renderY + 5 + brassY,
                    this.Frames[this.currentFrame].getX(),
                    this.Frames[this.currentFrame].getY(), 5, 3);
        }
        if (this.cap.MetalBurning[AllomancyCapabilities.matCopper]) {
            gig.drawTexturedModalRect(renderX+75, renderY + 5 + copperY,
                    this.Frames[this.currentFrame].getX(),
                    this.Frames[this.currentFrame].getY(), 5, 3);
        }
        if (this.cap.MetalBurning[AllomancyCapabilities.matBronze]) {
            gig.drawTexturedModalRect(renderX+82, renderY + 5 + bronzeY,
                    this.Frames[this.currentFrame].getX(),
                    this.Frames[this.currentFrame].getY(), 5, 3);
        }


        if (this.animationCounter > 6) // Draw the burning symbols...
        {
            this.animationCounter = 0;
            this.currentFrame++;
            if (this.currentFrame > 3) {
                this.currentFrame = 0;
            }
        }
        
        double motionX, motionY, motionZ;
        //Spawn in metal particles
        if ((this.cap.MetalBurning[AllomancyCapabilities.matIron] || this.cap.MetalBurning[AllomancyCapabilities.matSteel]) && (event instanceof RenderGameOverlayEvent.Post)){
            for (Entity entity : Allomancy.XPC.particleTargets) {
                motionX = ((player.posX - entity.posX) * -1) * .03;
                motionY = (((player.posY - entity.posY + 1.2) * -1) * .03) + .021;
                motionZ = ((player.posZ - entity.posZ) * -1) * .03;
                particle = new ParticleMetal(player.worldObj,
                        player.posX
                                - (Math.sin(Math.toRadians(player
                                        .getRotationYawHead())) * .7d),
                        player.posY - .2, player.posZ
                                + (Math.cos(Math.toRadians(player
                                        .getRotationYawHead())) * .7d),
                        motionX, motionY, motionZ);
                Minecraft.getMinecraft().effectRenderer.addEffect(particle);
            }
            for (vector3 v : Allomancy.XPC.particleBlockTargets) {
                motionX = ((player.posX - (v.X + .5)) * -1) * .03;
                motionY = (((player.posY - (v.Y + .2)) * -1) * .03);
                motionZ = ((player.posZ - (v.Z + .5)) * -1) * .03;
                particle = new ParticleMetal(player.worldObj,
                        player.posX
                                - (Math.sin(Math.toRadians(player
                                        .getRotationYawHead())) * .7d),
                        player.posY - .7, player.posZ
                                + (Math.cos(Math.toRadians(player
                                        .getRotationYawHead())) * .7d),
                        motionX, motionY, motionZ);
                Minecraft.getMinecraft().effectRenderer.addEffect(particle);
            }
            Allomancy.XPC.particleBlockTargets.clear();
        }
    }

    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public void onSound(PlaySoundAtEntityEvent event) {
        double motionX, motionY, motionZ;
        EntityPlayerSP player;
        player = Minecraft.getMinecraft().thePlayer;
        if ((player == null) || (event.getEntity() == null) || ((player.getDistanceToEntity(event.getEntity()) > 20) || (player.getDistanceToEntity(event.getEntity()) < .5))) {
            return;
        }
        AllomancyCapabilities cap = AllomancyCapabilities.forPlayer(player);
        //Spawn sound particles
                if (cap.MetalBurning[AllomancyCapabilities.matTin]) {
                    if (event.getSound().toString().contains("step") 
                            || event.getSound().toString().contains("mob")
                            || event.getSound().toString().contains("hostile")
                            || event.getSound().toString().contains(".big")
                            || event.getSound().toString().contains("scream")
                            || event.getSound().toString().contains("bow")) {
                        motionX = ((player.posX - (event.getEntity().posX + .5)) * -0.7)/ player.getDistanceToEntity(event.getEntity());
                        motionY = (((player.posY - (event.getEntity().posY + .2)) * -0.7)/ player.getDistanceToEntity(event.getEntity()));
                        motionZ = ((player.posZ - (event.getEntity().posZ + .5)) * -0.7) /player.getDistanceToEntity(event.getEntity());
                        Particle particle = new ParticleSound(player.worldObj,
                                player.posX + (Math.sin(Math.toRadians(player.getRotationYawHead())) * -.7d),
                                player.posY + .2, 
                                player.posZ + (Math.cos(Math.toRadians(player.getRotationYawHead())) * .7d),
                                motionX, motionY, motionZ, event);
                        Minecraft.getMinecraft().effectRenderer.addEffect(particle);
                    }

                }
            }
    
}
