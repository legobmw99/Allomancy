package com.legobmw99.allomancy.modules.consumables.item.recipe;

import com.legobmw99.allomancy.modules.consumables.ConsumeSetup;
import com.legobmw99.allomancy.modules.materials.MaterialsSetup;
import com.legobmw99.allomancy.setup.Metal;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.item.crafting.SpecialRecipe;
import net.minecraft.item.crafting.SpecialRecipeSerializer;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fml.RegistryObject;

import javax.annotation.Nonnull;
import java.util.Arrays;

public class VialItemRecipe extends SpecialRecipe {
    private static final Ingredient INGREDIENT_FLAKES = Ingredient.fromItems(MaterialsSetup.FLAKES.subList(0, Metal.values().length).stream()
            .map(RegistryObject::get).toArray(Item[]::new));
    private static final Ingredient INGREDIENT_VIAL = Ingredient.fromItems(ConsumeSetup.VIAL.get());

    private ItemStack item_result = ItemStack.EMPTY;


    public VialItemRecipe(ResourceLocation idIn) {
        super(idIn);
    }

    @Override
    public boolean matches(CraftingInventory inv, World worldIn) {
        this.item_result = ItemStack.EMPTY;

        boolean[] metals = new boolean[Metal.values().length];
        Arrays.fill(metals, false);
        boolean[] ingredients = {false, false};

        for (int i = 0; i < inv.getSizeInventory(); ++i) {
            ItemStack itemstack = inv.getStackInSlot(i);
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
            CompoundNBT nbt = new CompoundNBT();
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
        return ConsumeSetup.VIAL_RECIPE_SERIALIZER.get();
    }


    public static class Serializer extends SpecialRecipeSerializer<VialItemRecipe> {

        public Serializer() {
            super(VialItemRecipe::new);
        }
    }
}
