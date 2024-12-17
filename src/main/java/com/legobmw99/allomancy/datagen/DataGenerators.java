package com.legobmw99.allomancy.datagen;

import com.legobmw99.allomancy.Allomancy;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.data.AdvancementProvider;
import net.neoforged.neoforge.common.data.DatapackBuiltinEntriesProvider;
import net.neoforged.neoforge.data.event.GatherDataEvent;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@EventBusSubscriber(bus = EventBusSubscriber.Bus.MOD, modid = Allomancy.MODID, value = Dist.CLIENT)
public class DataGenerators {

    @SubscribeEvent
    public static void gatherData(final GatherDataEvent.Client event) {
        var generator = event.getGenerator();
        var packOutput = generator.getPackOutput();
        var lookup = event.getLookupProvider();
        var fileHelper = event.getExistingFileHelper();

        event.addProvider(new Recipes.Runner(packOutput, lookup));

        event.addProvider(new LootTableProvider(packOutput, Collections.emptySet(),
                                                List.of(new LootTableProvider.SubProviderEntry(BlockLootTables::new,
                                                                                               LootContextParamSets.BLOCK)),
                                                lookup));
        event.addProvider(new LootModifiers(packOutput, lookup));

        BlockTags blocktags = new BlockTags(packOutput, lookup, fileHelper);
        event.addProvider(blocktags);
        event.addProvider(new ItemTags(packOutput, lookup, blocktags.contentsGetter(), fileHelper));
        event.addProvider(new AdvancementProvider(packOutput, lookup, fileHelper, List.of(new Advancements())));

        DatapackBuiltinEntriesProvider datapackProvider = new DatapackEntries(packOutput, lookup);
        CompletableFuture<HolderLookup.Provider> lookupProvider = datapackProvider.getRegistryProvider();

        event.addProvider(datapackProvider);
        event.addProvider(new DamageTags(packOutput, lookupProvider, fileHelper));
        event.addProvider(new BannerTags(packOutput, lookup, fileHelper));


        event.addProvider(new Languages(packOutput));
        event.addProvider(new BlockStates(packOutput, fileHelper));
        event.addProvider(new ItemModels(packOutput, fileHelper));

        event.addProvider(new EquipmentAssets(packOutput));
        // TODO client item models. None are interesting except Vial is conditional on having the component
        // can also support smithing for aluminum hats
        // waiting on https://github.com/neoforged/NeoForge/pull/1725
    }
}
