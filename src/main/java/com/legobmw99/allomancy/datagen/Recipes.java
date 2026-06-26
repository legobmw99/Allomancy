package com.legobmw99.allomancy.datagen;

import com.legobmw99.allomancy.Allomancy;
import com.legobmw99.allomancy.api.enums.Metal;
import com.legobmw99.allomancy.modules.combat.CombatSetup;
import com.legobmw99.allomancy.modules.consumables.ConsumeSetup;
import com.legobmw99.allomancy.modules.consumables.item.recipe.VialItemRecipe;
import com.legobmw99.allomancy.modules.extras.ExtrasSetup;
import com.legobmw99.allomancy.modules.world.WorldSetup;
import com.legobmw99.allomancy.modules.world.recipe.InvestingRecipe;
import com.legobmw99.allomancy.util.AllomancyTags;
import net.minecraft.advancements.triggers.InventoryChangeTrigger;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.*;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.AbstractCookingRecipe;
import net.minecraft.world.item.crafting.CookingBookCategory;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.conditions.NotCondition;
import net.neoforged.neoforge.common.conditions.TagEmptyCondition;

import java.util.*;
import java.util.concurrent.CompletableFuture;

final class Recipes extends RecipeProvider {
    private final Map<Character, Ingredient> defaultIngredients = new HashMap<>();

    private final HolderGetter<Item> items;

    private Recipes(HolderLookup.Provider registries, RecipeOutput output) {
        super(registries, output);
        this.items = registries.lookupOrThrow(Registries.ITEM);

        add('i', AllomancyTags.INGOT_TAGS.get(Metal.IRON.getIndex()));
        add('g', AllomancyTags.INGOT_TAGS.get(Metal.GOLD.getIndex()));
        add('s', Items.STICK);
        add('S', ItemTags.WOODEN_SLABS);
        add('G', Tags.Items.GLASS_BLOCKS_COLORLESS);
        add('I', AllomancyTags.STORAGE_BLOCK_ITEM_TAGS.get(Metal.IRON.getIndex()));
        add('W', Items.WOOL.gray());
        add('O', Tags.Items.OBSIDIANS);
        add('C', Tags.Items.COBBLESTONES);
        add('A', AllomancyTags.INGOT_TAGS.get(Metal.ALUMINUM.getIndex()));
        add('B', AllomancyTags.STORAGE_BLOCK_ITEM_TAGS.get(Metal.BRONZE.getIndex()));
        add('b', AllomancyTags.INGOT_TAGS.get(Metal.BRONZE.getIndex()));
        add('n', AllomancyTags.NUGGET_TAGS.get(Metal.BRONZE.getIndex()));

    }


    private void buildShapeless(RecipeOutput consumer,
                                RecipeCategory cat,
                                ItemLike result,
                                int count,
                                ItemLike criterion,
                                Ingredient... ingredients) {
        buildShapeless(consumer, cat, result, count, criterion, "", ingredients);
    }

