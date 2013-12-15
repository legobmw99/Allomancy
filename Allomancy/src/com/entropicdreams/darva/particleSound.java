package com.entropicdreams.darva;

import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.client.renderer.texture.SimpleTexture;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.util.Icon;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public class particleSound extends EntityFX {

	public particleSound(World world, double x, double y,
			double z, double par8, double par10, double par12,
			String soundType) {
		super(world, x, y, z, par8, par10, par12);
		SimpleTexture map = null;
		
		this.motionX *= 0.009999999776482582D;
        this.motionY *= 0.009999999776482582D;
        this.motionZ *= 0.009999999776482582D;
        this.motionY += 0.1D;
        this.particleScale *= 0.75F;
        this.particleMaxAge = 16;
        this.noClip = true;
		switch (soundType)
		{
		case "mob.pig.step":
				if (Minecraft.getMinecraft().renderEngine.loadTexture(new ResourceLocation("allomancy", "pigstep.png" ), map))
				this.setParticleIcon((Icon) map); 
		break;
		default:
		}

	}

}
