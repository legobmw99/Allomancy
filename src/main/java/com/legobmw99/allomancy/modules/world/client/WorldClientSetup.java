package com.legobmw99.allomancy.modules.world.client;

import com.legobmw99.allomancy.modules.world.WorldSetup;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.FluidModel;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.resources.Identifier;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.client.event.RegisterFluidModelsEvent;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;

public final class WorldClientSetup {

    public static void register(IEventBus bus) {
        bus.addListener(WorldClientSetup::registerClientExtensions);
        bus.addListener(WorldClientSetup::onRegisterFluidModels);

    }

    private static void onRegisterFluidModels(RegisterFluidModelsEvent event) {
        event.register(new FluidModel.Unbaked(new Material(Identifier.withDefaultNamespace("block/water_still")),
                                              new Material(Identifier.withDefaultNamespace("block/water_flow")),
                                              new Material(Identifier.withDefaultNamespace("block/water_overlay")),
                                              _ -> 0xFFD3E9FF), WorldSetup.LERASIUM_FLUID.get());

    }

    private static void registerClientExtensions(RegisterClientExtensionsEvent event) {
        event.registerFluidType(new IClientFluidTypeExtensions() {
            @Override
            public Identifier getRenderOverlayTexture(Minecraft mc) {
                return Identifier.withDefaultNamespace("textures/misc/underwater.png");
            }
        }, WorldSetup.LERAS_TYPE.get());
    }


    private WorldClientSetup() {}

}
