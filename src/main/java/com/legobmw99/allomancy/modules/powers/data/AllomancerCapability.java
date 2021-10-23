package com.legobmw99.allomancy.modules.powers.data;

import com.legobmw99.allomancy.Allomancy;
import com.legobmw99.allomancy.api.data.IAllomancerData;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;

public class AllomancerCapability {

    public static final Capability<IAllomancerData> PLAYER_CAP = CapabilityManager.get(new CapabilityToken<>() {
    });

    public static final ResourceLocation IDENTIFIER = new ResourceLocation(Allomancy.MODID, "allomancy_data");

    public static void registerCapability(final RegisterCapabilitiesEvent event) {
        event.register(IAllomancerData.class);
    }

}
