package com.legobmw99.allomancy.integration.jei;

import com.legobmw99.allomancy.modules.consumables.ConsumeSetup;
import com.legobmw99.allomancy.modules.world.WorldSetup;
import com.legobmw99.allomancy.modules.world.recipe.InvestingRecipe;
import com.legobmw99.allomancy.util.ItemDisplay;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.widgets.IRecipeExtrasBuilder;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.category.IRecipeCategory;
import mezz.jei.api.recipe.types.IRecipeHolderType;
import mezz.jei.api.recipe.types.IRecipeType;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.crafting.RecipeHolder;
import org.jetbrains.annotations.Nullable;

public class InvestingRecipeCategory implements IRecipeCategory<RecipeHolder<InvestingRecipe>> {
    public static final IRecipeHolderType<InvestingRecipe> TYPE =
            IRecipeType.create(WorldSetup.INVESTING_RECIPE.get());

    private final IDrawable icon;

    public InvestingRecipeCategory(IGuiHelper helper) {
        icon = helper.createDrawableItemLike(ConsumeSetup.LERASIUM_NUGGET.get());
    }

    @Override
    public IRecipeType<RecipeHolder<InvestingRecipe>> getRecipeType() {
        return TYPE;
    }

    @Override
    public Component getTitle() {
        return Component.translatable("allomancy.jei.investing");
    }

    @Override
    public @Nullable IDrawable getIcon() {
        return icon;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, RecipeHolder<InvestingRecipe> recipe, IFocusGroup focuses) {
        builder.addInputSlot(28, 14).add(recipe.value().ingredient());

        // TODO better liquid rendering?
        builder.addSlot(RecipeIngredientRole.CRAFTING_STATION, 38, 24).add(WorldSetup.LERASIUM_FLUID.get());
        builder.addOutputSlot(96, 24).add(recipe.value().result()).setOutputSlotBackground();
    }

    @Override
    public void createRecipeExtras(IRecipeExtrasBuilder builder,
                                   RecipeHolder<InvestingRecipe> recipe,
                                   IFocusGroup focuses) {
        builder.addRecipeArrow().setPosition(64, 24);
        builder.addText(ItemDisplay.addColorToText("allomancy.jei.investing.description", ChatFormatting.DARK_PURPLE),
                        getWidth(), getHeight());
    }

    @Override
    public @Nullable Identifier getIdentifier(RecipeHolder<InvestingRecipe> recipe) {
        return recipe.id().identifier();
    }

    @Override
    public int getWidth() {
        return 128;
    }

    @Override
    public int getHeight() {
        return 64;
    }
}
