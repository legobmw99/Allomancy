package com.legobmw99.allomancy.datagen;

import com.legobmw99.allomancy.Allomancy;
import com.legobmw99.allomancy.api.enums.Metal;
import com.legobmw99.allomancy.modules.combat.CombatSetup;
import com.legobmw99.allomancy.modules.extras.ExtrasSetup;
import com.legobmw99.allomancy.modules.world.WorldSetup;
import com.legobmw99.allomancy.util.AllomancyTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.*;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.world.entity.EntityType;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.data.ExistingFileHelper;

import java.util.concurrent.CompletableFuture;

public final class TagProvider {

    public static class EntityTypes extends EntityTypeTagsProvider {
        public EntityTypes(PackOutput output,
                           CompletableFuture<HolderLookup.Provider> provider,
                           ExistingFileHelper helper) {
            super(output, provider, Allomancy.MODID, helper);
        }

        @Override
        protected void addTags(HolderLookup.Provider provider) {
            tag(EntityTypeTags.IMPACT_PROJECTILES).replace(false).add(CombatSetup.NUGGET_PROJECTILE.get());

            tag(AllomancyTags.HEMALURGIC_CHARGERS)
                    .replace(false)
                    .addTag(EntityTypeTags.RAIDERS)
                    .add(EntityType.WITHER)
                    .add(EntityType.ENDER_DRAGON)
                    .add(EntityType.ELDER_GUARDIAN)
                    .add(EntityType.GHAST)
                    .add(EntityType.ZOGLIN)
                    .add(EntityType.WARDEN)
                    .add(EntityType.WITHER_SKELETON)
                    .add(EntityType.SKELETON_HORSE)
                    .add(EntityType.SHULKER);
        }
    }

    public static class Structures extends StructureTagsProvider {
        public Structures(PackOutput output,
                          CompletableFuture<HolderLookup.Provider> provider,
                          ExistingFileHelper helper) {
            super(output, provider, Allomancy.MODID, helper);
        }

        @Override
        protected void addTags(HolderLookup.Provider provider) {
            tag(AllomancyTags.SEEKABLE).replace(false).add(WorldSetup.WELL);
        }
    }


    static class Banners extends BannerPatternTagsProvider {

        Banners(PackOutput output, CompletableFuture<HolderLookup.Provider> provider, ExistingFileHelper helper) {
            super(output, provider, Allomancy.MODID, helper);
        }

        @Override
        protected void addTags(HolderLookup.Provider provider) {
            for (Metal mt : Metal.values()) {
                Allomancy.LOGGER.debug("Creating banner tag for {}", mt.getName());

                this
                        .tag(ExtrasSetup.PATTERN_KEYS.get(mt.getIndex()))
                        .add(ExtrasSetup.PATTERNS.get(mt.getIndex()).getKey());

            }
        }
    }


    public static class Biomes extends BiomeTagsProvider {
        public Biomes(PackOutput output,
                      CompletableFuture<HolderLookup.Provider> provider,
                      ExistingFileHelper helper) {
            super(output, provider, Allomancy.MODID, helper);
        }

        @Override
        protected void addTags(HolderLookup.Provider lookup) {
            tag(AllomancyTags.SPAWNS_WELLS).addTag(Tags.Biomes.IS_PEAK);
        }
    }

    static class DamageTypes extends DamageTypeTagsProvider {
        DamageTypes(PackOutput output, CompletableFuture<HolderLookup.Provider> provider, ExistingFileHelper helper) {
            super(output, provider, Allomancy.MODID, helper);
        }

        @Override
        protected void addTags(HolderLookup.Provider lookup) {
            tag(DamageTypeTags.IS_PROJECTILE).add(CombatSetup.COIN_DAMAGE);
            tag(AllomancyTags.IS_COIN_HIT).add(CombatSetup.COIN_DAMAGE);
        }
    }

    private TagProvider() {}
}
