package com.legobmw99.allomancy.modules.world.client;

import com.legobmw99.allomancy.modules.world.WorldSetup;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.chunk.ChunkSectionLayer;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;
import org.jetbrains.annotations.Nullable;

public final class WorldClientSetup {

    public static void register(IEventBus bus) {
        bus.addListener(WorldClientSetup::clientInit);
        bus.addListener(WorldClientSetup::registerClientExtensions);
    }

    private static void clientInit(final FMLClientSetupEvent e) {
        e.enqueueWork(() -> ItemBlockRenderTypes.setRenderLayer(WorldSetup.LERASIUM_FLUID.get(),
                                                                ChunkSectionLayer.TRANSLUCENT));
    }

    private static void registerClientExtensions(RegisterClientExtensionsEvent event) {
        event.registerFluidType(new LerasiumFluidExtension(), WorldSetup.LERAS_TYPE.get());
    }

    private static class LerasiumFluidExtension implements IClientFluidTypeExtensions {
        private static final ResourceLocation LERASIUM_FLUID_TEXTURE =
                ResourceLocation.withDefaultNamespace("block/water_still");
        private static final ResourceLocation LERASIUM_FLUID_FLOWING =
                ResourceLocation.withDefaultNamespace("block/water_flow");
        private static final ResourceLocation LERASIUM_FLUID_ROVERLAY =
                ResourceLocation.withDefaultNamespace("textures/misc/underwater.png");
        private static final ResourceLocation LERASIUM_FLUID_OVERLAY =
                ResourceLocation.withDefaultNamespace("block/water_overlay");

        @Override
        public ResourceLocation getStillTexture() {
            return LERASIUM_FLUID_TEXTURE;
        }

        @Override
        public ResourceLocation getFlowingTexture() {
            return LERASIUM_FLUID_FLOWING;
        }

        @Override
        public @Nullable ResourceLocation getOverlayTexture() {
            return LERASIUM_FLUID_OVERLAY;
        }

        @Override
        public ResourceLocation getRenderOverlayTexture(Minecraft mc) {
            return LERASIUM_FLUID_ROVERLAY;
        }

        @Override
        public int getTintColor() {
            return 0xFFD3E9FF;
        }
    }

    private WorldClientSetup() {}

}
