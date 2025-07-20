package com.legobmw99.allomancy.datagen;

import com.legobmw99.allomancy.Allomancy;
import com.legobmw99.allomancy.modules.combat.CombatSetup;
import com.legobmw99.allomancy.modules.world.WorldSetup;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.world.damagesource.DamageType;
import net.minecraftforge.common.data.DatapackBuiltinEntriesProvider;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Set;
import java.util.concurrent.CompletableFuture;

public class DatapackEntries extends DatapackBuiltinEntriesProvider {
    public static final RegistrySetBuilder BUILDER = new RegistrySetBuilder()
            .add(Registries.DAMAGE_TYPE,
                 context -> context.register(CombatSetup.COIN_DAMAGE, new DamageType("allomancy.coin", 0.0f)))
            .add(Registries.CONFIGURED_FEATURE, WorldSetup::bootstrapConfigured)
            .add(Registries.PLACED_FEATURE, WorldSetup::bootstrapPlaced)
            .add(Registries.STRUCTURE, WorldSetup::bootstrapStructures)
            .add(Registries.TEMPLATE_POOL, WorldSetup::bootstrapTemplatePools)
            .add(Registries.STRUCTURE_SET, WorldSetup::bootstrapStructureSets)
            .add(ForgeRegistries.Keys.BIOME_MODIFIERS, WorldSetup::bootstrapBiomeModifier);


    public DatapackEntries(PackOutput output, CompletableFuture<HolderLookup.Provider> provider) {
        super(output, provider, BUILDER, Set.of("minecraft", Allomancy.MODID));
    }
}