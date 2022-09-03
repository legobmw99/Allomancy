package com.legobmw99.allomancy.datagen;

import com.legobmw99.allomancy.Allomancy;
import net.minecraft.data.DataGenerator;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class DataGenerators {

    @SubscribeEvent
    public static void gatherData(GatherDataEvent event) {
        DataGenerator generator = event.getGenerator();
        generator.addProvider(event.includeServer(), new Recipes(generator));
        generator.addProvider(event.includeServer(), new LootTables(generator));
        BlockTags blocktags = new BlockTags(generator, Allomancy.MODID, event.getExistingFileHelper());
        generator.addProvider(event.includeServer(), blocktags);
        generator.addProvider(event.includeServer(), new ItemTags(generator, blocktags, Allomancy.MODID, event.getExistingFileHelper()));
        generator.addProvider(event.includeServer(), new Advancements(generator, event.getExistingFileHelper()));
        generator.addProvider(event.includeServer(), new BannerTag(generator, event.getExistingFileHelper()));

        generator.addProvider(event.includeClient(), new Languages(generator, Allomancy.MODID, "en_us"));
        generator.addProvider(event.includeClient(), new BlockStates(generator, Allomancy.MODID, event.getExistingFileHelper()));
        generator.addProvider(event.includeClient(), new ItemModels(generator, Allomancy.MODID, event.getExistingFileHelper()));

    }
}
