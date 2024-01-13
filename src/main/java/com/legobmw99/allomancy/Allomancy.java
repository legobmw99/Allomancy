package com.legobmw99.allomancy;

import com.legobmw99.allomancy.modules.combat.CombatSetup;
import com.legobmw99.allomancy.modules.combat.client.CombatClientSetup;
import com.legobmw99.allomancy.modules.consumables.ConsumeSetup;
import com.legobmw99.allomancy.modules.extras.ExtrasSetup;
import com.legobmw99.allomancy.modules.materials.MaterialsSetup;
import com.legobmw99.allomancy.modules.powers.PowersSetup;
import com.legobmw99.allomancy.modules.powers.client.PowersClientSetup;
import com.legobmw99.allomancy.modules.powers.client.gui.MetalOverlay;
import com.legobmw99.allomancy.modules.powers.data.AllomancerAttachment;
import com.legobmw99.allomancy.network.Network;
import com.legobmw99.allomancy.util.AllomancyConfig;
import com.legobmw99.allomancy.util.ItemDisplay;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


@Mod(Allomancy.MODID)
public class Allomancy {

    public static final String MODID = "allomancy";

    public static final Logger LOGGER = LogManager.getLogger();


    public Allomancy(IEventBus bus, Dist dist) {

        // Register our setup events on the necessary buses
        bus.addListener(Allomancy::init);
        bus.addListener(Allomancy::clientInit);
        bus.addListener(AllomancyConfig::onLoad);
        bus.addListener(AllomancyConfig::onReload);
        bus.addListener(CombatClientSetup::registerEntityRenders);
        bus.addListener(ItemDisplay::addTabContents);
        bus.addListener(Network::registerPayloads);

        bus.addListener(MetalOverlay::registerGUI);

        if (dist.isClient()) {
            bus.addListener(PowersClientSetup::registerKeyBinding);
            bus.addListener(PowersClientSetup::registerParticle);
            PowersClientSetup.register(bus);
        }

        NeoForge.EVENT_BUS.addListener(PowersSetup::registerCommands);

        // Register all Registries
        AllomancerAttachment.register(bus);
        PowersSetup.register(bus);
        CombatSetup.register(bus);
        ConsumeSetup.register(bus);
        MaterialsSetup.register(bus);
        ExtrasSetup.register(bus);
        ItemDisplay.register(bus);

        AllomancyConfig.register();

    }

    public static void clientInit(final FMLClientSetupEvent e) {
        PowersSetup.clientInit(e);
    }

    public static void init(final FMLCommonSetupEvent e) {
        PowersSetup.init(e);
        MaterialsSetup.init(e);
    }

}
