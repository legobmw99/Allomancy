package com.legobmw99.allomancy.integration.jei;

import com.legobmw99.allomancy.Allomancy;
import com.mojang.serialization.Codec;
import mezz.jei.api.ingredients.IIngredientHelper;
import mezz.jei.api.ingredients.IIngredientRenderer;
import mezz.jei.api.ingredients.IIngredientType;
import mezz.jei.api.ingredients.subtypes.UidContext;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public record EntityIngredient(EntityType<?> type) {
    public static final IIngredientType<EntityIngredient> ENTITY_TYPE = () -> EntityIngredient.class;

    public static final Codec<EntityIngredient> CODEC =
            EntityType.CODEC.xmap(EntityIngredient::new, EntityIngredient::type);


    public static class Renderer implements IIngredientRenderer<EntityIngredient> {
        private static final Identifier MISSING = Allomancy.id("textures/item/missingno.png");
        /**
         * Entity types that will not render, as they either errored or are the wrong type
         */
        private static final Set<EntityType<?>> IGNORED_ENTITIES = new HashSet<>();

        /**
         * Square size of the renderer in pixels
         */
        private final int size;

        /**
         * Cache of entities for each entity type
         */
        private final Map<EntityType<?>, Entity> ENTITY_MAP = new HashMap<>();

        public Renderer(int size) {
            this.size = size;
        }

        @Override
        public int getWidth() {
            return size;
        }

        @Override
        public int getHeight() {
            return size;
        }


        @Override
        public void render(GuiGraphics guiGraphics, EntityIngredient ingredient) {
            // ignored -- we only use the version that receives x, y
        }

        @Override
        public void render(GuiGraphics graphics, @Nullable EntityIngredient input, int x, int y) {
            // https://github.com/SlimeKnights/Mantle/blob/27c2ea2ed33167631de79d498597880cb3067fa8/src/main/java/slimeknights/mantle/plugin/jei/entity/EntityIngredientRenderer.java#L59

            if (input != null) {
                Level world = Minecraft.getInstance().level;
                EntityType<?> type = input.type();
                if (world != null && !IGNORED_ENTITIES.contains(type)) {
                    Entity entity;
                    // players cannot be created using the type, but we can use the client player
                    // side effect is it renders armor/items
                    if (type == EntityType.PLAYER) {
                        entity = Minecraft.getInstance().player;
                    } else {
                        // entity is created with the client world, but the entity map is thrown away when JEI
                        // restarts
                        // so they should be okay I think
                        entity = ENTITY_MAP.computeIfAbsent(type, t -> t.create(world, EntitySpawnReason.COMMAND));
                    }
                    // only can draw living entities, plus non-living ones don't get recipes anyways
                    if (entity instanceof LivingEntity livingEntity) {
                        // scale down large mobs, but don't scale up small ones
                        int scale = size / 2;
                        float height = entity.getBbHeight();
                        float width = entity.getBbWidth();
                        if (height > 2 || width > 2) {
                            scale = (int) (size / (Math.max(height, width)) - 1);
                        }
                        // catch exceptions drawing the entity to be safe, any caught exceptions blacklist the entity
                        try {
                            double my = Minecraft.getInstance().mouseHandler.getScaledYPos(
                                    Minecraft.getInstance().getWindow());
                            double mx = Minecraft.getInstance().mouseHandler.getScaledXPos(
                                    Minecraft.getInstance().getWindow());

                            // https://github.com/XFactHD/FramedBlocks/blob/d6e578a06013369e5f2f579151564250d0e7cc3b/src/main/java/io/github/xfacthd/framedblocks/common/compat/jade/FramedBlockElement.java#L42
                            ScreenRectangle bounds =
                                    new ScreenRectangle(x, y, size, size).transformMaxBounds(graphics.pose());
                            InventoryScreen.renderEntityInInventoryFollowsMouse(graphics, bounds.left(), bounds.top(),
                                                                                bounds.right(), bounds.bottom(),
                                                                                scale, 0.0625F, (float) (mx),
                                                                                (float) (my), livingEntity);
                            return;
                        } catch (Exception e) {
                            Allomancy.LOGGER.error("Error drawing entity {}",
                                                   BuiltInRegistries.ENTITY_TYPE.getKey(type), e);
                            IGNORED_ENTITIES.add(type);
                            ENTITY_MAP.remove(type);
                        }
                    } else {
                        // not living, so might as well skip next time
                        IGNORED_ENTITIES.add(type);
                        ENTITY_MAP.remove(type);
                    }
                }

                // fallback, draw a pink and black "spawn egg"

                int offset = (size - 16) / 2;
                graphics.blit(RenderPipelines.GUI_TEXTURED_PREMULTIPLIED_ALPHA, MISSING, offset, offset, 0, 0, 16, 16,
                              16, 16);
            }
        }


        @Override
        public List<Component> getTooltip(EntityIngredient type, TooltipFlag flag) {
            List<Component> tooltip = new ArrayList<>();
            tooltip.add(type.type().getDescription());
            if (flag.isAdvanced()) {
                tooltip.add(
                        (Component.literal(BuiltInRegistries.ENTITY_TYPE.getKey(type.type()).toString())).withStyle(
                                ChatFormatting.DARK_GRAY));
            }
            return tooltip;
        }

    }

    public static class Helper implements IIngredientHelper<EntityIngredient> {
        @Override
        public IIngredientType<EntityIngredient> getIngredientType() {
            return EntityIngredient.ENTITY_TYPE;
        }

        @Override
        public String getDisplayName(EntityIngredient type) {
            return type.type().getDescription().getString();
        }

        @Override
        public Object getUid(EntityIngredient ingredient, UidContext context) {
            return getIdentifier(ingredient).toString();
        }

        @Override
        public Identifier getIdentifier(EntityIngredient type) {
            return BuiltInRegistries.ENTITY_TYPE.getKey(type.type());
        }

        @Override
        public EntityIngredient copyIngredient(EntityIngredient type) {
            return type;
        }

        @Override
        public String getErrorInfo(@Nullable EntityIngredient type) {
            if (type == null) {
                return "null";
            }
            return getIdentifier(type).toString();
        }
    }
}
