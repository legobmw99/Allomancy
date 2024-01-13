package com.legobmw99.allomancy.modules.materials.world;

import com.legobmw99.allomancy.Allomancy;
import com.legobmw99.allomancy.modules.materials.MaterialsConfig;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.entries.LootTableReference;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.LootTableLoadEvent;

public class LootTableInjector {
    // TODO replace https://docs.neoforged.net/docs/resources/server/glm/
    @SubscribeEvent
    public static void onLootTableLoad(final LootTableLoadEvent event) {
        if (event.getName() == null) {
            return;
        }

        String name = event.getName().toString();
        if (MaterialsConfig.generate_lerasium.get() &&
            (name.equals("minecraft:chests/simple_dungeon") || name.equals("minecraft:chests/desert_pyramid") || name.equals("minecraft:chests/jungle_temple") ||
             name.equals("minecraft:chests/woodland_mansion") || name.equals("minecraft:chests/end_city_treasure"))) {
            //Inject a Lerasium loot table into the above vanilla tables
            Allomancy.LOGGER.info("Adding lerasium to Loot Table: " + name);
            event
                    .getTable()
                    .addPool(LootPool
                                     .lootPool()
                                     .name("lerasium_inject")
                                     .add(LootTableReference.lootTableReference(new ResourceLocation(Allomancy.MODID, "inject/lerasium")))
                                     .build());
        }
        if (MaterialsConfig.generate_unbreakable_daggers.get() &&
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
