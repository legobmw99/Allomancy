package com.entropicdreams.darva.handlers.keyhandlers;

import java.util.EnumSet;

import com.entropicdreams.darva.AllomancyData;
import com.entropicdreams.darva.handlers.PacketHandler;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.client.settings.KeyBinding;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.client.registry.KeyBindingRegistry.KeyHandler;
import cpw.mods.fml.common.TickType;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
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
		Minecraft mc = FMLClientHandler.instance().getClient();
		if (mc.currentScreen == null) {
			if (player == null)
				return;
			if (keyDown == false) {
				keyDown = true;
				data = AllomancyData.forPlayer(player);
				switch (data.getSelected()) {
				case 1:
					// toggle Steel.
					if (data.MetalAmounts[data.matSteel] > 0)
						data.MetalBurning[data.matSteel] = !data.MetalBurning[data.matSteel];
					player.sendQueue.addToSendQueue(PacketHandler.changeBurn(
							AllomancyData.matSteel,
							data.MetalBurning[data.matSteel]));
					break;
				case 2:
					// toggle Pewter.
					if (data.MetalAmounts[data.matPewter] > 0)
						data.MetalBurning[data.matPewter] = !data.MetalBurning[data.matPewter];
					player.sendQueue.addToSendQueue(PacketHandler.changeBurn(
							AllomancyData.matPewter,
							data.MetalBurning[data.matPewter]));
					break;
				case 3:
					// toggle Bronze.
					if (data.MetalAmounts[data.matBronze] > 0)
						data.MetalBurning[data.matBronze] = !data.MetalBurning[data.matBronze];
					player.sendQueue.addToSendQueue(PacketHandler.changeBurn(
							AllomancyData.matBronze,
							data.MetalBurning[data.matBronze]));
					break;
				case 4:
					// toggle Brass.
					if (data.MetalAmounts[data.matBrass] > 0)
						data.MetalBurning[data.matBrass] = !data.MetalBurning[data.matBrass];
					player.sendQueue.addToSendQueue(PacketHandler.changeBurn(
							AllomancyData.matBrass,
							data.MetalBurning[data.matBrass]));
					break;
				default:
					break;
				}
			}
		}
	}

	@Override
	public void keyUp(EnumSet<TickType> types, KeyBinding kb, boolean tickEnd) {
		// TODO Auto-generated method stub
		if (keyDown) {
			keyDown = false;
		}
	}

	@Override
	public EnumSet<TickType> ticks() {
		// TODO Auto-generated method stub
		return EnumSet.of(TickType.CLIENT);
	}

}
