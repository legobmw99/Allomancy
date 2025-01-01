package com.legobmw99.allomancy;

import com.legobmw99.allomancy.modules.combat.CombatSetup;
import com.legobmw99.allomancy.modules.combat.client.CombatClientSetup;
import com.legobmw99.allomancy.modules.consumables.ConsumeSetup;
import com.legobmw99.allomancy.modules.extras.ExtrasSetup;
import com.legobmw99.allomancy.modules.materials.MaterialsSetup;
import com.legobmw99.allomancy.modules.powers.PowersSetup;
import com.legobmw99.allomancy.modules.powers.client.PowersClientSetup;
import com.legobmw99.allomancy.modules.powers.client.gui.MetalOverlay;
import com.legobmw99.allomancy.modules.powers.client.util.Inputs;
import com.legobmw99.allomancy.modules.powers.data.AllomancerAttachment;
import com.legobmw99.allomancy.modules.powers.network.Network;
import com.legobmw99.allomancy.util.AllomancyConfig;
import com.legobmw99.allomancy.util.ItemDisplay;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.neoforged.neoforge.common.NeoForge;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


@Mod(Allomancy.MODID)
public class Allomancy {

    public static final String MODID = "allomancy";

    public static final Logger LOGGER = LogManager.getLogger();

    public static ResourceLocation rl(String path) {
        return ResourceLocation.fromNamespaceAndPath(MODID, path);
    }

    public Allomancy(IEventBus bus, ModContainer container, Dist dist) {

        if (dist.isClient()) {
            PowersClientSetup.register(bus);
            bus.addListener(PowersClientSetup::clientInit);
            bus.addListener(PowersClientSetup::registerParticle);

            bus.addListener(MetalOverlay::registerGUI);
            bus.addListener(Inputs::registerKeyBinding);
            container.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);
        }

        AllomancerAttachment.register(bus);
        bus.addListener(PowersSetup::init);
        bus.addListener(Network::registerPayloads);

        ExtrasSetup.register(bus);
        bus.addListener(ExtrasSetup::registerCapabilities);
        NeoForge.EVENT_BUS.addListener(ExtrasSetup::registerCommands);

        CombatSetup.register(bus);
        bus.addListener(CombatClientSetup::registerEntityRenders);

        ConsumeSetup.register(bus);
        bus.addListener(ConsumeSetup::onModifyComponents);

        MaterialsSetup.register(bus);

        ItemDisplay.register(bus);
        bus.addListener(ItemDisplay::addTabContents);

        AllomancyConfig.register(container);
        bus.addListener(AllomancyConfig::onLoad);
        bus.addListener(AllomancyConfig::onReload);


    }
}
