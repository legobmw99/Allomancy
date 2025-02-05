package com.legobmw99.allomancy.modules.combat.client;

import com.legobmw99.allomancy.modules.combat.CombatSetup;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;

public final class CombatClientSetup {

    public static void register(IEventBus bus) {
        bus.addListener(CombatClientSetup::registerEntityRenders);
    }

    private static void registerEntityRenders(final EntityRenderersEvent.RegisterRenderers e) {
        e.registerEntityRenderer(CombatSetup.NUGGET_PROJECTILE.get(), ThrownItemRenderer::new);
    }

    private CombatClientSetup() {}
}
