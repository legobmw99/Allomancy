package com.legobmw99.allomancy.datagen;

import com.legobmw99.allomancy.Allomancy;
import com.legobmw99.allomancy.api.enums.Metal;
import com.legobmw99.allomancy.modules.combat.CombatSetup;
import com.legobmw99.allomancy.modules.consumables.ConsumeSetup;
import com.legobmw99.allomancy.modules.consumables.item.component.FlakeStorage;
import com.legobmw99.allomancy.modules.extras.advancement.AllomanticallyActivatedBlockTrigger;
import com.legobmw99.allomancy.modules.extras.advancement.MetalUsedOnEntityTrigger;
import com.legobmw99.allomancy.modules.extras.advancement.MetalUsedOnPlayerTrigger;
import com.legobmw99.allomancy.modules.materials.MaterialsSetup;
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

        Advancement.Builder
                .advancement()
                .parent(Advancement.Builder
                                .advancement()
                                .build(ResourceLocation.withDefaultNamespace("adventure/root")))
                .display(ConsumeSetup.ALLOMANTIC_GRINDER.get(),
                         Component.translatable("advancements.local_metallurgist.title"),
                         Component.translatable("advancements.local_metallurgist.desc"), null, AdvancementType.TASK,
                         true, true, false)
                .addCriterion("grinder",
                              InventoryChangeTrigger.TriggerInstance.hasItems(ConsumeSetup.ALLOMANTIC_GRINDER.get()))
                .save(saver, "allomancy:main/metallurgist");

        Advancement.Builder
                .advancement()
                .parent(Advancement.Builder.advancement().build(Allomancy.rl("main/metallurgist")))
                .display(ConsumeSetup.LERASIUM_NUGGET.get(),
                         Component.translatable("advancements.dna_entangled.title"),
                         Component.translatable("advancements.dna_entangled.desc"), null, AdvancementType.TASK, true,
                         false, true)
                .addCriterion("impossible",
                              CriteriaTriggers.IMPOSSIBLE.createCriterion(new ImpossibleTrigger.TriggerInstance()))
                .save(saver, "allomancy:main/dna_entangled");

        Advancement.Builder
                .advancement()
                .parent(Advancement.Builder.advancement().build(Allomancy.rl("main/metallurgist")))
                .display(CombatSetup.MISTCLOAK.get(), Component.translatable("advancements.become_mistborn.title"),
                         Component.translatable("advancements.become_mistborn.desc"), null, AdvancementType.CHALLENGE,
                         true, true, true)
                .addCriterion("lerasium_nugget",
                              ConsumeItemTrigger.TriggerInstance.usedItem(getter, ConsumeSetup.LERASIUM_NUGGET.get()))
                .rewards(AdvancementRewards.Builder.experience(100))
                .save(saver, "allomancy:main/become_mistborn");

        ItemStack vial = new ItemStack(ConsumeSetup.VIAL.get());
        vial.set(ConsumeSetup.FLAKE_STORAGE.get(), new FlakeStorage.Mutable().add(Metal.GOLD).toImmutable());

        var allMetals = Advancement.Builder
                .advancement()
                .parent(Advancement.Builder.advancement().build(Allomancy.rl("main/metallurgist")))
                .display(vial, Component.translatable("advancements.metallic_collector.title"),
                         Component.translatable("advancements.metallic_collector.desc"), null,
                         AdvancementType.CHALLENGE, true, true, false);
        for (var flake : MaterialsSetup.FLAKES) {
            allMetals.addCriterion("has_" + flake.getId().getPath(),
                                   InventoryChangeTrigger.TriggerInstance.hasItems(flake.get()));
        }

        allMetals.save(saver, "allomancy:main/metallic_collector");


        Advancement.Builder
                .advancement()
                .parent(Advancement.Builder.advancement().build(Allomancy.rl("main/metallurgist")))
                .display(CombatSetup.COIN_BAG.get(), Component.translatable("advancements.coinshot.title"),
                         Component.translatable("advancements.coinshot.desc"), null, AdvancementType.TASK, true, true,
                         false)
                .addCriterion("nugget_kill", KilledTrigger.TriggerInstance.playerKilledEntity(Optional.empty(),
                                                                                              DamageSourcePredicate.Builder
                                                                                                      .damageType()
                                                                                                      .tag(TagPredicate.is(
                                                                                                              CombatSetup.IS_COIN_HIT))))
                .save(saver, "allomancy:main/coinshot");


        var tinFoilPredicate = EntityPredicate.wrap(EntityPredicate.Builder
                                                            .entity()
                                                            .equipment(EntityEquipmentPredicate.Builder
                                                                               .equipment()
                                                                               .head(ItemPredicate.Builder
                                                                                             .item()
                                                                                             .of(getter,
                                                                                                 CombatSetup.ALUMINUM_HELMET))));

        Advancement.Builder
                .advancement()
                .parent(Advancement.Builder.advancement().build(Allomancy.rl("main/metallurgist")))
                .display(CombatSetup.ALUMINUM_HELMET.get(), Component.translatable("advancements.tin_foil_hat.title"),
                         Component.translatable("advancements.tin_foil_hat.desc"), null, AdvancementType.TASK, true,
                         false, true)
                .addCriterion("attempted_nicrosil_manipulation",
                              MetalUsedOnPlayerTrigger.TriggerInstance.instance(tinFoilPredicate, Metal.NICROSIL))
                .addCriterion("attempted_chromium_manipulation",
                              MetalUsedOnPlayerTrigger.TriggerInstance.instance(tinFoilPredicate, Metal.CHROMIUM))
                .requirements(AdvancementRequirements.Strategy.OR)
                .save(saver, "allomancy:main/tin_foil_hat");

        Advancement.Builder
                .advancement()
                .parent(Advancement.Builder.advancement().build(Allomancy.rl("main/metallurgist")))
                .display(Items.CLOCK, Component.translatable("advancements.time_warp.title"),
                         Component.translatable("advancements.time_warp.desc"), null, AdvancementType.TASK, true,
                         true, true)
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
                .parent(Advancement.Builder.advancement().build(Allomancy.rl("main/metallurgist")))
                .display(Blocks.IRON_BLOCK, Component.translatable("advancements.consequences.title"),
                         Component.translatable("advancements.consequences.desc"), null, AdvancementType.TASK, true,
                         false, true)
                .addCriterion("pushed_iron_golem",
                              MetalUsedOnEntityTrigger.TriggerInstance.instance(null, ironGolemPredicate,
                                                                                Metal.STEEL))
                .addCriterion("pulled_iron_golem",
                              MetalUsedOnEntityTrigger.TriggerInstance.instance(null, ironGolemPredicate, Metal.IRON))
                .requirements(AdvancementRequirements.Strategy.OR)
                .save(saver, "allomancy:main/consequences");


        Advancement.Builder
                .advancement()
                .parent(Advancement.Builder.advancement().build(Allomancy.rl("main/coinshot")))
                .display(Blocks.BELL, Component.translatable("advancements.going_loud.title"),
                         Component.translatable("advancements.going_loud.desc"), null, AdvancementType.TASK, true,
                         true, true)
                .addCriterion("allomantically_activate_bell",
                              AllomanticallyActivatedBlockTrigger.TriggerInstance.activatedBlock(Blocks.BELL))
                .save(saver, "allomancy:main/going_loud");
    }


}
