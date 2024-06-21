package com.legobmw99.allomancy.modules.powers;

import com.legobmw99.allomancy.modules.powers.client.gui.MetalOverlay;
import com.legobmw99.allomancy.modules.powers.util.Physical;
import com.legobmw99.allomancy.util.AllomancyConfig;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.ModConfigSpec;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class PowersConfig {

    public static final Set<String> whitelist = new HashSet<>();
    public static ModConfigSpec.IntValue max_metal_detection;
    public static ModConfigSpec.BooleanValue animate_selection;
    public static ModConfigSpec.BooleanValue enable_overlay;
    public static ModConfigSpec.EnumValue<MetalOverlay.SCREEN_LOC> overlay_position;
    public static ModConfigSpec.BooleanValue random_mistings;
    public static ModConfigSpec.BooleanValue generate_whitelist;
    public static ModConfigSpec.BooleanValue respect_player_UUID;
    public static ModConfigSpec.ConfigValue<List<? extends String>> cfg_whitelist;

    public static void init(ModConfigSpec.Builder server_builder,
                            ModConfigSpec.Builder common_builder,
                            ModConfigSpec.Builder client_builder) {
        common_builder.comment("Settings for the gameplay elements of the mod").push("gameplay");
        random_mistings = common_builder.comment("Spawn players as a random Misting").define("random_mistings", true);
        respect_player_UUID = common_builder
                .comment(
                        "Decides whether your spawn metal is based off your UUID (this will cause it to be " +
                        "consistent across worlds)")
                .define("respect_player_UUID", false);
        common_builder.pop();

        server_builder.comment("Settings for the gameplay elements of the mod").push("gameplay");
        generate_whitelist =
                server_builder.comment("Regenerate the metal whitelist").define("regenerate_whitelist", true);
        cfg_whitelist = server_builder
                .comment("List of registry names of items and blocks that are counted as 'metal'")
                .defineList("whitelist", new ArrayList<>(), o -> o instanceof String);
        server_builder.pop();

        client_builder.push("graphics");
        max_metal_detection = client_builder
                .comment("Maximum iron/steelsight distance. Can have a HUGE impact on performance")
                .defineInRange("max_metal_distance", 15, 3, 30);
        animate_selection = client_builder.comment("Animate the selection wheel").define("animate_selection", true);
        enable_overlay = client_builder.comment("Enable the screen overlay").define("overlay_enabled", true);
        overlay_position = client_builder
                .comment("Screen Overlay Position")
                .defineEnum("overlay_position", MetalOverlay.SCREEN_LOC.TOP_LEFT);
        client_builder.pop();

    }

    public static void refresh(final ModConfigEvent e) {
        ModConfig cfg = e.getConfig();
        if (cfg.getSpec() == AllomancyConfig.SERVER_CONFIG) {
            refresh_whitelist();
        }
    }

    private static void refresh_whitelist() {
        whitelist.clear();
        whitelist.addAll(cfg_whitelist.get());
    }

    public static void load_whitelist(final ModConfigEvent.Loading e) {
        ModConfig cfg = e.getConfig();
        if (cfg.getSpec() == AllomancyConfig.SERVER_CONFIG) {
            if (generate_whitelist.get()) {
                cfg_whitelist.set(Physical.default_whitelist());
                generate_whitelist.set(false);
            }
            refresh_whitelist();
        }
    }


}
