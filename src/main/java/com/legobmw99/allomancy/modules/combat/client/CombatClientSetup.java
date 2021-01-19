package com.legobmw99.allomancy.modules.combat.client;

import com.legobmw99.allomancy.modules.combat.CombatSetup;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.SpriteRenderer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.client.registry.RenderingRegistry;

public class CombatClientSetup {
    @OnlyIn(Dist.CLIENT)
    public static void registerEntityRenders() {
        //Use renderSnowball for nugget projectiles
        RenderingRegistry.registerEntityRenderingHandler(CombatSetup.NUGGET_PROJECTILE.get(),
                                                         manager -> new SpriteRenderer<>(manager, Minecraft.getInstance().getItemRenderer()));
    }
}
