package com.legobmw99.allomancy.integration.jei;

import com.legobmw99.allomancy.Allomancy;
import com.legobmw99.allomancy.modules.extras.ExtrasSetup;
import com.legobmw99.allomancy.util.AllomancyTags;
import com.legobmw99.allomancy.util.ItemDisplay;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.widgets.IRecipeExtrasBuilder;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.category.IRecipeCategory;
import mezz.jei.api.recipe.types.IRecipeType;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SpawnEggItem;
import net.neoforged.neoforge.common.Tags;
import org.jetbrains.annotations.Nullable;

import java.util.List;


public class SpikingRecipeCategory implements IRecipeCategory<SpikingRecipeCategory.Values> {
    public static final IRecipeType<Values> TYPE = IRecipeType.create(Allomancy.id("spiking"), Values.class);

    private final IDrawable icon;

    public SpikingRecipeCategory(IGuiHelper helper) {
        icon = helper.createDrawableItemLike(ExtrasSetup.BRONZE_EARRING.get());
    }

    @Override
    public IRecipeType<Values> getRecipeType() {
        return TYPE;
    }

    @Override
    public Component getTitle() {
        return Component.translatable("allomancy.jei.spiking");
    }

    @Override
    public @Nullable IDrawable getIcon() {
        return icon;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, Values recipe, IFocusGroup focuses) {

        List<? extends EntityType<?>> options = Minecraft.getInstance().level
                .registryAccess()
                .lookupOrThrow(Registries.ENTITY_TYPE)
                .getOrThrow(AllomancyTags.HEMALURGIC_CHARGERS)
                .stream()
                .map(Holder::value)
                .toList();

        var i = builder
                .addSlot(RecipeIngredientRole.INPUT, 4, 44)
                .setCustomRenderer(EntityIngredient.ENTITY_TYPE, new EntityIngredient.Renderer(48))
                .addIngredients(EntityIngredient.ENTITY_TYPE, options.stream().map(EntityIngredient::new).toList())
                .addRichTooltipCallback((recipeSlotView, tooltip) -> {
                    tooltip.add(ItemDisplay.addColorToText("jei.tooltip.recipe.tag", ChatFormatting.GRAY, ""));
                    tooltip.add(Component
                                        .translatableWithFallback(
                                                Tags.getTagTranslationKey(AllomancyTags.HEMALURGIC_CHARGERS),
                                                "#" + AllomancyTags.HEMALURGIC_CHARGERS.location())
                                        .withStyle(ChatFormatting.GRAY));
                });

        var e = builder
                .addInvisibleIngredients(RecipeIngredientRole.INPUT)
                .addIngredients(VanillaTypes.ITEM_STACK, options.stream().map(t -> {
                    var egg = SpawnEggItem.byId(t);
                    if (egg != null) {
                        return new ItemStack(egg);
                    }
                    return ItemStack.EMPTY;
                }).toList());

        builder.createFocusLink(i, e);

        builder.addInputSlot(20, 20).add(ExtrasSetup.BRONZE_EARRING.get());
        builder.addOutputSlot(96, 32).add(ExtrasSetup.CHARGED_BRONZE_EARRING.get()).setOutputSlotBackground();

    }

    @Override
    public void createRecipeExtras(IRecipeExtrasBuilder builder, Values recipe, IFocusGroup focuses) {
        builder.addRecipeArrow().setPosition(64, 32);
        builder.addText(ItemDisplay.addColorToText("allomancy.jei.spiking.description", ChatFormatting.DARK_RED),
                        getWidth(), getHeight());
    }

    @Override
    public @Nullable Identifier getIdentifier(Values recipe) {
        return Allomancy.id("spiking_earring");
    }

    @Override
    public int getWidth() {
        return 128;
    }

    @Override
    public int getHeight() {
        return 96;
    }

    public enum Values {
        EARRING
    }

}
