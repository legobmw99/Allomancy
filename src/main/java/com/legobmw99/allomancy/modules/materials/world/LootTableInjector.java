package com.legobmw99.allomancy.modules.materials.world;

import com.legobmw99.allomancy.Allomancy;
import com.legobmw99.allomancy.modules.materials.MaterialsConfig;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.TableLootEntry;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.LootTableLoadEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class LootTableInjector {
    @SubscribeEvent
    public void onLootTableLoad(final LootTableLoadEvent event) {
        String name = event.getName().toString();
        if (MaterialsConfig.generate_lerasium.get() &&
                (name.equals("minecraft:chests/simple_dungeon") || name.equals("minecraft:chests/desert_pyramid")
                        || name.equals("minecraft:chests/jungle_temple") || name.equals("minecraft:chests/woodland_mansion")
                        || name.equals("minecraft:chests/end_city_treasure"))) {
            //Inject a Lerasium loot table into the above vanilla tables
            Allomancy.LOGGER.debug("Adding lerasium to Loot Table: " + name);
            event.getTable().addPool(LootPool.builder().name("lerasium_inject").addEntry(TableLootEntry.builder(new ResourceLocation(Allomancy.MODID, "inject/lerasium"))).build());
        }
        if (MaterialsConfig.generate_unbreakable_daggers.get() &&
                (name.equals("minecraft:chests/end_city_treasure") || name.equals("minecraft:chests/woodland_mansion")
                        || name.equals("minecraft:chests/pillager_outpost"))) {
            //Inject a unbreakable Obsidian Dagger loot table into the above vanilla tables
            Allomancy.LOGGER.debug("Adding obsidian dagger to Loot Table: " + name);
            event.getTable().addPool(LootPool.builder().name("obsidian_dagger").addEntry(TableLootEntry.builder(new ResourceLocation(Allomancy.MODID, "inject/obsidian_dagger"))).build());
        }
    }
}
