package com.legobmw99.allomancy.datagen;

import com.legobmw99.allomancy.Allomancy;
import com.legobmw99.allomancy.modules.combat.CombatSetup;
import com.legobmw99.allomancy.modules.extras.ExtrasSetup;
import com.legobmw99.allomancy.modules.materials.MaterialsSetup;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.world.damagesource.DamageType;
import net.neoforged.neoforge.common.data.DatapackBuiltinEntriesProvider;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.Set;
import java.util.concurrent.CompletableFuture;

public class DatapackEntries extends DatapackBuiltinEntriesProvider {

    public static final RegistrySetBuilder BUILDER = new RegistrySetBuilder()
            .add(Registries.DAMAGE_TYPE, context -> {
                context.register(CombatSetup.COIN_DAMAGE, new DamageType("allomancy.coin", 0.0f));
            })
            .add(Registries.CONFIGURED_FEATURE, MaterialsSetup::bootstrapConfigured)
            .add(Registries.PLACED_FEATURE, MaterialsSetup::bootstrapPlaced)
            .add(Registries.BANNER_PATTERN, ExtrasSetup::bootstrapBanners)
            .add(NeoForgeRegistries.Keys.BIOME_MODIFIERS, MaterialsSetup::bootstrapBiomeModifier);

    public DatapackEntries(PackOutput output, CompletableFuture<HolderLookup.Provider> provider) {
        super(output, provider, BUILDER, Set.of("minecraft", Allomancy.MODID));
    }
}
