package com.legobmw99.allomancy.integration.jei;

import com.legobmw99.allomancy.Allomancy;
import com.legobmw99.allomancy.modules.consumables.item.recipe.VialItemRecipe;
import com.legobmw99.allomancy.modules.extras.ExtrasSetup;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import mezz.jei.api.registration.IVanillaCategoryExtensionRegistration;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

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
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        registration.addCraftingStation(InvestingRecipeCategory.TYPE, ExtrasSetup.CHARGED_BRONZE_EARRING);
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        // TODO: actually list/show mobs? Use InventoryScreen#renderEntityInInventoryFollowsMouse
        registration.addIngredientInfo(ExtrasSetup.CHARGED_BRONZE_EARRING,
                                       Component.translatable("allomancy.jei.charged_earring.1"),
                                       Component.translatable("allomancy.jei.charged_earring.2"),
                                       Component.translatable("allomancy.jei.charged_earring.3"));

        if (RecipeEventHandler.recipes != null) {
            registration.addRecipes(InvestingRecipeCategory.TYPE, RecipeEventHandler.recipes);
        }
    }

}
