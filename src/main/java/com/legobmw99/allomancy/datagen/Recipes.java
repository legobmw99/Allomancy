package com.legobmw99.allomancy.datagen;

import com.legobmw99.allomancy.Allomancy;
import com.legobmw99.allomancy.api.enums.Metal;
import com.legobmw99.allomancy.modules.combat.CombatSetup;
import com.legobmw99.allomancy.modules.consumables.ConsumeSetup;
import com.legobmw99.allomancy.modules.consumables.item.recipe.VialItemRecipe;
import com.legobmw99.allomancy.modules.extras.ExtrasSetup;
import com.legobmw99.allomancy.modules.materials.MaterialsSetup;
import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.neoforged.neoforge.common.Tags;

import java.util.*;
import java.util.concurrent.CompletableFuture;

final class Recipes extends RecipeProvider {
    private final Map<Character, Ingredient> defaultIngredients = new HashMap<>();

    private final HolderGetter<Item> items;

    private Recipes(HolderLookup.Provider registries, RecipeOutput output) {
        super(registries, output);
        this.items = registries.lookupOrThrow(Registries.ITEM);

        add('i', Tags.Items.INGOTS_IRON);
        add('g', Tags.Items.INGOTS_GOLD);
        add('s', Items.STICK);
        add('S', ItemTags.WOODEN_SLABS);
        add('G', Items.GLASS);
        add('I', Tags.Items.STORAGE_BLOCKS_IRON);
        add('W', Items.GRAY_WOOL);
        add('O', Tags.Items.OBSIDIANS);
        add('C', Items.COBBLESTONE);
        add('A', ing("c:ingots/aluminum"));

    }


    private void buildShapeless(RecipeOutput consumer,
                                RecipeCategory cat,
                                ItemLike result,
                                int count,
                                Item criterion,
                                Ingredient... ingredients) {
        buildShapeless(consumer, cat, result, count, criterion, "", ingredients);
    }

    private void buildShapeless(RecipeOutput consumer,
                                RecipeCategory cat,
                                ItemLike result,
                                int count,
                                Item criterion,
                                String save,
                                Ingredient... ingredients) {
        Allomancy.LOGGER.debug("Creating Shapeless Recipe for {}",
                               BuiltInRegistries.ITEM.getKey(result.asItem()) + " " + save);

        ShapelessRecipeBuilder builder = ShapelessRecipeBuilder.shapeless(this.items, cat, result, count);

        builder.unlockedBy("has_" + BuiltInRegistries.ITEM.getKey(criterion).getPath(),
                           InventoryChangeTrigger.TriggerInstance.hasItems(criterion));

        for (Ingredient ingredient : ingredients) {
            builder.requires(ingredient);
        }

        if (save.isEmpty()) {
            builder.save(consumer);
        } else {
            builder.save(consumer, Allomancy.MODID + ":" + save);
        }
    }

    private void buildSmeltingAndBlasting(ItemLike result, List<ItemLike> ingredient, float xp) {
        String name = BuiltInRegistries.ITEM.getKey(result.asItem()).getPath();
        Allomancy.LOGGER.debug("Creating Smelting and Blasting Recipe for {}", name);

        this.oreBlasting(ingredient, RecipeCategory.MISC, result, xp, 100, name);
        this.oreSmelting(ingredient, RecipeCategory.MISC, result, xp, 200, name);
    }

    private static String mixing_save(String metal) {
        return metal + "_flakes_from_mixing";
    }

    private static String alloy_save(String metal) {
        return metal + "_ingot_from_alloying";
    }

    private Ingredient ing(String tag) {
        return tag(ItemTags.create(ResourceLocation.parse(tag)));
    }

    private Ingredient ing(TagKey<Item> tag) {
        return tag(tag);
    }

    private static Ingredient ing(ItemLike itemProvider) {
        return Ingredient.of(itemProvider);
    }

    private static Ingredient[] repeat(Ingredient ing, int n) {
        return repeatWith(ing, n);
    }

