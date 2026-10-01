package com.legobmw99.allomancy.util;

import com.legobmw99.allomancy.modules.powers.PowersConfig;
import com.legobmw99.allomancy.modules.powers.util.Physical;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.ModConfigSpec;


public final class AllomancyConfig {

    private static final ModConfigSpec LOCAL_CONFIG;
    private static final ModConfigSpec CLIENT_CONFIG;
    public static final ModConfigSpec SYNCED_CONFIG;


    static {
        var LOCAL_BUILDER = new ModConfigSpec.Builder();
        var CLIENT_BUILDER = new ModConfigSpec.Builder();
        var SYNCED_BUILDER = new ModConfigSpec.Builder();

        PowersConfig.init(SYNCED_BUILDER, LOCAL_BUILDER, CLIENT_BUILDER);

        LOCAL_CONFIG = LOCAL_BUILDER.build();
        CLIENT_CONFIG = CLIENT_BUILDER.build();
        SYNCED_CONFIG = SYNCED_BUILDER.build();

    }

    private AllomancyConfig() {}

    private static void onReload(final ModConfigEvent.Reloading e) {
        PowersConfig.refresh(e);
    }

    private static void onLoad(final ModConfigEvent.Loading e) {
        PowersConfig.refresh(e);
    }

    public static void register(ModContainer container, IEventBus bus) {
        container.registerConfig(ModConfig.Type.LOCAL, LOCAL_CONFIG);
        container.registerConfig(ModConfig.Type.CLIENT, CLIENT_CONFIG);
        container.registerConfig(ModConfig.Type.SYNCED, SYNCED_CONFIG);

        bus.addListener(AllomancyConfig::onLoad);
        bus.addListener(AllomancyConfig::onReload);
        bus.addListener(Physical::repopulateWhitelist);
    }
}
