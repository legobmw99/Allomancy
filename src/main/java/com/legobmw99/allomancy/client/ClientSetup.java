package com.legobmw99.allomancy.client;

import com.legobmw99.allomancy.entity.GoldNuggetEntity;
import com.legobmw99.allomancy.entity.IronNuggetEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.SpriteRenderer;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import org.lwjgl.glfw.GLFW;

public class ClientSetup {
    @OnlyIn(Dist.CLIENT)
    public static KeyBinding burn;

    public static void initKeyBindings() {
        burn = new KeyBinding("key.burn", GLFW.GLFW_KEY_F, "key.categories.allomancy");
        ClientRegistry.registerKeyBinding(burn);
    }

    @OnlyIn(Dist.CLIENT)
    public static void registerEntityRenders() {
        //Use renderSnowball for nugget projectiles
        RenderingRegistry.registerEntityRenderingHandler(GoldNuggetEntity.class, manager -> new SpriteRenderer<GoldNuggetEntity>(manager, Minecraft.getInstance().getItemRenderer()));
        RenderingRegistry.registerEntityRenderingHandler(IronNuggetEntity.class, manager -> new SpriteRenderer<IronNuggetEntity>(manager, Minecraft.getInstance().getItemRenderer()));
    }
}
