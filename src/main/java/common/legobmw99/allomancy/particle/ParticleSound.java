package common.legobmw99.allomancy.particle;

import net.minecraft.client.audio.ISound;
import net.minecraft.client.particle.Particle;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ParticleSound extends Particle {

    double entityX, entityY, entityZ;

    public ParticleSound(World world, double x, double y, double z, double motionX, double motionY, double motionZ, ISound sound) {

        super(world, x, y, z, motionX, motionY, motionZ);

        this.motionX = motionX;
        this.motionY = motionY + 0.009D;
        this.motionZ = motionZ;
        this.setParticleTextureIndex(64);
        this.particleScale *= 1.2F;
        this.particleMaxAge = 15;
        this.canCollide = false; // canCollide
        String soundName = sound.getSoundLocation().toString();
        System.out.println(soundName);
        if (soundName.contains("step")) {
            // Blue
            this.particleGreen = 0;
            this.particleBlue = 1F;
            this.particleRed = 0;
        }

        if (soundName.contains("pig") || soundName.contains("rabbit") || soundName.contains("sheep") || soundName.contains("cow") || soundName.contains("cat") || soundName.contains("bat") || soundName.contains("horse") || soundName.contains("wolf")
                || soundName.contains("mooshroom") || soundName.contains("villager") || soundName.contains("golem") || soundName.contains("chicken")) {
            // Green
            this.particleGreen = 1;
            this.particleBlue = 0.25F;
            this.particleRed = 0;
        }

        if (soundName.contains("skeleton") || soundName.contains("hostile") || soundName.contains("zombie") || soundName.contains("slime") || soundName.contains("silverfish") || soundName.contains("spider") || soundName.contains("blaze")
                || soundName.contains("witch") || soundName.contains("guardian") || soundName.contains("magmacube") || soundName.contains("endermen") || soundName.contains("enderdragon") || soundName.contains("ghast") || soundName.contains("spider")
                || soundName.contains("silverfish") || soundName.contains("creeper") || soundName.contains("bow")) {
            // Red
            this.particleGreen = 0.15F;
            this.particleBlue = 0.15F;
            this.particleRed = 1;
        }

    }

    @Override
    public void onUpdate() {
        if (((this.posX - entityX) < 1.7) && ((this.posY - entityY) < 2.5) && ((this.posZ - entityZ) < 1.7)) {
            this.setExpired();
        }

        this.prevPosX = this.posX;
        this.prevPosY = this.posY;
        this.prevPosZ = this.posZ;

        if (this.particleAge++ >= this.particleMaxAge) {
            this.setExpired();
        }

        this.move(this.motionX, this.motionY, this.motionZ);

    }
    /*
     * Old, antequated code which allowed for custom textures. May come back one day
     * @Override public int getFXLayer() { return 3; }
     * @Override public void func_180434_a(WorldRenderer wr, Entity e, float p3, float p4, float p5, float p6, float p7, float p8) { Minecraft.getMinecraft().getTextureManager().bindTexture(loc); wr.startDrawingQuads(); super.func_180434_a(wr, e, p3, p4,
     * p5, p6, p7, p8); Tessellator.getInstance().draw(); GlStateManager.disableBlend(); GlStateManager.enableLighting(); }
     */

}
