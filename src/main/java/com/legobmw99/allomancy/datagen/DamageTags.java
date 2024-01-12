package com.legobmw99.allomancy.datagen;

import com.legobmw99.allomancy.Allomancy;
import com.legobmw99.allomancy.modules.combat.CombatSetup;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageType;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class DamageTags extends TagsProvider<DamageType> {
    public DamageTags(PackOutput out, CompletableFuture<HolderLookup.Provider> lookup, @Nullable ExistingFileHelper existingFileHelper) {
        super(out, Registries.DAMAGE_TYPE, lookup, Allomancy.MODID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider lookup) {
        tag(DamageTypeTags.IS_PROJECTILE).add(CombatSetup.COIN_DAMAGE);
        tag(CombatSetup.IS_COIN_HIT).add(CombatSetup.COIN_DAMAGE);
    }
}
