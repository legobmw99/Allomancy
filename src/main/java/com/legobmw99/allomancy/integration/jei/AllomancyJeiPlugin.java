package com.legobmw99.allomancy.integration.jei;

import com.legobmw99.allomancy.Allomancy;
import com.legobmw99.allomancy.modules.consumables.ConsumeSetup;
import com.legobmw99.allomancy.modules.consumables.item.recipe.VialItemRecipe;
import com.legobmw99.allomancy.modules.extras.ExtrasSetup;
import com.legobmw99.allomancy.modules.world.recipe.InvestingRecipe;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import mezz.jei.api.registration.IVanillaCategoryExtensionRegistration;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;

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
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        // TODO: actually list/show mobs? Would have same registry problem as below.
        registration.addIngredientInfo(
                List.of(ExtrasSetup.CHARGED_BRONZE_EARRING.toStack(), ExtrasSetup.BRONZE_EARRING.toStack()),
                VanillaTypes.ITEM_STACK, Component.translatable("allomancy.jei.charged_earring.1"),
                Component.translatable("allomancy.jei.charged_earring.2"),
                Component.translatable("allomancy.jei.charged_earring.3"));

        // TODO figure out how to use Registry now
        registration.addRecipes(InvestingRecipeCategory.TYPE, List.of(new RecipeHolder<>(
                ResourceKey.create(Registries.RECIPE, Allomancy.rl("lerasium_investing")),
                new InvestingRecipe(Ingredient.of(Items.NETHER_STAR), ConsumeSetup.LERASIUM_NUGGET.toStack()))));
    }

}
