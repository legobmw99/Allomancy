package com.legobmw99.allomancy.modules.powers;

import com.legobmw99.allomancy.Allomancy;
import com.legobmw99.allomancy.modules.combat.CombatSetup;
import com.legobmw99.allomancy.modules.consumables.ConsumeSetup;
import com.legobmw99.allomancy.util.AllomancyConfig;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.*;

public class PowersConfig {

    public static final Set<String> whitelist = new HashSet<>();
    public static ForgeConfigSpec.IntValue max_metal_detection;
    public static ForgeConfigSpec.BooleanValue animate_selection;
    public static ForgeConfigSpec.BooleanValue enable_overlay;
    public static ForgeConfigSpec.EnumValue<SCREEN_LOC> overlay_position;
    public static ForgeConfigSpec.BooleanValue random_mistings;
    public static ForgeConfigSpec.BooleanValue generate_whitelist;
    public static ForgeConfigSpec.BooleanValue respect_player_UUID;
    public static ForgeConfigSpec.ConfigValue<List<? extends String>> cfg_whitelist;
    private static HashSet<String> defaultList;

    public static void init(ForgeConfigSpec.Builder server_builder, ForgeConfigSpec.Builder common_builder, ForgeConfigSpec.Builder client_builder) {
        common_builder.comment("Settings for the gameplay elements of the mod").push("gameplay");
        random_mistings = common_builder.comment("Spawn players as a random Misting").define("random_mistings", true);
        respect_player_UUID = common_builder
                .comment("Decides whether your spawn metal is based off your UUID (this will cause it to be consistent across worlds)")
                .define("respect_player_UUID", false);
        common_builder.pop();

        server_builder.comment("Settings for the gameplay elements of the mod").push("gameplay");
        generate_whitelist = server_builder.comment("Regenerate the metal whitelist").define("regenerate_whitelist", true);
        cfg_whitelist = server_builder
                .comment("List of registry names of items and blocks that are counted as 'metal'")
                .defineList("whitelist", new ArrayList<>(), o -> o instanceof String);
        server_builder.pop();

        client_builder.push("graphics");
        max_metal_detection = client_builder.comment("Maximum iron/steelsight distance. Can have a HUGE impact on performance").defineInRange("max_metal_distance", 15, 3, 30);
        animate_selection = client_builder.comment("Animate the selection wheel").define("animate_selection", true);
        enable_overlay = client_builder.comment("Enable the screen overlay").define("overlay_enabled", true);
        overlay_position = client_builder.comment("Screen Overlay Position").defineEnum("overlay_position", SCREEN_LOC.TOP_LEFT);
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
                ArrayList<String> list = new ArrayList<>(default_whitelist());
                list.sort(String::compareTo);
                cfg_whitelist.set(list);
                generate_whitelist.set(false);
            }
            refresh_whitelist();
        }
    }

    private static Set<String> default_whitelist() {
        defaultList = new HashSet<>();

        add(Items.CHAINMAIL_BOOTS);
        add(Items.BUCKET);
        add(Items.LAVA_BUCKET);
        add(Items.MILK_BUCKET);
        add(Items.COD_BUCKET);
        add(Items.PUFFERFISH_BUCKET);
        add(Items.SALMON_BUCKET);
        add(Items.TROPICAL_FISH_BUCKET);
        add(Items.WATER_BUCKET);
        add(Items.CAULDRON);
        add(Items.COMPASS);
        add(Items.CHAINMAIL_HELMET);
        add(Items.CHAINMAIL_LEGGINGS);
        add(Items.MINECART);
        add(Items.CHEST_MINECART);
        add(Items.HOPPER_MINECART);
        add(Items.FURNACE_MINECART);
        add(Items.TNT_MINECART);
        add(Items.CHAINMAIL_CHESTPLATE);
        add(Items.CLOCK);
        add(Items.SHEARS);
        add(Items.SHIELD);
        add(Items.NETHERITE_INGOT);
        add(Items.NETHERITE_HELMET);
        add(Items.NETHERITE_CHESTPLATE);
        add(Items.NETHERITE_LEGGINGS);
        add(Items.NETHERITE_BOOTS);
        add(Items.NETHERITE_HOE);
        add(Items.NETHERITE_PICKAXE);
        add(Items.NETHERITE_SHOVEL);
        add(Items.NETHERITE_SWORD);
        add(Items.NETHERITE_AXE);
        add(Items.CROSSBOW);

        add(Blocks.ANVIL);
        add(Blocks.CHIPPED_ANVIL);
        add(Blocks.DAMAGED_ANVIL);
        add(Blocks.CAULDRON);
        add(Blocks.SMITHING_TABLE);
        add(Blocks.STONECUTTER);
        add(Blocks.CHAIN);
        add(Blocks.HOPPER);
        add(Blocks.PISTON_HEAD);
        add(Blocks.MOVING_PISTON);
        add(Blocks.STICKY_PISTON);
        add(Blocks.BLAST_FURNACE);
        add(Blocks.BELL);
        add(Blocks.PISTON);
        add(Blocks.LIGHT_WEIGHTED_PRESSURE_PLATE);
        add(Blocks.HEAVY_WEIGHTED_PRESSURE_PLATE);
        add(Blocks.RAIL);
        add(Blocks.ACTIVATOR_RAIL);
        add(Blocks.DETECTOR_RAIL);
        add(Blocks.POWERED_RAIL);
        add(Blocks.LANTERN);
        add(Blocks.TRAPPED_CHEST);
        add(Blocks.TRIPWIRE_HOOK);
        add(Blocks.SOUL_LANTERN);
        add(Blocks.NETHERITE_BLOCK);
        add(Blocks.LODESTONE);
        add(Blocks.GILDED_BLACKSTONE);

        add(ConsumeSetup.VIAL.get());
        add(ConsumeSetup.LERASIUM_NUGGET.get());
        add(ConsumeSetup.ALLOMANTIC_GRINDER.get());
        add(CombatSetup.COIN_BAG.get());


        ForgeRegistries.ITEMS.getKeys().stream().filter(Objects::nonNull).filter(PowerUtils::doesResourceContainsMetal).forEach(PowersConfig::add);

        ForgeRegistries.BLOCKS.getKeys().stream().filter(Objects::nonNull).filter(PowerUtils::doesResourceContainsMetal).forEach(PowersConfig::add);

        return defaultList;

    }


    private static void add(String s) {
        Allomancy.LOGGER.info("Adding " + s + " to the default whitelist!");
        defaultList.add(s);
    }

    private static void add(ResourceLocation r) {
        add(r.toString());
    }

    private static void add(Block block) {
        add(ForgeRegistries.BLOCKS.getKey(block));
    }

    private static void add(Item item) {
        add(ForgeRegistries.ITEMS.getKey(item));
    }

    public enum SCREEN_LOC {
        TOP_RIGHT,
        BOTTOM_RIGHT,
        TOP_LEFT,
        BOTTOM_LEFT
    }

}
