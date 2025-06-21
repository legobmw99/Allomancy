package com.legobmw99.allomancy.datagen;

import com.legobmw99.allomancy.Allomancy;
import net.minecraft.data.advancements.AdvancementProvider;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.data.event.GatherDataEvent;

import java.util.Collections;
import java.util.List;

@EventBusSubscriber(modid = Allomancy.MODID, value = Dist.CLIENT)
public final class DataGenerators {

    @SubscribeEvent
    public static void gatherData(final GatherDataEvent.Client event) {
        var generator = event.getGenerator();
        var packOutput = generator.getPackOutput();

        event.createDatapackRegistryObjects(DatapackEntries.BUILDER);

        var lookup = event.getLookupProvider();


        event.addProvider(new Languages(packOutput));
        event.addProvider(new ModelFiles(packOutput));
        event.addProvider(new EquipmentAssets(packOutput));
        event.addProvider(new ParticleDescriptions(packOutput));

        event.addProvider(new Recipes.Runner(packOutput, lookup));
        event.addProvider(new AdvancementProvider(packOutput, lookup, List.of(new Advancements())));

        event.addProvider(new LootTableProvider(packOutput, Collections.emptySet(),
                                                List.of(new LootTableProvider.SubProviderEntry(BlockLootTables::new,
                                                                                               LootContextParamSets.BLOCK)),
                                                lookup));
        event.addProvider(new LootModifiers(packOutput, lookup));

        var blocktags = event.addProvider(new TagProvider.Blocks(packOutput, lookup));
        event.addProvider(new TagProvider.Items(packOutput, lookup, blocktags.contentsGetter()));
        event.addProvider(new TagProvider.Biomes(packOutput, lookup));
        event.addProvider(new TagProvider.Structures(packOutput, lookup));
        event.addProvider(new TagProvider.DamageTypes(packOutput, lookup));
        event.addProvider(new TagProvider.Banners(packOutput, lookup));
        event.addProvider(new TagProvider.EntityTypes(packOutput, lookup));

    }

    private DataGenerators() {}
}
