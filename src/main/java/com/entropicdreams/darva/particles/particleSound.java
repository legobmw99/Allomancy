package com.entropicdreams.darva.particles;

import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

import org.lwjgl.opengl.GL11;

public class particleSound extends EntityFX {

	private ResourceLocation loc;
	private ResourceLocation oldLoc = new ResourceLocation(
			"textures/particle/particles.png");

	public particleSound(World world, double x, double y, double z,
			double par8, double par10, double par12, String soundType,
			double toX, double toY, double toZ) {
		super(world, x, y, z, par8, par10, par12);

		this.motionX = ((x - toX) * -1) * .02;
		this.motionY = ((y - toY) * -1) * .02;
		this.motionZ = ((z - toZ) * -1) * .02;
		this.motionY += 0.009D;
		this.particleScale *= 1F;
		this.particleMaxAge = 25;
		this.noClip = true;

		if (soundType.contains("pig") || soundType.contains("sheep")
				|| soundType.contains("cow") || soundType.contains("horse")
				|| soundType.contains("mooshroom")
				|| soundType.contains("villager")
				|| soundType.contains("golem") || soundType.contains("chicken")
				|| soundType.contains("step"))
			loc = new ResourceLocation("allomancy",
					"textures/soundicons/passivestep.png");

		if (soundType.contains("skeleton") || soundType.contains("zombie")
				|| soundType.contains("slime")
				|| soundType.contains("silverfish")
				|| soundType.contains("spider") || soundType.contains("witch")
				|| soundType.contains("enderman")
				|| soundType.contains("ghast")
				|| soundType.contains("silverfish")
				|| soundType.contains("creeper"))
			loc = new ResourceLocation("allomancy",
					"textures/soundicons/aggiestep.png");

		if (soundType.contains("random.bow"))
			loc = new ResourceLocation("allomancy",
					"textures/soundicons/bowshot.png");

		if (loc == null)
			loc = new ResourceLocation("allomancy",
					"textures/soundicons/unknown.png");
	}

	@Override
	public int getFXLayer() {
		// TODO Auto-generated method stub
		return 3;
	}

	@Override
	public void renderParticle(Tessellator par1Tessellator, float par2,
			float par3, float par4, float par5, float par6, float par7) {
		// TODO Auto-generated method stub

		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		GL11.glDepthMask(false);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GL11.glAlphaFunc(GL11.GL_GREATER, 0.003921569F);
		par1Tessellator.startDrawingQuads();

		Minecraft.getMinecraft().renderEngine.bindTexture(loc);

		float f6 = 0f;
		float f7 = 1;
		float f8 = 0f;
		float f9 = 1;
		float f10 = 0.1F * this.particleScale;

		if (this.particleIcon != null) {
			f6 = this.particleIcon.getMinU();
			f7 = this.particleIcon.getMaxU();
			f8 = this.particleIcon.getMinV();
			f9 = this.particleIcon.getMaxV();
		}

		float f11 = (float) (this.prevPosX + (this.posX - this.prevPosX)
				* par2 - interpPosX);
		float f12 = (float) (this.prevPosY + (this.posY - this.prevPosY)
				* par2 - interpPosY);
		float f13 = (float) (this.prevPosZ + (this.posZ - this.prevPosZ)
				* par2 - interpPosZ);
		float f14 = 1.0F;
		par1Tessellator.setColorRGBA_F(this.particleRed * f14,
				this.particleGreen * f14, this.particleBlue * f14,
				this.particleAlpha);
		par1Tessellator.addVertexWithUV(
				f11 - par3 * f10 - par6 * f10, f12 - par4
						* f10, f13 - par5 * f10 - par7 * f10,
				f7, f9);
		par1Tessellator.addVertexWithUV(
				f11 - par3 * f10 + par6 * f10, f12 + par4
						* f10, f13 - par5 * f10 + par7 * f10,
				f7, f8);
		par1Tessellator.addVertexWithUV(
				f11 + par3 * f10 + par6 * f10, f12 + par4
						* f10, f13 + par5 * f10 + par7 * f10,
				f6, f8);
		par1Tessellator.addVertexWithUV(
				f11 + par3 * f10 - par6 * f10, f12 - par4
						* f10, f13 + par5 * f10 - par7 * f10,
				f6, f9);

		par1Tessellator.draw();
		GL11.glDisable(GL11.GL_BLEND);
		GL11.glDepthMask(true);
		GL11.glAlphaFunc(GL11.GL_GREATER, 0.1F);

		// Minecraft.getMinecraft().renderEngine.bindTexture(oldLoc);
	}

}