    private void buildShapeless(RecipeOutput consumer,
                                RecipeCategory cat,
                                ItemLike result,
                                int count,
                                ItemLike criterion,
                                String save,
                                Ingredient... ingredients) {
        Allomancy.LOGGER.debug("Creating Shapeless Recipe for {}",
                               BuiltInRegistries.ITEM.getKey(result.asItem()) + " " + save);

        ShapelessRecipeBuilder builder = ShapelessRecipeBuilder.shapeless(this.items, cat, result, count);

        builder.unlockedBy("has_" + BuiltInRegistries.ITEM.getKey(criterion.asItem()).getPath(),
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

        this.oreBlasting(ingredient, RecipeCategory.MISC, CookingBookCategory.MISC, result, xp, 100, name);
        this.oreSmelting(ingredient, RecipeCategory.MISC, CookingBookCategory.MISC, result, xp, 200, name);
    }

    private static String alloy_save(String metal) {
        return "raw_" + metal + "_from_alloying";
    }

    private Ingredient ing(String tag) {
        return tag(ItemTags.create(Identifier.parse(tag)));
    }

    private Ingredient ing(TagKey<Item> tag) {
        return tag(tag);
    }

    private static Ingredient ing(ItemLike itemProvider) {
        return Ingredient.of(itemProvider);
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
        buildShaped(consumer, RecipeCategory.REDSTONE, ExtrasSetup.IRON_LEVER, Items.IRON_INGOT, "s", "I");
        buildShaped(consumer, RecipeCategory.REDSTONE, ExtrasSetup.IRON_BUTTON, Items.IRON_INGOT, "i", "I");
        buildShapeless(consumer, RecipeCategory.REDSTONE, ExtrasSetup.INVERTED_IRON_BUTTON, 1,
                       ExtrasSetup.IRON_BUTTON_ITEM, "inverted_from_iron_button", ing(ExtrasSetup.IRON_BUTTON));
        buildShapeless(consumer, RecipeCategory.REDSTONE, ExtrasSetup.IRON_BUTTON, 1,
                       ExtrasSetup.INVERTED_IRON_BUTTON_ITEM, "iron_button_from_inverted",
                       ing(ExtrasSetup.INVERTED_IRON_BUTTON));


        buildShaped(consumer, RecipeCategory.FOOD, ConsumeSetup.ALLOMANTIC_GRINDER, Items.IRON_INGOT, "ggg", "iii",
                    "ggg");

        buildShaped(consumer, RecipeCategory.FOOD, ConsumeSetup.VIAL, 4, Items.GLASS, " S ", "G G", " G ");
        buildShaped(consumer, RecipeCategory.COMBAT, CombatSetup.MISTCLOAK, ConsumeSetup.VIAL, "W W", "WWW", "WWW");
        buildShaped(consumer, RecipeCategory.COMBAT, CombatSetup.ALUMINUM_HELMET,
                    WorldSetup.INGOTS.get(Metal.ALUMINUM.getIndex()), "AAA", "A A");

        buildShaped(consumer, RecipeCategory.COMBAT, CombatSetup.OBSIDIAN_DAGGER, CombatSetup.MISTCLOAK, "  O", " O ",
                    "s  ");
        buildShaped(consumer, RecipeCategory.COMBAT, CombatSetup.KOLOSS_BLADE, ConsumeSetup.LERASIUM_NUGGET, "CC",
                    "CC", "sC");

        buildShaped(consumer, RecipeCategory.MISC, ExtrasSetup.BRONZE_EARRING, ConsumeSetup.ALLOMANTIC_GRINDER, " b ",
                    "B n");

        // Ore Recipes

        for (int i = 0; i < WorldSetup.ORE_METALS.length; i++) {
            WorldSetup.OreConfig config = WorldSetup.ORE_METALS[i];
            var raw = WorldSetup.RAW_ORE_ITEMS.get(config.index());
            var rawBlock = WorldSetup.RAW_ORE_BLOCKS_ITEMS.get(i);
            var ore = WorldSetup.ORE_BLOCKS_ITEMS.get(i);
            var deep_ore = WorldSetup.DEEPSLATE_ORE_BLOCKS_ITEMS.get(i);
            var ingot = WorldSetup.INGOTS.get(config.index());
            buildSmeltingAndBlasting(ingot, List.of(raw, ore, deep_ore), config.xp());

            ShapedRecipeBuilder
                    .shaped(this.registries.lookupOrThrow(Registries.ITEM), RecipeCategory.BUILDING_BLOCKS, rawBlock)
                    .unlockedBy("has_" + raw.getId().getPath(), InventoryChangeTrigger.TriggerInstance.hasItems(raw))
                    .showNotification(true)
                    .define('t', AllomancyTags.RAW_ORE_TAGS.get(config.index()))
                    .define('r', raw)
                    .pattern("ttt")
                    .pattern("trt")
                    .pattern("ttt")
                    .save(consumer);

            buildShapeless(consumer, RecipeCategory.MISC, raw, 9, rawBlock, raw.getId().getPath() + "_from_block",
                           ing(rawBlock));
        }


        // Most metal based recipes
        for (int i = 0; i < WorldSetup.METAL_ITEM_LEN; i++) {

            // Grinder recipes
            var flake = WorldSetup.FLAKES.get(i);
            buildShapeless(consumer, RecipeCategory.MISC, flake, 2, ConsumeSetup.ALLOMANTIC_GRINDER,
                           ing(ConsumeSetup.ALLOMANTIC_GRINDER), ing(AllomancyTags.INGOT_TAGS.get(i)));

            if (i < Metal.values().length) {
                Metal mt = Metal.getMetal(i);
                if (mt.isVanilla()) {
                    continue;
                }

                if (mt.isAlloy()) {
                    var raw = WorldSetup.RAW_ORE_ITEMS.get(i);
                    var ingot = WorldSetup.INGOTS.get(i);
                    buildSmeltingAndBlasting(ingot, List.of(raw), 0.7F);
                }
            }

            // Block and nugget crafting/uncrafting
            var block = WorldSetup.STORAGE_BLOCK_ITEMS.get(i);
            var ingot = WorldSetup.INGOTS.get(i);
            var nugget = WorldSetup.NUGGETS.get(i);
            TagKey<Item> nugget_tag = AllomancyTags.NUGGET_TAGS.get(i);
            TagKey<Item> ingot_tag = AllomancyTags.INGOT_TAGS.get(i);

            // building up
            ShapedRecipeBuilder
                    .shaped(this.registries.lookupOrThrow(Registries.ITEM), RecipeCategory.BUILDING_BLOCKS, block)
                    .unlockedBy("has_" + ingot.getId().getPath(),
                                InventoryChangeTrigger.TriggerInstance.hasItems(ingot))
                    .showNotification(true)
                    .define('t', ingot_tag)
                    .define('i', ingot)
                    .pattern("ttt")
                    .pattern("tit")
                    .pattern("ttt")
                    .save(consumer);

            ShapedRecipeBuilder
                    .shaped(this.registries.lookupOrThrow(Registries.ITEM), RecipeCategory.MISC, ingot)
                    .unlockedBy("has_" + nugget.getId().getPath(),
                                InventoryChangeTrigger.TriggerInstance.hasItems(nugget))
                    .showNotification(true)
                    .define('t', nugget_tag)
                    .define('n', nugget)
                    .pattern("ttt")
                    .pattern("tnt")
                    .pattern("ttt")
                    .save(consumer, ingot.getId() + "_from_nuggets");


            // breaking down
            buildShapeless(consumer, RecipeCategory.MISC, ingot, 9, block, ingot.getId().getPath() + "_from_block",
                           ing(block));
            buildShapeless(consumer, RecipeCategory.MISC, nugget, 9, ingot, ing(ingot));
        }


        // pattern recipes
        for (Metal mt : Metal.values()) {
            buildShapeless(consumer, RecipeCategory.MISC, ExtrasSetup.PATTERN_ITEMS.get(mt.getIndex()), 1,
                           WorldSetup.FLAKES.get(mt.getIndex()), ing(Items.PAPER),
                           ing(WorldSetup.FLAKES.get(mt.getIndex())));
        }

        // ALLOYS
        buildShapeless(consumer, RecipeCategory.MISC, WorldSetup.RAW_ORE_ITEMS.get(Metal.STEEL.getIndex()), 1,
                       Items.BLAZE_POWDER, alloy_save("steel"),
                       ing(AllomancyTags.RAW_ORE_TAGS.get(Metal.IRON.getIndex())), ing(ItemTags.SAND),
                       ing(Items.BLAZE_POWDER));

        buildShapeless(consumer, RecipeCategory.MISC, WorldSetup.RAW_ORE_ITEMS.get(Metal.PEWTER.getIndex()), 4,
                       WorldSetup.RAW_ORE_ITEMS.get(Metal.TIN.getIndex()), alloy_save("pewter"),
                       repeatWith(ing(AllomancyTags.RAW_ORE_TAGS.get(Metal.TIN.getIndex())), 3,
                                  ing(AllomancyTags.RAW_ORE_TAGS.get(WorldSetup.LEAD))));

        buildShapeless(consumer, RecipeCategory.MISC, WorldSetup.RAW_ORE_ITEMS.get(Metal.BRASS.getIndex()), 2,
                       WorldSetup.RAW_ORE_ITEMS.get(Metal.ZINC.getIndex()), alloy_save("brass"),
                       ing(AllomancyTags.RAW_ORE_TAGS.get(Metal.COPPER.getIndex())),
                       ing(AllomancyTags.RAW_ORE_TAGS.get(Metal.ZINC.getIndex())));

        buildShapeless(consumer, RecipeCategory.MISC, WorldSetup.RAW_ORE_ITEMS.get(Metal.BRONZE.getIndex()), 4,
                       WorldSetup.RAW_ORE_ITEMS.get(Metal.TIN.getIndex()), alloy_save("bronze"),
                       repeatWith(ing(AllomancyTags.RAW_ORE_TAGS.get(Metal.COPPER.getIndex())), 3,
                                  ing(AllomancyTags.RAW_ORE_TAGS.get(Metal.TIN.getIndex()))));

        var doesNotHaveNickel = new TagEmptyCondition<>(ItemTags.create(Identifier.parse("c:raw_materials/nickel")));
        var hasNickel = new NotCondition(doesNotHaveNickel);

        buildShapeless(consumer.withConditions(hasNickel), RecipeCategory.MISC,
                       WorldSetup.RAW_ORE_ITEMS.get(Metal.NICROSIL.getIndex()), 4,
                       WorldSetup.RAW_ORE_ITEMS.get(Metal.CHROMIUM.getIndex()),
                       alloy_save("nicrosil") + "_with_nickel", repeatWith(ing("c:raw_materials/nickel"), 2,
                                                                           ing(AllomancyTags.RAW_ORE_TAGS.get(
                                                                                   Metal.CHROMIUM.getIndex())),
                                                                           ing(Tags.Items.GEMS_QUARTZ)));

        buildShapeless(consumer.withConditions(doesNotHaveNickel), RecipeCategory.MISC,
                       WorldSetup.RAW_ORE_ITEMS.get(Metal.NICROSIL.getIndex()), 4,
                       WorldSetup.RAW_ORE_ITEMS.get(Metal.CHROMIUM.getIndex()), alloy_save("nicrosil"),
                       repeatWith(ing(AllomancyTags.RAW_ORE_TAGS.get(Metal.CHROMIUM.getIndex())), 2,
                                  ing(AllomancyTags.RAW_ORE_TAGS.get(Metal.IRON.getIndex())),
                                  ing(Tags.Items.GEMS_QUARTZ)));

        buildShapeless(consumer, RecipeCategory.MISC, WorldSetup.RAW_ORE_ITEMS.get(Metal.DURALUMIN.getIndex()), 4,
                       WorldSetup.RAW_ORE_ITEMS.get(Metal.ALUMINUM.getIndex()), alloy_save("duralumin"),
                       repeatWith(ing(AllomancyTags.RAW_ORE_TAGS.get(Metal.ALUMINUM.getIndex())), 3,
                                  ing(AllomancyTags.RAW_ORE_TAGS.get(Metal.COPPER.getIndex()))));

        buildShapeless(consumer, RecipeCategory.MISC, WorldSetup.RAW_ORE_ITEMS.get(Metal.BENDALLOY.getIndex()), 4,
                       WorldSetup.RAW_ORE_ITEMS.get(Metal.CADMIUM.getIndex()), alloy_save("bendalloy"),
                       repeatWith(ing(AllomancyTags.RAW_ORE_TAGS.get(WorldSetup.LEAD)), 2,
                                  ing(AllomancyTags.RAW_ORE_TAGS.get(Metal.CADMIUM.getIndex())),
                                  ing(AllomancyTags.RAW_ORE_TAGS.get(Metal.TIN.getIndex()))));

        buildShapeless(consumer, RecipeCategory.MISC, WorldSetup.RAW_ORE_ITEMS.get(Metal.ELECTRUM.getIndex()), 2,
                       WorldSetup.RAW_ORE_ITEMS.get(WorldSetup.SILVER), alloy_save("electrum"),
                       ing(AllomancyTags.RAW_ORE_TAGS.get(WorldSetup.SILVER)),
                       ing(AllomancyTags.RAW_ORE_TAGS.get(Metal.GOLD.getIndex())));

        Allomancy.LOGGER.debug("Creating Shaped Recipe for allomancy:coin_bag");
        ShapedRecipeBuilder
                .shaped(this.registries.lookupOrThrow(Registries.ITEM), RecipeCategory.COMBAT, CombatSetup.COIN_BAG)
                .unlockedBy("has_gold_nugget", InventoryChangeTrigger.TriggerInstance.hasItems(Items.GOLD_NUGGET))
                .showNotification(true)
                .define('#', Items.LEAD)
                .define('l', Items.LEATHER)
                .define('g', Tags.Items.NUGGETS_GOLD)
                .pattern(" #g")
                .pattern("l l")
                .pattern(" l ")
                .save(consumer);

        Allomancy.LOGGER.debug("Creating Special Recipe for Vial Filling");
        SpecialRecipeBuilder.special(() -> VialItemRecipe.INSTANCE).save(consumer, "allomancy:vial_filling_recipe");

        Allomancy.LOGGER.debug("Creating Special Recipe for Lerasium investing");
        consumer.accept(ResourceKey.create(Registries.RECIPE, Allomancy.id("lerasium_investing")),
                        new InvestingRecipe(tag(AllomancyTags.LERASIUM_CONVERSION),
                                            new ItemStackTemplate(ConsumeSetup.LERASIUM_NUGGET)), null);
    }

    private void buildShaped(RecipeOutput consumer,
                             RecipeCategory cat,
                             ItemLike result,
                             int count,
                             ItemLike criterion,
                             String... lines) {
        Allomancy.LOGGER.debug("Creating Shaped Recipe for {}", BuiltInRegistries.ITEM.getKey(result.asItem()));

        ShapedRecipeBuilder builder =
                ShapedRecipeBuilder.shaped(this.registries.lookupOrThrow(Registries.ITEM), cat, result, count);

        builder.unlockedBy("has_" + BuiltInRegistries.ITEM.getKey(criterion.asItem()).getPath(),
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
                             ItemLike criterion,
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

    @Override
    protected <T extends AbstractCookingRecipe> void oreCooking(AbstractCookingRecipe.Factory<T> factory,
                                                                List<ItemLike> smeltables,
                                                                RecipeCategory craftingCategory,
                                                                CookingBookCategory cookingCategory,
                                                                ItemLike result,
                                                                float experience,
                                                                int cookingTime,
                                                                String group,
                                                                String fromDesc) {
        for (ItemLike item : smeltables) {
            SimpleCookingRecipeBuilder
                    .generic(Ingredient.of(item), craftingCategory, cookingCategory, result, experience, cookingTime,
                             factory)
                    .group(group)
                    .unlockedBy(getHasName(item), this.has(item))
                    // overridden just to insert this allomancy:
                    .save(this.output, "allomancy:" + getItemName(result) + fromDesc + "_" + getItemName(item));
        }
    }
}
