package common.legobmw99.allomancy.common;

import common.legobmw99.allomancy.Allomancy;
import common.legobmw99.allomancy.network.packets.AllomancyChangeEmotionPacket;
import common.legobmw99.allomancy.network.packets.AllomancyDataPacket;
import common.legobmw99.allomancy.network.packets.AllomancyMoveEntityPacket;
import common.legobmw99.allomancy.network.packets.AllomancySelectMetalPacket;
import common.legobmw99.allomancy.network.packets.AllomancyStopFallPacket;
import common.legobmw99.allomancy.network.packets.AllomancyUpdateBurnPacket;

public class AllomancyPackets {
	public static void init()
    {
        registerPackets();
    }
    
    private static void registerPackets()
    {
        Allomancy.packetPipeline.registerPacket(AllomancyStopFallPacket.class);
        Allomancy.packetPipeline.registerPacket(AllomancySelectMetalPacket.class);
        Allomancy.packetPipeline.registerPacket(AllomancyChangeEmotionPacket.class);
        Allomancy.packetPipeline.registerPacket(AllomancyDataPacket.class);
        Allomancy.packetPipeline.registerPacket(AllomancyMoveEntityPacket.class);
        Allomancy.packetPipeline.registerPacket(AllomancyUpdateBurnPacket.class);
    }
}
