package com.legobmw99.allomancy.util.proxy;

import com.legobmw99.allomancy.handlers.ClientEventHandler;
import com.legobmw99.allomancy.util.Registry;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

public class ClientProxy extends CommonProxy {
    @Override
    public void preInit(FMLPreInitializationEvent e){
        super.preInit(e);
        Registry.registerEntityRenders();
    }
    @Override
    public void init(FMLCommonSetupEvent e) {
        super.init(e);
        MinecraftForge.EVENT_BUS.register(new ClientEventHandler());
        Registry.initKeyBindings();
        Registry.registerItemRenders();

    }
    @Override
    public void postInit(FMLPostInitializationEvent e) {
        super.postInit(e);
    }

    @Override
    public World getClientWorld() {
        return Minecraft.getInstance().world;
    }

    @Override
    public PlayerEntity getClientPlayer() {
        return Minecraft.getInstance().player;
    }

    @Override
    public void sendToServer(Object msg) {
        Registry.NETWORK.sendToServer(msg);
    }

    @Override
    public void sendTo(Object msg, ServerPlayerEntity player) {
        throw new IllegalStateException("Only run this on the server!");

    }
}
