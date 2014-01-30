package common.legobmw99.allomancy.handlers;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.event.entity.EntityEvent.EntityConstructing;

import common.legobmw99.allomancy.Allomancy;
import common.legobmw99.allomancy.common.AllomancyData;
import common.legobmw99.allomancy.network.packets.AllomancyBecomeMistbornPacket;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent.PlayerRespawnEvent;

public class PlayerTrackerHandler {
	@SubscribeEvent
	public void onPlayerRespawn(PlayerRespawnEvent event)
    {
            NBTTagCompound old = event.player.getEntityData();
            if (old.hasKey("Allomancy_Data"))
            {	
                event.player.getEntityData().setTag("Allomancy_Data", old.getCompoundTag("Allomancy_Data"));
            }
            
    }
	@SubscribeEvent
	public void onEntityConstruct(EntityConstructing event) {
		if (event.entity instanceof EntityPlayer) {
			event.entity.registerExtendedProperties(AllomancyData.IDENTIFIER,
					new AllomancyData((EntityPlayer) event.entity));
		}
	}
	@SubscribeEvent
	public void onPlayerLogin(PlayerLoggedInEvent event)
	{
		if (event.player instanceof EntityPlayerMP) {
			//Allomancy.packetPipeline.sendTo(new AllomancyDataPacket(AllomancyData.forPlayer(event.player)), (EntityPlayerMP) event.player);	
			if(AllomancyData.isMistborn == true){
				Allomancy.packetPipeline.sendTo(new AllomancyBecomeMistbornPacket(), (EntityPlayerMP) event.player);
			}
		}
	}
}

