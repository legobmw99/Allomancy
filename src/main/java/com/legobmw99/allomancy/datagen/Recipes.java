package com.legobmw99.allomancy.datagen;

import com.legobmw99.allomancy.Allomancy;
import com.legobmw99.allomancy.modules.combat.CombatSetup;
import com.legobmw99.allomancy.modules.consumables.ConsumeSetup;
import com.legobmw99.allomancy.modules.extras.ExtrasSetup;
import com.legobmw99.allomancy.modules.materials.MaterialsSetup;
import com.legobmw99.allomancy.setup.Metal;
import net.minecraft.advancements.criterion.InventoryChangeTrigger;
import net.minecraft.data.*;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.tags.ITag;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.IItemProvider;
import net.minecraftforge.common.Tags;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

public class Recipes extends RecipeProvider {

    private final Map<Character, Ingredient> defaultIngredients = new HashMap<>();

    public Recipes(DataGenerator generatorIn) {
        super(generatorIn);
        add('i', Tags.Items.INGOTS_IRON);
        add('g', Tags.Items.INGOTS_GOLD);
        add('s', Items.STICK);
        add('S', ItemTags.WOODEN_SLABS);
        add('G', Items.GLASS);
        add('I', Tags.Items.STORAGE_BLOCKS_IRON);
        add('W', Items.GRAY_WOOL);
        add('O', Tags.Items.OBSIDIAN);
        add('C', Items.COBBLESTONE);


    }

