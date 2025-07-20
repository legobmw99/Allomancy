package com.legobmw99.allomancy.integration.waila;

import com.legobmw99.allomancy.Allomancy;
import com.legobmw99.allomancy.api.enums.Metal;
import com.legobmw99.allomancy.modules.powers.data.AllomancerAttachment;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import snownee.jade.api.EntityAccessor;
import snownee.jade.api.IEntityComponentProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.config.IPluginConfig;


public class AllomancyTooltip implements IEntityComponentProvider {

    static final AllomancyTooltip INSTANCE = new AllomancyTooltip();

    private static MutableComponent translateMetal(Metal mt) {
        return Component.translatable("metals." + mt.getName().toLowerCase());
    }


    @Override
    public void appendTooltip(ITooltip tooltip, EntityAccessor accessor, IPluginConfig iPluginConfig) {
        var data = AllomancerAttachment.get(accessor.getPlayer());

        if (data.isBurning(Metal.BRONZE) && (data.isEnhanced() || !data.isBurning(Metal.COPPER))) {

            if (accessor.getEntity() instanceof Player player) {
                var dataOther = AllomancerAttachment.get(player);

                if (!dataOther.isBurning(Metal.COPPER) || (data.isEnhanced() && !dataOther.isEnhanced())) {

                    MutableComponent text = null;

                    for (Metal mt : Metal.values()) {
                        if (dataOther.isBurning(mt)) {
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
            }
        }
    }

    @Override
    public ResourceLocation getUid() {
        return Allomancy.rl("waila_bronze");
    }
}
