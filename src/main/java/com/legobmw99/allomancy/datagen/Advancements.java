package com.legobmw99.allomancy.datagen;

import com.legobmw99.allomancy.api.enums.Metal;
import com.legobmw99.allomancy.modules.combat.CombatSetup;
import com.legobmw99.allomancy.modules.consumables.ConsumeSetup;
import com.legobmw99.allomancy.modules.extras.ExtrasSetup;
import com.legobmw99.allomancy.modules.world.WorldSetup;
import com.legobmw99.allomancy.util.AllomancyTags;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.FrameType;
import net.minecraft.advancements.critereon.*;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.common.data.ForgeAdvancementProvider;

import java.util.function.Consumer;

public class Advancements implements ForgeAdvancementProvider.AdvancementGenerator {

    @Override
    public void generate(HolderLookup.Provider registries,
                         Consumer<Advancement> saver,
                         ExistingFileHelper existingFileHelper) {
        var getter = registries.lookupOrThrow(Registries.ITEM);

        var metallurgist = Advancement.Builder
                .advancement()
                .parent(Advancement.Builder.advancement().build(new ResourceLocation("adventure/root")))
                .display(ConsumeSetup.ALLOMANTIC_GRINDER.get(),
                         Component.translatable("advancements.allomancy.local_metallurgist.title"),
                         Component.translatable("advancements.allomancy.local_metallurgist.desc"), null,
                         FrameType.TASK, true, true, false)
                .addCriterion("grinder",
                              InventoryChangeTrigger.TriggerInstance.hasItems(ConsumeSetup.ALLOMANTIC_GRINDER.get()))
                .save(saver, "allomancy:main/metallurgist");

        Advancement.Builder
                .advancement()
                .parent(metallurgist)
                .display(ConsumeSetup.LERASIUM_NUGGET.get(),
                         Component.translatable("advancements.allomancy.dna_entangled.title"),
                         Component.translatable("advancements.allomancy.dna_entangled.desc"), null, FrameType.TASK,
                         true, false, true)
                .addCriterion("impossible", new ImpossibleTrigger.TriggerInstance())
                .save(saver, "allomancy:main/dna_entangled");

        var bling = Advancement.Builder
                .advancement()
                .parent(metallurgist)
                .display(ExtrasSetup.BRONZE_EARRING.get(),
                         Component.translatable("advancements.allomancy.blinged_out.title"),
                         Component.translatable("advancements.allomancy.blinged_out.desc"), null, FrameType.TASK,
                         true, false, false)
                .addCriterion("earring",
                              InventoryChangeTrigger.TriggerInstance.hasItems(ExtrasSetup.BRONZE_EARRING.get()))
                .save(saver, "allomancy:main/bling");

        var bloody = Advancement.Builder
                .advancement()
                .parent(bling)
                .display(ExtrasSetup.CHARGED_BRONZE_EARRING.get(),
                         Component.translatable("advancements.allomancy.bloody.title"),
                         Component.translatable("advancements.allomancy.bloody.desc"), null, FrameType.TASK, true,
                         false, false)
                .addCriterion("earring",

                              KilledTrigger.TriggerInstance.playerKilledEntity(
                                      new EntityPredicate.Builder().entityType(
                                              EntityTypePredicate.of(AllomancyTags.HEMALURGIC_CHARGERS)),
                                      new DamageSourcePredicate.Builder().source(
                                              new EntityPredicate.Builder().equipment(
                                                      new EntityEquipmentPredicate.Builder()
                                                              .mainhand(ItemPredicate.Builder
                                                                                .item()
                                                                                .of(ExtrasSetup.BRONZE_EARRING.get())
                                                                                .build())
                                                              .build()))))
                .save(saver, "allomancy:main/bloody");

        var well = Advancement.Builder
                .advancement()
                .parent(bloody)
                .display(ConsumeSetup.LERASIUM_NUGGET.get(),
                         Component.translatable("advancements.allomancy.well.title"),
                         Component.translatable("advancements.allomancy.well.desc"), null, FrameType.GOAL, true, true,
                         true)
                .addCriterion("in_well", PlayerTrigger.TriggerInstance.located(
                        LocationPredicate.Builder.location().setStructure(WorldSetup.WELL).build()))
                .save(saver, "allomancy:main/well");

        Advancement.Builder
                .advancement()
                .parent(well)
                .display(CombatSetup.MISTCLOAK.get(),
                         Component.translatable("advancements.allomancy.become_mistborn.title"),
                         Component.translatable("advancements.allomancy.become_mistborn.desc"), null,
                         FrameType.CHALLENGE, true, true, true)
                .addCriterion("lerasium_nugget",
                              ConsumeItemTrigger.TriggerInstance.usedItem(ConsumeSetup.LERASIUM_NUGGET.get()))
                .rewards(AdvancementRewards.Builder.experience(100))
                .save(saver, "allomancy:main/become_mistborn");

        ItemStack vial = new ItemStack(ConsumeSetup.VIAL.get());

        CompoundTag nbt = new CompoundTag();
        for (Metal mt : Metal.values()) {
            nbt.putBoolean(mt.getName(), true);
        }
        nbt.putInt("CustomModelData", 1);
        vial.setTag(nbt);

        var allMetals = Advancement.Builder
                .advancement()
                .parent(metallurgist)
                .display(vial, Component.translatable("advancements.allomancy.metallic_collector.title"),
                         Component.translatable("advancements.allomancy.metallic_collector.desc"), null,
                         FrameType.CHALLENGE, true, true, false);
        for (var flake : WorldSetup.FLAKES) {
            allMetals.addCriterion("has_" + flake.getId().getPath(),
                                   InventoryChangeTrigger.TriggerInstance.hasItems(flake.get()));
        }

        allMetals.save(saver, "allomancy:main/metallic_collector");


        var coinshot = Advancement.Builder
                .advancement()
                .parent(metallurgist)
                .display(CombatSetup.COIN_BAG.get(), Component.translatable("advancements.allomancy.coinshot.title"),
                         Component.translatable("advancements.allomancy.coinshot.desc"), null, FrameType.TASK, true,
                         true, false)
                .addCriterion("nugget_kill", KilledTrigger.TriggerInstance.playerKilledEntity(EntityPredicate.ANY,
                                                                                              DamageSourcePredicate.Builder
                                                                                                      .damageType()
                                                                                                      .tag(TagPredicate.is(
                                                                                                              AllomancyTags.IS_COIN_HIT))))
                .save(saver, "allomancy:main/coinshot");


    }
}
