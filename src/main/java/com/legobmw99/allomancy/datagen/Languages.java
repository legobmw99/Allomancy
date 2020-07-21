package com.legobmw99.allomancy.datagen;

import com.legobmw99.allomancy.modules.combat.CombatSetup;
import com.legobmw99.allomancy.modules.consumables.ConsumeSetup;
import com.legobmw99.allomancy.modules.extras.ExtrasSetup;
import com.legobmw99.allomancy.modules.materials.MaterialsSetup;
import com.legobmw99.allomancy.setup.Metal;
import net.minecraft.data.DataGenerator;
import net.minecraftforge.common.data.LanguageProvider;

public class Languages extends LanguageProvider {
    public Languages(DataGenerator gen, String modid, String locale) {
        super(gen, modid, locale);
    }

    @Override
    protected void addTranslations() {
        add("itemGroup.allomancy", "Allomancy");
        for(Metal mt: Metal.values()){
            add("metals." + mt.getName(), mt.getDisplayName());
        }

        add(MaterialsSetup.ALUMINUM_ORE.get(), "Aluminum Ore");
        add(MaterialsSetup.CADMIUM_ORE.get(), "Cadmium Ore");
        add(MaterialsSetup.CHROMIUM_ORE.get(), "Chromium Ore");
        add(MaterialsSetup.COPPER_ORE.get(), "Copper Ore");
        add(MaterialsSetup.LEAD_ORE.get(), "Lead Ore");
        add(MaterialsSetup.SILVER_ORE.get(), "Silver Ore");
        add(MaterialsSetup.TIN_ORE.get(), "Tin Ore");
        add(MaterialsSetup.ZINC_ORE.get(), "Zinc Ore");
        add(ExtrasSetup.IRON_BUTTON.get(), "Iron Button");
        add(ExtrasSetup.IRON_LEVER.get(), "Iron Lever");
        add("block.allomancy.iron_activation.lore",
                "This item seems too heavy to activate by ordinary means");

        add(ConsumeSetup.ALLOMANTIC_GRINDER.get(), "Hand Grinder");
        add(ConsumeSetup.LERASIUM_NUGGET.get(), "Lerasium Nugget");
        add("item.allomancy.lerasium_nugget.lore",
                "This item is endowed with strange powers, perhaps you should ingest it?");
        add(CombatSetup.MISTCLOAK.get(), "Mistcloak");
        add(CombatSetup.COIN_BAG.get(), "Coin Bag");
        add(CombatSetup.OBSIDIAN_DAGGER.get(), "Obsidian Dagger");
        add(CombatSetup.KOLOSS_BLADE.get(), "Koloss Blade");
        add("item.allomancy.koloss_blade.lore",
                "This item is too heavy for the average person to wield.");
        add(ConsumeSetup.VIAL.get(), "Allomantic Vial");
        add("item.allomancy.vial.lore_count","Contains %d metals");
        add("item.allomancy.vial.lore_inst", "Hold SHIFT to view");

        add(MaterialsSetup.ALUMINUM_INGOT.get(), "Aluminum Ingot");
        add(MaterialsSetup.CADMIUM_INGOT.get(), "Cadmium Ingot");
        add(MaterialsSetup.CHROMIUM_INGOT.get(), "Chromium Ingot");
        add(MaterialsSetup.COPPER_INGOT.get(), "Copper Ingot");
        add(MaterialsSetup.BRASS_INGOT.get(), "Brass Ingot");
        add(MaterialsSetup.BRONZE_INGOT.get(), "Bronze Ingot");
        add(MaterialsSetup.LEAD_INGOT.get(), "Lead Ingot");
        add(MaterialsSetup.SILVER_INGOT.get(), "Silver Ingot");
        add(MaterialsSetup.TIN_INGOT.get(), "Tin Ingot");
        add(MaterialsSetup.ZINC_INGOT.get(), "Zinc Ingot");

        for(Metal mt: Metal.values()){
            add(MaterialsSetup.FLAKES.get(mt.getIndex()).get(), mt.getDisplayName() + " Flakes");
        }
        add(MaterialsSetup.FLAKES.get(MaterialsSetup.LEAD).get(), "Lead Flakes");
        add(MaterialsSetup.FLAKES.get(MaterialsSetup.SILVER).get(), "Silver Flakes");

        add(CombatSetup.NUGGET_PROJECTILE.get(), "Nugget Projectile");

        add("advancements.become_mistborn.title", "Become Mistborn!");
        add("advancements.become_mistborn.desc", "You have a power most people envy...");

        add("key.categories.allomancy", "Allomancy");
        add("key.burn", "Burn Metals");

        add("commands.allomancy.getpowers", "%s currently has Allomantic powers: %s");
        add("commands.allomancy.addpower", "%s added Allomantic power %s");
        add("commands.allomancy.removepower", "%s removed Allomantic power %s");
        add("commands.allomancy.unrecognized", "Unrecognized Allomancy power: '%s'");
    }

    @Override
    public String getName() {
        return "Allomancy Language";
    }
}
