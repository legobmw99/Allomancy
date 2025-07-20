package com.legobmw99.allomancy.modules.world.client;

import com.legobmw99.allomancy.modules.world.WorldSetup;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.extensions.common.IClientFluidTypeExtensions;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import org.jetbrains.annotations.Nullable;

public final class WorldClientSetup {


    public static void clientInit(final FMLClientSetupEvent e) {
        e.enqueueWork(
                () -> ItemBlockRenderTypes.setRenderLayer(WorldSetup.LERASIUM_FLUID.get(), RenderType.translucent()));
    }


    public static class LerasiumFluidExtension implements IClientFluidTypeExtensions {
        private static final ResourceLocation LERASIUM_FLUID_TEXTURE = new ResourceLocation("block/water_still");
        private static final ResourceLocation LERASIUM_FLUID_FLOWING = new ResourceLocation("block/water_flow");
        private static final ResourceLocation LERASIUM_FLUID_ROVERLAY =
                new ResourceLocation("textures/misc/underwater.png");
        private static final ResourceLocation LERASIUM_FLUID_OVERLAY = new ResourceLocation("block/water_overlay");

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