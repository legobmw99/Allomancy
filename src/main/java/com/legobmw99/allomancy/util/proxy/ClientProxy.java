package com.legobmw99.allomancy.util.proxy;

import com.legobmw99.allomancy.handlers.ClientEventHandler;
import com.legobmw99.allomancy.util.Registry;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;

public class ClientProxy extends CommonProxy {

    @Override
    public void clientInit(final FMLClientSetupEvent e){
       Registry.registerEntityRenders();
    }

    @Override
    public void serverInit(FMLServerStartingEvent e) {
        // no-op
    }


    @Override
    public void init(FMLCommonSetupEvent e) {
        super.init(e);
        MinecraftForge.EVENT_BUS.register(new ClientEventHandler());
    }

    @Override
    public void loadComplete(FMLLoadCompleteEvent e) {
        super.loadComplete(e);
        Registry.initKeyBindings();
    }

    @Override
    public World getClientWorld() {
        return Minecraft.getInstance().world;
    }

    @Override
    public PlayerEntity getClientPlayer() {
        return Minecraft.getInstance().player;
    }

}
