package com.legobmw99.allomancy;

import com.legobmw99.allomancy.modules.combat.CombatSetup;
import com.legobmw99.allomancy.modules.combat.client.CombatClientSetup;
import com.legobmw99.allomancy.modules.consumables.ConsumeSetup;
import com.legobmw99.allomancy.modules.extras.ExtrasSetup;
import com.legobmw99.allomancy.modules.powers.client.PowersClientSetup;
import com.legobmw99.allomancy.modules.powers.data.AllomancerAttachment;
import com.legobmw99.allomancy.modules.powers.network.Network;
import com.legobmw99.allomancy.modules.world.WorldSetup;
import com.legobmw99.allomancy.modules.world.client.WorldClientSetup;
import com.legobmw99.allomancy.util.AllomancyConfig;
import com.legobmw99.allomancy.util.ItemDisplay;
import com.mojang.logging.LogUtils;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import org.slf4j.Logger;


@Mod(Allomancy.MODID)
public class Allomancy {

    public static final String MODID = "allomancy";

    public static final Logger LOGGER = LogUtils.getLogger();

    public static ResourceLocation rl(String path) {
        return ResourceLocation.fromNamespaceAndPath(MODID, path);
    }

    public Allomancy(IEventBus bus, ModContainer container, Dist dist) {

        if (dist.isClient()) {
            PowersClientSetup.register(bus);
            WorldClientSetup.register(bus);
            CombatClientSetup.register(bus);
            container.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);
        }

        AllomancerAttachment.register(bus);
        Network.register(bus);
        ExtrasSetup.register(bus);
        CombatSetup.register(bus);
        ConsumeSetup.register(bus);
        WorldSetup.register(bus);
        ItemDisplay.register(bus);
        AllomancyConfig.register(container, bus);

    }
}
