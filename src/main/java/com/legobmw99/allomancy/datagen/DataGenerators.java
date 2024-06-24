package com.legobmw99.allomancy.datagen;

import com.legobmw99.allomancy.Allomancy;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.data.AdvancementProvider;
import net.neoforged.neoforge.common.data.DatapackBuiltinEntriesProvider;
import net.neoforged.neoforge.data.event.GatherDataEvent;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@EventBusSubscriber(bus = EventBusSubscriber.Bus.MOD, modid = Allomancy.MODID)
public class DataGenerators {

    @SubscribeEvent
    public static void gatherData(final GatherDataEvent event) {
        var generator = event.getGenerator();
        var packOutput = generator.getPackOutput();
        var lookup = event.getLookupProvider();
        var fileHelper = event.getExistingFileHelper();
        generator.addProvider(event.includeServer(), new Recipes(packOutput, lookup));
        generator.addProvider(event.includeServer(), new LootTableProvider(packOutput, Collections.emptySet(),
                                                                           List.of(new LootTableProvider.SubProviderEntry(
                                                                                   BlockLootTables::new,
                                                                                   LootContextParamSets.BLOCK)),
                                                                           lookup));
        generator.addProvider(event.includeServer(), new LootModifiers(packOutput, lookup));

        BlockTags blocktags = new BlockTags(packOutput, lookup, fileHelper);
        generator.addProvider(event.includeServer(), blocktags);
        generator.addProvider(event.includeServer(),
                              new ItemTags(packOutput, lookup, blocktags.contentsGetter(), fileHelper));
        generator.addProvider(event.includeServer(),
                              new AdvancementProvider(packOutput, lookup, fileHelper, List.of(new Advancements())));

        DatapackBuiltinEntriesProvider datapackProvider = new DatapackEntries(packOutput, lookup);
        CompletableFuture<HolderLookup.Provider> lookupProvider = datapackProvider.getRegistryProvider();

        generator.addProvider(event.includeServer(), datapackProvider);
        generator.addProvider(event.includeServer(), new DamageTags(packOutput, lookupProvider, fileHelper));
        generator.addProvider(event.includeServer(), new BannerTags(packOutput, lookupProvider, fileHelper));


        generator.addProvider(event.includeClient(), new Languages(packOutput));
        generator.addProvider(event.includeClient(), new BlockStates(packOutput, fileHelper));
        generator.addProvider(event.includeClient(), new ItemModels(packOutput, fileHelper));

    }
}
