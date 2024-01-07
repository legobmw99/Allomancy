package com.legobmw99.allomancy.modules.powers.data;

import com.legobmw99.allomancy.Allomancy;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

public class AllomancerAttachment {

    private static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES = DeferredRegister.create(NeoForgeRegistries.ATTACHMENT_TYPES, Allomancy.MODID);

    public static final DeferredHolder<AttachmentType<?>, AttachmentType<DefaultAllomancerData>> ALLOMANCY_DATA = ATTACHMENT_TYPES.register("allomancy_data", () -> AttachmentType
            .serializable(DefaultAllomancerData::new)
            .copyOnDeath()
            .build());


    public static void register(IEventBus bus) {
        ATTACHMENT_TYPES.register(bus);
    }

}
