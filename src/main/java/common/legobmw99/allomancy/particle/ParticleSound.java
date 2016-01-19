package common.legobmw99.allomancy.particle;

import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ParticleSound extends EntityFX {

	private ResourceLocation loc = new ResourceLocation("allomancy", "textures/soundicons/unknown.png");
	private ResourceLocation oldLoc = new ResourceLocation(
			"textures/particle/particles.png");

	public ParticleSound(World world, double x, double y, double z,
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
				|| soundType.contains("step")) {
			this.loc = new ResourceLocation("allomancy",
					"textures/soundicons/passivestep.png");
		}

		if (soundType.contains("skeleton") || soundType.contains("zombie")
				|| soundType.contains("slime")
				|| soundType.contains("silverfish")
				|| soundType.contains("spider") || soundType.contains("witch")
				|| soundType.contains("enderman")
				|| soundType.contains("ghast")
				|| soundType.contains("silverfish")
				|| soundType.contains("creeper")) {
			this.loc = new ResourceLocation("allomancy",
					"textures/soundicons/aggiestep.png");
		}

		if (soundType.contains("random.bow")) {
			this.loc = new ResourceLocation("allomancy",
					"textures/soundicons/bowshot.png");
		}

		if (this.loc == null) {
			this.loc = new ResourceLocation("allomancy",
					"textures/soundicons/unknown.png");
		}
	}

	@Override
	public int getFXLayer() {
		return 3;
	}
	
	@Override
	public void func_180434_a(WorldRenderer wr, Entity e, float p3, float p4, float p5, float p6, float p7, float p8)
	{

	Minecraft.getMinecraft().getTextureManager().bindTexture(loc);
	   GlStateManager.enableBlend();
       GlStateManager.blendFunc(770, 771);
       wr.startDrawingQuads();
	float f6 = ((float)this.particleAge + p3) / (float)this.particleMaxAge;
	this.particleScale = this.particleScale * (1.0F - f6 * f6 * 0.5F);
	super.func_180434_a(wr, e, p3, p4, p5, p6, p7, p8);
    Tessellator.getInstance().draw();
    GlStateManager.disableBlend();
    GlStateManager.enableLighting();
	}

}
