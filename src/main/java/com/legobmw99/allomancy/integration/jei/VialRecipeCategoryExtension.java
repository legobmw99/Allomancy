package com.legobmw99.allomancy.integration.jei;

import com.legobmw99.allomancy.api.enums.Metal;
import com.legobmw99.allomancy.modules.consumables.item.VialItem;
import com.legobmw99.allomancy.modules.consumables.item.component.FlakeStorage;
import com.legobmw99.allomancy.modules.consumables.item.recipe.VialItemRecipe;
import com.legobmw99.allomancy.modules.world.WorldSetup;
import com.legobmw99.allomancy.util.AllomancyTags;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.ingredient.ICraftingGridHelper;
import mezz.jei.api.gui.ingredient.IRecipeSlotDrawable;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.category.extensions.vanilla.crafting.ICraftingCategoryExtension;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.display.SlotDisplay;

import java.util.ArrayList;
import java.util.List;

public class VialRecipeCategoryExtension implements ICraftingCategoryExtension<VialItemRecipe> {

    @Override
    public List<SlotDisplay> getIngredients(RecipeHolder<VialItemRecipe> recipeHolder) {
        return List.of(recipeHolder.value().getBase().display());
    }


    @Override
    public void setRecipe(RecipeHolder<VialItemRecipe> recipeHolder,
                          IRecipeLayoutBuilder builder,
                          ICraftingGridHelper craftingGridHelper,
                          IFocusGroup focuses) {

        builder.setShapeless();

        Ingredient base = recipeHolder.value().getBase();
        List<ItemStack> flakes = new ArrayList<>();

        for (Metal metalToAdd : Metal.values()) {
            flakes.add(WorldSetup.FLAKES.get(metalToAdd.getIndex()).toStack());

        }

        var input = craftingGridHelper.createAndSetInputs(builder, List.of(flakes, List.of()), 0, 0);
        input
                .get(0)
                .addRichTooltipCallback(
                        (_, tooltip) -> tooltip.add(Component.translatable("allomancy.jei.flake_input")));

        input.get(1).add(base);
        input
                .get(1)
                .addRichTooltipCallback(
                        (_, tooltip) -> tooltip.add(Component.translatable("allomancy.jei.vial_input")));

        var output = craftingGridHelper.createAndSetOutputs(builder, base.display());
        output.addRichTooltipCallback(
                (_, tooltip) -> tooltip.add(Component.translatable("allomancy.jei.vial_output")));

    }

    @Override
    public void onDisplayedIngredientsUpdate(RecipeHolder<VialItemRecipe> recipeHolder,
                                             List<IRecipeSlotDrawable> recipeSlots,
                                             IFocusGroup focuses) {
        ItemStack flake = ItemStack.EMPTY;
        ItemStack vial = ItemStack.EMPTY;

        for (IRecipeSlotDrawable slot : recipeSlots) {
            if (slot.getRole() != RecipeIngredientRole.INPUT) {
                continue;
            }
            ItemStack stack = slot.getDisplayedIngredient(VanillaTypes.ITEM_STACK).orElse(ItemStack.EMPTY);
            if (stack.isEmpty()) {
                continue;
            }
            if (stack.is(AllomancyTags.FLAKES_TAG)) {
                flake = stack;
            } else {
                vial = stack;
            }
        }

        if (vial.isEmpty() || flake.isEmpty()) {
            return;
        }

        Metal flakeMetal = null;
        for (Metal mt : Metal.values()) {
            if (flake.is(WorldSetup.FLAKES.get(mt.getIndex()))) {
                flakeMetal = mt;
                break;
            }
        }
        if (flakeMetal == null) {
            return;
        }

        ItemStack result = vial.copy();
        VialItem.fillVial(result, new FlakeStorage.Mutable().add(flakeMetal).toImmutable());

        for (IRecipeSlotDrawable slot : recipeSlots) {
            if (slot.getRole() != RecipeIngredientRole.OUTPUT) {
                continue;
            }
            slot.createDisplayOverrides().add(result);
        }
    }
}
