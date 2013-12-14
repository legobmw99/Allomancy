package com.entropicdreams.darva.handlers.keyhandlers;

import java.util.EnumSet;

import com.entropicdreams.darva.AllomancyData;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.client.settings.KeyBinding;
import cpw.mods.fml.client.registry.KeyBindingRegistry.KeyHandler;
import cpw.mods.fml.common.TickType;

public class BurnSecondKeyBind extends KeyHandler {

	public BurnSecondKeyBind(KeyBinding[] keyBindings, boolean[] repeatings) {
		super(keyBindings, repeatings);
		// TODO Auto-generated constructor stub
	}

	private boolean keyDown;
	@Override
	public String getLabel() {
		// TODO Auto-generated method stub
		return "Burn Second";
	}

	@Override
	public void keyDown(EnumSet<TickType> types, KeyBinding kb,
			boolean tickEnd, boolean isRepeat) {
		// TODO Auto-generated method stub
		EntityClientPlayerMP player;
		player = Minecraft.getMinecraft().thePlayer;
		AllomancyData data;
		
		if (player == null)
			return;
		if (keyDown == false)
		{
			keyDown = true;
			data = AllomancyData.forPlayer(player);
			switch (data.getSelected())
			{
			case 1:
				//toggle Steel.
				if (data.getSteel() > 0)
					data.setbSteel(!data.isbSteel());
				break;
			case 2:
				//toggle Pewter.
				if (data.getPewter() > 0)
					data.setbPewter(!data.isbPewter());
				break;
			case 3:
				//toggle Bronze.
				if (data.getBronze() > 0)
					data.setbBronze(!data.isbBronze());
				break;
			case 4:
				//toggle Brass.
				if (data.getBrass() > 0)
					data.setbBrass(!data.isbBrass());
				break;
			default:
				break;
			}
		}
	}

	@Override
	public void keyUp(EnumSet<TickType> types, KeyBinding kb, boolean tickEnd) {
		// TODO Auto-generated method stub
		if (keyDown)
		{
			keyDown = false;
		}
	}

	@Override
	public EnumSet<TickType> ticks() {
		// TODO Auto-generated method stub
		return EnumSet.of(TickType.CLIENT);
	}

}
