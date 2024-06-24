package com.legobmw99.allomancy.datagen;

import com.legobmw99.allomancy.Allomancy;
import com.legobmw99.allomancy.modules.combat.CombatSetup;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.DamageTypeTagsProvider;
import net.minecraft.tags.DamageTypeTags;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

class DamageTags extends DamageTypeTagsProvider {
    DamageTags(PackOutput out,
               CompletableFuture<HolderLookup.Provider> lookup,
               @Nullable ExistingFileHelper existingFileHelper) {
        super(out, lookup, Allomancy.MODID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider lookup) {
        tag(DamageTypeTags.IS_PROJECTILE).add(CombatSetup.COIN_DAMAGE);
        tag(Tags.DamageTypes.IS_MAGIC).add(CombatSetup.COIN_DAMAGE);
        tag(CombatSetup.IS_COIN_HIT).add(CombatSetup.COIN_DAMAGE);
    }
}
