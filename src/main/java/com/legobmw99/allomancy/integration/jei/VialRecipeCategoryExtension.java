package com.legobmw99.allomancy.integration.jei;

import com.legobmw99.allomancy.api.enums.Metal;
import com.legobmw99.allomancy.modules.consumables.ConsumeSetup;
import com.legobmw99.allomancy.modules.consumables.item.recipe.VialItemRecipe;
import com.legobmw99.allomancy.modules.world.WorldSetup;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.builder.IRecipeSlotBuilder;
import mezz.jei.api.gui.ingredient.ICraftingGridHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.category.extensions.vanilla.crafting.ICraftingCategoryExtension;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class VialRecipeCategoryExtension implements ICraftingCategoryExtension {

    public VialRecipeCategoryExtension(VialItemRecipe ignored) {}

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, ICraftingGridHelper craftingGridHelper, IFocusGroup focuses) {

        builder.setShapeless();

        List<ItemStack> input1 = new ArrayList<>();
        List<ItemStack> input2 = new ArrayList<>();
        List<ItemStack> outputs = new ArrayList<>();
        input2.add(new ItemStack(ConsumeSetup.VIAL.get()));

        for (Metal metalToAdd : Metal.values()) {
            input1.add(new ItemStack(WorldSetup.FLAKES.get(metalToAdd.getIndex()).get()));

            ItemStack vialFilled = new ItemStack(ConsumeSetup.VIAL.get());
            CompoundTag nbt = new CompoundTag();
            for (Metal mt : Metal.values()) {
                nbt.putBoolean(mt.getName(), mt == metalToAdd);
            }
            nbt.putInt("CustomModelData", 1);
            vialFilled.setTag(nbt);

            outputs.add(vialFilled);
        }

        var input = craftingGridHelper.createAndSetInputs(builder, Arrays.asList(input1, input2), 0, 0);
        input.get(0).addRichTooltipCallback((recipeSlotView, tooltip) -> {
            tooltip.add(Component.translatable("allomancy.jei.flake_input"));

        });
        input.get(1).addRichTooltipCallback((recipeSlotView, tooltip) -> {
            tooltip.add(Component.translatable("allomancy.jei.vial_input"));

        });

        var output = craftingGridHelper.createAndSetOutputs(builder, outputs);
        output.addRichTooltipCallback((recipeSlotView, tooltip) -> {
            tooltip.add(Component.translatable("allomancy.jei.vial_output"));
        });
        builder.createFocusLink(new IRecipeSlotBuilder[]{input.get(0), output});
    }


}
