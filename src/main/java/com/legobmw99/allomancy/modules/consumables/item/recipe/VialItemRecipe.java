package com.legobmw99.allomancy.modules.consumables.item.recipe;

import com.legobmw99.allomancy.api.enums.Metal;
import com.legobmw99.allomancy.modules.consumables.ConsumeSetup;
import com.legobmw99.allomancy.modules.materials.MaterialsSetup;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.function.Supplier;

public class VialItemRecipe extends CustomRecipe {
    private static final Ingredient INGREDIENT_FLAKES = Ingredient.of(MaterialsSetup.FLAKES.subList(0, Metal.values().length).stream().map(Supplier::get).toArray(Item[]::new));
    private static final Ingredient INGREDIENT_VIAL = Ingredient.of(ConsumeSetup.VIAL.get());

    private ItemStack item_result = ItemStack.EMPTY;


    public VialItemRecipe(CraftingBookCategory catIn) {
        super(catIn);
    }

    @Override
    public boolean matches(CraftingContainer inv, Level worldIn) {
        this.item_result = ItemStack.EMPTY;

        boolean[] metals = new boolean[Metal.values().length];
        Arrays.fill(metals, false);
        boolean[] ingredients = {false, false};

        for (int i = 0; i < inv.getContainerSize(); ++i) {
            ItemStack itemstack = inv.getItem(i);
            if (!itemstack.isEmpty()) {
                if (INGREDIENT_FLAKES.test(itemstack)) {
                    for (Metal mt : Metal.values()) {
                        if (itemstack.getItem() == MaterialsSetup.FLAKES.get(mt.getIndex()).get()) {
                            if (metals[mt.getIndex()]) {
                                return false;
                            }

                            metals[mt.getIndex()] = true;
                            ingredients[1] = true;
                        }
                    }
                } else if (INGREDIENT_VIAL.test(itemstack)) {
                    if (itemstack.getTag() != null) {
                        for (Metal mt : Metal.values()) {
                            if (itemstack.getTag().contains(mt.getName())) {
                                boolean hasMetalAlready = itemstack.getTag().getBoolean(mt.getName());
                                if (metals[mt.getIndex()] && hasMetalAlready) {
                                    return false;
                                } else {
                                    metals[mt.getIndex()] = metals[mt.getIndex()] || hasMetalAlready;
                                }
                            }
                        }
                    }

                    ingredients[0] = true;
                } else {
                    return false;
                }

            }
        }
        if (ingredients[0] && ingredients[1]) {
            this.item_result = new ItemStack(ConsumeSetup.VIAL.get(), 1);
            CompoundTag nbt = new CompoundTag();
            for (Metal mt : Metal.values()) {
                nbt.putBoolean(mt.getName(), metals[mt.getIndex()]);
            }
            nbt.putInt("CustomModelData", 1);
            this.item_result.setTag(nbt);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public ItemStack assemble(CraftingContainer p_44001_, RegistryAccess p_267165_) {
        return this.item_result.copy();
    }

    @Override
    public boolean isSpecial() {
        return true;
    }


    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return width * height >= 2;
    }

    @Nonnull
    @Override
    public RecipeSerializer<?> getSerializer() {
        return ConsumeSetup.VIAL_RECIPE_SERIALIZER.get();
    }


    public static class Serializer extends SimpleCraftingRecipeSerializer<VialItemRecipe> {

        public Serializer() {
            super(VialItemRecipe::new);
        }
    }
}
