package com.legobmw99.allomancy.item.recipe;

import com.legobmw99.allomancy.Allomancy;
import com.legobmw99.allomancy.util.Registry;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.item.crafting.SpecialRecipe;
import net.minecraft.item.crafting.SpecialRecipeSerializer;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

import javax.annotation.Nonnull;

public class VialItemRecipe extends SpecialRecipe {
    private static final Ingredient INGREDIENT_FLAKES = Ingredient.fromItems(Registry.flakes);
    private static final Ingredient INGREDIENT_VIAL = Ingredient.fromItems(Registry.vial);

    private ItemStack item_result = ItemStack.EMPTY;


    public VialItemRecipe(ResourceLocation idIn) {
        super(idIn);
    }

    @Override
    public boolean matches(CraftingInventory inv, World worldIn) {
        this.item_result = ItemStack.EMPTY;

        boolean[] metals = {false, false, false, false, false, false, false, false};
        boolean[] ingredients = {false, false};

        for (int i = 0; i < inv.getSizeInventory(); ++i) {
            ItemStack itemstack = inv.getStackInSlot(i);
            if (!itemstack.isEmpty()) {
                if (INGREDIENT_FLAKES.test(itemstack)) {
                    for (int j = 0; j < metals.length; j++) {
                        if (itemstack.getItem() == Registry.flakes[j]) {
                            if (metals[j]) {
                                return false;
                            }

                            metals[j] = true;
                            ingredients[1] = true;
                        }
                    }
                } else if (INGREDIENT_VIAL.test(itemstack)) {
                    if (itemstack.getTag() != null) {
                        for (int j = 0; j < metals.length; j++) {
                            if (itemstack.getTag().contains(Registry.allomanctic_metals[j])) {
                                boolean hasMetalAlready = itemstack.getTag().getBoolean(Registry.allomanctic_metals[j]);
                                if (metals[j] && hasMetalAlready) {
                                    return false;
                                } else {
                                    metals[j] = metals[j] || hasMetalAlready;
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
            this.item_result = new ItemStack(Registry.vial, 1);
            CompoundNBT nbt = new CompoundNBT();
            for (int i = 0; i < metals.length; i++) {
                nbt.putBoolean(Registry.allomanctic_metals[i], metals[i]);
            }
            this.item_result.setTag(nbt);
            return true;
        } else {
            return false;
        }
    }


    @Override
    public ItemStack getCraftingResult(CraftingInventory inv) { //getCraftingResult
        return this.item_result.copy();
    }

    @Override
    public boolean isDynamic() {
        return true;
    }

    @Override
    public ItemStack getRecipeOutput() {
        return this.item_result;
    }

    @Override
    public boolean canFit(int width, int height) {
        return width * height >= 2;
    }

    @Nonnull
    @Override
    public IRecipeSerializer<?> getSerializer() {
        return Registry.vial_recipe_serializer;
    }


    public static class Serializer extends SpecialRecipeSerializer<VialItemRecipe> {

        public Serializer() {
            super(VialItemRecipe::new);
            setRegistryName(new ResourceLocation(Allomancy.MODID, "vial_filling"));
        }
    }
}
