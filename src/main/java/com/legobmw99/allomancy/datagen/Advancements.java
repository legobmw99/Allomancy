package com.legobmw99.allomancy.datagen;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.legobmw99.allomancy.Allomancy;
import com.legobmw99.allomancy.modules.combat.CombatSetup;
import com.legobmw99.allomancy.modules.consumables.ConsumeSetup;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.FrameType;
import net.minecraft.advancements.criterion.ConsumeItemTrigger;
import net.minecraft.data.AdvancementProvider;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DirectoryCache;
import net.minecraft.data.IDataProvider;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TranslationTextComponent;

import java.io.IOException;
import java.nio.file.Path;
import java.util.function.Consumer;

public class Advancements extends AdvancementProvider {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();

    private final DataGenerator gen;

    public Advancements(DataGenerator generatorIn) {
        super(generatorIn);
        this.gen = generatorIn;
    }

    private static void registerAdvancements(Consumer<Advancement> consumer) {
        Advancement.Builder
                .builder()
                .withParent(Advancement.Builder.builder().build(new ResourceLocation("adventure/root"))) // hacky
                .withDisplay(CombatSetup.MISTCLOAK.get(), new TranslationTextComponent("advancements.become_mistborn.title"),
                             new TranslationTextComponent("advancements.become_mistborn.desc"), null, FrameType.CHALLENGE, true, true, true)
                .withCriterion("lerasium_nugget", ConsumeItemTrigger.Instance.forItem(ConsumeSetup.LERASIUM_NUGGET.get()))
                .withRewards(AdvancementRewards.Builder.experience(100))
                .register(consumer, "allomancy:main/become_mistborn");

    }

    @Override
    public void act(DirectoryCache cache) {
        Path outputFolder = this.gen.getOutputFolder();
        Consumer<Advancement> consumer = (advancement) -> {

            Path path = outputFolder.resolve("data/" + advancement.getId().getNamespace() + "/advancements/" + advancement.getId().getPath() + ".json");
            try {
                IDataProvider.save(GSON, cache, advancement.copy().serialize(), path);
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
