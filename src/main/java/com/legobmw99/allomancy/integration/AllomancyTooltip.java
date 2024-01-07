package com.legobmw99.allomancy.integration;

import com.legobmw99.allomancy.Allomancy;
import com.legobmw99.allomancy.api.enums.Metal;
import com.legobmw99.allomancy.modules.powers.data.AllomancerAttachment;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
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
        var data = accessor.getPlayer().getData(AllomancerAttachment.ALLOMANCY_DATA);

        if (data.isBurning(Metal.BRONZE) && (data.isEnhanced() || !data.isBurning(Metal.COPPER))) {

            if (accessor.getEntity().hasData(AllomancerAttachment.ALLOMANCY_DATA)) {
                var dataOther = accessor.getEntity().getData(AllomancerAttachment.ALLOMANCY_DATA);

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
        return new ResourceLocation(Allomancy.MODID, "waila_bronze");
    }
}
