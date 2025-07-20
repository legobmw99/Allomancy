package com.legobmw99.allomancy.datagen;

import com.legobmw99.allomancy.Allomancy;
import com.legobmw99.allomancy.api.enums.Metal;
import com.legobmw99.allomancy.modules.combat.CombatSetup;
import com.legobmw99.allomancy.modules.consumables.ConsumeSetup;
import com.legobmw99.allomancy.modules.extras.ExtrasSetup;
import com.legobmw99.allomancy.modules.world.WorldSetup;
import net.minecraft.data.PackOutput;
import net.minecraft.world.item.DyeColor;
import net.minecraftforge.common.data.LanguageProvider;

import java.util.Arrays;
import java.util.Locale;
import java.util.stream.Collectors;

public class Languages extends LanguageProvider {


    public Languages(PackOutput gen) {
        super(gen, Allomancy.MODID, "en_us");
    }

    private static String getDisplayName(Metal mt) {
        return toTitleCase(mt.getName());
    }

    private static String toTitleCase(String in) {
        return in.substring(0, 1).toUpperCase(Locale.US) + in.substring(1);
    }

    private static String getDisplayName(DyeColor color) {
        String[] trans = color.getName().split("_");
        return Arrays.stream(trans).map(Languages::toTitleCase).collect(Collectors.joining(" "));
    }

