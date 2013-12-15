package com.entropicdreams.darva;

import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.util.Icon;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public class particleSound extends EntityFX {

	public particleSound(World world, double x, double y,
			double z, double par8, double par10, double par12,
			String soundType) {
		super(world, x, y, z, par8, par10, par12);
		
		
		
		switch (soundType)
		{
		case "mob.pig.step":
			this.setParticleTextureIndex(par1);
		break;
		default:
		}

	}

}
