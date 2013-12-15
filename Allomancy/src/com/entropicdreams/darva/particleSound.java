package com.entropicdreams.darva;

import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.client.renderer.texture.SimpleTexture;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.texture.TextureObject;
import net.minecraft.util.Icon;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public class particleSound extends EntityFX {

	public particleSound(World world, double x, double y,
			double z, double par8, double par10, double par12,
			String soundType) {
		super(world, x, y, z, par8, par10, par12);
		TextureObject map = null;
		
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
				
				map = Minecraft.getMinecraft().renderEngine.getTexture(new ResourceLocation("allomancy","/soundicons/pigstep.png"));
				
				this.setParticleIcon((Icon) map); 
		break;
		default:
		}

	}

	@Override
	public int getFXLayer() {
		// TODO Auto-generated method stub
		return 2;
	}

}
