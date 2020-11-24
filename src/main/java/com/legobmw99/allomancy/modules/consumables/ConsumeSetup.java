package com.legobmw99.allomancy.modules.consumables;

import com.legobmw99.allomancy.Allomancy;
import com.legobmw99.allomancy.modules.consumables.item.GrinderItem;
import com.legobmw99.allomancy.modules.consumables.item.LerasiumItem;
import com.legobmw99.allomancy.modules.consumables.item.VialItem;
import com.legobmw99.allomancy.modules.consumables.item.recipe.VialItemRecipe;
import net.minecraft.item.Item;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.SpecialRecipeSerializer;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class ConsumeSetup {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, Allomancy.MODID);
    public static final DeferredRegister<IRecipeSerializer<?>> RECIPES = DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, Allomancy.MODID);
    public static final RegistryObject<GrinderItem> ALLOMANTIC_GRINDER = ITEMS.register("allomantic_grinder", GrinderItem::new);
    public static final RegistryObject<LerasiumItem> LERASIUM_NUGGET = ITEMS.register("lerasium_nugget", LerasiumItem::new);
    public static final RegistryObject<VialItem> VIAL = ITEMS.register("vial", VialItem::new);
    public static final RegistryObject<SpecialRecipeSerializer<VialItemRecipe>> VIAL_RECIPE_SERIALIZER = RECIPES.register("vial_filling", VialItemRecipe.Serializer::new);

    public static void register() {
        ITEMS.register(FMLJavaModLoadingContext.get().getModEventBus());
        RECIPES.register(FMLJavaModLoadingContext.get().getModEventBus());
    }

}
