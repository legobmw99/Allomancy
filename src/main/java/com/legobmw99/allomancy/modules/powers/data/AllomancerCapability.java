package com.legobmw99.allomancy.modules.powers.data;

import com.legobmw99.allomancy.Allomancy;
import com.legobmw99.allomancy.api.data.IAllomancerData;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;

public class AllomancerCapability {

    @CapabilityInject(IAllomancerData.class)
    public static final Capability<IAllomancerData> PLAYER_CAP = null;

    public static final ResourceLocation IDENTIFIER = new ResourceLocation(Allomancy.MODID, "allomancy_data");

    public static void register() {
        CapabilityManager.INSTANCE.register(IAllomancerData.class);
    }

}
