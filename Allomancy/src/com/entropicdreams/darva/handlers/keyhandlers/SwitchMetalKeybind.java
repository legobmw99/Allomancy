package com.entropicdreams.darva.handlers.keyhandlers;

import java.util.EnumSet;

import com.entropicdreams.darva.AllomancyData;
import com.entropicdreams.darva.handlers.PacketHandler;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.client.settings.KeyBinding;
import cpw.mods.fml.client.registry.KeyBindingRegistry.KeyHandler;
import cpw.mods.fml.common.TickType;
import cpw.mods.fml.common.network.PacketDispatcher;

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
			EntityClientPlayerMP player;
			player = Minecraft.getMinecraft().thePlayer;
			if (player == null || !Minecraft.getMinecraft().inGameHasFocus)
				return;
			if (keyDown == false)
			{
				keyDown = true;
				AllomancyData data = AllomancyData.forPlayer(player);
				data.setSelected(data.getSelected()+1);
				player.sendQueue.addToSendQueue(PacketHandler.updateSelectedMetal(data.getSelected()));
			}

	}

	@Override
	public void keyUp(EnumSet<TickType> types, KeyBinding kb, boolean tickEnd) {
		// TODO Auto-generated method stub
		if (keyDown == true)
			keyDown = false;
	}

	@Override
	public EnumSet<TickType> ticks() {
		// TODO Auto-generated method stub
		return tickTypes;
	}

}
