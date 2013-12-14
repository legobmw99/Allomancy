package com.entropicdreams.darva.handlers.keyhandlers;

import java.util.EnumSet;

import com.entropicdreams.darva.AllomancyData;
import com.entropicdreams.darva.handlers.PacketHandler;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.client.settings.KeyBinding;
import cpw.mods.fml.client.registry.KeyBindingRegistry.KeyHandler;
import cpw.mods.fml.common.TickType;

public class BurnFirstKeyBind extends KeyHandler {

	private boolean keyDown = false;
	public BurnFirstKeyBind(KeyBinding[] keyBindings, boolean[] repeatings) {
		super(keyBindings, repeatings);
	}

	@Override
	public String getLabel() {
		return "Burn First";
	}

	@Override
	public void keyDown(EnumSet<TickType> types, KeyBinding kb,
			boolean tickEnd, boolean isRepeat) {
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
				//toggle iron.
				if (data.getIron() > 0)
					data.setbIron(!data.isbIron());
				player.sendQueue.addToSendQueue(PacketHandler.changeBurn(AllomancyData.matIron, data.isbIron()));
				break;
			case 2:
				//toggle Tin.
				if (data.getTin() > 0)
					data.setbTin(!data.isbTin());
				player.sendQueue.addToSendQueue(PacketHandler.changeBurn(AllomancyData.matTin, data.isbTin()));
				break;
			case 3:
				//toggle Copper.
				if (data.getCopper() > 0)
					data.setbCopper(!data.isbCopper());
				player.sendQueue.addToSendQueue(PacketHandler.changeBurn(AllomancyData.matCopper, data.isbCopper()));
				break;
			case 4:
				//toggle Zinc.
				if (data.getZinc() > 0)
					data.setbZinc(!data.isbZinc());
				player.sendQueue.addToSendQueue(PacketHandler.changeBurn(AllomancyData.matZinc, data.isbZinc()));
				break;
			default:
				break;
			}
		}
	}

	@Override
	public void keyUp(EnumSet<TickType> types, KeyBinding kb, boolean tickEnd) {
		if (keyDown == true)
		{
			keyDown = false;
		}
				
	}

	@Override
	public EnumSet<TickType> ticks() {
		return  EnumSet.of(TickType.CLIENT);
	}

}
