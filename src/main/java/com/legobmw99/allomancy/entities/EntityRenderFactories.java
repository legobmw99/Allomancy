package com.legobmw99.allomancy.entities;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.SpriteRenderer;
import net.minecraftforge.fml.client.registry.IRenderFactory;

public class EntityRenderFactories {
    public static final IronFactory IRON_FACTORY = new IronFactory();

    public static class IronFactory implements IRenderFactory<IronNuggetEntity> {

        @Override
        public EntityRenderer<? super IronNuggetEntity> createRenderFor(EntityRendererManager manager) {
            return new SpriteRenderer<>(manager, Minecraft.getInstance().getItemRenderer());
        }

    }

    public static final GoldFactory GOLD_FACTORY = new GoldFactory();

    public static class GoldFactory implements IRenderFactory<GoldNuggetEntity> {

        @Override
        public EntityRenderer<? super GoldNuggetEntity> createRenderFor(EntityRendererManager manager) {
            return new SpriteRenderer<>(manager, Minecraft.getInstance().getItemRenderer());
        }

    }
}
