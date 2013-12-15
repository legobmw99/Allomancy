package com.entropicdreams.darva.handlers;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.client.particle.EntityFlameFX;
import net.minecraftforge.client.event.sound.PlaySoundEffectEvent;
import net.minecraftforge.client.event.sound.PlaySoundEvent;
import net.minecraftforge.client.event.sound.PlaySoundSourceEvent;
import net.minecraftforge.client.event.sound.SoundResultEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.ForgeSubscribe;

public class SoundHandler {

	@ForgeSubscribe
	public void onSound(PlaySoundEvent event)
	{
		EntityClientPlayerMP player;
		player = Minecraft.getMinecraft().thePlayer;
		switch (event.name)
		{
		case "step.stone":
		case "step.grass":
			EntityFX particle = new EntityFlameFX(null, player.posX, player.posY, player.posZ, 0, 0, 0);
			Minecraft.getMinecraft().effectRenderer.addEffect(particle);
		break;
		default:
			System.out.println(event.name);
		
		}
		
	}
}