    private static Ingredient[] repeatWith(Ingredient ing, int n, Ingredient... extras) {
        int size = n + extras.length;
        Ingredient[] out = new Ingredient[size];
        for (int i = 0; i < n; i++) {
            out[i] = ing;
        }

        if (extras.length > 0) {
            System.arraycopy(extras, 0, out, n, size - n);
        }

        return out;
    }

    @Override
    protected void buildRecipes() {
        var consumer = this.output;
        // Basic Shaped Recipes
        buildShaped(consumer, RecipeCategory.REDSTONE, ExtrasSetup.IRON_LEVER.get(), Items.IRON_INGOT, "s", "I");
        buildShaped(consumer, RecipeCategory.REDSTONE, ExtrasSetup.IRON_BUTTON.get(), Items.IRON_INGOT, "i", "I");
        buildShapeless(consumer, RecipeCategory.REDSTONE, ExtrasSetup.INVERTED_IRON_BUTTON.get(), 1,
                       ExtrasSetup.IRON_BUTTON_ITEM.get(), "inverted_from_iron_button",
                       ing(ExtrasSetup.IRON_BUTTON.get()));
        buildShapeless(consumer, RecipeCategory.REDSTONE, ExtrasSetup.IRON_BUTTON.get(), 1,
                       ExtrasSetup.INVERTED_IRON_BUTTON_ITEM.get(), "iron_button_from_inverted",
                       ing(ExtrasSetup.INVERTED_IRON_BUTTON.get()));


        buildShaped(consumer, RecipeCategory.FOOD, ConsumeSetup.ALLOMANTIC_GRINDER.get(), Items.IRON_INGOT, "ggg",
                    "iii", "ggg");

        buildShaped(consumer, RecipeCategory.FOOD, ConsumeSetup.VIAL.get(), 4, Items.GLASS, " S ", "G G", " G ");
        buildShaped(consumer, RecipeCategory.COMBAT, CombatSetup.MISTCLOAK.get(), ConsumeSetup.VIAL.get(), "W W",
                    "WWW", "WWW");
        buildShaped(consumer, RecipeCategory.COMBAT, CombatSetup.ALUMINUM_HELMET.get(),
                    MaterialsSetup.INGOTS.get(Metal.ALUMINUM.getIndex()).get(), "AAA", "A A");

        buildShaped(consumer, RecipeCategory.COMBAT, CombatSetup.OBSIDIAN_DAGGER.get(), CombatSetup.MISTCLOAK.get(),
                    "  O", " O ", "s  ");
        buildShaped(consumer, RecipeCategory.COMBAT, CombatSetup.KOLOSS_BLADE.get(),
                    ConsumeSetup.LERASIUM_NUGGET.get(), "CC", "CC", "sC");

        // Ore Recipes
        // order must be the same as MaterialsSetup.ORE_METALS
        int[] ore_metal_indexes =
                {Metal.ALUMINUM.getIndex(), Metal.CADMIUM.getIndex(), Metal.CHROMIUM.getIndex(), MaterialsSetup.LEAD,
                 MaterialsSetup.SILVER, Metal.TIN.getIndex(), Metal.ZINC.getIndex()};
        float[] ore_metal_xp = {0.6F, 0.7F, 0.7F, 0.4F, 1.0F, 0.6F, 0.6F};
        for (int i = 0; i < MaterialsSetup.ORE_METALS.length; i++) {
            var raw = MaterialsSetup.RAW_ORE_ITEMS.get(i).get();
            var ore = MaterialsSetup.ORE_BLOCKS_ITEMS.get(i).get();
            var deep_ore = MaterialsSetup.DEEPSLATE_ORE_BLOCKS_ITEMS.get(i).get();
            var ingot = MaterialsSetup.INGOTS.get(ore_metal_indexes[i]).get();
            buildSmeltingAndBlasting(ingot, List.of(raw, ore, deep_ore), ore_metal_xp[i]);
        }


        buildShapeless(consumer, RecipeCategory.MISC, ConsumeSetup.LERASIUM_NUGGET.get(), 1, Items.NETHER_STAR,
                       ing(Items.NETHER_STAR), ing(Tags.Items.STORAGE_BLOCKS_GOLD));

        // Most metal based recipes
        for (int i = 0; i < MaterialsSetup.METAL_ITEM_LEN; i++) {

            // Grinder recipes
            Item flake = MaterialsSetup.FLAKES.get(i).get();
            String flakeType = BuiltInRegistries.ITEM
                    .getKey(flake)
                    .getPath()
                    .substring(0, BuiltInRegistries.ITEM.getKey(flake).getPath().indexOf('_'));
            buildShapeless(consumer, RecipeCategory.MISC, flake, 2, ConsumeSetup.ALLOMANTIC_GRINDER.get(),
                           ing(ConsumeSetup.ALLOMANTIC_GRINDER.get()), ing("c:" + "ingots/" + flakeType));

            if (i < Metal.values().length && Metal.getMetal(i).isVanilla()) {
                continue;
            }

            // Block and nugget crafting/uncrafting
            Item block = MaterialsSetup.STORAGE_BLOCK_ITEMS.get(i).get();
            Item ingot = MaterialsSetup.INGOTS.get(i).get();
            Item nugget = MaterialsSetup.NUGGETS.get(i).get();

            // building up
            buildShapeless(consumer, RecipeCategory.BUILDING_BLOCKS, block, 1, ingot, repeat(ing(ingot), 9));
            buildShapeless(consumer, RecipeCategory.MISC, ingot, 1, nugget,
                           BuiltInRegistries.ITEM.getKey(ingot).getPath() + "_from_nuggets", repeat(ing(nugget), 9));

            // breaking down
            buildShapeless(consumer, RecipeCategory.MISC, ingot, 9, block,
                           BuiltInRegistries.ITEM.getKey(ingot).getPath() + "_from_block", ing(block));
            buildShapeless(consumer, RecipeCategory.MISC, nugget, 9, ingot, ing(ingot));
        }


        // pattern recipes
        for (Metal mt : Metal.values()) {
            buildShapeless(consumer, RecipeCategory.MISC, ExtrasSetup.PATTERN_ITEMS.get(mt.getIndex()).get(), 1,
                           MaterialsSetup.FLAKES.get(mt.getIndex()).get(), ing(Items.PAPER),
                           ing(MaterialsSetup.FLAKES.get(mt.getIndex()).get()));
        }

        // Mixing/Alloying Recipes
        // GRINDER
        buildShapeless(consumer, RecipeCategory.MISC, MaterialsSetup.FLAKES.get(Metal.STEEL.getIndex()).get(), 2,
                       ConsumeSetup.ALLOMANTIC_GRINDER.get(), mixing_save("steel"), ing(Items.COAL),
                       ing(MaterialsSetup.FLAKES.get(Metal.IRON.getIndex()).get()));

        buildShapeless(consumer, RecipeCategory.MISC, MaterialsSetup.FLAKES.get(Metal.PEWTER.getIndex()).get(), 3,
                       ConsumeSetup.ALLOMANTIC_GRINDER.get(), mixing_save("pewter"),
                       repeatWith(ing(MaterialsSetup.FLAKES.get(Metal.TIN.getIndex()).get()), 2,
                                  ing(MaterialsSetup.FLAKES.get(MaterialsSetup.LEAD).get())));

        buildShapeless(consumer, RecipeCategory.MISC, MaterialsSetup.FLAKES.get(Metal.BRASS.getIndex()).get(), 4,
                       ConsumeSetup.ALLOMANTIC_GRINDER.get(), mixing_save("brass"),
                       repeatWith(ing(MaterialsSetup.FLAKES.get(Metal.COPPER.getIndex()).get()), 3,
                                  ing(MaterialsSetup.FLAKES.get(Metal.ZINC.getIndex()).get())));

        buildShapeless(consumer, RecipeCategory.MISC, MaterialsSetup.FLAKES.get(Metal.BRONZE.getIndex()).get(), 4,
                       ConsumeSetup.ALLOMANTIC_GRINDER.get(), mixing_save("bronze"),
                       repeatWith(ing(MaterialsSetup.FLAKES.get(Metal.COPPER.getIndex()).get()), 3,
                                  ing(MaterialsSetup.FLAKES.get(Metal.TIN.getIndex()).get())));

        buildShapeless(consumer, RecipeCategory.MISC, MaterialsSetup.FLAKES.get(Metal.DURALUMIN.getIndex()).get(), 4,
                       ConsumeSetup.ALLOMANTIC_GRINDER.get(), mixing_save("duralumin"),
                       repeatWith(ing(MaterialsSetup.FLAKES.get(Metal.ALUMINUM.getIndex()).get()), 3,
                                  ing(MaterialsSetup.FLAKES.get(Metal.COPPER.getIndex()).get())));

        buildShapeless(consumer, RecipeCategory.MISC, MaterialsSetup.FLAKES.get(Metal.ELECTRUM.getIndex()).get(), 2,
                       ConsumeSetup.ALLOMANTIC_GRINDER.get(), mixing_save("electrum"),
                       ing(MaterialsSetup.FLAKES.get(Metal.GOLD.getIndex()).get()),
                       ing(MaterialsSetup.FLAKES.get(MaterialsSetup.SILVER).get()));

        buildShapeless(consumer, RecipeCategory.MISC, MaterialsSetup.FLAKES.get(Metal.BENDALLOY.getIndex()).get(), 3,
                       ConsumeSetup.ALLOMANTIC_GRINDER.get(), mixing_save("bendalloy"),
                       repeatWith(ing(MaterialsSetup.FLAKES.get(Metal.CADMIUM.getIndex()).get()), 2,
                                  ing(MaterialsSetup.FLAKES.get(MaterialsSetup.LEAD).get())));

        buildShapeless(consumer, RecipeCategory.MISC, MaterialsSetup.FLAKES.get(Metal.NICROSIL.getIndex()).get(), 4,
                       ConsumeSetup.ALLOMANTIC_GRINDER.get(), mixing_save("nicrosil"),
                       repeatWith(ing(MaterialsSetup.FLAKES.get(Metal.CHROMIUM.getIndex()).get()), 3,
                                  ing(MaterialsSetup.FLAKES.get(Metal.IRON.getIndex()).get())));

        // ALLOYS
        buildShapeless(consumer, RecipeCategory.MISC, MaterialsSetup.INGOTS.get(Metal.STEEL.getIndex()).get(), 4,
                       Items.COAL, alloy_save("steel"), repeatWith(ing(Items.IRON_INGOT), 3, ing(Items.COAL)));

        buildShapeless(consumer, RecipeCategory.MISC, MaterialsSetup.INGOTS.get(Metal.PEWTER.getIndex()).get(), 3,
                       MaterialsSetup.INGOTS.get(Metal.TIN.getIndex()).get(), alloy_save("pewter"),
                       repeatWith(ing("c:ingots/tin"), 2, ing("c:ingots/lead")));

        buildShapeless(consumer, RecipeCategory.MISC, MaterialsSetup.INGOTS.get(Metal.BRASS.getIndex()).get(), 4,
                       MaterialsSetup.INGOTS.get(Metal.ZINC.getIndex()).get(), alloy_save("brass"),
                       repeatWith(ing(Items.COPPER_INGOT), 3, ing("c:ingots/zinc")));

        buildShapeless(consumer, RecipeCategory.MISC, MaterialsSetup.INGOTS.get(Metal.BRONZE.getIndex()).get(), 4,
                       MaterialsSetup.INGOTS.get(Metal.TIN.getIndex()).get(), alloy_save("bronze"),
                       repeatWith(ing(Items.COPPER_INGOT), 3, ing("c:ingots/tin")));

        buildShapeless(consumer, RecipeCategory.MISC, MaterialsSetup.INGOTS.get(Metal.DURALUMIN.getIndex()).get(), 4,
                       MaterialsSetup.INGOTS.get(Metal.ALUMINUM.getIndex()).get(), alloy_save("duralumin"),
                       repeatWith(ing("c:ingots/aluminum"), 3, ing(Items.COPPER_INGOT)));

        buildShapeless(consumer, RecipeCategory.MISC, MaterialsSetup.INGOTS.get(Metal.ELECTRUM.getIndex()).get(), 2,
                       MaterialsSetup.INGOTS.get(MaterialsSetup.SILVER).get(), alloy_save("electrum"),
                       ing("c:ingots/silver"), ing(Items.GOLD_INGOT));

        buildShapeless(consumer, RecipeCategory.MISC, MaterialsSetup.INGOTS.get(Metal.BENDALLOY.getIndex()).get(), 3,
                       MaterialsSetup.INGOTS.get(Metal.CADMIUM.getIndex()).get(), alloy_save("bendalloy"),
                       repeatWith(ing("c:ingots/cadmium"), 2, ing("c:ingots/lead")));

        buildShapeless(consumer, RecipeCategory.MISC, MaterialsSetup.INGOTS.get(Metal.NICROSIL.getIndex()).get(), 4,
                       MaterialsSetup.INGOTS.get(Metal.CHROMIUM.getIndex()).get(), alloy_save("nicrosil"),
                       repeatWith(ing("c:ingots/chromium"), 3, ing(Items.IRON_INGOT)));


        Allomancy.LOGGER.debug("Creating Shaped Recipe for allomancy:coin_bag");
        ShapedRecipeBuilder
                .shaped(this.registries.lookupOrThrow(Registries.ITEM), RecipeCategory.COMBAT,
                        CombatSetup.COIN_BAG.get())
                .unlockedBy("has_gold_nugget", InventoryChangeTrigger.TriggerInstance.hasItems(Items.GOLD_NUGGET))
                .showNotification(true)
                .define('#', Items.LEAD)
                .define('l', Items.LEATHER)
                .define('g', Items.GOLD_NUGGET)
                .pattern(" #g")
                .pattern("l l")
                .pattern(" l ")
                .save(consumer);

        Allomancy.LOGGER.debug("Creating Special Recipe for Vial Filling");
        SpecialRecipeBuilder.special(VialItemRecipe::new).save(consumer, "allomancy:vial_filling_recipe");

    }

