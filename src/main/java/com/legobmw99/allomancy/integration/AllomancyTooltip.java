package com.legobmw99.allomancy.integration;

import com.legobmw99.allomancy.api.enums.Metal;
import com.legobmw99.allomancy.modules.powers.data.AllomancerCapability;
import mcp.mobius.waila.api.IEntityAccessor;
import mcp.mobius.waila.api.IEntityComponentProvider;
import mcp.mobius.waila.api.IPluginConfig;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TranslatableComponent;

import java.util.List;

public class AllomancyTooltip implements IEntityComponentProvider {

    static final AllomancyTooltip INSTANCE = new AllomancyTooltip();

    private static TranslatableComponent translateMetal(Metal mt) {
        return new TranslatableComponent("metals." + mt.getName().toLowerCase());
    }

    @Override
    public void appendBody(List<Component> tooltip, IEntityAccessor accessor, IPluginConfig config) {
        accessor.getPlayer().getCapability(AllomancerCapability.PLAYER_CAP).ifPresent(cap -> {
            if (cap.isBurning(Metal.BRONZE) && (cap.isEnhanced() || !cap.isBurning(Metal.COPPER))) {
                accessor.getEntity().getCapability(AllomancerCapability.PLAYER_CAP).ifPresent(capOther -> {
                    if (!capOther.isBurning(Metal.COPPER) || (cap.isEnhanced() && !capOther.isEnhanced())) {

                        MutableComponent text = null;

                        for (Metal mt : Metal.values()) {
                            if (capOther.isBurning(mt)) {
                                if (text == null) {
                                    text = translateMetal(mt);
                                } else {
                                    text = text.append(", ").append(translateMetal(mt));
                                }
                            }
                        }

                        if (text != null) {
                            tooltip.add(text);
                        }
                    }
                });
            }
        });
    }
}
