package com.legobmw99.allomancy.integration;

import mcp.mobius.waila.api.IRegistrar;
import mcp.mobius.waila.api.IWailaPlugin;
import mcp.mobius.waila.api.TooltipPosition;
import mcp.mobius.waila.api.WailaPlugin;
import net.minecraft.entity.player.PlayerEntity;

@WailaPlugin
public class AllomancyHwylaPlugin implements IWailaPlugin {
    @Override
    public void register(IRegistrar iRegistrar) {
        iRegistrar.registerComponentProvider(AllomancyTooltip.INSTANCE, TooltipPosition.BODY, PlayerEntity.class);
    }
}
