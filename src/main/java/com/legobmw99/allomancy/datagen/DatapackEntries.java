package com.legobmw99.allomancy.datagen;

import com.legobmw99.allomancy.modules.combat.CombatSetup;
import com.legobmw99.allomancy.modules.extras.ExtrasSetup;
import com.legobmw99.allomancy.modules.materials.MaterialsSetup;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.damagesource.DamageType;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

class DatapackEntries {

    public static final RegistrySetBuilder BUILDER = new RegistrySetBuilder()
            .add(Registries.DAMAGE_TYPE,
                 context -> context.register(CombatSetup.COIN_DAMAGE, new DamageType("allomancy.coin", 0.0f)))
            .add(Registries.CONFIGURED_FEATURE, MaterialsSetup::bootstrapConfigured)
            .add(Registries.PLACED_FEATURE, MaterialsSetup::bootstrapPlaced)
            .add(Registries.BANNER_PATTERN, ExtrasSetup::bootstrapBanners)
            .add(Registries.STRUCTURE, ExtrasSetup::bootstrapStructures)
            .add(Registries.TEMPLATE_POOL, ExtrasSetup::bootstrapTemplatePools)
            .add(Registries.STRUCTURE_SET, ExtrasSetup::bootstrapStructureSets)
            .add(NeoForgeRegistries.Keys.BIOME_MODIFIERS, MaterialsSetup::bootstrapBiomeModifier);


}
