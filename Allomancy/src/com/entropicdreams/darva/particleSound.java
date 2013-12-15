package com.entropicdreams.darva;

import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.client.renderer.texture.SimpleTexture;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.texture.TextureObject;
import net.minecraft.util.Icon;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public class particleSound extends EntityFX {

	private ResourceLocation loc;
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
				
				loc = new ResourceLocation("allomancy","textures/soundicons/pigstep.png");
				
		break;
		default:
		}

	}
	@Override
	public void renderParticle(Tessellator par1Tessellator, float par2,
			float par3, float par4, float par5, float par6, float par7) {
		// TODO Auto-generated method stub
		
		Minecraft.getMinecraft().renderEngine.bindTexture(loc);
		
		super.renderParticle(par1Tessellator, par2, par3, par4, par5, par6, par7);
	}


}
