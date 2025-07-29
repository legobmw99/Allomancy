package com.legobmw99.allomancy.datagen;

import com.legobmw99.allomancy.Allomancy;
import com.legobmw99.allomancy.api.enums.Metal;
import com.legobmw99.allomancy.modules.combat.CombatSetup;
import com.legobmw99.allomancy.modules.extras.ExtrasSetup;
import com.legobmw99.allomancy.modules.world.WorldSetup;
import com.legobmw99.allomancy.util.AllomancyTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.data.ExistingFileHelper;

import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

public class ItemTagProvider extends ItemTagsProvider {


    public ItemTagProvider(PackOutput gen,
                           CompletableFuture<HolderLookup.Provider> lookupProvider,
                           CompletableFuture<TagsProvider.TagLookup<Block>> blockTagProvider,
                           ExistingFileHelper exFileHelper) {
        super(gen, lookupProvider, blockTagProvider, Allomancy.MODID, exFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider pProvider) {

        for (Metal mt : Metal.values()) {
            if (mt.isVanilla()) {
                continue;
            }

            var nugget = WorldSetup.NUGGETS.get(mt.getIndex()).getKey();
            var ingot = WorldSetup.INGOTS.get(mt.getIndex()).getKey();
            var block = WorldSetup.STORAGE_BLOCK_ITEMS.get(mt.getIndex()).getKey();

            addForgeTag("nuggets", nugget);
            addForgeTag("nuggets/" + mt.getName(), nugget);
            addForgeTag("ingots", ingot);
            addForgeTag("ingots/" + mt.getName(), ingot);
            addForgeTag("storage_blocks", block);
            addForgeTag("storage_blocks/" + mt.getName(), block);


        }
        addForgeTag("nuggets", WorldSetup.NUGGETS.get(WorldSetup.LEAD).getKey());
        addForgeTag("nuggets/lead", WorldSetup.NUGGETS.get(WorldSetup.LEAD).getKey());
        addForgeTag("ingots", WorldSetup.INGOTS.get(WorldSetup.LEAD).getKey());
        addForgeTag("ingots/lead", WorldSetup.INGOTS.get(WorldSetup.LEAD).getKey());
        addForgeTag("storage_blocks", WorldSetup.STORAGE_BLOCK_ITEMS.get(WorldSetup.LEAD).getKey());
        addForgeTag("storage_blocks/lead", WorldSetup.STORAGE_BLOCK_ITEMS.get(WorldSetup.LEAD).getKey());

        addForgeTag("nuggets", WorldSetup.NUGGETS.get(WorldSetup.SILVER).getKey());
        addForgeTag("nuggets/silver", WorldSetup.NUGGETS.get(WorldSetup.SILVER).getKey());
        addForgeTag("ingots", WorldSetup.INGOTS.get(WorldSetup.SILVER).getKey());
        addForgeTag("ingots/silver", WorldSetup.INGOTS.get(WorldSetup.SILVER).getKey());
        addForgeTag("storage_blocks", WorldSetup.STORAGE_BLOCK_ITEMS.get(WorldSetup.SILVER).getKey());
        addForgeTag("storage_blocks/silver", WorldSetup.STORAGE_BLOCK_ITEMS.get(WorldSetup.SILVER).getKey());


        for (int i = 0; i < WorldSetup.ORE_METALS.length; i++) {
            var ore = WorldSetup.ORE_BLOCKS_ITEMS.get(i).getKey();
            var ds_ore = WorldSetup.DEEPSLATE_ORE_BLOCKS_ITEMS.get(i).getKey();
            var raw_block = WorldSetup.RAW_ORE_BLOCKS_ITEMS.get(i).getKey();
            var raw = WorldSetup.RAW_ORE_ITEMS.get(i).getKey();

            addForgeTag("ores/" + WorldSetup.ORE_METALS[i], ore, ds_ore);
            addForgeTag("ores", ore, ds_ore);
            addForgeTag("ores_in_ground/stone", ore);
            addForgeTag("ores_in_ground/deepslate", ds_ore);
            addForgeTag("storage_blocks", raw_block);
            addForgeTag("storage_blocks/raw_" + WorldSetup.ORE_METALS[i], raw_block);
            addForgeTag("raw_materials", raw);
            addForgeTag("raw_materials/" + WorldSetup.ORE_METALS[i], raw);
        }

        tag(ItemTags.SWORDS).replace(false).add(CombatSetup.KOLOSS_BLADE.get());
        tag(ItemTags.TRIMMABLE_ARMOR)
                .replace(false)
                .add(CombatSetup.ALUMINUM_HELMET.get())
                .add(CombatSetup.MISTCLOAK.get());

        tag(AllomancyTags.FLAKES_TAG).add(WorldSetup.FLAKES.stream().map(Supplier::get).toArray(Item[]::new));
        tag(AllomancyTags.REPAIRS_MISTCLOAK).add(net.minecraft.world.item.Items.GRAY_WOOL);
        tag(AllomancyTags.OBSIDIAN_REPAIR).add(net.minecraft.world.item.Items.OBSIDIAN,
                                               net.minecraft.world.item.Items.CRYING_OBSIDIAN);
        tag(AllomancyTags.REPAIRS_ALUMINUM).addTag(
                ItemTags.create(new ResourceLocation("forge", "ingots/aluminum")));

        tag(AllomancyTags.LERASIUM_CONVERSION).addTag(Tags.Items.NETHER_STARS);
        tag(AllomancyTags.TIN_FOIL_HATS).add(CombatSetup.ALUMINUM_HELMET.get());
        tag(AllomancyTags.SPECIAL_EARRINGS).add(ExtrasSetup.CHARGED_BRONZE_EARRING.get());
        tag(AllomancyTags.ONE_HIT_WEAPONS).add(CombatSetup.KOLOSS_BLADE.get());

    }

    private void addForgeTag(String name, ResourceKey<Item>... items) {
        // see ForgeItemTagsProvider
        Allomancy.LOGGER.debug("Creating item tag for forge:" + name);

        tag(net.minecraft.tags.ItemTags.create(new ResourceLocation("forge", name))).replace(false).add(items);
    }


    @Override
    public String getName() {
        return "Allomancy Item Tags";
    }
}
