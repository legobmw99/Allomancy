package com.legobmw99.allomancy.util.proxy;


import com.legobmw99.allomancy.Allomancy;
import com.legobmw99.allomancy.commands.AllomancyPowerCommand;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;

public class ServerProxy extends CommonProxy {

    @Override
    public void clientInit(final FMLClientSetupEvent e) {
        // no-op
    }

    @Override
    public void init(final FMLCommonSetupEvent e) {
        super.init(e);
    }

    @Override
    public void serverInit(final FMLServerStartingEvent e) {
        Allomancy.LOGGER.info("Registering Allomancy Command");
        AllomancyPowerCommand.register(e.getCommandDispatcher());
    }

    @Override
    public World getClientWorld() {
        throw new IllegalStateException("Only run this on the client!");
    }

    @Override
    public PlayerEntity getClientPlayer() {
        throw new IllegalStateException("Only run this on the client!");
    }

}