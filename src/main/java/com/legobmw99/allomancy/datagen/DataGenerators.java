package com.legobmw99.allomancy.datagen;

import com.legobmw99.allomancy.Allomancy;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.data.event.GatherDataEvent;

@EventBusSubscriber(modid = Allomancy.MODID, value = Dist.CLIENT)
public final class DataGenerators {

    @SubscribeEvent
    public static void gatherData(final GatherDataEvent.Client event) {
        event.createWorldRegistryObjects(DatapackEntries.WORLD_BUILDER);
        event.createReloadableRegistryObjects(DatapackEntries.RELOADABLE_BUILDER);

        event.createProvider(Languages::new);
        event.createProvider(ModelFiles::new);
        event.createProvider(EquipmentAssets::new);
        event.createProvider(ParticleDescriptions::new);

        event.createProvider(LootModifiers::new);

        event.createBlockAndItemTags(TagProvider.Blocks::new, TagProvider.Items::new);
        event.createProvider(TagProvider.Biomes::new);
        event.createProvider(TagProvider.DamageTypes::new);
        event.createProvider(TagProvider.Structures::new);
        event.createProvider(TagProvider.Banners::new);
        event.createProvider(TagProvider.EntityTypes::new);
        event.createProvider(TagProvider.Fluids::new);
    }

    private DataGenerators() {}
}
