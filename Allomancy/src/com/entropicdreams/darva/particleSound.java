package com.entropicdreams.darva;

import org.lwjgl.opengl.GL11;

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
			String soundType, double toX, double toY, double toZ) {
		super(world, x, y, z, par8, par10, par12);
		TextureObject map = null;
		
		
		this.motionX = (x - toX) *.02;
        this.motionY = (y - toY) *.002;
        this.motionZ *= (z - toZ) *.02;
        this.motionY += 0.009D;
        this.particleScale *= 1F;
        this.particleMaxAge = 25;
        this.noClip = true;
        this.particleTextureIndexX =0;
        this.particleTextureIndexY = 0;
        this.particleTextureJitterX =0;
        this.particleTextureJitterY = 0;
        
        this.setParticleTextureIndex(0);
        
		switch (soundType)
		{
		case "mob.pig.step":
				
				loc = new ResourceLocation("allomancy","textures/soundicons/pigstep.png");
				
		break;
		default:
		}

	}
	@Override
	public int getFXLayer() {
		// TODO Auto-generated method stub
		return 0;
	}
	@Override
	public void renderParticle(Tessellator par1Tessellator, float par2,
			float par3, float par4, float par5, float par6, float par7) {
		// TODO Auto-generated method stub
		
		Minecraft.getMinecraft().renderEngine.bindTexture(loc);
        
		float f6 = 0f;
        float f7 = 1;
        float f8 = 0f;
        float f9 = 1 ;
        float f10 = 0.1F * this.particleScale;

        if (this.particleIcon != null)
        {
            f6 = this.particleIcon.getMinU();
            f7 = this.particleIcon.getMaxU();
            f8 = this.particleIcon.getMinV();
            f9 = this.particleIcon.getMaxV();
        }

        float f11 = (float)(this.prevPosX + (this.posX - this.prevPosX) * (double)par2 - interpPosX);
        float f12 = (float)(this.prevPosY + (this.posY - this.prevPosY) * (double)par2 - interpPosY);
        float f13 = (float)(this.prevPosZ + (this.posZ - this.prevPosZ) * (double)par2 - interpPosZ);
        float f14 = 1.0F;
        par1Tessellator.setColorRGBA_F(this.particleRed * f14, this.particleGreen * f14, this.particleBlue * f14, this.particleAlpha);
        par1Tessellator.addVertexWithUV((double)(f11 - par3 * f10 - par6 * f10), (double)(f12 - par4 * f10), (double)(f13 - par5 * f10 - par7 * f10), (double)f7, (double)f9);
        par1Tessellator.addVertexWithUV((double)(f11 - par3 * f10 + par6 * f10), (double)(f12 + par4 * f10), (double)(f13 - par5 * f10 + par7 * f10), (double)f7, (double)f8);
        par1Tessellator.addVertexWithUV((double)(f11 + par3 * f10 + par6 * f10), (double)(f12 + par4 * f10), (double)(f13 + par5 * f10 + par7 * f10), (double)f6, (double)f8);
        par1Tessellator.addVertexWithUV((double)(f11 + par3 * f10 - par6 * f10), (double)(f12 - par4 * f10), (double)(f13 + par5 * f10 - par7 * f10), (double)f6, (double)f9);
        
	}


}
