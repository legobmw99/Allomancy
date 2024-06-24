package com.legobmw99.allomancy.modules.consumables;

import com.legobmw99.allomancy.Allomancy;
import com.legobmw99.allomancy.modules.consumables.item.GrinderItem;
import com.legobmw99.allomancy.modules.consumables.item.LerasiumItem;
import com.legobmw99.allomancy.modules.consumables.item.VialItem;
import com.legobmw99.allomancy.modules.consumables.item.component.FlakeStorage;
import com.legobmw99.allomancy.modules.consumables.item.recipe.VialItemRecipe;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public final class ConsumeSetup {

    private static final DeferredRegister.DataComponents DATA_COMPONENTS =
            DeferredRegister.createDataComponents(Allomancy.MODID);
    public static final Supplier<DataComponentType<FlakeStorage>> FLAKE_STORAGE =
            DATA_COMPONENTS.registerComponentType("flake_storage", builder -> builder
                    .persistent(FlakeStorage.CODEC)
                    .networkSynchronized(FlakeStorage.STREAM_CODEC));

    private static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(Allomancy.MODID);

    public static final DeferredItem<GrinderItem> ALLOMANTIC_GRINDER =
            ITEMS.register("allomantic_grinder", GrinderItem::new);
    public static final DeferredItem<LerasiumItem> LERASIUM_NUGGET =
            ITEMS.register("lerasium_nugget", LerasiumItem::new);
    public static final DeferredItem<VialItem> VIAL = ITEMS.register("vial", VialItem::new);

    private static final DeferredRegister<RecipeSerializer<?>> RECIPES =
            DeferredRegister.create(Registries.RECIPE_SERIALIZER, Allomancy.MODID);
    public static final Supplier<VialItemRecipe.Serializer> VIAL_RECIPE_SERIALIZER =
            RECIPES.register("vial_filling", VialItemRecipe.Serializer::new);

    private ConsumeSetup() {}

    public static void register(IEventBus bus) {
        DATA_COMPONENTS.register(bus);
        ITEMS.register(bus);
        RECIPES.register(bus);
    }

}
