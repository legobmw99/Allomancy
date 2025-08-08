package com.legobmw99.allomancy.datagen;

import com.legobmw99.allomancy.api.enums.Metal;
import com.legobmw99.allomancy.modules.combat.CombatSetup;
import com.legobmw99.allomancy.modules.consumables.ConsumeSetup;
import com.legobmw99.allomancy.modules.consumables.item.component.FlakeStorage;
import com.legobmw99.allomancy.modules.extras.ExtrasSetup;
import com.legobmw99.allomancy.modules.extras.advancement.AllomanticallyActivatedBlockTrigger;
import com.legobmw99.allomancy.modules.extras.advancement.MetalUsedOnEntityTrigger;
import com.legobmw99.allomancy.modules.extras.advancement.MetalUsedOnPlayerTrigger;
import com.legobmw99.allomancy.modules.world.WorldSetup;
import com.legobmw99.allomancy.util.AllomancyTags;
import net.minecraft.advancements.*;
import net.minecraft.advancements.critereon.*;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.advancements.AdvancementSubProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;

import java.util.Optional;
import java.util.function.Consumer;

class Advancements implements AdvancementSubProvider {


    @Override
    public void generate(HolderLookup.Provider registries, Consumer<AdvancementHolder> saver) {
        var getter = registries.lookupOrThrow(Registries.ITEM);

        var metallurgist = Advancement.Builder
                .advancement()
                .parent(Advancement.Builder
                                .advancement()
                                .build(ResourceLocation.withDefaultNamespace("adventure/root")))
                .display(ConsumeSetup.ALLOMANTIC_GRINDER.get(),
                         Component.translatable("advancements.allomancy.local_metallurgist.title"),
                         Component.translatable("advancements.allomancy.local_metallurgist.desc"), null,
                         AdvancementType.TASK, true, true, false)
                .addCriterion("grinder",
                              InventoryChangeTrigger.TriggerInstance.hasItems(ConsumeSetup.ALLOMANTIC_GRINDER.get()))
                .save(saver, "allomancy:main/metallurgist");

        Advancement.Builder
                .advancement()
                .parent(metallurgist)
                .display(ConsumeSetup.LERASIUM_NUGGET.get(),
                         Component.translatable("advancements.allomancy.dna_entangled.title"),
                         Component.translatable("advancements.allomancy.dna_entangled.desc"), null,
                         AdvancementType.TASK, true, false, true)
                .addCriterion("impossible",
                              CriteriaTriggers.IMPOSSIBLE.createCriterion(new ImpossibleTrigger.TriggerInstance()))
                .save(saver, "allomancy:main/dna_entangled");

        var bling = Advancement.Builder
                .advancement()
                .parent(metallurgist)
                .display(ExtrasSetup.BRONZE_EARRING.get(),
                         Component.translatable("advancements.allomancy.blinged_out.title"),
                         Component.translatable("advancements.allomancy.blinged_out.desc"), null,
                         AdvancementType.TASK, true, false, false)
                .addCriterion("earring",
                              InventoryChangeTrigger.TriggerInstance.hasItems(ExtrasSetup.BRONZE_EARRING.get()))
                .save(saver, "allomancy:main/bling");

        var bloody = Advancement.Builder
                .advancement()
                .parent(bling)
                .display(ExtrasSetup.CHARGED_BRONZE_EARRING.get(),
                         Component.translatable("advancements.allomancy.bloody.title"),
                         Component.translatable("advancements.allomancy.bloody.desc"), null, AdvancementType.TASK,
                         true, false, false)
                .addCriterion("earring",

                              KilledTrigger.TriggerInstance.playerKilledEntity(
                                      new EntityPredicate.Builder().entityType(
                                              EntityTypePredicate.of(registries.lookupOrThrow(Registries.ENTITY_TYPE),
                                                                     AllomancyTags.HEMALURGIC_CHARGERS)),
                                      new DamageSourcePredicate.Builder().source(
                                              new EntityPredicate.Builder().equipment(
                                                      new EntityEquipmentPredicate.Builder().mainhand(
                                                              ItemPredicate.Builder
                                                                      .item()
                                                                      .of(registries.lookupOrThrow(Registries.ITEM),
                                                                          ExtrasSetup.BRONZE_EARRING.get()))))))
                .save(saver, "allomancy:main/bloody");

        var well = Advancement.Builder
                .advancement()
                .parent(bloody)
                .display(ConsumeSetup.LERASIUM_NUGGET.get(),
                         Component.translatable("advancements.allomancy.well.title"),
                         Component.translatable("advancements.allomancy.well.desc"), null, AdvancementType.GOAL, true,
                         true, true)
                .addCriterion("in_well", PlayerTrigger.TriggerInstance.located(LocationPredicate.Builder.inStructure(
                        registries.lookupOrThrow(Registries.STRUCTURE).getOrThrow(WorldSetup.WELL))))
                .save(saver, "allomancy:main/well");

        Advancement.Builder
                .advancement()
                .parent(well)
                .display(CombatSetup.MISTCLOAK.get(),
                         Component.translatable("advancements.allomancy.become_mistborn.title"),
                         Component.translatable("advancements.allomancy.become_mistborn.desc"), null,
                         AdvancementType.CHALLENGE, true, true, true)
                .addCriterion("lerasium_nugget",
                              ConsumeItemTrigger.TriggerInstance.usedItem(getter, ConsumeSetup.LERASIUM_NUGGET.get()))
                .rewards(AdvancementRewards.Builder.experience(100))
                .save(saver, "allomancy:main/become_mistborn");

        ItemStack vial = ConsumeSetup.VIAL.toStack();
        vial.set(ConsumeSetup.FLAKE_STORAGE.get(), new FlakeStorage.Mutable().add(Metal.GOLD).toImmutable());

        var allMetals = Advancement.Builder
                .advancement()
                .parent(metallurgist)
                .display(vial, Component.translatable("advancements.allomancy.metallic_collector.title"),
                         Component.translatable("advancements.allomancy.metallic_collector.desc"), null,
                         AdvancementType.CHALLENGE, true, true, false);
        for (var flake : WorldSetup.FLAKES) {
            allMetals.addCriterion("has_" + flake.getId().getPath(),
                                   InventoryChangeTrigger.TriggerInstance.hasItems(flake.get()));
        }

        allMetals.save(saver, "allomancy:main/metallic_collector");


        var coinshot = Advancement.Builder
                .advancement()
                .parent(metallurgist)
                .display(CombatSetup.COIN_BAG.get(), Component.translatable("advancements.allomancy.coinshot.title"),
                         Component.translatable("advancements.allomancy.coinshot.desc"), null, AdvancementType.TASK,
                         true, true, false)
                .addCriterion("nugget_kill", KilledTrigger.TriggerInstance.playerKilledEntity(Optional.empty(),
                                                                                              DamageSourcePredicate.Builder
                                                                                                      .damageType()
                                                                                                      .tag(TagPredicate.is(
                                                                                                              AllomancyTags.IS_COIN_HIT))))
                .save(saver, "allomancy:main/coinshot");


        var tinFoilPredicate = EntityPredicate.wrap(EntityPredicate.Builder
                                                            .entity()
                                                            .equipment(EntityEquipmentPredicate.Builder
                                                                               .equipment()
                                                                               .head(ItemPredicate.Builder
                                                                                             .item()
                                                                                             .of(getter,
                                                                                                 AllomancyTags.TIN_FOIL_HATS))));

        Advancement.Builder
                .advancement()
                .parent(metallurgist)
                .display(CombatSetup.ALUMINUM_HELMET.get(),
                         Component.translatable("advancements.allomancy.tin_foil_hat.title"),
                         Component.translatable("advancements.allomancy.tin_foil_hat.desc"), null,
                         AdvancementType.TASK, true, false, true)
                .addCriterion("attempted_nicrosil_manipulation",
                              MetalUsedOnPlayerTrigger.TriggerInstance.instance(tinFoilPredicate, Metal.NICROSIL))
                .addCriterion("attempted_chromium_manipulation",
                              MetalUsedOnPlayerTrigger.TriggerInstance.instance(tinFoilPredicate, Metal.CHROMIUM))
                .requirements(AdvancementRequirements.Strategy.OR)
                .save(saver, "allomancy:main/tin_foil_hat");

        Advancement.Builder
                .advancement()
                .parent(metallurgist)
                .display(Items.CLOCK, Component.translatable("advancements.allomancy.time_warp.title"),
                         Component.translatable("advancements.allomancy.time_warp.desc"), null, AdvancementType.TASK,
                         true, true, true)
                .addCriterion("got_slowed_down",
                              MetalUsedOnPlayerTrigger.TriggerInstance.instance(null, Metal.CADMIUM))
                .addCriterion("got_sped_up", MetalUsedOnPlayerTrigger.TriggerInstance.instance(null, Metal.BENDALLOY))
                .requirements(AdvancementRequirements.Strategy.AND)
                .save(saver, "allomancy:main/time_warp");


        var ironGolemPredicate = EntityPredicate.wrap(EntityPredicate.Builder
                                                              .entity()
                                                              .of(registries.lookupOrThrow(Registries.ENTITY_TYPE),
                                                                  EntityType.IRON_GOLEM));

        Advancement.Builder
                .advancement()
                .parent(metallurgist)
                .display(Blocks.IRON_BLOCK, Component.translatable("advancements.allomancy.consequences.title"),
                         Component.translatable("advancements.allomancy.consequences.desc"), null,
                         AdvancementType.TASK, true, false, true)
                .addCriterion("pushed_iron_golem",
                              MetalUsedOnEntityTrigger.TriggerInstance.instance(null, ironGolemPredicate,
                                                                                Metal.STEEL))
                .addCriterion("pulled_iron_golem",
                              MetalUsedOnEntityTrigger.TriggerInstance.instance(null, ironGolemPredicate, Metal.IRON))
                .requirements(AdvancementRequirements.Strategy.OR)
                .save(saver, "allomancy:main/consequences");


        Advancement.Builder
                .advancement()
                .parent(coinshot)
                .display(Blocks.BELL, Component.translatable("advancements.allomancy.going_loud.title"),
                         Component.translatable("advancements.allomancy.going_loud.desc"), null, AdvancementType.TASK,
                         true, true, true)
                .addCriterion("allomantically_activate_bell",
                              AllomanticallyActivatedBlockTrigger.TriggerInstance.activatedBlock(Blocks.BELL))
                .save(saver, "allomancy:main/going_loud");
    }


}