    @Override
    protected void addTranslations() {
        add("tabs.allomancy.main_tab", "Allomancy");


        for (int i = 0; i < WorldSetup.ORE_METALS.length; i++) {
            String metal = WorldSetup.ORE_METALS[i].name();
            var ore = WorldSetup.ORE_BLOCKS.get(i).get();
            var ds = WorldSetup.DEEPSLATE_ORE_BLOCKS.get(i).get();
            var rawb = WorldSetup.RAW_ORE_BLOCKS.get(i).get();
            var raw = WorldSetup.RAW_ORE_ITEMS.get(i).get();

            add(ore, toTitleCase(metal) + " Ore");

            add(ds, "Deepslate " + toTitleCase(metal) + " Ore");
            add(rawb, "Block of Raw " + toTitleCase(metal));
            add(raw, "Raw " + toTitleCase(metal));

        }

        add(ExtrasSetup.IRON_BUTTON.get(), "Iron Button");
        add(ExtrasSetup.INVERTED_IRON_BUTTON.get(), "Inverted Iron Button");
        add(ExtrasSetup.IRON_LEVER.get(), "Iron Lever");
        add("block.allomancy.iron_activation.lore", "This item seems too heavy to activate by ordinary means");

        add(ConsumeSetup.ALLOMANTIC_GRINDER.get(), "Hand Grinder");
        add(ConsumeSetup.LERASIUM_NUGGET.get(), "Lerasium Nugget");
        add("item.allomancy.lerasium_nugget.lore",
            "This item is endowed with strange powers, perhaps you should ingest it?");
        add(CombatSetup.MISTCLOAK.get(), "Mistcloak");
        add(ExtrasSetup.BRONZE_EARRING.get(), "Bronze Earring");
        add("item.allomancy.bronze_earring.lore", "Useless, but sharp...");
        add(ExtrasSetup.CHARGED_BRONZE_EARRING.get(), "Charged Bronze Earring");
        add("item.allomancy.charged_bronze_earring.lore", "You hear a pulsing in the distance...");
        add(CombatSetup.ALUMINUM_HELMET.get(), "Aluminum Helmet");
        add(CombatSetup.COIN_BAG.get(), "Coin Bag");
        add(CombatSetup.OBSIDIAN_DAGGER.get(), "Obsidian Dagger");
        add(CombatSetup.KOLOSS_BLADE.get(), "Koloss Blade");
        add("item.allomancy.koloss_blade.lore", "This item is too heavy for the average person to wield.");
        add(ConsumeSetup.VIAL.get(), "Allomantic Vial");
        add("item.allomancy.vial.lore_count", "Contains %d metals");
        add("item.allomancy.vial.lore_inst", "Hold SHIFT to view");
        add("death.attack.allomancy.coin", "%1$s was perforated by coins from %2$s");

        for (Metal mt : Metal.values()) {
            add("metals." + mt.getName(), getDisplayName(mt));
            add("key.metals." + mt.getName(), "Toggle " + getDisplayName(mt));

            add(WorldSetup.FLAKES.get(mt.getIndex()).get(), getDisplayName(mt) + " Flakes");
            add(ExtrasSetup.PATTERN_ITEMS.get(mt.getIndex()).get(), "Banner Pattern");
            add("item.allomancy." + mt.getName() + "_pattern.desc", getDisplayName(mt) + " Symbol");

            if (mt.isVanilla()) {
                continue;
            }

            add(WorldSetup.NUGGETS.get(mt.getIndex()).get(), getDisplayName(mt) + " Nugget");
            add(WorldSetup.INGOTS.get(mt.getIndex()).get(), getDisplayName(mt) + " Ingot");
            add(WorldSetup.STORAGE_BLOCKS.get(mt.getIndex()).get(), getDisplayName(mt) + " Block");
        }

        add(WorldSetup.FLAKES.get(WorldSetup.LEAD).get(), "Lead Flakes");
        add(WorldSetup.NUGGETS.get(WorldSetup.LEAD).get(), "Lead Nugget");
        add(WorldSetup.INGOTS.get(WorldSetup.LEAD).get(), "Lead Ingot");
        add(WorldSetup.STORAGE_BLOCKS.get(WorldSetup.LEAD).get(), "Lead Block");

        add(WorldSetup.FLAKES.get(WorldSetup.SILVER).get(), "Silver Flakes");
        add(WorldSetup.NUGGETS.get(WorldSetup.SILVER).get(), "Silver Nugget");
        add(WorldSetup.INGOTS.get(WorldSetup.SILVER).get(), "Silver Ingot");
        add(WorldSetup.STORAGE_BLOCKS.get(WorldSetup.SILVER).get(), "Silver Block");

        add(CombatSetup.NUGGET_PROJECTILE.get(), "Nugget Projectile");

        add("advancements.allomancy.local_metallurgist.title", "Local Metallurgist!");
        add("advancements.allomancy.local_metallurgist.desc", "Craft a grinder to begin mixing metals");
        add("advancements.allomancy.dna_entangled.title", "Spiritual DNA Entanglement");
        add("advancements.allomancy.dna_entangled.desc",
            "Your DNA is too entangled with the spiritual realm to use Lerasium");
        add("advancements.allomancy.blinged_out.title", "A small gift");
        add("advancements.allomancy.blinged_out.desc", "What harm could a simple earring do?");
        add("advancements.allomancy.bloody.title", "Local... Hemalurgist?");
        add("advancements.allomancy.bloody.desc", "You now have blood on your hands (and ears)");
        add("advancements.allomancy.well.title", "Ascendant");
        add("advancements.allomancy.well.desc", "A well of great power");
        add("advancements.allomancy.become_mistborn.title", "Mistborn.");
        add("advancements.allomancy.become_mistborn.desc", "You have a power most people envy...");
        add("advancements.allomancy.metallic_collector.title", "Metallic Collector");
        add("advancements.allomancy.metallic_collector.desc",
            "Collect every single metallic flake, even the useless ones");
        add("advancements.allomancy.coinshot.title", "Coinshot");
        add("advancements.allomancy.coinshot.desc", "Kill a mob with the bag of coins.");
        add("advancements.allomancy.tin_foil_hat.title", "Tin foil hat");
        add("advancements.allomancy.tin_foil_hat.desc", "Protect yourself, and be a bit paranoid too");
        add("advancements.allomancy.time_warp.title", "Sub-time bubble?");
        add("advancements.allomancy.time_warp.desc", "Time travel? Not quite!");
        add("advancements.allomancy.consequences.title", "Consequences, Vin");
        add("advancements.allomancy.consequences.desc", "Learn what happens when you push on a much heavier target.");
        add("advancements.allomancy.going_loud.title", "Going Loud");
        add("advancements.allomancy.going_loud.desc", "Allomancy can grant great stealth, unless you do that!");

        add("key.categories.allomancy", "Allomancy");
        add("key.burn", "Burn Metals");
        add("key.hud", "Show Vial HUD");
        add("allomancy.gui", "Select Metal");

        add("commands.allomancy.getpowers", "%s currently has Allomantic powers: %s");
        add("commands.allomancy.addpower", "%s added Allomantic power %s");
        add("commands.allomancy.removepower", "%s removed Allomantic power %s");
        add("commands.allomancy.unrecognized", "Unrecognized Allomancy power: '%s'");
        add("commands.allomancy.err_add", "Unable to add power %s, already had");
        add("commands.allomancy.err_remove", "Unable to remove power %s, did not have");


        for (DyeColor color : DyeColor.values()) {
            for (Metal mt : Metal.values()) {
                add("block.minecraft.banner.allomancy." + mt.getName() + "." + color.getName(),
                    getDisplayName(color) + " " + getDisplayName(mt) + " Symbol");
            }
        }

        add("config.jade.plugin_allomancy.waila_bronze", "Allomancy: Show burning metals when seeking");

        add("allomancy.networking.failed", "Allomancy packet failed to play: %s");
        add("allomancy.networking.kicked", "Requested illegal action: %s");

    }
}