    private void buildShaped(RecipeOutput consumer,
                             RecipeCategory cat,
                             ItemLike result,
                             int count,
                             Item criterion,
                             String... lines) {
        Allomancy.LOGGER.debug("Creating Shaped Recipe for {}", BuiltInRegistries.ITEM.getKey(result.asItem()));

        ShapedRecipeBuilder builder =
                ShapedRecipeBuilder.shaped(this.registries.lookupOrThrow(Registries.ITEM), cat, result, count);

        builder.unlockedBy("has_" + BuiltInRegistries.ITEM.getKey(criterion).getPath(),
                           InventoryChangeTrigger.TriggerInstance.hasItems(criterion));
        builder.showNotification(true);

        Set<Character> characters = new HashSet<>();
        for (String line : lines) {
            builder.pattern(line);
            line.chars().forEach(value -> characters.add((char) value));
        }

        for (Character c : characters) {
            if (this.defaultIngredients.containsKey(c)) {
                builder.define(c, this.defaultIngredients.get(c));
            }
        }

        builder.save(consumer);
    }

    private void buildShaped(RecipeOutput consumer,
                             RecipeCategory cat,
                             ItemLike result,
                             Item criterion,
                             String... lines) {
        buildShaped(consumer, cat, result, 1, criterion, lines);
    }

    private void add(char c, TagKey<Item> itemTag) {
        this.defaultIngredients.put(c, tag(itemTag));
    }

    private void add(char c, ItemLike itemProvider) {
        this.defaultIngredients.put(c, Ingredient.of(itemProvider));
    }

    private void add(char c, Ingredient ingredient) {
        this.defaultIngredients.put(c, ingredient);
    }

    public static class Runner extends RecipeProvider.Runner {
        public Runner(PackOutput out, CompletableFuture<HolderLookup.Provider> lookup) {
            super(out, lookup);
        }

        @Override
        protected RecipeProvider createRecipeProvider(HolderLookup.Provider registries, RecipeOutput output) {
            return new Recipes(registries, output);
        }

        @Override
        public String getName() {
            return "Allomancy recipes";
        }
    }
}
