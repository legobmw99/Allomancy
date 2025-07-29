package com.legobmw99.allomancy.modules.consumables.item.recipe;

import com.legobmw99.allomancy.api.enums.Metal;
import com.legobmw99.allomancy.modules.consumables.ConsumeSetup;
import com.legobmw99.allomancy.modules.consumables.item.VialItem;
import com.legobmw99.allomancy.modules.consumables.item.component.FlakeStorage;
import com.legobmw99.allomancy.modules.world.WorldSetup;
import net.minecraft.core.HolderLookup;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;

import javax.annotation.Nonnull;
import java.util.Arrays;

import static com.legobmw99.allomancy.modules.consumables.ConsumeSetup.FLAKE_STORAGE;

public class VialItemRecipe extends CustomRecipe {


    private static final Ingredient INGREDIENT_VIAL = Ingredient.of(ConsumeSetup.VIAL.get());

    public VialItemRecipe(CraftingBookCategory catIn) {
        super(catIn);
    }

    @Override
    public boolean matches(CraftingInput input, Level worldIn) {

        boolean[] metals = new boolean[Metal.values().length];
        Arrays.fill(metals, false);

        boolean hasVial = false;
        boolean hasFlake = false;

        for (ItemStack stack : input.items()) {
            if (stack.isEmpty()) {
                continue;
            }
            if (INGREDIENT_VIAL.test(stack) && !hasVial) {
                FlakeStorage storage = stack.get(FLAKE_STORAGE);
                if (storage != null) {
                    for (Metal mt : Metal.values()) {
                        boolean hasMetalAlready = storage.contains(mt);
                        if (metals[mt.getIndex()] && hasMetalAlready) {
                            return false;
                        } else {
                            metals[mt.getIndex()] |= hasMetalAlready;
                        }
                    }
                }
                hasVial = true;
                continue;
            }
            boolean any = false;
            for (Metal mt : Metal.values()) {
                if (stack.getItem() == WorldSetup.FLAKES.get(mt.getIndex()).get()) {
                    if (metals[mt.getIndex()]) {
                        return false;
                    }

                    metals[mt.getIndex()] = true;
                    hasFlake = true;
                    any = true;
                    break;
                }
            }
            if (!any) {
                return false;
            }
        }
        return hasVial && hasFlake;

    }

    @Override
    public ItemStack assemble(CraftingInput input, HolderLookup.Provider provider) {

        FlakeStorage.Mutable storage = new FlakeStorage.Mutable();

        for (var stack : input.items()) {
            if (stack.isEmpty()) {
                continue;
            }
            if (INGREDIENT_VIAL.test(stack)) {
                storage.addAll(stack.get(FLAKE_STORAGE));
                continue;
            }
            for (Metal mt : Metal.values()) {
                if (stack.getItem() == WorldSetup.FLAKES.get(mt.getIndex()).get()) {
                    storage.add(mt);
                    break;
                }
            }
        }

        var item_result = ConsumeSetup.VIAL.toStack();
        VialItem.fillVial(item_result, storage.toImmutable());
        return item_result;

    }

    @Override
    public boolean isSpecial() {
        return true;
    }


    @Nonnull
    @Override
    public RecipeSerializer<? extends CustomRecipe> getSerializer() {
        return ConsumeSetup.VIAL_RECIPE_SERIALIZER.get();
    }


}
