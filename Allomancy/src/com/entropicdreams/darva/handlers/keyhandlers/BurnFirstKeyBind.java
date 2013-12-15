package com.entropicdreams.darva.handlers.keyhandlers;

import java.util.EnumSet;

import com.entropicdreams.darva.AllomancyData;
import com.entropicdreams.darva.handlers.PacketHandler;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.client.settings.KeyBinding;
import cpw.mods.fml.client.registry.KeyBindingRegistry.KeyHandler;
import cpw.mods.fml.common.TickType;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
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
				if (data.MetalAmounts[data.matIron] > 0)
					data.MetalBurning[data.matIron] = !data.MetalBurning[data.matIron];
				player.sendQueue.addToSendQueue(PacketHandler.changeBurn(AllomancyData.matIron, data.MetalBurning[data.matIron]));
				break;
			case 2:
				//toggle Tin.
				if (data.MetalAmounts[data.matTin] > 0)
					data.MetalBurning[data.matTin] = !data.MetalBurning[data.matTin];
				player.sendQueue.addToSendQueue(PacketHandler.changeBurn(AllomancyData.matTin, data.MetalBurning[data.matTin]));
				break;
			case 3:
				//toggle Copper.
				if (data.MetalAmounts[data.matCopper] > 0)
					data.MetalBurning[data.matCopper] = !data.MetalBurning[data.matCopper];
				player.sendQueue.addToSendQueue(PacketHandler.changeBurn(AllomancyData.matCopper, data.MetalBurning[data.matCopper]));
				break;
			case 4:
				//toggle Zinc.
				if (data.MetalAmounts[data.matZinc] > 0)
					data.MetalBurning[data.matZinc] = !data.MetalBurning[data.matZinc];
				player.sendQueue.addToSendQueue(PacketHandler.changeBurn(AllomancyData.matZinc, data.MetalBurning[data.matZinc]));
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
