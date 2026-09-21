package com.legobmw99.allomancy.datagen;

import com.legobmw99.allomancy.modules.combat.CombatSetup;
import com.legobmw99.allomancy.modules.extras.ExtrasSetup;
import com.legobmw99.allomancy.modules.world.WorldSetup;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.advancements.AdvancementProvider;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.Collections;
import java.util.List;

final class DatapackEntries {

    public static final RegistrySetBuilder WORLD_BUILDER = new RegistrySetBuilder()
            .add(Registries.DAMAGE_TYPE,
                 context -> context.register(CombatSetup.COIN_DAMAGE, new DamageType("allomancy.coin", 0.0f)))
            .add(Registries.FEATURE, WorldSetup::bootstrapFeature)
            .add(Registries.PLACED_FEATURE, WorldSetup::bootstrapPlaced)
            .add(Registries.BANNER_PATTERN, ExtrasSetup::bootstrapBanners)
            .add(Registries.STRUCTURE, WorldSetup::bootstrapStructures)
            .add(Registries.TEMPLATE_POOL, WorldSetup::bootstrapTemplatePools)
            .add(Registries.STRUCTURE_SET, WorldSetup::bootstrapStructureSets)
            .add(NeoForgeRegistries.Keys.BIOME_MODIFIERS, WorldSetup::bootstrapBiomeModifier);

    public static final RegistrySetBuilder RELOADABLE_BUILDER = new RegistrySetBuilder()
            .add(Registries.LOOT_TABLE, new LootTableProvider(Collections.emptySet(),
                                                              List.of(new LootTableProvider.SubProviderEntry(
                                                                              BlockLootTables::new,
                                                                              LootContextParamSets.BLOCK),
                                                                      new LootTableProvider.SubProviderEntry(
                                                                              StructureLootTables::new,
                                                                              LootContextParamSets.CHEST))))
            .add(RecipeProvider.asBootstrap(Recipes::new))
            .add(Registries.ADVANCEMENT, new AdvancementProvider(List.of(Advancements::new)));

    private DatapackEntries() {}
}
