package com.entropicdreams.darva.handlers;

import java.util.EnumSet;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiIngame;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.texture.SimpleTexture;
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
		meterLoc = new ResourceLocation("allomancy:textures/overlay/meter.png");
		
		
		
	}
	@Override
	public void tickStart(EnumSet<TickType> type, Object... tickData) {

	}

	@Override
	public void tickEnd(EnumSet<TickType> type, Object... tickData) {
		
		GuiIngame gig = new GuiIngame(Minecraft.getMinecraft());
		Minecraft.getMinecraft().renderEngine.bindTexture(meterLoc);
		gig.drawTexturedModalRect(5, 5, 0, 0, 16, 120);
		System.out.println("tick");
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
