package com.entropicdreams.darva.handlers;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.client.particle.EntityFX;
import net.minecraftforge.client.event.sound.PlaySoundEvent;
import net.minecraftforge.event.ForgeSubscribe;

import com.entropicdreams.darva.AllomancyData;
import com.entropicdreams.darva.particles.particleSound;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class SoundHandler {

	@ForgeSubscribe
	public void onSound(PlaySoundEvent event) {
		EntityClientPlayerMP player;
		player = Minecraft.getMinecraft().thePlayer;
		AllomancyData data = AllomancyData.forPlayer(player);
		if (data.MetalBurning[AllomancyData.matTin]) {

			if (event.name.contains("step") || event.name.contains(".big")
					|| event.name.contains("scream")
					|| event.name.contains("random.bow")) {
				EntityFX particle = new particleSound(player.worldObj,
						player.posX
								+ -(Math.sin(Math.toRadians(player
										.getRotationYawHead())) * .7d),
						player.posY - .2, player.posZ
								+ (Math.cos(Math.toRadians(player
										.getRotationYawHead())) * .7d), 0, 0,
						0, event.name, event.x, event.y, event.z);
				Minecraft.getMinecraft().effectRenderer.addEffect(particle);
			}

		}
	}
}
