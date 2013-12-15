package com.entropicdreams.darva;

import net.minecraft.client.particle.EntityFX;
import net.minecraft.world.World;

public class particleSound extends EntityFX {

	public particleSound(World world, double x, double y,
			double z, double par8, double par10, double par12,
			String soundType) {
		super(world, x, y, z, par8, par10, par12);
		
		switch (soundType)
		{
		case "mob.pig.step":
			this.setParticleIcon(IconRegister.registerIcon("allomancy:"+"soundcion/step");
		break;
		default:
		}

	}

	@Override
	public void setParticleTextureIndex(int par1) {
		// TODO Auto-generated method stub
		super.setParticleTextureIndex(par1);
	}

}
