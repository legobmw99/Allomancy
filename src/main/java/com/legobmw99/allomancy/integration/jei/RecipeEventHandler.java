package com.legobmw99.allomancy.integration.jei;

import com.legobmw99.allomancy.Allomancy;
import com.legobmw99.allomancy.modules.world.WorldSetup;
import com.legobmw99.allomancy.modules.world.recipe.InvestingRecipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;
import net.neoforged.neoforge.client.event.RecipesReceivedEvent;
import net.neoforged.neoforge.event.OnDatapackSyncEvent;

import java.util.List;

/**
 * This handles the fact that recipes are not, by default, sent to the client anymore.
 */
@EventBusSubscriber(modid = Allomancy.MODID)
final class RecipeEventHandler {
    private RecipeEventHandler() {}

    public static List<RecipeHolder<InvestingRecipe>> recipes = null;

    @SubscribeEvent
    public static void onRecipesRecieved(final RecipesReceivedEvent event) {
        recipes = event.getRecipeMap().byType(WorldSetup.INVESTING_RECIPE.get()).stream().toList();
    }

    @SubscribeEvent
    public static void onClientDisconnect(final ClientPlayerNetworkEvent.LoggingOut event) {
        recipes = null;
    }

    @SubscribeEvent
    public static void onDataSync(final OnDatapackSyncEvent event) {
        event.sendRecipes(WorldSetup.INVESTING_RECIPE.get());
    }
}
