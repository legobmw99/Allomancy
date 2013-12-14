package com.entropicdreams.darva.handlers;

import java.util.EnumSet;

import net.minecraft.client.settings.KeyBinding;
import cpw.mods.fml.client.registry.KeyBindingRegistry.KeyHandler;
import cpw.mods.fml.common.TickType;

public class SwitchMetalKeybind extends KeyHandler {
	private EnumSet tickTypes = EnumSet.of(TickType.CLIENT);
	private boolean keyDown = false;
	public SwitchMetalKeybind(KeyBinding[] keyBindings, boolean[] repeatings) {
		super(keyBindings, repeatings);
		// TODO Auto-generated constructor stub
	}

	@Override
	public String getLabel() {
		// TODO Auto-generated method stub
		return "Switch Metals";
	}

	@Override
	public void keyDown(EnumSet<TickType> types, KeyBinding kb,
			boolean tickEnd, boolean isRepeat) {
			PlayerClientEntityMP player;
			if (keyDown == false)
			{
				keyDown = true;
				
			}

	}

	@Override
	public void keyUp(EnumSet<TickType> types, KeyBinding kb, boolean tickEnd) {
		// TODO Auto-generated method stub

	}

	@Override
	public EnumSet<TickType> ticks() {
		// TODO Auto-generated method stub
		return tickTypes;
	}

}
