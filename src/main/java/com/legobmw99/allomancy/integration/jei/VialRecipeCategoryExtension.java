package com.legobmw99.allomancy.integration.jei;

import com.legobmw99.allomancy.api.enums.Metal;
import com.legobmw99.allomancy.modules.consumables.ConsumeSetup;
import com.legobmw99.allomancy.modules.consumables.item.VialItem;
import com.legobmw99.allomancy.modules.consumables.item.component.FlakeStorage;
import com.legobmw99.allomancy.modules.consumables.item.recipe.VialItemRecipe;
import com.legobmw99.allomancy.modules.world.WorldSetup;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.builder.IRecipeSlotBuilder;
import mezz.jei.api.gui.ingredient.ICraftingGridHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.category.extensions.vanilla.crafting.ICraftingCategoryExtension;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.display.SlotDisplay;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class VialRecipeCategoryExtension implements ICraftingCategoryExtension<VialItemRecipe> {

    @Override
    public List<SlotDisplay> getIngredients(RecipeHolder<VialItemRecipe> recipeHolder) {
        return List.of();
    }


    @Override
    public void setRecipe(RecipeHolder<VialItemRecipe> recipeHolder,
                          IRecipeLayoutBuilder builder,
                          ICraftingGridHelper craftingGridHelper,
                          IFocusGroup focuses) {

        builder.setShapeless();

        List<ItemStack> input1 = new ArrayList<>();
        List<ItemStack> input2 = new ArrayList<>();
        List<ItemStack> outputs = new ArrayList<>();
        input2.add(ConsumeSetup.VIAL.toStack());

        for (Metal metalToAdd : Metal.values()) {
            input1.add(WorldSetup.FLAKES.get(metalToAdd.getIndex()).toStack());

            ItemStack vialFilled = ConsumeSetup.VIAL.toStack();
            VialItem.fillVial(vialFilled, new FlakeStorage.Mutable().add(metalToAdd).toImmutable());

            outputs.add(vialFilled);
        }

        var input = craftingGridHelper.createAndSetInputs(builder, Arrays.asList(input1, input2), 0, 0);
        input
                .get(0)
                .addRichTooltipCallback((recipeSlotView, tooltip) -> tooltip.add(
                        Component.translatable("allomancy.jei.flake_input")));
        input
                .get(1)
                .addRichTooltipCallback(
                        (recipeSlotView, tooltip) -> tooltip.add(Component.translatable("allomancy.jei.vial_input")));

        var output = craftingGridHelper.createAndSetOutputs(builder, outputs);
        output.addRichTooltipCallback(
                (recipeSlotView, tooltip) -> tooltip.add(Component.translatable("allomancy.jei.vial_output")));
        builder.createFocusLink(new IRecipeSlotBuilder[]{input.getFirst(), output});
    }

}
