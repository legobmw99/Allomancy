package com.legobmw99.allomancy.entities;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderSnowball;
import net.minecraft.init.Items;
import net.minecraftforge.fml.client.registry.IRenderFactory;

public class EntityRenderFactories {
	public static final IronFactory IRON_FACTORY = new IronFactory();
    
    public static class IronFactory implements IRenderFactory<EntityIronNugget> {

        @Override
        public Render<? super EntityIronNugget> createRenderFor(RenderManager manager) {
            return new RenderSnowball(manager, Items.IRON_NUGGET,  Minecraft.getMinecraft().getRenderItem());
        }

    }
    
	public static final GoldFactory GOLD_FACTORY = new GoldFactory();
    
    public static class GoldFactory implements IRenderFactory<EntityGoldNugget> {

        @Override
        public Render<? super EntityGoldNugget> createRenderFor(RenderManager manager) {
            return new RenderSnowball(manager, Items.GOLD_NUGGET,  Minecraft.getMinecraft().getRenderItem());
        }

    }
}
