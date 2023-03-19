package com.legobmw99.allomancy.datagen;

import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraftforge.common.data.ForgeAdvancementProvider;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Collections;
import java.util.List;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class DataGenerators {

    @SubscribeEvent
    public static void gatherData(GatherDataEvent event) {
        var generator = event.getGenerator();
        var packOutput = generator.getPackOutput();
        var lookup = event.getLookupProvider();
        var fileHelper = event.getExistingFileHelper();
        generator.addProvider(event.includeServer(), new Recipes(packOutput));
        generator.addProvider(event.includeServer(), new LootTableProvider(packOutput, Collections.emptySet(),
                                                                           List.of(new LootTableProvider.SubProviderEntry(BlockLootTables::new, LootContextParamSets.BLOCK),
                                                                                   new LootTableProvider.SubProviderEntry(DungeonLootTables::new, LootContextParamSets.EMPTY))));
        BlockTags blocktags = new BlockTags(packOutput, lookup, fileHelper);
        generator.addProvider(event.includeServer(), blocktags);
        generator.addProvider(event.includeServer(), new ItemTags(packOutput, lookup, blocktags.contentsGetter(), fileHelper));
        generator.addProvider(event.includeServer(), new ForgeAdvancementProvider(packOutput, lookup, fileHelper, List.of(new Advancements())));
        generator.addProvider(event.includeServer(), new BannerTag(packOutput, lookup, fileHelper));

        generator.addProvider(event.includeClient(), new Languages(packOutput));
        generator.addProvider(event.includeClient(), new BlockStates(packOutput, fileHelper));
        generator.addProvider(event.includeClient(), new ItemModels(packOutput, fileHelper));

    }
}
