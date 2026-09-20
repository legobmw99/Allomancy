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
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementRequirements;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.AdvancementType;
import net.minecraft.advancements.predicates.DamageSourcePredicate;
import net.minecraft.advancements.predicates.ItemPredicate;
import net.minecraft.advancements.predicates.LocationPredicate;
import net.minecraft.advancements.predicates.TagPredicate;
import net.minecraft.advancements.predicates.entity.EntityEquipmentPredicate;
import net.minecraft.advancements.predicates.entity.EntityPredicate;
import net.minecraft.advancements.predicates.entity.EntityTypePredicate;
import net.minecraft.advancements.triggers.*;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.advancements.AdvancementSubProvider;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;

import java.util.Optional;

class Advancements extends AdvancementSubProvider {


    protected Advancements(BootstrapContext<Advancement> output) {
        super(output);
    }

    @Override
    public void generate() {
        var metallurgist = Advancement.Builder
                .advancement()
                .parent(Advancement.Builder.advancement().build(Identifier.withDefaultNamespace("adventure/root")))
                .display(ConsumeSetup.ALLOMANTIC_GRINDER.asItem(),
                         Component.translatable("advancements.allomancy.local_metallurgist.title"),
                         Component.translatable("advancements.allomancy.local_metallurgist.desc"),
                         AdvancementType.TASK, true, true, false)
                .addCriterion("grinder",
                              InventoryChangeTrigger.TriggerInstance.hasItems(ConsumeSetup.ALLOMANTIC_GRINDER))
                .save(output, "allomancy:main/metallurgist");

        Advancement.Builder
                .advancement()
                .parent(metallurgist)
                .display(ConsumeSetup.LERASIUM_NUGGET.asItem(),
                         Component.translatable("advancements.allomancy.dna_entangled.title"),
                         Component.translatable("advancements.allomancy.dna_entangled.desc"), AdvancementType.TASK,
                         true, false, true)
                .addCriterion("impossible",
                              CriteriaTriggers.IMPOSSIBLE.createCriterion(new ImpossibleTrigger.TriggerInstance()))
                .save(output, "allomancy:main/dna_entangled");

        var bling = Advancement.Builder
                .advancement()
                .parent(metallurgist)
                .display(ExtrasSetup.BRONZE_EARRING.asItem(),
                         Component.translatable("advancements.allomancy.blinged_out.title"),
                         Component.translatable("advancements.allomancy.blinged_out.desc"), AdvancementType.TASK,
                         true, false, false)
                .addCriterion("earring", InventoryChangeTrigger.TriggerInstance.hasItems(ExtrasSetup.BRONZE_EARRING))
                .save(output, "allomancy:main/bling");

        var bloody = Advancement.Builder
                .advancement()
                .parent(bling)
                .display(ExtrasSetup.CHARGED_BRONZE_EARRING.asItem(),
                         Component.translatable("advancements.allomancy.bloody.title"),
                         Component.translatable("advancements.allomancy.bloody.desc"), AdvancementType.TASK, true,
                         false, false)
                .addCriterion("earring",

                              KilledTrigger.TriggerInstance.playerKilledEntity(
                                      new EntityPredicate.Builder().entityType(
                                              EntityTypePredicate.of(output.lookup(Registries.ENTITY_TYPE),
                                                                     AllomancyTags.HEMALURGIC_CHARGERS)),
                                      new DamageSourcePredicate.Builder().source(
                                              new EntityPredicate.Builder().equipment(
                                                      new EntityEquipmentPredicate.Builder().mainhand(
                                                              ItemPredicate.Builder
                                                                      .item()
                                                                      .of(output.lookup(Registries.ITEM),
                                                                          ExtrasSetup.BRONZE_EARRING))))))
                .save(output, "allomancy:main/bloody");

        var well = Advancement.Builder
                .advancement()
                .parent(bloody)
                .display(ConsumeSetup.LERASIUM_NUGGET.asItem(),
                         Component.translatable("advancements.allomancy.well.title"),
                         Component.translatable("advancements.allomancy.well.desc"), AdvancementType.GOAL, true, true,
                         true)
                .addCriterion("in_well", PlayerTrigger.TriggerInstance.located(LocationPredicate.Builder.inStructure(
                        output.lookup(Registries.STRUCTURE).getOrThrow(WorldSetup.WELL))))
                .save(output, "allomancy:main/well");

        Advancement.Builder
                .advancement()
                .parent(well)
                .display(CombatSetup.MISTCLOAK.asItem(),
                         Component.translatable("advancements.allomancy.become_mistborn.title"),
                         Component.translatable("advancements.allomancy.become_mistborn.desc"),
                         AdvancementType.CHALLENGE, true, true, true)
                .addCriterion("lerasium_nugget",
                              ConsumeItemTrigger.TriggerInstance.usedItem(output.lookup(Registries.ITEM),
                                                                          ConsumeSetup.LERASIUM_NUGGET))
                .rewards(AdvancementRewards.Builder.experience(100))
                .save(output, "allomancy:main/become_mistborn");

        ItemStackTemplate vial = new ItemStackTemplate(ConsumeSetup.VIAL, DataComponentPatch
                .builder()
                .set(ConsumeSetup.FLAKE_STORAGE.get(), new FlakeStorage.Mutable().add(Metal.GOLD).toImmutable())
                .build());

        var allMetals = Advancement.Builder
                .advancement()
                .parent(metallurgist)
                .display(vial, Component.translatable("advancements.allomancy.metallic_collector.title"),
                         Component.translatable("advancements.allomancy.metallic_collector.desc"),
                         AdvancementType.CHALLENGE, true, true, false);
        for (var flake : WorldSetup.FLAKES) {
            allMetals.addCriterion("has_" + flake.getId().getPath(),
                                   InventoryChangeTrigger.TriggerInstance.hasItems(flake));
        }

        allMetals.save(output, "allomancy:main/metallic_collector");


        var coinshot = Advancement.Builder
                .advancement()
                .parent(metallurgist)
                .display(CombatSetup.COIN_BAG.asItem(),
                         Component.translatable("advancements.allomancy.coinshot.title"),
                         Component.translatable("advancements.allomancy.coinshot.desc"), AdvancementType.TASK, true,
                         true, false)
                .addCriterion("nugget_kill", KilledTrigger.TriggerInstance.playerKilledEntity(Optional.empty(),
                                                                                              DamageSourcePredicate.Builder
                                                                                                      .damageType()
                                                                                                      .tag(TagPredicate.is(
                                                                                                              this.damageTypes,
                                                                                                              AllomancyTags.IS_COIN_HIT))))
                .save(output, "allomancy:main/coinshot");


        var tinFoilPredicate = EntityPredicate.wrap(EntityPredicate.Builder
                                                            .entity()
                                                            .equipment(EntityEquipmentPredicate.Builder
                                                                               .equipment()
                                                                               .head(ItemPredicate.Builder
                                                                                             .item()
                                                                                             .of(output.lookup(
                                                                                                         Registries.ITEM),
                                                                                                 AllomancyTags.TIN_FOIL_HATS))));

        Advancement.Builder
                .advancement()
                .parent(metallurgist)
                .display(CombatSetup.ALUMINUM_HELMET.asItem(),
                         Component.translatable("advancements.allomancy.tin_foil_hat.title"),
                         Component.translatable("advancements.allomancy.tin_foil_hat.desc"), AdvancementType.TASK,
                         true, false, true)
                .addCriterion("attempted_nicrosil_manipulation",
                              MetalUsedOnPlayerTrigger.TriggerInstance.instance(tinFoilPredicate, Metal.NICROSIL))
                .addCriterion("attempted_chromium_manipulation",
                              MetalUsedOnPlayerTrigger.TriggerInstance.instance(tinFoilPredicate, Metal.CHROMIUM))
                .requirements(AdvancementRequirements.Strategy.OR)
                .save(output, "allomancy:main/tin_foil_hat");

        Advancement.Builder
                .advancement()
                .parent(metallurgist)
                .display(Items.CLOCK.asItem(), Component.translatable("advancements.allomancy.time_warp.title"),
                         Component.translatable("advancements.allomancy.time_warp.desc"), AdvancementType.TASK, true,
                         true, true)
                .addCriterion("got_slowed_down",
                              MetalUsedOnPlayerTrigger.TriggerInstance.instance(null, Metal.CADMIUM))
                .addCriterion("got_sped_up", MetalUsedOnPlayerTrigger.TriggerInstance.instance(null, Metal.BENDALLOY))
                .requirements(AdvancementRequirements.Strategy.AND)
                .save(output, "allomancy:main/time_warp");


        var ironGolemPredicate = EntityPredicate.wrap(
                EntityPredicate.Builder.entity().of(output.lookup(Registries.ENTITY_TYPE), EntityTypes.IRON_GOLEM));

        Advancement.Builder
                .advancement()
                .parent(metallurgist)
                .display(Blocks.IRON_BLOCK.asItem(),
                         Component.translatable("advancements.allomancy.consequences.title"),
                         Component.translatable("advancements.allomancy.consequences.desc"), AdvancementType.TASK,
                         true, false, true)
                .addCriterion("pushed_iron_golem",
                              MetalUsedOnEntityTrigger.TriggerInstance.instance(null, ironGolemPredicate,
                                                                                Metal.STEEL))
                .addCriterion("pulled_iron_golem",
                              MetalUsedOnEntityTrigger.TriggerInstance.instance(null, ironGolemPredicate, Metal.IRON))
                .requirements(AdvancementRequirements.Strategy.OR)
                .save(output, "allomancy:main/consequences");


        Advancement.Builder
                .advancement()
                .parent(coinshot)
                .display(Blocks.BELL.asItem(), Component.translatable("advancements.allomancy.going_loud.title"),
                         Component.translatable("advancements.allomancy.going_loud.desc"), AdvancementType.TASK, true,
                         true, true)
                .addCriterion("allomantically_activate_bell",
                              AllomanticallyActivatedBlockTrigger.TriggerInstance.activatedBlock(
                                      output.lookup(Registries.BLOCK), Blocks.BELL))
                .save(output, "allomancy:main/going_loud");
    }
}
