package com.entropicdreams.darva.handlers;

import java.util.EnumSet;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.client.gui.GuiIngame;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.texture.SimpleTexture;
import net.minecraft.client.renderer.texture.TextureObject;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.Icon;
import net.minecraft.util.ResourceLocation;
import cpw.mods.fml.common.ITickHandler;
import cpw.mods.fml.common.TickType;

public class renderHandler implements ITickHandler {
private final Minecraft mc;
private SimpleTexture meter;
private ResourceLocation meterLoc;


	public renderHandler()
	{
		mc = Minecraft.getMinecraft();
		meterLoc = new ResourceLocation("allomancy", "textures/overlay/meter.png");
		
		
		
	}
	@Override
	public void tickStart(EnumSet<TickType> type, Object... tickData) {
		EntityClientPlayerMP player;
		player = mc.thePlayer;
		NBTTagCompound base;
		NBTTagCompound allomancy;
		
		if (player == null)
			return;

		base = player.getEntityData();
		if (base == null)
		{
		return;
		}

		if (!base.hasKey("allomancy"))
		{
			allomancy = new NBTTagCompound();
			allomancy.setName("allomancy");
			allomancy.setInteger("iron", 0);
			allomancy.setInteger("steel",0);
			allomancy.setInteger("zinc",0);
			allomancy.setInteger("pewter",0);
			allomancy.setInteger("tin",0);
			allomancy.setInteger("copper",0);
			allomancy.setInteger("bronze", 0);
			allomancy.setInteger("brass", 0);
			base.setCompoundTag("allomancy", base);
		}
				
		
	}

	@Override
	public void tickEnd(EnumSet<TickType> type, Object... tickData) {

		EntityClientPlayerMP player;
		player = mc.thePlayer;
		NBTTagCompound base;
		NBTTagCompound allomancy;
		if (player == null)
			return;
		if (player.getEntityData() == null)
		{
			return;
		}
		allomancy = player.getEntityData().getCompoundTag("allomancy");
		if (allomancy == null)
		{
			return;
		}
		
		//left hand side.
		int ironY, steelY, tinY, pewterY;
		//right hand side
		int copperY, bronzeY, zincY, brassY;
		
		GuiIngame gig = new GuiIngame(Minecraft.getMinecraft());
		Minecraft.getMinecraft().renderEngine.bindTexture(meterLoc);
		TextureObject obj;
		obj = Minecraft.getMinecraft().renderEngine.getTexture(meterLoc);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, obj.getGlTextureId());

		
		ironY = allomancy.getInteger("iron") ; //This will be replaced with a call to get the actual value of a players iron
				   //reserves eventually.
		gig.drawTexturedModalRect(6, 220+ironY, 7, 1+ironY, 3, 10-ironY);
		
		steelY = 10 - allomancy.getInteger("steel");
		gig.drawTexturedModalRect(13, 220+steelY, 13, 1+steelY,3,10-steelY );
		
		tinY = 10 - allomancy.getInteger("tin");
		gig.drawTexturedModalRect(31, 220+tinY, 19, 1+tinY,3,10-tinY );

		pewterY = 10 - allomancy.getInteger("pewter");
		gig.drawTexturedModalRect(38, 220+pewterY, 25, 1+pewterY,3,10-pewterY );

		copperY = 10 - allomancy.getInteger("copper");
		gig.drawTexturedModalRect(381, 220+copperY, 31, 1+copperY,3,10-copperY );
		
		bronzeY = 10 -allomancy.getInteger("bronze");
		gig.drawTexturedModalRect(388, 220+bronzeY, 37, 1+bronzeY,3,10-bronzeY );

		zincY = 10 - allomancy.getInteger("zinc");
		gig.drawTexturedModalRect(406, 220+zincY, 43, 1+zincY,3,10-zincY );

		brassY = 10 - allomancy.getInteger("brass");
		gig.drawTexturedModalRect(413, 220+brassY, 49, 1+brassY,3,10-brassY );


		//Draw the gauges second, so that highlights and decorations show over the bar.
		gig.drawTexturedModalRect(5, 215, 0, 0, 5, 20);
		gig.drawTexturedModalRect(12, 215, 0, 0, 5, 20);
		
		gig.drawTexturedModalRect(30, 215, 0, 0, 5, 20);
		gig.drawTexturedModalRect(37, 215, 0, 0, 5, 20);
		
		gig.drawTexturedModalRect(380, 215, 0, 0, 5, 20);
		gig.drawTexturedModalRect(387, 215, 0, 0, 5, 20);
		
		gig.drawTexturedModalRect(405, 215, 0, 0, 5, 20);
		gig.drawTexturedModalRect(412, 215, 0, 0, 5, 20);

		
	}

	@Override
	public EnumSet<TickType> ticks() {
		return EnumSet.of(TickType.RENDER);
	}

	@Override
	public String getLabel() {
		return "renderHandler";
	}

}
