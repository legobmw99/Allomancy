package com.legobmw99.allomancy.integration.jei;

import com.legobmw99.allomancy.api.enums.Metal;
import com.legobmw99.allomancy.modules.consumables.item.VialItem;
import com.legobmw99.allomancy.modules.consumables.item.component.FlakeStorage;
import com.legobmw99.allomancy.modules.consumables.item.recipe.VialItemRecipe;
import com.legobmw99.allomancy.modules.world.WorldSetup;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.ingredient.ICraftingGridHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.category.extensions.vanilla.crafting.ICraftingCategoryExtension;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
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

        List<ItemStack> input1 = new ArrayList<>();
        SlotDisplay display = recipeHolder.value().getBase().display();

        for (Metal metalToAdd : Metal.values()) {
            input1.add(WorldSetup.FLAKES.get(metalToAdd.getIndex()).toStack());

        }

        var input = craftingGridHelper.createAndSetInputs(builder, List.of(input1, List.of()), 0, 0);
        input
                .get(0)
                .addRichTooltipCallback(
                        (_, tooltip) -> tooltip.add(Component.translatable("allomancy.jei.flake_input")));

        input.get(1).add(display);
        input
                .get(1)
                .addRichTooltipCallback(
                        (_, tooltip) -> tooltip.add(Component.translatable("allomancy.jei.vial_input")));

        List<ItemStack> outputs = new ArrayList<>();
        for (Metal metalToAdd : Metal.values()) {
            // TODO: this is wrong when the recipe is a tag, but the focus link requires lengths to be the same
            ItemStack vialFilled = display.resolveForFirstStack(input.get(1).getContextMap());
            VialItem.fillVial(vialFilled, new FlakeStorage.Mutable().add(metalToAdd).toImmutable());
            outputs.add(vialFilled);
        }

        var output = craftingGridHelper.createAndSetOutputs(builder, outputs);
        output.addRichTooltipCallback(
                (_, tooltip) -> tooltip.add(Component.translatable("allomancy.jei.vial_output")));

        builder.createFocusLink(input.getFirst(), output);
    }

}
