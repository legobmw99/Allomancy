package com.legobmw99.allomancy.datagen;

import com.legobmw99.allomancy.Allomancy;
import com.legobmw99.allomancy.modules.combat.CombatSetup;
import com.legobmw99.allomancy.modules.consumables.ConsumeSetup;
import com.legobmw99.allomancy.modules.extras.ExtrasSetup;
import com.legobmw99.allomancy.modules.materials.MaterialsSetup;
import net.minecraft.advancements.criterion.InventoryChangeTrigger;
import net.minecraft.data.*;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.Tag;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.Tags;
import net.minecraftforge.fml.RegistryObject;

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
        buildShaped(consumer, ConsumeSetup.ALLOMANTIC_GRINDER.get(), Items.IRON_INGOT, "iii", "ggg", "iii");
        buildShaped(consumer, ConsumeSetup.VIAL.get(), 3, Items.GLASS, " S ", "G G", " G ");
        buildShaped(consumer, CombatSetup.MISTCLOAK.get(), ConsumeSetup.VIAL.get(), "iii", "ggg", "iii");
        buildShaped(consumer, CombatSetup.OBSIDIAN_DAGGER.get(), CombatSetup.MISTCLOAK.get(), "  O", " O ", "s  ");
        buildShaped(consumer, CombatSetup.KOLOSS_BLADE.get(), ConsumeSetup.LERASIUM_NUGGET.get(), "CC", "CC", "sC");

        // Ore Recipes
        buildSmeltingAndBlasting(consumer, MaterialsSetup.COPPER_INGOT.get(), MaterialsSetup.COPPER_ORE.get(), 0.8F);
        buildSmeltingAndBlasting(consumer, MaterialsSetup.ZINC_INGOT.get(), MaterialsSetup.ZINC_ORE.get(), 0.6F);
        buildSmeltingAndBlasting(consumer, MaterialsSetup.TIN_INGOT.get(), MaterialsSetup.TIN_ORE.get(), 0.6F);
        buildSmeltingAndBlasting(consumer, MaterialsSetup.LEAD_INGOT.get(), MaterialsSetup.LEAD_ORE.get(), 0.4F);

        buildShapless(consumer, ConsumeSetup.LERASIUM_NUGGET.get(), 1, Items.NETHER_STAR, ing(Items.NETHER_STAR), ing(Tags.Items.STORAGE_BLOCKS_GOLD));

        // Grinder Recipes
        for (RegistryObject<Item> flake_reg : MaterialsSetup.FLAKES) {
            Item flake = flake_reg.get();
            String flakeType = flake.getRegistryName().getPath().substring(0, flake.getRegistryName().getPath().indexOf('_'));
            buildShapless(consumer, flake, 2, ConsumeSetup.ALLOMANTIC_GRINDER.get(), ing(ConsumeSetup.ALLOMANTIC_GRINDER.get()),
                    ing(new ItemTags.Wrapper(new ResourceLocation("forge",
                            "ingots/" + flakeType))));
        }

        // Mixing Recipes
        buildShapless(consumer, MaterialsSetup.FLAKES.get(Allomancy.STEEL).get(), 2, ConsumeSetup.ALLOMANTIC_GRINDER.get(),
                "allomancy:steel_flakes_from_mixing",
                ing(Items.COAL), ing(MaterialsSetup.FLAKES.get(Allomancy.IRON).get()));
        buildShapless(consumer, MaterialsSetup.FLAKES.get(Allomancy.PEWTER).get(), 2, ConsumeSetup.ALLOMANTIC_GRINDER.get(),
                "allomancy:pewter_flakes_from_mixing",
                ing(MaterialsSetup.FLAKES.get(Allomancy.TIN).get()), ing(MaterialsSetup.FLAKES.get(8).get()));
        buildShapless(consumer, MaterialsSetup.FLAKES.get(Allomancy.BRASS).get(), 2, ConsumeSetup.ALLOMANTIC_GRINDER.get(),
                "allomancy:brass_flakes_from_mixing",
                ing(MaterialsSetup.FLAKES.get(Allomancy.ZINC).get()), ing(MaterialsSetup.FLAKES.get(Allomancy.COPPER).get()));
        buildShapless(consumer, MaterialsSetup.FLAKES.get(Allomancy.BRONZE).get(), 2, ConsumeSetup.ALLOMANTIC_GRINDER.get(),
                "allomancy:bronze_flakes_from_mixing",
                ing(MaterialsSetup.FLAKES.get(Allomancy.TIN).get()), ing(MaterialsSetup.FLAKES.get(Allomancy.COPPER).get()));

        Allomancy.LOGGER.debug("Creating Shaped Recipe for allomancy:coin_bag");
        ShapedRecipeBuilder.shapedRecipe(CombatSetup.COIN_BAG.get())
                .addCriterion("has_gold_nugget", InventoryChangeTrigger.Instance.forItems(CombatSetup.MISTCLOAK.get()))
                .key('#', Items.LEAD).key('l', Items.LEATHER).key('g', Items.GOLD_NUGGET)
                .patternLine(" #g").patternLine("l l").patternLine(" l ")
                .build(consumer);

        Allomancy.LOGGER.debug("Creating Special Recipe for Vial Filling");
        CustomRecipeBuilder.func_218656_a(ConsumeSetup.VIAL_RECIPE_SERIALIZER.get())
                .build(consumer, "allomancy:vial_filling_recipe");

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
            if (defaultIngredients.containsKey(c)) {
                builder.key(c, defaultIngredients.get(c));
            }
        }

        builder.build(consumer);
    }

    protected void buildShaped(Consumer<IFinishedRecipe> consumer, IItemProvider result, Item criterion, String... lines) {
        buildShaped(consumer, result, 1, criterion, lines);
    }


    protected void buildShapless(Consumer<IFinishedRecipe> consumer, IItemProvider result, int count, Item criterion, Ingredient... ingredients) {
        buildShapless(consumer, result, count, criterion, "", ingredients);
    }

    protected void buildShapless(Consumer<IFinishedRecipe> consumer, IItemProvider result, int count, Item criterion, String save, Ingredient... ingredients) {
        Allomancy.LOGGER.debug("Creating Shapeless Recipe for " + result.asItem().getRegistryName() + " " + save);

        ShapelessRecipeBuilder builder = ShapelessRecipeBuilder.shapelessRecipe(result, count);

        builder.addCriterion("has_" + criterion.getRegistryName().getPath(), InventoryChangeTrigger.Instance.forItems(criterion));

        for (Ingredient ingredient : ingredients) {
            builder.addIngredient(ingredient);
        }

        if (save.equals("")) {
            builder.build(consumer);
        } else {
            builder.build(consumer, save);
        }
    }


    protected void buildSmeltingAndBlasting(Consumer<IFinishedRecipe> consumer, IItemProvider result, IItemProvider ingredient, float xp) {
        Allomancy.LOGGER.debug("Creating Smelting and Blasting Recipe for " + result.asItem().getRegistryName());

        CookingRecipeBuilder smelt = CookingRecipeBuilder.smeltingRecipe(ing(ingredient), result, xp, 200);
        CookingRecipeBuilder blast = CookingRecipeBuilder.blastingRecipe(ing(ingredient), result, xp, 100);

        smelt.addCriterion("has_" + ingredient.asItem().getRegistryName().getPath(), InventoryChangeTrigger.Instance.forItems(ingredient));
        blast.addCriterion("has_" + ingredient.asItem().getRegistryName().getPath(), InventoryChangeTrigger.Instance.forItems(ingredient));

        smelt.build(consumer);
        blast.build(consumer, result.asItem().getRegistryName() + "_from_blasting");

    }

    protected void add(char c, Tag<Item> itemTag) {
        defaultIngredients.put(c, Ingredient.fromTag(itemTag));
    }

    protected void add(char c, IItemProvider itemProvider) {
        defaultIngredients.put(c, Ingredient.fromItems(itemProvider));
    }

    protected void add(char c, Ingredient ingredient) {
        defaultIngredients.put(c, ingredient);
    }

    protected Ingredient ing(Tag<Item> itemTag) {
        return Ingredient.fromTag(itemTag);
    }

    protected Ingredient ing(IItemProvider itemProvider) {
        return Ingredient.fromItems(itemProvider);
    }

    protected Ingredient ing(Ingredient ingredient) {
        return ingredient;
    }


    @Override
    public String getName() {
        return "Allomancy Recipes";
    }
}
