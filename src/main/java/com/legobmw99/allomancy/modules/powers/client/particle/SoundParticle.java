package com.legobmw99.allomancy.modules.powers.client.particle;

import net.minecraft.client.particle.*;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.SoundCategory;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class SoundParticle extends SpriteTexturedParticle {
    public SoundParticle(World world, double x, double y, double z, double motionX, double motionY, double motionZ, SoundCategory typeIn) {
        super((ClientWorld) world, x, y, z, motionX, motionY, motionZ);

        this.motionX = motionX;
        this.motionY = motionY + 0.009D;
        this.motionZ = motionZ;
        this.particleScale *= 1.5F;
        this.canCollide = false;
        setAlphaF(1.0F);
        setMaxAge(20);

        switch (typeIn) {
            case HOSTILE: // red
                setColor(1F, 0.15F, 0.15F);
                break;
            case PLAYERS: // yellow
                setColor(1F, 1F, 0F);
                break;
            case NEUTRAL: // green
                setColor(0F, 1F, 0F);
                break;
            default: // neutral/blue
                setColor(0F, 0F, 1F);
        }
    }


    @Override
    public void tick() {

        this.prevPosX = this.posX;
        this.prevPosY = this.posY;
        this.prevPosZ = this.posZ;
        if (this.age++ >= this.maxAge) {
            this.setExpired();
        } else {
            this.move(this.motionX, this.motionY, this.motionZ);
        }

    }

    @Override
    public IParticleRenderType getRenderType() {
        return IParticleRenderType.PARTICLE_SHEET_OPAQUE;
    }


    @OnlyIn(Dist.CLIENT)
    public static class Factory implements IParticleFactory<SoundParticleData> {
        private final IAnimatedSprite spriteSet;

        public Factory(IAnimatedSprite sprite) {
            this.spriteSet = sprite;
        }

        public Particle makeParticle(SoundParticleData data, ClientWorld worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
            SoundParticle sp = new SoundParticle(worldIn, x, y, z, xSpeed, ySpeed, zSpeed, data.getSoundType());
            sp.selectSpriteRandomly(this.spriteSet);
            return sp;
        }
    }

}