    @Override
    protected void registerRecipes(Consumer<IFinishedRecipe> consumer) {
        // Basic Shaped Recipes
        buildShaped(consumer, ExtrasSetup.IRON_LEVER.get(), Items.IRON_INGOT, "s", "I");
        buildShaped(consumer, ExtrasSetup.IRON_BUTTON.get(), Items.IRON_INGOT, "i", "I");
        buildShaped(consumer, ConsumeSetup.ALLOMANTIC_GRINDER.get(), Items.IRON_INGOT, "ggg", "iii", "ggg");
        buildShaped(consumer, ConsumeSetup.VIAL.get(), 4, Items.GLASS, " S ", "G G", " G ");
        buildShaped(consumer, CombatSetup.MISTCLOAK.get(), ConsumeSetup.VIAL.get(), "W W", "WWW", "WWW");
        buildShaped(consumer, CombatSetup.OBSIDIAN_DAGGER.get(), CombatSetup.MISTCLOAK.get(), "  O", " O ", "s  ");
        buildShaped(consumer, CombatSetup.KOLOSS_BLADE.get(), ConsumeSetup.LERASIUM_NUGGET.get(), "CC", "CC", "sC");

        // Ore Recipes
        buildSmeltingAndBlasting(consumer, MaterialsSetup.INGOTS.get(Metal.ALUMINUM.getIndex()).get(), MaterialsSetup.ALUMINUM_ORE.get(), 0.6F);
        buildSmeltingAndBlasting(consumer, MaterialsSetup.INGOTS.get(Metal.CADMIUM.getIndex()).get(), MaterialsSetup.CADMIUM_ORE.get(), 0.7F);
        buildSmeltingAndBlasting(consumer, MaterialsSetup.INGOTS.get(Metal.CHROMIUM.getIndex()).get(), MaterialsSetup.CHROMIUM_ORE.get(), 0.7F);
        buildSmeltingAndBlasting(consumer, MaterialsSetup.INGOTS.get(Metal.COPPER.getIndex()).get(), MaterialsSetup.COPPER_ORE.get(), 0.8F);
        buildSmeltingAndBlasting(consumer, MaterialsSetup.INGOTS.get(Metal.TIN.getIndex()).get(), MaterialsSetup.TIN_ORE.get(), 0.6F);
        buildSmeltingAndBlasting(consumer, MaterialsSetup.INGOTS.get(Metal.ZINC.getIndex()).get(), MaterialsSetup.ZINC_ORE.get(), 0.6F);
        buildSmeltingAndBlasting(consumer, MaterialsSetup.INGOTS.get(MaterialsSetup.LEAD).get(), MaterialsSetup.LEAD_ORE.get(), 0.4F);
        buildSmeltingAndBlasting(consumer, MaterialsSetup.INGOTS.get(MaterialsSetup.SILVER).get(), MaterialsSetup.SILVER_ORE.get(), 1.0F);

        buildShapeless(consumer, ConsumeSetup.LERASIUM_NUGGET.get(), 1, Items.NETHER_STAR, ing(Items.NETHER_STAR), ing(Tags.Items.STORAGE_BLOCKS_GOLD));

        // Most metal based recipes
        for (int i = 0; i < MaterialsSetup.METAL_ITEM_LEN; i++) {

            // Grinder recipes
            Item flake = MaterialsSetup.FLAKES.get(i).get();
            String flakeType = flake.getRegistryName().getPath().substring(0, flake.getRegistryName().getPath().indexOf('_'));
            buildShapeless(consumer, flake, 2, ConsumeSetup.ALLOMANTIC_GRINDER.get(), ing(ConsumeSetup.ALLOMANTIC_GRINDER.get()), ing("forge:" + "ingots/" + flakeType));

            if (i == Metal.GOLD.getIndex() || i == Metal.IRON.getIndex()) {
                continue;
            }

            // Block and nugget crafting/uncrafting
            Item block = MaterialsSetup.STORAGE_BLOCK_ITEMS.get(i).get();
            Item ingot = MaterialsSetup.INGOTS.get(i).get();
            Item nugget = MaterialsSetup.NUGGETS.get(i).get();

            // building up
            buildShapeless(consumer, block, 1, ingot, repeat(ing(ingot), 9));
            buildShapeless(consumer, ingot, 1, nugget, "allomancy:" + ingot.getRegistryName().getPath() + "_from_nuggets", repeat(ing(nugget), 9));

            // breaking down
            buildShapeless(consumer, ingot, 9, block, "allomancy:" + ingot.getRegistryName().getPath() + "_from_block", ing(block));
            buildShapeless(consumer, nugget, 9, ingot, ing(ingot));
        }


        // Mixing/Alloying Recipes
        // GRINDER
        buildShapeless(consumer, MaterialsSetup.FLAKES.get(Metal.STEEL.getIndex()).get(), 2, ConsumeSetup.ALLOMANTIC_GRINDER.get(), mixing_save("steel"), ing(Items.COAL),
                       ing(MaterialsSetup.FLAKES.get(Metal.IRON.getIndex()).get()));

        buildShapeless(consumer, MaterialsSetup.FLAKES.get(Metal.PEWTER.getIndex()).get(), 3, ConsumeSetup.ALLOMANTIC_GRINDER.get(), mixing_save("pewter"),
                       repeatWith(ing(MaterialsSetup.FLAKES.get(Metal.TIN.getIndex()).get()), 2, ing(MaterialsSetup.FLAKES.get(MaterialsSetup.LEAD).get())));

        buildShapeless(consumer, MaterialsSetup.FLAKES.get(Metal.BRASS.getIndex()).get(), 4, ConsumeSetup.ALLOMANTIC_GRINDER.get(), mixing_save("brass"),
                       repeatWith(ing(MaterialsSetup.FLAKES.get(Metal.COPPER.getIndex()).get()), 3, ing(MaterialsSetup.FLAKES.get(Metal.ZINC.getIndex()).get())));

        buildShapeless(consumer, MaterialsSetup.FLAKES.get(Metal.BRONZE.getIndex()).get(), 4, ConsumeSetup.ALLOMANTIC_GRINDER.get(), mixing_save("bronze"),
                       repeatWith(ing(MaterialsSetup.FLAKES.get(Metal.COPPER.getIndex()).get()), 3, ing(MaterialsSetup.FLAKES.get(Metal.TIN.getIndex()).get())));

        buildShapeless(consumer, MaterialsSetup.FLAKES.get(Metal.DURALUMIN.getIndex()).get(), 4, ConsumeSetup.ALLOMANTIC_GRINDER.get(), mixing_save("duralumin"),
                       repeatWith(ing(MaterialsSetup.FLAKES.get(Metal.ALUMINUM.getIndex()).get()), 3, ing(MaterialsSetup.FLAKES.get(Metal.COPPER.getIndex()).get())));

        buildShapeless(consumer, MaterialsSetup.FLAKES.get(Metal.ELECTRUM.getIndex()).get(), 2, ConsumeSetup.ALLOMANTIC_GRINDER.get(), mixing_save("electrum"),
                       ing(MaterialsSetup.FLAKES.get(Metal.GOLD.getIndex()).get()), ing(MaterialsSetup.FLAKES.get(MaterialsSetup.SILVER).get()));

        buildShapeless(consumer, MaterialsSetup.FLAKES.get(Metal.BENDALLOY.getIndex()).get(), 3, ConsumeSetup.ALLOMANTIC_GRINDER.get(), mixing_save("bendalloy"),
                       repeatWith(ing(MaterialsSetup.FLAKES.get(Metal.CADMIUM.getIndex()).get()), 2, ing(MaterialsSetup.FLAKES.get(MaterialsSetup.LEAD).get())));

        buildShapeless(consumer, MaterialsSetup.FLAKES.get(Metal.NICROSIL.getIndex()).get(), 4, ConsumeSetup.ALLOMANTIC_GRINDER.get(), mixing_save("nicrosil"),
                       repeatWith(ing(MaterialsSetup.FLAKES.get(Metal.CHROMIUM.getIndex()).get()), 3, ing(MaterialsSetup.FLAKES.get(Metal.IRON.getIndex()).get())));

        // ALLOYS
        buildShapeless(consumer, MaterialsSetup.INGOTS.get(Metal.STEEL.getIndex()).get(), 4, Items.COAL, alloy_save("steel"),
                       repeatWith(ing(Items.IRON_INGOT), 3, ing(Items.COAL)));

        buildShapeless(consumer, MaterialsSetup.INGOTS.get(Metal.PEWTER.getIndex()).get(), 3, MaterialsSetup.INGOTS.get(Metal.TIN.getIndex()).get(), alloy_save("pewter"),
                       repeatWith(ing("forge:ingots/tin"), 2, ing("forge:ingots/lead")));

        buildShapeless(consumer, MaterialsSetup.INGOTS.get(Metal.BRASS.getIndex()).get(), 4, MaterialsSetup.INGOTS.get(Metal.COPPER.getIndex()).get(), alloy_save("brass"),
                       repeatWith(ing("forge:ingots/copper"), 3, ing("forge:ingots/zinc")));

        buildShapeless(consumer, MaterialsSetup.INGOTS.get(Metal.BRONZE.getIndex()).get(), 4, MaterialsSetup.INGOTS.get(Metal.COPPER.getIndex()).get(), alloy_save("bronze"),
                       repeatWith(ing("forge:ingots/copper"), 3, ing("forge:ingots/tin")));

        buildShapeless(consumer, MaterialsSetup.INGOTS.get(Metal.DURALUMIN.getIndex()).get(), 4, MaterialsSetup.INGOTS.get(Metal.ALUMINUM.getIndex()).get(),
                       alloy_save("duralumin"), repeatWith(ing("forge:ingots/aluminum"), 3, ing("forge:ingots/copper")));

        buildShapeless(consumer, MaterialsSetup.INGOTS.get(Metal.ELECTRUM.getIndex()).get(), 2, MaterialsSetup.INGOTS.get(MaterialsSetup.SILVER).get(), alloy_save("electrum"),
                       ing("forge:ingots/silver"), ing(Items.GOLD_INGOT));

        buildShapeless(consumer, MaterialsSetup.INGOTS.get(Metal.BENDALLOY.getIndex()).get(), 3, MaterialsSetup.INGOTS.get(Metal.CADMIUM.getIndex()).get(), alloy_save("bendalloy"),
                       repeatWith(ing("forge:ingots/cadmium"), 2, ing("forge:ingots/lead")));

        buildShapeless(consumer, MaterialsSetup.INGOTS.get(Metal.NICROSIL.getIndex()).get(), 4, MaterialsSetup.INGOTS.get(Metal.CHROMIUM.getIndex()).get(), alloy_save("nicrosil"),
                       repeatWith(ing("forge:ingots/chromium"), 3, ing(Items.IRON_INGOT)));


        Allomancy.LOGGER.debug("Creating Shaped Recipe for allomancy:coin_bag");
        ShapedRecipeBuilder
                .shapedRecipe(CombatSetup.COIN_BAG.get())
                .addCriterion("has_gold_nugget", InventoryChangeTrigger.Instance.forItems(CombatSetup.MISTCLOAK.get()))
                .key('#', Items.LEAD)
                .key('l', Items.LEATHER)
                .key('g', Items.GOLD_NUGGET)
                .patternLine(" #g")
                .patternLine("l l")
                .patternLine(" l ")
                .build(consumer);

        Allomancy.LOGGER.debug("Creating Special Recipe for Vial Filling");
        CustomRecipeBuilder.customRecipe(ConsumeSetup.VIAL_RECIPE_SERIALIZER.get()).build(consumer, "allomancy:vial_filling_recipe");

    }


