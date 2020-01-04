package com.legobmw99.allomancy.modules.powers.client;

import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import org.lwjgl.glfw.GLFW;

public class PowerClientSetup {
    @OnlyIn(Dist.CLIENT)
    public static KeyBinding burn;

    public static void initKeyBindings() {
        burn = new KeyBinding("key.burn", GLFW.GLFW_KEY_F, "key.categories.allomancy");
        ClientRegistry.registerKeyBinding(burn);
    }

}
