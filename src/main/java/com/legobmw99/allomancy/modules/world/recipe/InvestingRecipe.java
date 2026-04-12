package com.legobmw99.allomancy.modules.world.recipe;

import com.legobmw99.allomancy.modules.world.WorldSetup;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;

public record InvestingRecipe(Ingredient ingredient,
                              ItemStackTemplate result) implements Recipe<InvestingRecipe.InvestingWrapper> {

    @Override
    public boolean matches(InvestingWrapper input, Level level) {
        return !input.isEmpty() && this.ingredient.acceptsItem(input.getItem(0).typeHolder());
    }

    @Override
    public ItemStack assemble(InvestingWrapper input) {
        return result.withCount(result.count() * input.getItem(0).count()).create();
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

    @Override
    public boolean showNotification() {
        return false;
    }

    @Override
    public String group() {
        return "";
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
