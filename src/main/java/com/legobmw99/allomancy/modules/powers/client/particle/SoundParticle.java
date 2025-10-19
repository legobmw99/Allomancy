package com.legobmw99.allomancy.modules.powers.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SingleQuadParticle;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

public final class SoundParticle extends SingleQuadParticle {

    private SoundParticle(Level world,
                          double x,
                          double y,
                          double z,
                          double motionX,
                          double motionY,
                          double motionZ,
                          SoundSource typeIn,
                          SpriteSet spriteSet) {
        super((ClientLevel) world, x, y, z, motionX, motionY, motionZ, spriteSet.first());

        this.xd = motionX;
        this.yd = motionY + 0.009D;
        this.zd = motionZ;
        this.quadSize *= 1.5F;
        this.hasPhysics = false;
        setAlpha(1.0F);
        setLifetime(20);

        switch (typeIn) {
            case HOSTILE -> // red
                    setColor(1.0F, 0.15F, 0.15F);
            case PLAYERS -> // yellow
                    setColor(1.0F, 1.0F, 0.0F);
            case NEUTRAL -> // green
                    setColor(0.0F, 1.0F, 0.0F);
            default -> // neutral/blue
                    setColor(0.0F, 0.0F, 1.0F);
        }
    }


    @Override
    public void tick() {

        this.xo = this.x;
        this.yo = this.y;
        this.zo = this.z;
        if (this.age >= this.lifetime) {
            this.remove();
        } else {
            this.move(this.xd, this.yd, this.zd);
        }
        this.age++;

    }

    @Override
    protected Layer getLayer() {
        return Layer.OPAQUE;
    }

    public static class Provider implements ParticleProvider<SoundParticleData> {

        private final SpriteSet spriteSet;

        public Provider(SpriteSet spriteSet) {
            this.spriteSet = spriteSet;
        }


        @Override
        public @Nullable SingleQuadParticle createParticle(SoundParticleData particleOptions,
                                                           ClientLevel level,
                                                           double x,
                                                           double y,
                                                           double z,
                                                           double xSpeed,
                                                           double ySpeed,
                                                           double zSpeed,
                                                           RandomSource random) {
            return new SoundParticle(level, x, y, z, xSpeed, ySpeed, zSpeed, particleOptions.getSoundType(),
                                     this.spriteSet);
        }
    }

}
