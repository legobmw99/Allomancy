package com.legobmw99.allomancy.integration.jei;

import com.legobmw99.allomancy.Allomancy;
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
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

public class InvestingRecipeCategory implements IRecipeCategory<InvestingRecipe> {
    public static final RecipeType<InvestingRecipe> TYPE =
            RecipeType.create(Allomancy.MODID, "investing", InvestingRecipe.class);

    private final IDrawable icon;

    public InvestingRecipeCategory(IGuiHelper helper) {
        icon = helper.createDrawableItemLike(ConsumeSetup.LERASIUM_NUGGET.get());
    }

    @Override
    public RecipeType<InvestingRecipe> getRecipeType() {
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
    public void setRecipe(IRecipeLayoutBuilder builder, InvestingRecipe recipe, IFocusGroup focuses) {
        builder.addInputSlot(30, 14).addIngredients(recipe.getIngredient());

        // TODO better liquid rendering?
        builder.addSlot(RecipeIngredientRole.CATALYST, 40, 24).addFluidStack(WorldSetup.LERASIUM_FLUID.get());
        builder.addOutputSlot(96, 24).addItemStack(recipe.getResult()).setOutputSlotBackground();
    }

    @Override
    public void createRecipeExtras(IRecipeExtrasBuilder builder, InvestingRecipe recipe, IFocusGroup focuses) {
        builder.addRecipeArrow().setPosition(64, 24);
        builder.addText(ItemDisplay.addColorToText("allomancy.jei.investing.description", ChatFormatting.DARK_PURPLE),
                        getWidth(), getHeight());
    }

    @Override
    public @Nullable ResourceLocation getRegistryName(InvestingRecipe recipe) {
        return recipe.getId();
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
