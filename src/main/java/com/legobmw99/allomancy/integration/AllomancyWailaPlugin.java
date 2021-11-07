package com.legobmw99.allomancy.integration;

import mcp.mobius.waila.api.IRegistrar;
import mcp.mobius.waila.api.IWailaPlugin;
import mcp.mobius.waila.api.TooltipPosition;
import net.minecraft.world.entity.player.Player;

public class AllomancyWailaPlugin implements IWailaPlugin {
    @Override
    public void register(IRegistrar iRegistrar) {
        iRegistrar.addComponent(AllomancyTooltip.INSTANCE, TooltipPosition.BODY, Player.class);
    }
}
