package com.legobmw99.allomancy.modules.powers.data;

import com.legobmw99.allomancy.Allomancy;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.function.Supplier;

public final class AllomancerAttachment {

    private static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES =
            DeferredRegister.create(NeoForgeRegistries.ATTACHMENT_TYPES, Allomancy.MODID);

    public static final Supplier<AttachmentType<AllomancerData>> ALLOMANCY_DATA =
            ATTACHMENT_TYPES.register("allomancy_data", () -> AttachmentType
                    .builder(AllomancerData::new)
                    .serialize(AllomancerData.CODEC)
                    .copyOnDeath()
                    .build());

    private AllomancerAttachment() {}

    public static void register(IEventBus bus) {
        ATTACHMENT_TYPES.register(bus);
    }

}