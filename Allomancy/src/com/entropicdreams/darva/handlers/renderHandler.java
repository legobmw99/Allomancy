package com.entropicdreams.darva.handlers;

import java.util.EnumSet;

import org.lwjgl.opengl.GL11;
import org.lwjgl.util.Point;

import com.entropicdreams.darva.AllomancyData;

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
private AllomancyData data;
private int animationCounter = 0;
private int currentFrame = 0;

private Point[] Frames = { new Point(72,0), new Point (72, 4), new Point(72,8), new Point(72,12) };

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

		if (!Minecraft.getMinecraft().inGameHasFocus)
			return;
		
		EntityClientPlayerMP player;
		player = mc.thePlayer;
		if (player == null)
			return;
		
		animationCounter++;
		
		data = data.forPlayer(player);
		//left hand side.
		int ironY, steelY, tinY, pewterY;
		//right hand side
		int copperY, bronzeY, zincY, brassY;
		
		GuiIngame gig = new GuiIngame(Minecraft.getMinecraft());
		Minecraft.getMinecraft().renderEngine.bindTexture(meterLoc);
		TextureObject obj;
		obj = Minecraft.getMinecraft().renderEngine.getTexture(meterLoc);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, obj.getGlTextureId());

		switch (data.getSelected())
		{
		case 0:
			break;
		case 1:
			gig.drawTexturedModalRect(3, 213, 54, 0, 16, 24);
			break;
		case 2:
			gig.drawTexturedModalRect(28, 213, 54, 0, 16, 24);
			break;
		case 3:
			gig.drawTexturedModalRect(378, 213, 54, 0, 16, 24);
			break;
		case 4:
			gig.drawTexturedModalRect(403, 213, 54, 0, 16, 24);
			break;

		}
		
		
		
		ironY = 10 - data.getIron() ; //This will be replaced with a call to get the actual value of a players iron
				   //reserves eventually.
		gig.drawTexturedModalRect(6, 220+ironY, 7, 1+ironY, 3, 10-ironY);
		
		steelY = 10 - data.getSteel();
		gig.drawTexturedModalRect(13, 220+steelY, 13, 1+steelY,3,10-steelY );
		
		tinY = 10 - data.getTin();
		gig.drawTexturedModalRect(31, 220+tinY, 19, 1+tinY,3,10-tinY );

		pewterY = 10 - data.getPewter();
		gig.drawTexturedModalRect(38, 220+pewterY, 25, 1+pewterY,3,10-pewterY );

		copperY = 10 - data.getCopper();
		gig.drawTexturedModalRect(381, 220+copperY, 31, 1+copperY,3,10-copperY );
		
		bronzeY = 10 -data.getBronze();
		gig.drawTexturedModalRect(388, 220+bronzeY, 37, 1+bronzeY,3,10-bronzeY );

		zincY = 10 - data.getZinc();;
		gig.drawTexturedModalRect(406, 220+zincY, 43, 1+zincY,3,10-zincY );

		brassY = 10 - data.getBrass();
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

			gig.drawTexturedModalRect(5, 220+ironY, Frames[currentFrame].getX() , Frames[currentFrame].getY(), 5, 3);
			if (animationCounter > 6) //Draw the burning symbols...
			{			
			animationCounter = 0;
			currentFrame++;
			if (currentFrame > 3)
				currentFrame = 0;
		}
		
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
