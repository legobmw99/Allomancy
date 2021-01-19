package com.legobmw99.allomancy.setup;

import com.legobmw99.allomancy.modules.materials.MaterialsConfig;
import com.legobmw99.allomancy.modules.powers.PowersConfig;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.config.ModConfig;


public class AllomancyConfig {

    public static final ForgeConfigSpec COMMON_CONFIG;
    public static final ForgeConfigSpec CLIENT_CONFIG;

    static {
        ForgeConfigSpec.Builder COMMON_BUILDER = new ForgeConfigSpec.Builder();
        ForgeConfigSpec.Builder CLIENT_BUILDER = new ForgeConfigSpec.Builder();
        MaterialsConfig.init(COMMON_BUILDER, CLIENT_BUILDER);
        PowersConfig.init(COMMON_BUILDER, CLIENT_BUILDER);

        COMMON_CONFIG = COMMON_BUILDER.build();
        CLIENT_CONFIG = CLIENT_BUILDER.build();
    }

    public static void onReload(final ModConfig.Reloading e) {
        PowersConfig.refresh(e);
    }


    public static void onLoad(final ModConfig.Loading e) {
        PowersConfig.load_whitelist(e);
    }

}
