package com.legobmw99.allomancy.modules.combat.client;

import com.legobmw99.allomancy.modules.combat.CombatSetup;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;
import net.minecraftforge.client.event.EntityRenderersEvent;

public class CombatClientSetup {
    public static void registerEntityRenders(final EntityRenderersEvent.RegisterRenderers e) {
        e.registerEntityRenderer(CombatSetup.NUGGET_PROJECTILE.get(), ThrownItemRenderer::new);
    }
}