    protected void buildShaped(Consumer<IFinishedRecipe> consumer, IItemProvider result, int count, Item criterion, String... lines) {
        Allomancy.LOGGER.debug("Creating Shaped Recipe for " + result.asItem().getRegistryName());

        ShapedRecipeBuilder builder = ShapedRecipeBuilder.shapedRecipe(result, count);

        builder.addCriterion("has_" + criterion.getRegistryName().getPath(), InventoryChangeTrigger.Instance.forItems(criterion));

        Set<Character> characters = new HashSet<>();
        for (String line : lines) {
            builder.patternLine(line);
            line.chars().forEach(value -> characters.add((char) value));
        }

        for (Character c : characters) {
            if (this.defaultIngredients.containsKey(c)) {
                builder.key(c, this.defaultIngredients.get(c));
            }
        }

        builder.build(consumer);
    }

    protected void buildShaped(Consumer<IFinishedRecipe> consumer, IItemProvider result, Item criterion, String... lines) {
        buildShaped(consumer, result, 1, criterion, lines);
    }


    protected static void buildShapeless(Consumer<IFinishedRecipe> consumer, IItemProvider result, int count, Item criterion, Ingredient... ingredients) {
        buildShapeless(consumer, result, count, criterion, "", ingredients);
    }

    protected static void buildShapeless(Consumer<IFinishedRecipe> consumer, IItemProvider result, int count, Item criterion, String save, Ingredient... ingredients) {
        Allomancy.LOGGER.debug("Creating Shapeless Recipe for " + result.asItem().getRegistryName() + " " + save);

        ShapelessRecipeBuilder builder = ShapelessRecipeBuilder.shapelessRecipe(result, count);

        builder.addCriterion("has_" + criterion.getRegistryName().getPath(), InventoryChangeTrigger.Instance.forItems(criterion));

        for (Ingredient ingredient : ingredients) {
            builder.addIngredient(ingredient);
        }

        if (save.isEmpty()) {
            builder.build(consumer);
        } else {
            builder.build(consumer, save);
        }
    }


