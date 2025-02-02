package com.legobmw99.allomancy.datagen;

import com.legobmw99.allomancy.Allomancy;
import com.legobmw99.allomancy.modules.combat.CombatSetup;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.EntityTypeTagsProvider;

import java.util.concurrent.CompletableFuture;

public class EntityTypeTags extends EntityTypeTagsProvider {
    public EntityTypeTags(PackOutput output, CompletableFuture<HolderLookup.Provider> provider) {
        super(output, provider, Allomancy.MODID);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        tag(net.minecraft.tags.EntityTypeTags.IMPACT_PROJECTILES)
                .replace(false)
                .add(CombatSetup.NUGGET_PROJECTILE.get());
    }
}
