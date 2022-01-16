package com.legobmw99.allomancy.util;

import com.legobmw99.allomancy.modules.materials.MaterialsConfig;
import com.legobmw99.allomancy.modules.powers.PowersConfig;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.config.ModConfigEvent;


public class AllomancyConfig {

    public static final ForgeConfigSpec COMMON_CONFIG;
    public static final ForgeConfigSpec CLIENT_CONFIG;
    public static final ForgeConfigSpec SERVER_CONFIG;


    static {
        var COMMON_BUILDER = new ForgeConfigSpec.Builder();
        var CLIENT_BUILDER = new ForgeConfigSpec.Builder();
        var SERVER_BUILDER = new ForgeConfigSpec.Builder();

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
