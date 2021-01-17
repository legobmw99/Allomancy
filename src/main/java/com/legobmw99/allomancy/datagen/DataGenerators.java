package com.legobmw99.allomancy.datagen;

import com.legobmw99.allomancy.Allomancy;
import net.minecraft.data.DataGenerator;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.GatherDataEvent;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class DataGenerators {

    @SubscribeEvent
    public static void gatherData(GatherDataEvent event) {
        DataGenerator generator = event.getGenerator();
        if (event.includeServer()) {
            generator.addProvider(new Recipes(generator));
            generator.addProvider(new LootTables(generator));
            BlockTags blocktags = new BlockTags(generator, Allomancy.MODID, event.getExistingFileHelper());
            generator.addProvider(blocktags);
            generator.addProvider(new ItemTags(generator, blocktags, Allomancy.MODID, event.getExistingFileHelper()));
            generator.addProvider(new Advancements(generator));
        }
        if (event.includeClient()) {
            generator.addProvider(new Languages(generator, Allomancy.MODID, "en_us"));
            generator.addProvider(new BlockStates(generator, Allomancy.MODID, event.getExistingFileHelper()));
            generator.addProvider(new ItemModels(generator, Allomancy.MODID, event.getExistingFileHelper()));
        }
    }
}
