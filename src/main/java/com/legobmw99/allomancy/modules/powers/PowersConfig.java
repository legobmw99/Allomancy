package com.legobmw99.allomancy.modules.powers;

import com.legobmw99.allomancy.modules.powers.client.gui.MetalOverlay;
import com.legobmw99.allomancy.modules.powers.util.Physical;
import com.legobmw99.allomancy.util.AllomancyConfig;
import net.minecraft.resources.Identifier;
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

    public static void init(ModConfigSpec.Builder synced, ModConfigSpec.Builder local, ModConfigSpec.Builder client) {
        local.comment("Settings for the gameplay elements of the mod").push("gameplay");
        random_mistings = local.comment("Spawn players as a random Misting").define("random_mistings", true);
        respect_player_UUID = local
                .comment("Decides whether your spawn metal is based off your UUID (this will cause it to be " +
                         "consistent across worlds)")
                .define("respect_player_UUID", false);
        local.pop();

        synced.comment("Settings for the gameplay elements of the mod").push("gameplay");
        cfg_whitelist = synced
                .comment("List of registry names of items and blocks that are counted as 'metal'")
                .defineListAllowEmpty("whitelist", Physical::default_whitelist, String::new, o -> {
                    if (o instanceof String s) {
                        return Identifier.tryParse(s) != null;
                    }
                    return false;
                });
        synced.pop();

        client.push("graphics");
        max_metal_detection = client
                .comment("Maximum iron/steel sight distance. Can have an impact on performance")
                .defineInRange("max_metal_distance", 15, 3, 30);
        animate_selection = client.comment("Animate the selection wheel").define("animate_selection", true);
        enable_overlay = client.comment("Enable the metal vial HUD").define("overlay_enabled", true);
        overlay_position = client
                .comment("Metal vial HUD position")
                .defineEnum("overlay_position", MetalOverlay.SCREEN_LOC.TOP_LEFT);
        client.pop();

    }

    public static void refresh(ModConfigEvent e) {
        ModConfig cfg = e.getConfig();
        if (cfg.getSpec() == AllomancyConfig.SYNCED_CONFIG) {
            whitelist.clear();
            whitelist.addAll(cfg_whitelist.get());
        }
    }
}
