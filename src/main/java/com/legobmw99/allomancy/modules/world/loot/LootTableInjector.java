package com.legobmw99.allomancy.modules.world.loot;

import com.legobmw99.allomancy.Allomancy;
import com.legobmw99.allomancy.modules.world.WorldConfig;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.entries.LootTableReference;
import net.minecraftforge.event.LootTableLoadEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class LootTableInjector {
    @SubscribeEvent
    public static void onLootTableLoad(final LootTableLoadEvent event) {
        String name = event.getName().toString();

        if (WorldConfig.generate_unbreakable_daggers.get() &&
            (name.equals("minecraft:chests/end_city_treasure") || name.equals("minecraft:chests/woodland_mansion") || name.equals("minecraft:chests/pillager_outpost"))) {
            //Inject an unbreakable Obsidian Dagger loot table into the above vanilla tables
            Allomancy.LOGGER.info("Adding obsidian dagger to Loot Table: " + name);
            event
                    .getTable()
                    .addPool(LootPool
                                     .lootPool()
                                     .name("obsidian_dagger")
                                     .add(LootTableReference.lootTableReference(new ResourceLocation(Allomancy.MODID, "inject/obsidian_dagger")))
                                     .build());
        }
    }
}
