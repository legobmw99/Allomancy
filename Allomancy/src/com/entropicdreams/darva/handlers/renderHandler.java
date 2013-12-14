package com.entropicdreams.darva.handlers;

import java.util.EnumSet;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiIngame;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.texture.SimpleTexture;
import net.minecraft.client.renderer.texture.TextureObject;
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

	}

	@Override
	public void tickEnd(EnumSet<TickType> type, Object... tickData) {
		
		
		int ironY, steelY, tinY, pewterY;
		
		GuiIngame gig = new GuiIngame(Minecraft.getMinecraft());
		Minecraft.getMinecraft().renderEngine.bindTexture(meterLoc);
		TextureObject obj;
		obj = Minecraft.getMinecraft().renderEngine.getTexture(meterLoc);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, obj.getGlTextureId());

		
		ironY = 0; //This will be replaced with a call to get the actual value of a players iron
				   //reserves eventually.
		gig.drawTexturedModalRect(6, 220, 7+ironY, 1+ironY, 3, 20-ironY);
		
		steelY = 0;
		
		gig.drawTexturedModalRect(13, 220, 13+steelY, 1+steelY,3,20-steelY );
		

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
