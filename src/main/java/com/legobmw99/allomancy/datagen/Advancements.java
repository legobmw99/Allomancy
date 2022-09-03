package com.legobmw99.allomancy.datagen;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.legobmw99.allomancy.Allomancy;
import com.legobmw99.allomancy.modules.combat.CombatSetup;
import com.legobmw99.allomancy.modules.consumables.ConsumeSetup;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.FrameType;
import net.minecraft.advancements.critereon.ConsumeItemTrigger;
import net.minecraft.advancements.critereon.ImpossibleTrigger;
import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraft.data.advancements.AdvancementProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.data.ExistingFileHelper;

import java.io.IOException;
import java.nio.file.Path;
import java.util.function.Consumer;

public class Advancements extends AdvancementProvider {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();

    private final DataGenerator gen;

    public Advancements(DataGenerator generatorIn, ExistingFileHelper existingFileHelper) {
        super(generatorIn, existingFileHelper);
        this.gen = generatorIn;
    }

    private static void registerAdvancements(Consumer<Advancement> consumer) {


        Advancement.Builder
                .advancement()
                .parent(Advancement.Builder.advancement().build(new ResourceLocation("adventure/root")))
                .display(ConsumeSetup.ALLOMANTIC_GRINDER.get(), Component.translatable("advancements.local_metallurgist.title"),
                         Component.translatable("advancements.local_metallurgist.desc"), null, FrameType.TASK, true, true, false)
                .addCriterion("grinder", InventoryChangeTrigger.TriggerInstance.hasItems(ConsumeSetup.ALLOMANTIC_GRINDER.get()))
                .save(consumer, "allomancy:main/metallurgist");

        Advancement.Builder
                .advancement()
                .parent(Advancement.Builder.advancement().build(new ResourceLocation(Allomancy.MODID, "main/metallurgist")))
                .display(ConsumeSetup.LERASIUM_NUGGET.get(), Component.translatable("advancements.dna_entangled.title"), Component.translatable("advancements.dna_entangled.desc"),
                         null, FrameType.TASK, true, false, true)
                .addCriterion("impossible", new ImpossibleTrigger.TriggerInstance())
                .save(consumer, "allomancy:main/dna_entangled");

        Advancement.Builder
                .advancement()
                .parent(Advancement.Builder.advancement().build(new ResourceLocation(Allomancy.MODID, "main/metallurgist")))
                .display(CombatSetup.MISTCLOAK.get(), Component.translatable("advancements.become_mistborn.title"), Component.translatable("advancements.become_mistborn.desc"),
                         null, FrameType.CHALLENGE, true, true, true)
                .addCriterion("lerasium_nugget", ConsumeItemTrigger.TriggerInstance.usedItem(ConsumeSetup.LERASIUM_NUGGET.get()))
                .rewards(AdvancementRewards.Builder.experience(100))
                .save(consumer, "allomancy:main/become_mistborn");


    }

    @Override
    public void run(CachedOutput cache) {
        Path outputFolder = this.gen.getOutputFolder();
        Consumer<Advancement> consumer = (advancement) -> {

            Path path = outputFolder.resolve("data/" + advancement.getId().getNamespace() + "/advancements/" + advancement.getId().getPath() + ".json");
            try {
                DataProvider.saveStable(cache, advancement.deconstruct().serializeToJson(), path);
                Allomancy.LOGGER.debug("Creating advancement " + advancement.getId());
            } catch (IOException ioexception) {
                Allomancy.LOGGER.error("Couldn't save advancement {}", path, ioexception);
            }
        };

        registerAdvancements(consumer);
    }

    @Override
    public String getName() {
        return "Allomancy Advancements";
    }
}
