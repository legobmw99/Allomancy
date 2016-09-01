package common.legobmw99.allomancy.particle;

import net.minecraft.client.particle.Particle;
import net.minecraft.world.World;

public class ParticleMetal extends Particle {

	public ParticleMetal(World world, double x, double y,double z, double motionX, double motionY, double motionZ) {
		
		super(world, x, y + 1, z, motionX, motionY, motionZ);

		this.particleScale = .5f;
		this.setParticleTextureIndex(97);
		this.motionX = motionX;
		this.motionY = motionY;
		this.motionZ = motionZ;
		this.particleMaxAge = 30;
	}

	@Override
	public void onUpdate() {
		this.prevPosX = this.posX;
		this.prevPosY = this.posY;
		this.prevPosZ = this.posZ;

		if (this.particleAge++ >= this.particleMaxAge) {
			this.setExpired();
		}

		this.moveEntity(this.motionX, this.motionY, this.motionZ);

	}
}
