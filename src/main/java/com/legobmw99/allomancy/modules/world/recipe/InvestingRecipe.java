package com.legobmw99.allomancy.modules.world.recipe;

import com.legobmw99.allomancy.modules.world.WorldSetup;
import net.minecraft.core.HolderLookup;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.items.ItemStackHandler;
import net.neoforged.neoforge.items.wrapper.RecipeWrapper;

public record InvestingRecipe(Ingredient ingredient,
                              ItemStack result) implements Recipe<InvestingRecipe.InvestingWrapper> {

    @Override
    public boolean matches(InvestingWrapper input, Level level) {
        return !input.isEmpty() && this.ingredient.acceptsItem(input.getItem(0).getItemHolder());
    }

    @Override
    public ItemStack assemble(InvestingWrapper input, HolderLookup.Provider registries) {
        return result.copyWithCount(result.getCount() * input.getItem(0).getCount());
    }

    @Override
    public boolean isSpecial() {
        return true;
    }

    @Override
    public RecipeSerializer<? extends Recipe<InvestingWrapper>> getSerializer() {
        return WorldSetup.INVESTING_RECIPE_SERIALIZER.get();
    }

    @Override
    public RecipeType<? extends Recipe<InvestingWrapper>> getType() {
        return WorldSetup.INVESTING_RECIPE.get();
    }

    @Override
    public PlacementInfo placementInfo() {
        return PlacementInfo.NOT_PLACEABLE;
    }

    @Override
    public RecipeBookCategory recipeBookCategory() {
        return RecipeBookCategories.CRAFTING_MISC;
    }

    public static class InvestingWrapper extends RecipeWrapper {
        public InvestingWrapper(ItemStack stack) {
            super(new ItemStackHandler(1));
            this.inv.insertItem(0, stack, false);
        }
    }


}
