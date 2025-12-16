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
import net.minecraft.resources.Identifier;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.data.BlockTagCopyingItemTagProvider;
import net.neoforged.neoforge.common.data.BlockTagsProvider;

import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

public final class TagProvider {

    public static class EntityTypes extends EntityTypeTagsProvider {
        public EntityTypes(PackOutput output, CompletableFuture<HolderLookup.Provider> provider) {
            super(output, provider, Allomancy.MODID);
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
        public Structures(PackOutput output, CompletableFuture<HolderLookup.Provider> provider) {
            super(output, provider, Allomancy.MODID);
        }

        @Override
        protected void addTags(HolderLookup.Provider provider) {
            tag(AllomancyTags.SEEKABLE).replace(false).add(WorldSetup.WELL);
        }
    }


    static class Items extends BlockTagCopyingItemTagProvider {

        Items(PackOutput gen,
              CompletableFuture<HolderLookup.Provider> lookupProvider,
              CompletableFuture<TagsProvider.TagLookup<Block>> blockTagProvider) {
            super(gen, lookupProvider, blockTagProvider, Allomancy.MODID);
        }

        private void addMetalTags(int index) {
            var nugget = WorldSetup.NUGGETS.get(index).get();
            var ingot = WorldSetup.INGOTS.get(index).get();
            var block = WorldSetup.STORAGE_BLOCK_ITEMS.get(index).get();
            var raw = WorldSetup.RAW_ORE_ITEMS.get(index).get();

            addCommonTag("nuggets", nugget);
            tag(AllomancyTags.NUGGET_TAGS.get(index)).add(nugget);
            addCommonTag("ingots", ingot);
            tag(AllomancyTags.INGOT_TAGS.get(index)).add(ingot);
            addCommonTag("storage_blocks", block);
            tag(AllomancyTags.STORAGE_BLOCK_ITEM_TAGS.get(index)).add(block);
            addCommonTag("raw_materials", raw);
            tag(AllomancyTags.RAW_ORE_TAGS.get(index)).add(raw);
        }

        @Override
        protected void addTags(HolderLookup.Provider provider) {
            for (Metal mt : Metal.values()) {
                if (mt.isVanilla()) {
                    continue;
                }
                addMetalTags(mt.getIndex());

            }
            addMetalTags(WorldSetup.LEAD);
            addMetalTags(WorldSetup.SILVER);

            for (int i = 0; i < WorldSetup.ORE_METALS.length; i++) {
                var ore = WorldSetup.ORE_BLOCKS_ITEMS.get(i).get();
                var ds_ore = WorldSetup.DEEPSLATE_ORE_BLOCKS_ITEMS.get(i).get();
                var raw_block = WorldSetup.RAW_ORE_BLOCKS_ITEMS.get(i).get();

                addCommonTag("ores/" + WorldSetup.ORE_METALS[i], ore, ds_ore);
                addCommonTag("ores", ore, ds_ore);
                addCommonTag("ores_in_ground/stone", ore);
                addCommonTag("ores_in_ground/deepslate", ds_ore);
                addCommonTag("storage_blocks", raw_block);
                addCommonTag("storage_blocks/raw_" + WorldSetup.ORE_METALS[i], raw_block);

            }

            tag(ItemTags.SWORDS).replace(false).add(CombatSetup.KOLOSS_BLADE.get());
            tag(ItemTags.HEAD_ARMOR).replace(false).add(CombatSetup.ALUMINUM_HELMET.get());
            tag(ItemTags.GAZE_DISGUISE_EQUIPMENT).replace(false).add(CombatSetup.ALUMINUM_HELMET.get());
            tag(ItemTags.CHEST_ARMOR).replace(false).add(CombatSetup.MISTCLOAK.get());
            tag(ItemTags.TRIMMABLE_ARMOR)
                    .replace(false)
                    .add(CombatSetup.ALUMINUM_HELMET.get())
                    .add(CombatSetup.MISTCLOAK.get());

            tag(AllomancyTags.FLAKES_TAG).add(WorldSetup.FLAKES.stream().map(Supplier::get).toArray(Item[]::new));
            tag(AllomancyTags.REPAIRS_MISTCLOAK).add(net.minecraft.world.item.Items.GRAY_WOOL);
            tag(AllomancyTags.OBSIDIAN_REPAIR).add(net.minecraft.world.item.Items.OBSIDIAN,
                                                   net.minecraft.world.item.Items.CRYING_OBSIDIAN);
            tag(AllomancyTags.REPAIRS_ALUMINUM).addTag(AllomancyTags.INGOT_TAGS.get(Metal.ALUMINUM.getIndex()));
            tag(AllomancyTags.LERASIUM_CONVERSION).addTag(Tags.Items.NETHER_STARS);
            tag(AllomancyTags.TIN_FOIL_HATS).add(CombatSetup.ALUMINUM_HELMET.get());
            tag(AllomancyTags.SPECIAL_EARRINGS).add(ExtrasSetup.CHARGED_BRONZE_EARRING.get());
            tag(AllomancyTags.ONE_HIT_WEAPONS).add(CombatSetup.KOLOSS_BLADE.get());
        }

