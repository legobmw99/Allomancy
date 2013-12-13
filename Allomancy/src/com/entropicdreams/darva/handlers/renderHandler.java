package com.entropicdreams.darva.handlers;

import java.util.EnumSet;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import cpw.mods.fml.common.ITickHandler;
import cpw.mods.fml.common.TickType;

public class renderHandler implements ITickHandler {
private final Minecraft mc;

	public renderHandler()
	{
		mc = Minecraft.getMinecraft();
	}
	@Override
	public void tickStart(EnumSet<TickType> type, Object... tickData) {

	}

	@Override
	public void tickEnd(EnumSet<TickType> type, Object... tickData) {
		
		ScaledResolution res = new ScaledResolution(mc.gameSettings, mc.displayWidth, mc.displayHeight);
		mc.entityRenderer.setupOverlayRendering();
		

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
