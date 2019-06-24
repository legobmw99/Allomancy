package com.legobmw99.allomancy.util.proxy;


import com.legobmw99.allomancy.util.Registry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import net.minecraftforge.fml.network.NetworkDirection;

public class ServerProxy extends CommonProxy {

    @Override
    public void clientInit(FMLClientSetupEvent e) {
        // no-op
    }

    @Override
    public void serverInit(FMLServerStartingEvent e) {
        e.getCommandDispatcher();//e.registerServerCommand(new PowerCommand());
    }

    @Override
    public World getClientWorld() {
        throw new IllegalStateException("Only run this on the client!");
    }

    @Override
    public PlayerEntity getClientPlayer() {
        throw new IllegalStateException("Only run this on the client!");
    }

    @Override
    public void sendToServer(Object msg) {
        throw new IllegalStateException("Only run this on the client!");
    }

    @Override
    public void sendTo(Object msg, ServerPlayerEntity player) {
        if (!(player instanceof FakePlayer)) {
            Registry.NETWORK.sendTo(msg, player.connection.netManager, NetworkDirection.PLAY_TO_CLIENT);
        }
    }
}