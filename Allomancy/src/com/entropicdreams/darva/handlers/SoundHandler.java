package com.entropicdreams.darva.handlers;

import com.entropicdreams.darva.AllomancyData;
import com.entropicdreams.darva.particleSound;

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
		AllomancyData data = AllomancyData.forPlayer(player);
		if (data.MetalBurning[data.matTin])
		{
			switch (event.name)
			{
			case "step.stone":
			case "step.grass":
				break;
			case "mob.pig.step":
			case "mob.sheep.step":
			case "mob.cow.step":
			case "mob.horse.step":
			case "mob.mooshroom.step":
			case "mob.villager.step":
			case "mob.skeleton.step":
			case "mob.zombie.step":
			case "mob.irongolem.walk":
			case "mob.slime.small":
			case "mob.slime.big":
			case "mob.silverfish.step":
			case "mob.spider.step":
			case "mob.witch.idle":
			case "mob.endermen.portal":
			case "mob.enderman.scream":
			case "mob.ghast.moan":	
			case "mob.chicken.step":	
			case "random.bow":
				EntityFX particle = new particleSound(player.worldObj, player.posX + -(Math.sin(Math.toRadians(player.getRotationYawHead())) * .7d), player.posY -.2, player.posZ +(Math.cos(Math.toRadians(player.getRotationYawHead())) * .7d), 0, 0, 0, event.name, event.x,event.y,event.z);
				Minecraft.getMinecraft().effectRenderer.addEffect(particle);
				
			break;
			default:
			
			}
		}
	}
}
