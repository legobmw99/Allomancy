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

        this.xd = motionX;
        this.yd = motionY + 0.009D;
        this.zd = motionZ;
        this.quadSize *= 1.5F;
        this.hasPhysics = false;
        setAlpha(1.0F);
        setLifetime(20);

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

        this.xo = this.x;
        this.yo = this.y;
        this.zo = this.z;
        if (this.age++ >= this.lifetime) {
            this.remove();
        } else {
            this.move(this.xd, this.yd, this.zd);
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

        public Particle createParticle(SoundParticleData data, ClientWorld worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
            SoundParticle sp = new SoundParticle(worldIn, x, y, z, xSpeed, ySpeed, zSpeed, data.getSoundType());
            sp.pickSprite(this.spriteSet);
            return sp;
        }
    }

}
