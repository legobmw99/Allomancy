package com.legobmw99.allomancy.entities;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.RenderSnowball;
import net.minecraft.item.Items;
import net.minecraftforge.fml.client.registry.IRenderFactory;

public class EntityRenderFactories {
	public static final IronFactory IRON_FACTORY = new IronFactory();
    
    public static class IronFactory implements IRenderFactory<EntityIronNugget> {

        @Override
        public EntityRenderer<? super EntityIronNugget> createRenderFor(EntityRendererManager manager) {
            return new RenderSnowball(manager, Items.IRON_NUGGET,  Minecraft.getMinecraft().getRenderItem());
        }

    }
    
	public static final GoldFactory GOLD_FACTORY = new GoldFactory();
    
    public static class GoldFactory implements IRenderFactory<EntityGoldNugget> {

        @Override
        public EntityRenderer<? super EntityGoldNugget> createRenderFor(EntityRendererManager manager) {
            return new RenderSnowball(manager, Items.GOLD_NUGGET,  Minecraft.getMinecraft().getRenderItem());
        }

    }
}
