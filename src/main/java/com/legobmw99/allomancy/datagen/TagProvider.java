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
import net.minecraft.resources.ResourceLocation;
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

        @Override
        protected void addTags(HolderLookup.Provider provider) {
            // TODO clean up to use Copy?

            for (Metal mt : Metal.values()) {
                if (mt.isVanilla()) {
                    continue;
                }

                var nugget = WorldSetup.NUGGETS.get(mt.getIndex()).get();
                var ingot = WorldSetup.INGOTS.get(mt.getIndex()).get();
                var block = WorldSetup.STORAGE_BLOCK_ITEMS.get(mt.getIndex()).get();

                addCommonTag("nuggets", nugget);
                addCommonTag("nuggets/" + mt.getName(), nugget);
                addCommonTag("ingots", ingot);
                addCommonTag("ingots/" + mt.getName(), ingot);
                addCommonTag("storage_blocks", block);
                addCommonTag("storage_blocks/" + mt.getName(), block);


            }
            addCommonTag("nuggets", WorldSetup.NUGGETS.get(WorldSetup.LEAD).get());
            addCommonTag("nuggets/lead", WorldSetup.NUGGETS.get(WorldSetup.LEAD).get());
            addCommonTag("ingots", WorldSetup.INGOTS.get(WorldSetup.LEAD).get());
            addCommonTag("ingots/lead", WorldSetup.INGOTS.get(WorldSetup.LEAD).get());
            addCommonTag("storage_blocks", WorldSetup.STORAGE_BLOCK_ITEMS.get(WorldSetup.LEAD).get());
            addCommonTag("storage_blocks/lead", WorldSetup.STORAGE_BLOCK_ITEMS.get(WorldSetup.LEAD).get());

            addCommonTag("nuggets", WorldSetup.NUGGETS.get(WorldSetup.SILVER).get());
            addCommonTag("nuggets/silver", WorldSetup.NUGGETS.get(WorldSetup.SILVER).get());
            addCommonTag("ingots", WorldSetup.INGOTS.get(WorldSetup.SILVER).get());
            addCommonTag("ingots/silver", WorldSetup.INGOTS.get(WorldSetup.SILVER).get());
            addCommonTag("storage_blocks", WorldSetup.STORAGE_BLOCK_ITEMS.get(WorldSetup.SILVER).get());
            addCommonTag("storage_blocks/silver", WorldSetup.STORAGE_BLOCK_ITEMS.get(WorldSetup.SILVER).get());


            for (int i = 0; i < WorldSetup.ORE_METALS.length; i++) {
                var ore = WorldSetup.ORE_BLOCKS_ITEMS.get(i).get();
                var ds_ore = WorldSetup.DEEPSLATE_ORE_BLOCKS_ITEMS.get(i).get();
                var raw_block = WorldSetup.RAW_ORE_BLOCKS_ITEMS.get(i).get();
                var raw = WorldSetup.RAW_ORE_ITEMS.get(i).get();

                addCommonTag("ores/" + WorldSetup.ORE_METALS[i], ore, ds_ore);
                addCommonTag("ores", ore, ds_ore);
                addCommonTag("ores_in_ground/stone", ore);
                addCommonTag("ores_in_ground/deepslate", ds_ore);
                addCommonTag("storage_blocks", raw_block);
                addCommonTag("storage_blocks/raw_" + WorldSetup.ORE_METALS[i], raw_block);
                addCommonTag("raw_materials", raw);
                addCommonTag("raw_materials/" + WorldSetup.ORE_METALS[i], raw);
            }

            tag(ItemTags.SWORDS).replace(false).add(CombatSetup.KOLOSS_BLADE.get());

            tag(ItemTags.CHEST_ARMOR).replace(false).add(CombatSetup.MISTCLOAK.get());

            tag(AllomancyTags.REPAIRS_MISTCLOAK).add(net.minecraft.world.item.Items.GRAY_WOOL);
            tag(AllomancyTags.OBSIDIAN_REPAIR).add(net.minecraft.world.item.Items.OBSIDIAN,
                                                   net.minecraft.world.item.Items.CRYING_OBSIDIAN);
            tag(AllomancyTags.REPAIRS_ALUMINUM).addTag(
                    ItemTags.create(ResourceLocation.fromNamespaceAndPath("c", "ingots/aluminum")));

            tag(ItemTags.GAZE_DISGUISE_EQUIPMENT).replace(false).add(CombatSetup.ALUMINUM_HELMET.get());

            tag(ItemTags.TRIMMABLE_ARMOR)
                    .replace(false)
                    .add(CombatSetup.ALUMINUM_HELMET.get())
                    .add(CombatSetup.MISTCLOAK.get());

            tag(AllomancyTags.FLAKES_TAG).add(WorldSetup.FLAKES.stream().map(Supplier::get).toArray(Item[]::new));

            tag(AllomancyTags.LERASIUM_CONVERSION).replace(false).addTag(Tags.Items.NETHER_STARS);
            tag(AllomancyTags.TIN_FOIL_HATS).add(CombatSetup.ALUMINUM_HELMET.get());
            tag(AllomancyTags.SPECIAL_EARRINGS).add(ExtrasSetup.CHARGED_BRONZE_EARRING.get());
        }

        private void addCommonTag(String name, Item... items) {
            Allomancy.LOGGER.debug("Creating item tag for c:{}", name);
            tag(ItemTags.create(ResourceLocation.fromNamespaceAndPath("c", name))).replace(false).add(items);
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
                addCommonTag("storage_blocks/" + mt.getName(), block);
                addCommonTag("storage_blocks", block);
                makePickaxeMineable(block);
                if (mt != Metal.ALUMINUM) {
                    addBeacon(block);
                }

            }

            var lead = WorldSetup.STORAGE_BLOCKS.get(WorldSetup.LEAD).get();
            addCommonTag("storage_blocks/lead", lead);
            var silver = WorldSetup.STORAGE_BLOCKS.get(WorldSetup.SILVER).get();
            addCommonTag("storage_blocks/silver", silver);
            addBeacon(silver);

            makePickaxeMineable(lead, silver);
            addCommonTag("storage_blocks", lead, silver);

        }

        private void addCommonTag(String name, Block... items) {
            Allomancy.LOGGER.debug("Creating block tag for c:{}", name);
            tag(BlockTags.create(ResourceLocation.fromNamespaceAndPath("c", name))).replace(false).add(items);
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

