package common.legobmw99.allomancy.particle;

import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.PlaySoundAtEntityEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ParticleSound extends EntityFX {


	double entityX,entityY,entityZ;
	
	public ParticleSound(World world, double x, double y, double z,
			double motionX, double motionY, double motionZ, PlaySoundAtEntityEvent event) {
		
		super(world, x, y, z, motionX, motionY, motionZ);

		this.motionX = motionX;
		this.motionY = motionY + 0.009D;
		this.motionZ = motionZ;
		this.setParticleTextureIndex(64);
		this.particleScale *= 1.2F;
		this.particleMaxAge = 15;
		this.noClip = true;
		entityX = event.entity.posX;
		entityX = event.entity.posX;
		entityX = event.entity.posX;



		if (event.name.contains("pig") 
				|| event.name.contains("sheep")
				|| event.name.contains("cow") 
				|| event.name.contains("horse")
				|| event.name.contains("mooshroom")
				|| event.name.contains("villager")
				|| event.name.contains("golem") 
				|| event.name.contains("chicken")
				|| event.name.contains("step")) {
			this.particleGreen = 1;
			this.particleBlue = 0.25F;
			this.particleRed = 0;
		}

		if (event.name.contains("skeleton") 
				|| event.name.contains("zombie")
				|| event.name.contains("slime")
				|| event.name.contains("silverfish")
				|| event.name.contains("spider") 
				|| event.name.contains("witch")
				|| event.name.contains("enderman")
				|| event.name.contains("ghast")
				|| event.name.contains("silverfish")
				|| event.name.contains("creeper")
				|| event.name.contains("bow")) {
			this.particleGreen = 0.15F;
			this.particleBlue = 0.15F;
			this.particleRed = 1;
		}



	}
	
	@Override
	public void onUpdate() {
		if (((this.posX - entityX) < 1.7) &&((this.posY - entityY) < 2.5) &&((this.posZ - entityZ) < 1.7)){
			this.setDead();
	}
		
		this.prevPosX = this.posX;
		this.prevPosY = this.posY;
		this.prevPosZ = this.posZ;

		if (this.particleAge++ >= this.particleMaxAge) {
			this.setDead();
		}

		this.moveEntity(this.motionX, this.motionY, this.motionZ);

	}
	/*@Override
	public int getFXLayer() {
		return 3;
	}
	
	@Override
	public void func_180434_a(WorldRenderer wr, Entity e, float p3, float p4, float p5, float p6, float p7, float p8)
	{

		Minecraft.getMinecraft().getTextureManager().bindTexture(loc);

       	wr.startDrawingQuads();
       	super.func_180434_a(wr, e, p3, p4, p5, p6, p7, p8);
       	Tessellator.getInstance().draw();
       	GlStateManager.disableBlend();
       	GlStateManager.enableLighting();
	}*/

}
