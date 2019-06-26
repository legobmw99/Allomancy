package com.legobmw99.allomancy.util;

import com.legobmw99.allomancy.Allomancy;
import com.legobmw99.allomancy.items.VialItem;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.SpecialRecipe;
import net.minecraft.item.crafting.SpecialRecipeSerializer;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

import javax.annotation.Nonnull;

public class VialItemRecipe extends SpecialRecipe {
    private ItemStack item_result = ItemStack.EMPTY;


    public VialItemRecipe(ResourceLocation idIn) {
        super(idIn);
    }

    @Override
    public boolean func_77569_a(CraftingInventory inv, World worldIn) { //matches
        this.item_result = ItemStack.EMPTY;

        boolean[] metals = {false, false, false, false, false, false, false, false};
        boolean[] ingredients = {false, false};

        for (int i = 0; i < inv.getSizeInventory(); ++i) {
            ItemStack itemstack = inv.getStackInSlot(i);

            if (!itemstack.isEmpty()) {
                if (!itemstack.getItem().getRegistryName().toString().matches("allomancy:.*_flakes") && !(itemstack.getItem() instanceof VialItem)) {
                    return false;
                }
                for (int j = 0; j < 8; j++) {
                    if (itemstack.getItem().getRegistryName().equals(new ResourceLocation(Allomancy.MODID, Registry.flake_metals[j] + "_flakes"))) {
                        ingredients[1] = true;
                        metals[j] = true;
                    }
                }
                if (itemstack.getItem() == Registry.vial) {
                    ingredients[0] = true;
                }
            }
        }


        for (int i = 0; i < inv.getSizeInventory(); ++i) {
            ItemStack itemstack = inv.getStackInSlot(i);
            if (!itemstack.isEmpty()) {
                if (itemstack.getItem() == Registry.vial) {
                    for (int j = 0; j < metals.length; j++) {
                        if (itemstack.getTag() != null) {
                            if (itemstack.getTag().contains(Registry.flake_metals[j])) {
                                if (metals[j] == true && itemstack.getTag().getBoolean(Registry.flake_metals[j]) == metals[j]) {
                                    return false;
                                } else {
                                    metals[j] = metals[j] || itemstack.getTag().getBoolean(Registry.flake_metals[j]);
                                }
                            }
                        }
                    }
                }
            }
        }
        if (ingredients[0] && ingredients[1]) {
            this.item_result = new ItemStack(Registry.vial, 1);
            CompoundNBT nbt = new CompoundNBT();
            for (int i = 0; i < metals.length; i++) {
                nbt.putBoolean(Registry.flake_metals[i], metals[i]);
            }
            this.item_result.setTag(nbt);
            return true;
        } else {
            return false;
        }
    }


    @Override
    public ItemStack func_77572_b(CraftingInventory inv) { //getCraftingResult
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
