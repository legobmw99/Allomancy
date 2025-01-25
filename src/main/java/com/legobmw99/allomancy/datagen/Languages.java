package com.legobmw99.allomancy.datagen;

import com.legobmw99.allomancy.Allomancy;
import com.legobmw99.allomancy.api.enums.Metal;
import com.legobmw99.allomancy.modules.combat.CombatSetup;
import com.legobmw99.allomancy.modules.consumables.ConsumeSetup;
import com.legobmw99.allomancy.modules.extras.ExtrasSetup;
import com.legobmw99.allomancy.modules.materials.MaterialsSetup;
import net.minecraft.data.PackOutput;
import net.minecraft.world.item.DyeColor;
import net.neoforged.neoforge.common.data.LanguageProvider;

import java.util.Arrays;
import java.util.Locale;
import java.util.stream.Collectors;

class Languages extends LanguageProvider {


    Languages(PackOutput gen) {
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


        for (int i = 0; i < MaterialsSetup.ORE_METALS.length; i++) {
            String metal = MaterialsSetup.ORE_METALS[i].name();
            var ore = MaterialsSetup.ORE_BLOCKS.get(i).get();
            var ds = MaterialsSetup.DEEPSLATE_ORE_BLOCKS.get(i).get();
            var rawb = MaterialsSetup.RAW_ORE_BLOCKS.get(i).get();
            var raw = MaterialsSetup.RAW_ORE_ITEMS.get(i).get();

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
        add(CombatSetup.ALUMINUM_HELMET.get(), "Aluminum Helmet");
        add(CombatSetup.COIN_BAG.get(), "Coin Bag");
        add(CombatSetup.OBSIDIAN_DAGGER.get(), "Obsidian Dagger");
        add(CombatSetup.KOLOSS_BLADE.get(), "Koloss Blade");
        add("item.allomancy.koloss_blade.lore", "This item is too heavy for the average person to wield.");
        add(ConsumeSetup.VIAL.get(), "Allomantic Vial");
        add("allomancy.flake_storage.lore_single", "Contains %s");
        add("allomancy.flake_storage.lore_count", "Contains %s metals");
        add("allomancy.flake_storage.lore_inst", "Hold SHIFT to view");
        add("death.attack.allomancy.coin", "%1$s was perforated by coins from %2$s");

        for (Metal mt : Metal.values()) {
            add("metals." + mt.getName(), getDisplayName(mt));
            add("key.metals." + mt.getName(), "Toggle " + getDisplayName(mt));

            add(MaterialsSetup.FLAKES.get(mt.getIndex()).get(), getDisplayName(mt) + " Flakes");
            add(ExtrasSetup.PATTERN_ITEMS.get(mt.getIndex()).get(), getDisplayName(mt) + " Banner Pattern");

            if (mt.isVanilla()) {
                continue;
            }

            add(MaterialsSetup.NUGGETS.get(mt.getIndex()).get(), getDisplayName(mt) + " Nugget");
            add(MaterialsSetup.INGOTS.get(mt.getIndex()).get(), getDisplayName(mt) + " Ingot");
            add(MaterialsSetup.STORAGE_BLOCKS.get(mt.getIndex()).get(), getDisplayName(mt) + " Block");
        }

        add(MaterialsSetup.FLAKES.get(MaterialsSetup.LEAD).get(), "Lead Flakes");
        add(MaterialsSetup.NUGGETS.get(MaterialsSetup.LEAD).get(), "Lead Nugget");
        add(MaterialsSetup.INGOTS.get(MaterialsSetup.LEAD).get(), "Lead Ingot");
        add(MaterialsSetup.STORAGE_BLOCKS.get(MaterialsSetup.LEAD).get(), "Lead Block");

        add(MaterialsSetup.FLAKES.get(MaterialsSetup.SILVER).get(), "Silver Flakes");
        add(MaterialsSetup.NUGGETS.get(MaterialsSetup.SILVER).get(), "Silver Nugget");
        add(MaterialsSetup.INGOTS.get(MaterialsSetup.SILVER).get(), "Silver Ingot");
        add(MaterialsSetup.STORAGE_BLOCKS.get(MaterialsSetup.SILVER).get(), "Silver Block");

        add(CombatSetup.NUGGET_PROJECTILE.get(), "Nugget Projectile");

        add("advancements.local_metallurgist.title", "Local Metallurgist!");
        add("advancements.local_metallurgist.desc", "Craft a grinder to begin mixing metals");
        add("advancements.dna_entangled.title", "Spiritual DNA Entanglement");
        add("advancements.dna_entangled.desc", "Your DNA is too entangled with the spiritual realm to use Lerasium");
        add("advancements.become_mistborn.title", "Become Mistborn!");
        add("advancements.become_mistborn.desc", "You have a power most people envy...");
        add("advancements.metallic_collector.title", "Metallic Collector");
        add("advancements.metallic_collector.desc", "Collect every single metallic flake, even the useless ones");
        add("advancements.coinshot.title", "Coinshot");
        add("advancements.coinshot.desc", "Kill a mob with the bag of coins.");
        add("advancements.tin_foil_hat.title", "Tin foil hat");
        add("advancements.tin_foil_hat.desc", "Protect yourself, and be a bit paranoid too");
        add("advancements.time_warp.title", "Sub-time bubble?");
        add("advancements.time_warp.desc", "Time travel? Not quite!");
        add("advancements.consequences.title", "Consequences, Vin");
        add("advancements.consequences.desc", "Learn what happens when you push on a much heavier target.");
        add("advancements.going_loud.title", "Going Loud");
        add("advancements.going_loud.desc", "Allomancy can grant great stealth, unless you do that!");

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
                add("allomancy." + mt.getName() + "." + color.getName(),
                    getDisplayName(color) + " " + getDisplayName(mt) + " Symbol");
            }
        }

        add("config.jade.plugin_allomancy.waila_bronze", "Allomancy: Show burning metals when seeking");

        add("allomancy.networking.failed", "Allomancy packet failed to play: %s");
        add("allomancy.networking.kicked", "Requested illegal action: %s");

        add("allomancy.configuration.gameplay", "Allomancy Gameplay Settings");
        add("allomancy.configuration.whitelist", "Metal Whitelist");
        add("allomancy.configuration.random_mistings", "Randomly Assign Mistings");
        add("allomancy.configuration.respect_player_UUID", "Misting from UUID");

        add("allomancy.configuration.graphics", "Allomancy Graphics Settings");
        add("allomancy.configuration.overlay_enabled", "Vial HUD Enabled");
        add("allomancy.configuration.overlay_position", "Vial HUD Position");
        add("allomancy.configuration.animate_selection", "Animate Metal Selection Wheel");
        add("allomancy.configuration.max_metal_distance", "Maximum Steelsight Distance");

    }
}
