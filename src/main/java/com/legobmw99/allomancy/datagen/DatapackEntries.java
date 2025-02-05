package com.legobmw99.allomancy.datagen;

import com.legobmw99.allomancy.modules.combat.CombatSetup;
import com.legobmw99.allomancy.modules.extras.ExtrasSetup;
import com.legobmw99.allomancy.modules.world.WorldSetup;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.damagesource.DamageType;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

final class DatapackEntries {

    public static final RegistrySetBuilder BUILDER = new RegistrySetBuilder()
            .add(Registries.DAMAGE_TYPE,
                 context -> context.register(CombatSetup.COIN_DAMAGE, new DamageType("allomancy.coin", 0.0f)))
            .add(Registries.CONFIGURED_FEATURE, WorldSetup::bootstrapConfigured)
            .add(Registries.PLACED_FEATURE, WorldSetup::bootstrapPlaced)
            .add(Registries.BANNER_PATTERN, ExtrasSetup::bootstrapBanners)
            .add(Registries.STRUCTURE, WorldSetup::bootstrapStructures)
            .add(Registries.TEMPLATE_POOL, WorldSetup::bootstrapTemplatePools)
            .add(Registries.STRUCTURE_SET, WorldSetup::bootstrapStructureSets)
            .add(NeoForgeRegistries.Keys.BIOME_MODIFIERS, WorldSetup::bootstrapBiomeModifier);


    private DatapackEntries() {}
}
