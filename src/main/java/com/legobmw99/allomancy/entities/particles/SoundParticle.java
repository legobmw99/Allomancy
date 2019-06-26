package com.legobmw99.allomancy.entities.particles;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.ISound;
import net.minecraft.client.particle.IParticleRenderType;
import net.minecraft.client.particle.SpriteTexturedParticle;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class SoundParticle extends SpriteTexturedParticle {
    public SoundParticle(World world, double x, double y, double z, double motionX, double motionY, double motionZ, ISound sound) {
        super(world, x, y, z, motionX, motionY, motionZ);
        setSprite(Minecraft.getInstance().getItemRenderer().getItemModelMesher().getParticleIcon(new ItemStack(Items.RABBIT_FOOT)));
        this.motionX = motionX;
        this.motionY = motionY + 0.009D;
        this.motionZ = motionZ;
        this.particleScale *= 1.2F;
        this.canCollide = false;
        setAlphaF(1.0F);
        setMaxAge(20);
        String soundName = sound.getSoundLocation().toString();

        if (soundName.contains("step") || soundName.contains("pickup") || soundName.contains("break")) {
            // Blue
            this.particleGreen = 0;
            this.particleBlue = 1F;
            this.particleRed = 0;
        }

        if (soundName.contains("pig") || soundName.contains("rabbit") || soundName.contains("sheep") || soundName.contains("cow") || soundName.contains("cat") || soundName.contains("bat")
                || soundName.contains("horse") || soundName.contains("wolf") || soundName.contains("mooshroom") || soundName.contains("villager") || soundName.contains("golem")
                || soundName.contains("chicken")) {
            // Green
            this.particleGreen = 1;
            this.particleBlue = 0.25F;
            this.particleRed = 0;
        }

        if (soundName.contains("skeleton") || soundName.contains("hostile") || soundName.contains("zombie") || soundName.contains("slime") || soundName.contains("blaze")
                || soundName.contains("witch") || soundName.contains("guardian") || soundName.contains("magmacube") || soundName.contains("endermen") || soundName.contains("enderdragon")
                || soundName.contains("ghast") || soundName.contains("spider") || soundName.contains("silverfish") || soundName.contains("creeper") || soundName.contains("arrow")) {
            // Red
            this.particleGreen = 0.15F;
            this.particleBlue = 0.15F;
            this.particleRed = 1;
        }

    }


    @Override
    public void tick() {

        this.prevPosX = this.posX;
        this.prevPosY = this.posY;
        this.prevPosZ = this.posZ;
        if (this.age++ >= this.maxAge) {
            this.setExpired();
        }

        this.move(this.motionX, this.motionY, this.motionZ);

    }

    @Override
    public void renderParticle(BufferBuilder buffer, ActiveRenderInfo entityIn, float partialTicks, float rotationX, float rotationZ, float rotationYZ, float rotationXY, float rotationXZ) {
        super.renderParticle(buffer, entityIn, partialTicks, rotationX, rotationZ, rotationYZ, rotationXY, rotationXZ);
    }

    @Override
    public IParticleRenderType getRenderType() {
        return IParticleRenderType.TERRAIN_SHEET;
    }

}
