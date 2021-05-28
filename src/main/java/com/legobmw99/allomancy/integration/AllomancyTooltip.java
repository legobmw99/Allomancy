package com.legobmw99.allomancy.integration;

import com.legobmw99.allomancy.modules.powers.data.AllomancyCapability;
import com.legobmw99.allomancy.util.Metal;
import mcp.mobius.waila.api.IEntityAccessor;
import mcp.mobius.waila.api.IEntityComponentProvider;
import mcp.mobius.waila.api.IPluginConfig;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

import java.util.List;

public class AllomancyTooltip implements IEntityComponentProvider {

    static final AllomancyTooltip INSTANCE = new AllomancyTooltip();

    private static TranslationTextComponent translateMetal(Metal mt) {
        return new TranslationTextComponent("metals." + mt.getName().toLowerCase());
    }

    @Override
    public void appendBody(List<ITextComponent> tooltip, IEntityAccessor accessor, IPluginConfig config) {
        accessor.getPlayer().getCapability(AllomancyCapability.PLAYER_CAP).ifPresent(cap -> {
            if (cap.isBurning(Metal.BRONZE) && (cap.isEnhanced() || !cap.isBurning(Metal.COPPER))) {
                accessor.getEntity().getCapability(AllomancyCapability.PLAYER_CAP).ifPresent(capOther -> {
                    if (!capOther.isBurning(Metal.COPPER) || (cap.isEnhanced() && !capOther.isEnhanced())) {

                        IFormattableTextComponent text = null;

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
