package com.legobmw99.allomancy.integration.jei;

import com.legobmw99.allomancy.Allomancy;
import com.legobmw99.allomancy.modules.consumables.item.recipe.VialItemRecipe;
import com.legobmw99.allomancy.modules.extras.ExtrasSetup;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.registration.*;
import net.minecraft.resources.ResourceLocation;

import java.util.Collections;
import java.util.List;

@JeiPlugin
public class AllomancyJeiPlugin implements IModPlugin {
    private static final ResourceLocation ID = Allomancy.rl("jei_plugin");

    @Override
    public ResourceLocation getPluginUid() {
        return ID;
    }

    @Override
    public void registerVanillaCategoryExtensions(IVanillaCategoryExtensionRegistration registration) {
        registration.getCraftingCategory().addExtension(VialItemRecipe.class, new VialRecipeCategoryExtension());
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        registration.addRecipeCategories(new InvestingRecipeCategory(registration.getJeiHelpers().getGuiHelper()));
        registration.addRecipeCategories(new SpikingRecipeCategory(registration.getJeiHelpers().getGuiHelper()));
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        registration.addCraftingStation(InvestingRecipeCategory.TYPE, ExtrasSetup.CHARGED_BRONZE_EARRING);
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        registration.addRecipes(SpikingRecipeCategory.TYPE, List.of(SpikingRecipeCategory.Values.EARRING));

        if (RecipeEventHandler.recipes != null) {
            registration.addRecipes(InvestingRecipeCategory.TYPE, RecipeEventHandler.recipes);
        }
    }

    @Override
    public void registerIngredients(IModIngredientRegistration registration) {
        registration.register(EntityIngredient.ENTITY_TYPE, Collections.emptyList(), new EntityIngredient.Helper(),
                              new EntityIngredient.Renderer(16), EntityIngredient.CODEC);
    }
}