        private void addCommonTag(String name, Item... items) {
            Allomancy.LOGGER.debug("Creating item tag for c:{}", name);
            tag(ItemTags.create(Identifier.fromNamespaceAndPath("c", name))).replace(false).add(items);
        }
    }


    static class Blocks extends BlockTagsProvider {

        Blocks(PackOutput gen, CompletableFuture<HolderLookup.Provider> lookupProvider) {
            super(gen, lookupProvider, Allomancy.MODID);
        }

        @Override
        protected void addTags(HolderLookup.Provider provider) {

            for (int i = 0; i < WorldSetup.ORE_METALS.length; i++) {
                var block = WorldSetup.ORE_BLOCKS.get(i).get();
                var ds = WorldSetup.DEEPSLATE_ORE_BLOCKS.get(i).get();
                var raw = WorldSetup.RAW_ORE_BLOCKS.get(i).get();

                addCommonTag("ores/" + WorldSetup.ORE_METALS[i], block, ds);
                addCommonTag("ores", block, ds);
                addCommonTag("ores_in_ground/stone", block);
                addCommonTag("ores_in_ground/deepslate", ds);

                addCommonTag("storage_blocks", raw);
                addCommonTag("storage_blocks/raw_" + WorldSetup.ORE_METALS[i], raw);

                makePickaxeMineable(block, ds, raw);
            }

            for (Metal mt : Metal.values()) {
                if (mt.isVanilla()) {
                    continue;
                }
                var block = WorldSetup.STORAGE_BLOCKS.get(mt.getIndex()).get();
                tag(AllomancyTags.STORAGE_BLOCK_TAGS.get(mt.getIndex())).add(block);
                addCommonTag("storage_blocks", block);
                makePickaxeMineable(block);
                if (mt != Metal.ALUMINUM) {
                    addBeacon(block);
                }

            }

            var lead = WorldSetup.STORAGE_BLOCKS.get(WorldSetup.LEAD).get();
            tag(AllomancyTags.STORAGE_BLOCK_TAGS.get(WorldSetup.LEAD)).add(lead);

            var silver = WorldSetup.STORAGE_BLOCKS.get(WorldSetup.SILVER).get();
            tag(AllomancyTags.STORAGE_BLOCK_TAGS.get(WorldSetup.SILVER)).add(silver);
            addBeacon(silver);

            makePickaxeMineable(lead, silver);
            addCommonTag("storage_blocks", lead, silver);

        }

        private void addCommonTag(String name, Block... items) {
            Allomancy.LOGGER.debug("Creating block tag for c:{}", name);
            tag(BlockTags.create(Identifier.fromNamespaceAndPath("c", name))).replace(false).add(items);
        }


        private void makePickaxeMineable(Block... items) {
            this.tag(BlockTags.MINEABLE_WITH_PICKAXE).replace(false).add(items);
            this.tag(BlockTags.NEEDS_STONE_TOOL).replace(false).add(items);

        }


        private void addBeacon(Block... items) {
            this.tag(BlockTags.BEACON_BASE_BLOCKS).replace(false).add(items);
        }
    }


    static class Banners extends BannerPatternTagsProvider {

        Banners(PackOutput pGenerator, CompletableFuture<HolderLookup.Provider> registries) {
            super(pGenerator, registries, Allomancy.MODID);
        }

        @Override
        protected void addTags(HolderLookup.Provider provider) {
            for (Metal mt : Metal.values()) {
                Allomancy.LOGGER.debug("Creating banner tag for {}", mt.getName());
                this.tag(AllomancyTags.PATTERN_TAGS.get(mt.getIndex())).add(ExtrasSetup.PATTERNS.get(mt.getIndex()));
            }
        }
    }


    public static class Biomes extends BiomeTagsProvider {
        public Biomes(PackOutput output, CompletableFuture<HolderLookup.Provider> provider) {
            super(output, provider, Allomancy.MODID);
        }

        @Override
        protected void addTags(HolderLookup.Provider lookup) {
            tag(AllomancyTags.SPAWNS_WELLS).addTag(Tags.Biomes.IS_MOUNTAIN_PEAK);
        }
    }

    static class DamageTypes extends DamageTypeTagsProvider {
        DamageTypes(PackOutput out, CompletableFuture<HolderLookup.Provider> lookup) {
            super(out, lookup, Allomancy.MODID);
        }

        @Override
        protected void addTags(HolderLookup.Provider lookup) {
            tag(DamageTypeTags.IS_PROJECTILE).add(CombatSetup.COIN_DAMAGE);
            tag(Tags.DamageTypes.IS_MAGIC).add(CombatSetup.COIN_DAMAGE);
            tag(AllomancyTags.IS_COIN_HIT).add(CombatSetup.COIN_DAMAGE);
        }
    }

    private TagProvider() {}
}

