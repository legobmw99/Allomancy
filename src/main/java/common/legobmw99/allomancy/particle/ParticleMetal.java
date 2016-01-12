package common.legobmw99.allomancy.particle;

import net.minecraft.client.particle.EntityFX;
import net.minecraft.world.World;

public class ParticleMetal extends EntityFX {

	public ParticleMetal(World par1World, double par2, double par4,
			double par6, double par8, double par10, double par12) {
		super(par1World, par2, par4 + 1, par6, par8, par10, par12);
		// TODO Auto-generated constructor stub

		this.particleScale = .5f;
		this.setParticleTextureIndex(97);
		this.motionX = par8;
		this.motionY = par10;
		this.motionZ = par12;
		this.particleMaxAge = 30;
		this.noClip = true;
	}

	@Override
	public void onUpdate() {
		this.prevPosX = this.posX;
		this.prevPosY = this.posY;
		this.prevPosZ = this.posZ;

		if (this.particleAge++ >= this.particleMaxAge) {
			this.setDead();
		}

		this.moveEntity(this.motionX, this.motionY, this.motionZ);

	}
}
