package com.legobmw99.allomancy.modules.powers;

import com.legobmw99.allomancy.modules.powers.client.gui.MetalOverlay;
import com.legobmw99.allomancy.modules.powers.util.Physical;
import com.legobmw99.allomancy.util.AllomancyConfig;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.ModConfigSpec;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public final class PowersConfig {

    public static final Set<String> whitelist = new HashSet<>();
    public static ModConfigSpec.IntValue max_metal_detection;
    public static ModConfigSpec.BooleanValue animate_selection;
    public static ModConfigSpec.BooleanValue enable_overlay;
    public static ModConfigSpec.EnumValue<MetalOverlay.SCREEN_LOC> overlay_position;
    public static ModConfigSpec.BooleanValue random_mistings;
    public static ModConfigSpec.BooleanValue respect_player_UUID;
    private static ModConfigSpec.ConfigValue<List<? extends String>> cfg_whitelist;

    private PowersConfig() {}

    public static void init(ModConfigSpec.Builder server_builder,
                            ModConfigSpec.Builder common_builder,
                            ModConfigSpec.Builder client_builder) {
        common_builder.comment("Settings for the gameplay elements of the mod").push("gameplay");
        random_mistings = common_builder.comment("Spawn players as a random Misting").define("random_mistings", true);
        respect_player_UUID = common_builder
                .comment("Decides whether your spawn metal is based off your UUID (this will cause it to be " +
                         "consistent across worlds)")
                .define("respect_player_UUID", false);
        common_builder.pop();

        server_builder.comment("Settings for the gameplay elements of the mod").push("gameplay");
        cfg_whitelist = server_builder
                .comment("List of registry names of items and blocks that are counted as 'metal'")
                .defineListAllowEmpty("whitelist", Physical::default_whitelist, String::new, o -> {
                    if (o instanceof String s) {
                        return ResourceLocation.tryParse(s) != null;
                    }
                    return false;
                });
        server_builder.pop();

        client_builder.push("graphics");
        max_metal_detection = client_builder
                .comment("Maximum iron/steel sight distance. Can have an impact on performance")
                .defineInRange("max_metal_distance", 15, 3, 30);
        animate_selection = client_builder.comment("Animate the selection wheel").define("animate_selection", true);
        enable_overlay = client_builder.comment("Enable the metal vial HUD").define("overlay_enabled", true);
        overlay_position = client_builder
                .comment("Metal vial HUD position")
                .defineEnum("overlay_position", MetalOverlay.SCREEN_LOC.TOP_LEFT);
        client_builder.pop();

    }

    public static void refresh(ModConfigEvent e) {
        ModConfig cfg = e.getConfig();
        if (cfg.getSpec() == AllomancyConfig.SERVER_CONFIG) {
            whitelist.clear();
            whitelist.addAll(cfg_whitelist.get());
        }
    }
}
