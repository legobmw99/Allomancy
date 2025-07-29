package com.legobmw99.allomancy.modules.world.recipe;

import com.legobmw99.allomancy.modules.world.WorldSetup;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.wrapper.RecipeWrapper;

public class InvestingRecipe implements Recipe<InvestingRecipe.InvestingWrapper> {
    private final ItemStack result;
    private final Ingredient ingredient;
    private final ResourceLocation id;

    public InvestingRecipe(ResourceLocation id, Ingredient ingredient, ItemStack result) {
        this.id = id;
        this.result = result;
        this.ingredient = ingredient;
    }

    @Override
    public boolean matches(InvestingWrapper input, Level level) {
        return !input.isEmpty() && this.ingredient.test(input.getItem(0));
    }


    @Override
    public boolean canCraftInDimensions(int pWidth, int pHeight) {
        return false;
    }

    @Override
    public ItemStack getResultItem(RegistryAccess pRegistryAccess) {
        return result.copy();
    }

    @Override
    public ItemStack assemble(InvestingWrapper input, RegistryAccess registries) {
        return result.copyWithCount(result.getCount() * input.getItem(0).getCount());
    }

    @Override
    public boolean isSpecial() {
        return true;
    }

    @Override
    public ResourceLocation getId() {
        return id;
    }

    @Override
    public RecipeSerializer<? extends Recipe<InvestingWrapper>> getSerializer() {
        return WorldSetup.INVESTING_RECIPE_SERIALIZER.get();
    }

    @Override
    public RecipeType<? extends Recipe<InvestingWrapper>> getType() {
        return WorldSetup.INVESTING_RECIPE.get();
    }


    public static class InvestingWrapper extends RecipeWrapper {
        public InvestingWrapper(ItemStack stack) {
            super(new ItemStackHandler(1));
            this.inv.insertItem(0, stack, false);
        }
    }

    public Ingredient getIngredient() {
        return this.ingredient;
    }

    public ItemStack getResult() {
        return this.result.copy();
    }
}
