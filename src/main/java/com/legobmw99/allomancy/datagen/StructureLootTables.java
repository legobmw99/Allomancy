package com.legobmw99.allomancy.datagen;

import com.legobmw99.allomancy.Allomancy;
import com.legobmw99.allomancy.api.enums.Metal;
import com.legobmw99.allomancy.modules.consumables.ConsumeSetup;
import com.legobmw99.allomancy.modules.consumables.item.component.FlakeStorage;
import com.legobmw99.allomancy.modules.world.loot.PlayerInvestmentCondition;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.loot.LootTableSubProvider;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.component.UseRemainder;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.SetComponentsFunction;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.predicates.AllOfCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.providers.number.ints.ContextIntProviders;

import java.util.Arrays;

public record StructureLootTables(LootTableSubProvider.Context output) implements LootTableSubProvider {


    @Override
    public void run() {
        LootItemCondition.Builder is_mistborn = new AllOfCondition.Builder(Arrays
                                                                                   .stream(Metal.values())
                                                                                   .map(PlayerInvestmentCondition.Builder::new)
                                                                                   .toArray(
                                                                                           PlayerInvestmentCondition.Builder[]::new));

        // note: at the moment, pots never set THIS_ENTITY, so it doesn't really matter
        LootItemCondition.Builder is_not_mistborn = is_mistborn.invert();

        output.accept(ResourceKey.create(Registries.LOOT_TABLE, Allomancy.id("pots/well_plain")), LootTable
                .lootTable()
                .withPool(LootPool
                                  .lootPool()
                                  .setRolls(ContextIntProviders.exactly(1))
                                  .add(LootItem.lootTableItem(ConsumeSetup.LERASIUM_NUGGET))
                                  .when(is_not_mistborn)));


        FlakeStorage storage;
        {
            var mut = new FlakeStorage.Mutable();
            for (Metal mt : Metal.values()) {
                mut.add(mt);
            }
            storage = mut.toImmutable();
        }
        output.accept(ResourceKey.create(Registries.LOOT_TABLE, Allomancy.id("pots/well_decorated_1")), LootTable
                .lootTable()
                .withPool(LootPool
                                  .lootPool()
                                  .setRolls(ContextIntProviders.exactly(1))
                                  .add(LootItem
                                               .lootTableItem(ConsumeSetup.VIAL)
                                               .apply(SetItemCountFunction.setCount(
                                                       ContextIntProviders.between(1, 7)))
                                               .apply(SetComponentsFunction.setComponent(
                                                       ConsumeSetup.FLAKE_STORAGE.get(), storage))
                                               .apply(SetComponentsFunction.setComponent(DataComponents.USE_REMAINDER,
                                                                                         new UseRemainder(
                                                                                                 new ItemStackTemplate(
                                                                                                         ConsumeSetup.VIAL))))
                                               .apply(SetComponentsFunction.setComponent(DataComponents.RARITY,
                                                                                         Rarity.UNCOMMON)))));


        output.accept(ResourceKey.create(Registries.LOOT_TABLE, Allomancy.id("pots/well_decorated_2")), LootTable
                .lootTable()
                .withPool(LootPool
                                  .lootPool()
                                  .setRolls(ContextIntProviders.exactly(1))
                                  .add(LootItem.lootTableItem(ConsumeSetup.ALLOMANTIC_GRINDER))
                                  .add(LootItem
                                               .lootTableItem(ConsumeSetup.VIAL)
                                               .setWeight(5)
                                               .apply(SetItemCountFunction.setCount(
                                                       ContextIntProviders.between(5, 18))))));
    }
}