    protected static void buildSmeltingAndBlasting(Consumer<IFinishedRecipe> consumer, IItemProvider result, IItemProvider ingredient, float xp) {
        Allomancy.LOGGER.debug("Creating Smelting and Blasting Recipe for " + result.asItem().getRegistryName());

        CookingRecipeBuilder smelt = CookingRecipeBuilder.smeltingRecipe(ing(ingredient), result, xp, 200);
        CookingRecipeBuilder blast = CookingRecipeBuilder.blastingRecipe(ing(ingredient), result, xp, 100);

        smelt.addCriterion("has_" + ingredient.asItem().getRegistryName().getPath(), InventoryChangeTrigger.Instance.forItems(ingredient));
        blast.addCriterion("has_" + ingredient.asItem().getRegistryName().getPath(), InventoryChangeTrigger.Instance.forItems(ingredient));

        smelt.build(consumer);
        blast.build(consumer, result.asItem().getRegistryName() + "_from_blasting");

    }

    private static String mixing_save(String metal) {
        return "allomancy:" + metal + "_flakes_from_mixing";
    }

    private static String alloy_save(String metal) {
        return "allomancy:" + metal + "_ingot_from_alloying";
    }

    protected void add(char c, ITag.INamedTag<Item> itemTag) {
        this.defaultIngredients.put(c, Ingredient.fromTag(itemTag));
    }

    protected void add(char c, IItemProvider itemProvider) {
        this.defaultIngredients.put(c, Ingredient.fromItems(itemProvider));
    }

    protected void add(char c, Ingredient ingredient) {
        this.defaultIngredients.put(c, ingredient);
    }

    protected static Ingredient ing(String tag) {
        return Ingredient.fromTag(ItemTags.makeWrapperTag(tag));
    }

    protected static Ingredient ing(ITag.INamedTag<Item> tag) {
        return Ingredient.fromTag(tag);
    }

    protected static Ingredient ing(IItemProvider itemProvider) {
        return Ingredient.fromItems(itemProvider);
    }

    protected static Ingredient ing(Ingredient ingredient) {
        return ingredient;
    }

    protected static Ingredient[] repeat(Ingredient ing, int n) {
        return repeatWith(ing, n);
    }

    protected static Ingredient[] repeatWith(Ingredient ing, int n, Ingredient... extras) {
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
    public String getName() {
        return "Allomancy Recipes";
    }
}
