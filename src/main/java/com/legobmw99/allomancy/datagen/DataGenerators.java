package com.legobmw99.allomancy.datagen;

import com.legobmw99.allomancy.Allomancy;
import com.legobmw99.allomancy.modules.combat.CombatSetup;
import com.legobmw99.allomancy.modules.extras.ExtrasSetup;
import com.legobmw99.allomancy.modules.world.WorldSetup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.advancements.AdvancementProvider;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import net.neoforged.neoforge.data.event.GatherDataRegistryEntriesEvent;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.Collections;
import java.util.List;

@EventBusSubscriber(modid = Allomancy.MODID, value = Dist.CLIENT)
public final class DataGenerators {

    @SubscribeEvent
    public static void gatherDatapackEntries(final GatherDataRegistryEntriesEvent event) {
        event
                .add(Registries.DAMAGE_TYPE,
                     context -> context.register(CombatSetup.COIN_DAMAGE, new DamageType("allomancy.coin", 0.0f)))
                .add(Registries.FEATURE, WorldSetup::bootstrapFeature)
                .add(Registries.PLACED_FEATURE, WorldSetup::bootstrapPlaced)
                .add(Registries.BANNER_PATTERN, ExtrasSetup::bootstrapBanners)
                .add(Registries.STRUCTURE, WorldSetup::bootstrapStructures)
                .add(Registries.TEMPLATE_POOL, WorldSetup::bootstrapTemplatePools)
                .add(Registries.STRUCTURE_SET, WorldSetup::bootstrapStructureSets)
                .add(NeoForgeRegistries.Keys.BIOME_MODIFIERS, WorldSetup::bootstrapBiomeModifier)
                .add(Registries.LOOT_TABLE, new LootTableProvider(Collections.emptySet(),
                                                                  List.of(new LootTableProvider.SubProviderEntry(
                                                                                  BlockLootTables::new,
                                                                                  LootContextParamSets.BLOCK),
                                                                          new LootTableProvider.SubProviderEntry(
                                                                                  StructureLootTables::new,
                                                                                  LootContextParamSets.CHEST))))
                .add(RecipeProvider.asBootstrap(Recipes::new))
                .add(Registries.ADVANCEMENT, new AdvancementProvider(List.of(Advancements::new)));

    }

    @SubscribeEvent
    public static void gatherData(final GatherDataEvent.Client event) {

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
