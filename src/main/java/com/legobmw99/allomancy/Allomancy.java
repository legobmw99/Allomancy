package com.legobmw99.allomancy;

import com.legobmw99.allomancy.modules.combat.CombatSetup;
import com.legobmw99.allomancy.modules.combat.client.CombatClientSetup;
import com.legobmw99.allomancy.modules.consumables.ConsumeSetup;
import com.legobmw99.allomancy.modules.extras.ExtrasSetup;
import com.legobmw99.allomancy.modules.materials.MaterialsSetup;
import com.legobmw99.allomancy.modules.powers.PowersSetup;
import com.legobmw99.allomancy.modules.powers.client.PowersClientSetup;
import com.legobmw99.allomancy.modules.powers.client.gui.MetalOverlay;
import com.legobmw99.allomancy.modules.powers.data.AllomancerCapability;
import com.legobmw99.allomancy.network.Network;
import com.legobmw99.allomancy.util.AllomancyConfig;
import com.legobmw99.allomancy.util.ItemDisplay;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(Allomancy.MODID)
public class Allomancy {

    public static final String MODID = "allomancy";

    public static final Logger LOGGER = LogManager.getLogger();

    public static Allomancy instance;

    public Allomancy() {
        instance = this;
        // Register our setup events on the necessary buses
        var modBus = FMLJavaModLoadingContext.get().getModEventBus();
        modBus.addListener(Allomancy::init);
        modBus.addListener(Allomancy::clientInit);
        modBus.addListener(AllomancyConfig::onLoad);
        modBus.addListener(AllomancyConfig::onReload);
        modBus.addListener(AllomancerCapability::registerCapability);
        modBus.addListener(CombatClientSetup::registerEntityRenders);
        modBus.addListener(ItemDisplay::registerCreativeTabs);
        modBus.addListener(ItemDisplay::addTabContents);

        modBus.addListener(MetalOverlay::registerGUI);

        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
            modBus.addListener(PowersClientSetup::registerKeyBinding);
            modBus.addListener(PowersClientSetup::registerParticle);
        });


        MinecraftForge.EVENT_BUS.addListener(PowersSetup::registerCommands);


        // Register all Registries
        PowersSetup.register();
        CombatSetup.register();
        ConsumeSetup.register();
        MaterialsSetup.register();
        ExtrasSetup.register();

        AllomancyConfig.register();

    }

    public static void clientInit(final FMLClientSetupEvent e) {
        PowersSetup.clientInit(e);
    }

    public static void init(final FMLCommonSetupEvent e) {
        PowersSetup.init(e);
        MaterialsSetup.init(e);
        e.enqueueWork(Network::registerPackets);

    }
}
