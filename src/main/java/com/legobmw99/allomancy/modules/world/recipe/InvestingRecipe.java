package com.legobmw99.allomancy.modules.world.recipe;

import com.legobmw99.allomancy.modules.world.WorldSetup;
import net.minecraft.core.HolderLookup;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;

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

    public static class InvestingWrapper implements RecipeInput {

        private final ItemStack stack;

        public InvestingWrapper(ItemStack stack) {
            this.stack = stack;
        }

        @Override
        public ItemStack getItem(int index) {
            return index == 0 ? stack : ItemStack.EMPTY;
        }

        @Override
        public int size() {
            return 1;
        }
    }


}
