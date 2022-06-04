package com.legobmw99.allomancy.datagen;

import com.legobmw99.allomancy.api.enums.Metal;
import com.legobmw99.allomancy.modules.combat.CombatSetup;
import com.legobmw99.allomancy.modules.consumables.ConsumeSetup;
import com.legobmw99.allomancy.modules.extras.ExtrasSetup;
import com.legobmw99.allomancy.modules.materials.MaterialsSetup;
import net.minecraft.data.DataGenerator;
import net.minecraft.world.item.DyeColor;
import net.minecraftforge.common.data.LanguageProvider;

import java.util.Arrays;
import java.util.Locale;
import java.util.stream.Collectors;

public class Languages extends LanguageProvider {


    public Languages(DataGenerator gen, String modid, String locale) {
        super(gen, modid, locale);
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
        add("itemGroup.allomancy", "Allomancy");


        for (int i = 0; i < MaterialsSetup.ORE_METALS.length; i++) {
            String metal = MaterialsSetup.ORE_METALS[i];
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
        add(ExtrasSetup.IRON_LEVER.get(), "Iron Lever");
        add("block.allomancy.iron_activation.lore", "This item seems too heavy to activate by ordinary means");

        add(ConsumeSetup.ALLOMANTIC_GRINDER.get(), "Hand Grinder");
        add(ConsumeSetup.LERASIUM_NUGGET.get(), "Lerasium Nugget");
        add("item.allomancy.lerasium_nugget.lore", "This item is endowed with strange powers, perhaps you should ingest it?");
        add(CombatSetup.MISTCLOAK.get(), "Mistcloak");
        add(CombatSetup.COIN_BAG.get(), "Coin Bag");
        add(CombatSetup.OBSIDIAN_DAGGER.get(), "Obsidian Dagger");
        add(CombatSetup.KOLOSS_BLADE.get(), "Koloss Blade");
        add("item.allomancy.koloss_blade.lore", "This item is too heavy for the average person to wield.");
        add(ConsumeSetup.VIAL.get(), "Allomantic Vial");
        add("item.allomancy.vial.lore_count", "Contains %d metals");
        add("item.allomancy.vial.lore_inst", "Hold SHIFT to view");

        for (Metal mt : Metal.values()) {
            add("metals." + mt.getName(), getDisplayName(mt));

            add(MaterialsSetup.FLAKES.get(mt.getIndex()).get(), getDisplayName(mt) + " Flakes");

            add(ExtrasSetup.PATTERN_ITEMS.get(mt.getIndex()).get(), "Banner Pattern");
            add("item.allomancy." + mt.getName() + "_pattern.desc", getDisplayName(mt) + " Symbol");

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

        add("key.categories.allomancy", "Allomancy");
        add("key.burn", "Burn Metals");
        add("key.hud", "Show HUD");

        add("commands.allomancy.getpowers", "%s currently has Allomantic powers: %s");
        add("commands.allomancy.addpower", "%s added Allomantic power %s");
        add("commands.allomancy.removepower", "%s removed Allomantic power %s");
        add("commands.allomancy.unrecognized", "Unrecognized Allomancy power: '%s'");
        add("commands.allomancy.err_add", "Unable to add power %s, already had");
        add("commands.allomancy.err_remove", "Unable to remove power %s, did not have");

        for (DyeColor color : DyeColor.values()) {
            for (Metal mt : Metal.values()) {
                add("block.minecraft.banner.allomancy_" + mt.getName() + "." + color.getName(), getDisplayName(color) + " " + getDisplayName(mt) + " Symbol");
            }
        }

    }

    @Override
    public String getName() {
        return "Allomancy Language";
    }
}
