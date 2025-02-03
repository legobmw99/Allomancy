package com.legobmw99.allomancy.modules.extras.client;

import com.legobmw99.allomancy.modules.extras.ExtrasSetup;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;
import org.jetbrains.annotations.Nullable;

public class ExtrasClientSetup {
    private ExtrasClientSetup() {}

    public static final ResourceLocation LERASIUM_FLUID_TEXTURE =
            ResourceLocation.fromNamespaceAndPath("minecraft", "block/water_still");
    public static final ResourceLocation LERASIUM_FLUID_FLOWING =
            ResourceLocation.fromNamespaceAndPath("minecraft", "block/water_flow");
    public static final ResourceLocation LERASIUM_FLUID_ROVERLAY =
            ResourceLocation.fromNamespaceAndPath("minecraft", "textures/misc/underwater.png");
    public static final ResourceLocation LERASIUM_FLUID_OVERLAY =
            ResourceLocation.fromNamespaceAndPath("minecraft", "block/water_overlay");

    public static void registerClientExtensions(RegisterClientExtensionsEvent event) {
        event.registerFluidType(new LerasiumFluidExtension(), ExtrasSetup.LERAS_TYPE.get());
    }

    private static class LerasiumFluidExtension implements IClientFluidTypeExtensions {
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
}
