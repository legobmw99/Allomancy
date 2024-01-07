package com.legobmw99.allomancy.util;

import com.legobmw99.allomancy.modules.materials.MaterialsConfig;
import com.legobmw99.allomancy.modules.powers.PowersConfig;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.ModConfigSpec;


public class AllomancyConfig {

    public static final ModConfigSpec COMMON_CONFIG;
    public static final ModConfigSpec CLIENT_CONFIG;
    public static final ModConfigSpec SERVER_CONFIG;


    static {
        var COMMON_BUILDER = new ModConfigSpec.Builder();
        var CLIENT_BUILDER = new ModConfigSpec.Builder();
        var SERVER_BUILDER = new ModConfigSpec.Builder();

        MaterialsConfig.init(COMMON_BUILDER);
        PowersConfig.init(SERVER_BUILDER, COMMON_BUILDER, CLIENT_BUILDER);

        COMMON_CONFIG = COMMON_BUILDER.build();
        CLIENT_CONFIG = CLIENT_BUILDER.build();
        SERVER_CONFIG = SERVER_BUILDER.build();

    }

    public static void onReload(final ModConfigEvent.Reloading e) {
        PowersConfig.refresh(e);
    }


    public static void onLoad(final ModConfigEvent.Loading e) {
        PowersConfig.load_whitelist(e);
    }

    public static void register() {
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, AllomancyConfig.COMMON_CONFIG);
        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, AllomancyConfig.CLIENT_CONFIG);
        ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, AllomancyConfig.SERVER_CONFIG);
    }
}
