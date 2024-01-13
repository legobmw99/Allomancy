package com.legobmw99.allomancy.datagen;

import com.legobmw99.allomancy.Allomancy;
import com.legobmw99.allomancy.modules.combat.CombatSetup;
import com.legobmw99.allomancy.modules.consumables.ConsumeSetup;
import com.legobmw99.allomancy.modules.materials.MaterialsSetup;
import net.minecraft.advancements.*;
import net.minecraft.advancements.critereon.*;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.data.AdvancementProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

import java.util.Optional;
import java.util.function.Consumer;

public class Advancements implements AdvancementProvider.AdvancementGenerator {

    @Override
    public void generate(HolderLookup.Provider registries, Consumer<AdvancementHolder> saver, ExistingFileHelper existingFileHelper) {
        Advancement.Builder
                .advancement()
                .parent(Advancement.Builder.advancement().build(new ResourceLocation("adventure/root")))
                .display(ConsumeSetup.ALLOMANTIC_GRINDER.get(), Component.translatable("advancements.local_metallurgist.title"),
                         Component.translatable("advancements.local_metallurgist.desc"), null, AdvancementType.TASK, true, true, false)
                .addCriterion("grinder", InventoryChangeTrigger.TriggerInstance.hasItems(ConsumeSetup.ALLOMANTIC_GRINDER.get()))
                .save(saver, "allomancy:main/metallurgist");

        Advancement.Builder
                .advancement()
                .parent(Advancement.Builder.advancement().build(new ResourceLocation(Allomancy.MODID, "main/metallurgist")))
                .display(ConsumeSetup.LERASIUM_NUGGET.get(), Component.translatable("advancements.dna_entangled.title"), Component.translatable("advancements.dna_entangled.desc"),
                         null, AdvancementType.TASK, true, false, true)
                .addCriterion("impossible", CriteriaTriggers.IMPOSSIBLE.createCriterion(new ImpossibleTrigger.TriggerInstance()))
                .save(saver, "allomancy:main/dna_entangled");

        Advancement.Builder
                .advancement()
                .parent(Advancement.Builder.advancement().build(new ResourceLocation(Allomancy.MODID, "main/metallurgist")))
                .display(CombatSetup.MISTCLOAK.get(), Component.translatable("advancements.become_mistborn.title"), Component.translatable("advancements.become_mistborn.desc"),
                         null, AdvancementType.CHALLENGE, true, true, true)
                .addCriterion("lerasium_nugget", ConsumeItemTrigger.TriggerInstance.usedItem(ConsumeSetup.LERASIUM_NUGGET.get()))
                .rewards(AdvancementRewards.Builder.experience(100))
                .save(saver, "allomancy:main/become_mistborn");

        ItemStack vial = new ItemStack(ConsumeSetup.VIAL.get());
        CompoundTag nbt = new CompoundTag();
        nbt.putInt("CustomModelData", 1);
        vial.setTag(nbt);

        var allMetals = Advancement.Builder
                .advancement()
                .parent(Advancement.Builder.advancement().build(new ResourceLocation(Allomancy.MODID, "main/metallurgist")))
                .display(vial, Component.translatable("advancements.metallic_collector.title"), Component.translatable("advancements.metallic_collector.desc"), null,
                         AdvancementType.CHALLENGE, true, true, false);
        for (var flake : MaterialsSetup.FLAKES) {
            allMetals.addCriterion("has_" + flake.getId().getPath(), InventoryChangeTrigger.TriggerInstance.hasItems(flake.get()));
        }

        allMetals.save(saver, "allomancy:main/metallic_collector");


        Advancement.Builder
                .advancement()
                .parent(Advancement.Builder.advancement().build(new ResourceLocation(Allomancy.MODID, "main/metallurgist")))
                .display(CombatSetup.COIN_BAG.get(), Component.translatable("advancements.coinshot.title"), Component.translatable("advancements.coinshot.desc"), null,
                         AdvancementType.TASK, true, true, false)
                .addCriterion("nugget_kill", KilledTrigger.TriggerInstance.playerKilledEntity(Optional.empty(), DamageSourcePredicate.Builder
                        .damageType()
                        .tag(TagPredicate.is(CombatSetup.IS_COIN_HIT))))
                .save(saver, "allomancy:main/coinshot");


        Advancement.Builder
                .advancement()
                .parent(Advancement.Builder.advancement().build(new ResourceLocation(Allomancy.MODID, "main/metallurgist")))
                .display(CombatSetup.ALUMINUM_HELMET.get(), Component.translatable("advancements.tin_foil_hat.title"), Component.translatable("advancements.tin_foil_hat.desc"),
                         null, AdvancementType.TASK, true, false, true)
                .addCriterion("tin_foil_hat", InventoryChangeTrigger.TriggerInstance.hasItems(CombatSetup.ALUMINUM_HELMET.get()))
                // TODO require that someone actually use allomancy on you
                .save(saver, "allomancy:main/tin_foil_hat");
    }
}
