package com.legobmw99.allomancy.datagen;

import com.legobmw99.allomancy.Allomancy;
import com.legobmw99.allomancy.modules.combat.CombatSetup;
import com.legobmw99.allomancy.modules.extras.ExtrasSetup;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.EntityTypeTagsProvider;
import net.minecraft.world.entity.EntityType;

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

        tag(ExtrasSetup.HEMALURGIC_CHARGERS)
                .replace(false)
                .addTag(net.minecraft.tags.EntityTypeTags.RAIDERS)
                .add(EntityType.WITHER)
                .add(EntityType.ENDER_DRAGON)
                .add(EntityType.WITHER_SKELETON)
                .add(EntityType.ELDER_GUARDIAN)
                .add(EntityType.GHAST)
                .add(EntityType.ZOGLIN)
                .add(EntityType.WARDEN)
                .add(EntityType.WITHER_SKELETON)
                .add(EntityType.SKELETON_HORSE)
                .add(EntityType.SHULKER);
    }
}
