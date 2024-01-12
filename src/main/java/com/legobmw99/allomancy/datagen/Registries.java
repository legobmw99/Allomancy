package com.legobmw99.allomancy.datagen;

import com.legobmw99.allomancy.Allomancy;
import com.legobmw99.allomancy.modules.combat.CombatSetup;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.data.PackOutput;
import net.minecraft.world.damagesource.DamageType;
import net.neoforged.neoforge.common.data.DatapackBuiltinEntriesProvider;

import java.util.Set;
import java.util.concurrent.CompletableFuture;

public class Registries extends DatapackBuiltinEntriesProvider {

    public static final RegistrySetBuilder BUILDER = new RegistrySetBuilder().add(net.minecraft.core.registries.Registries.DAMAGE_TYPE, context -> {
        context.register(CombatSetup.COIN_DAMAGE, new DamageType("allomancy.coin", 0.0f));
    });

    public Registries(PackOutput output, CompletableFuture<HolderLookup.Provider> provider) {
        super(output, provider, BUILDER, Set.of("minecraft", Allomancy.MODID));
    }
}
