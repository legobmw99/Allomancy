package com.legobmw99.allomancy.test.modules.consumables.recipe;

import com.legobmw99.allomancy.api.enums.Metal;
import com.legobmw99.allomancy.modules.consumables.ConsumeSetup;
import com.legobmw99.allomancy.modules.consumables.item.GrinderItem;
import com.legobmw99.allomancy.modules.world.WorldSetup;
import com.legobmw99.allomancy.test.AllomancyTest;
import com.legobmw99.allomancy.test.util.CallbackTest;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.neoforged.testframework.Test;
import net.neoforged.testframework.annotation.RegisterStructureTemplate;
import net.neoforged.testframework.gametest.StructureTemplateBuilder;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class GrinderCraftingTest {

    @RegisterStructureTemplate(AllomancyTest.MODID + ":crafter")
    public static final StructureTemplate CRAFTER = StructureTemplateBuilder.empty(1, 2, 2);


    private static Item getIngotItem(int metal_idx) {
        Supplier<Item> res = WorldSetup.INGOTS.get(metal_idx);
        if (res != null) {
            return res.get();
        }
        return switch (Metal.values()[metal_idx]) {
            case IRON -> Items.IRON_INGOT;
            case COPPER -> Items.COPPER_INGOT;
            case GOLD -> Items.GOLD_INGOT;
            default -> throw new IllegalStateException("Unexpected value: " + metal_idx);
        };
    }

    private static String getMetalName(int metal_idx) {
        if (metal_idx == WorldSetup.LEAD) {
            return "lead";
        }
        if (metal_idx == WorldSetup.SILVER) {
            return "silver";
        }
        return Metal.values()[metal_idx].getName();
    }


    public static void register(Consumer<Test> add) {
        String structureName = AllomancyTest.MODID + ":crafter";
        for (int i = 0; i < WorldSetup.METAL_ITEM_LEN; i++) {
            int I = i;
            String metal = getMetalName(i);
            add.accept(new CallbackTest("crafting_" + metal + "_flakes", helper -> {
                var ingot = getIngotItem(I);
                var flake = WorldSetup.FLAKES.get(I).get();

                var grinder = new ItemStackTemplate(ConsumeSetup.ALLOMANTIC_GRINDER);
                var ingoti = new ItemStackTemplate(ingot);

                helper.succeedIfCrafts(barrel -> {
                    var craftedFlake = barrel.getItem(0).is(flake) && barrel.getItem(0).getCount() == 2;
                    var retainedGrinder = barrel.getItem(1).is(ConsumeSetup.ALLOMANTIC_GRINDER.get());
                    var damagedGrinder = barrel.getItem(1).getDamageValue() == 1;
                    return craftedFlake && retainedGrinder && damagedGrinder;
                }, "Failed to craft flakes", grinder, ingoti);
            }, structureName, "item", "Tests that " + metal + " flake crafting works and the grinder is maintained"));
        }


        add.accept(new CallbackTest("crafting_breaks_grinder", helper -> {
            var ingot = getIngotItem(0);
            var flake = WorldSetup.FLAKES.get(0).get();

            var grinder = new ItemStackTemplate(ConsumeSetup.ALLOMANTIC_GRINDER, 1, DataComponentPatch
                    .builder()
                    .set(DataComponents.DAMAGE, GrinderItem.MAX_DAMAGE - 1)
                    .build());
            var ingoti = new ItemStackTemplate(ingot);

            helper.succeedIfCrafts(

                    barrel -> {
                        var craftedFlake = barrel.getItem(0).is(flake) && barrel.getItem(0).getCount() == 2;
                        var retainedGrinder = barrel.getItem(1).is(ConsumeSetup.ALLOMANTIC_GRINDER.get());
                        return craftedFlake && !retainedGrinder;
                    }, "Kept grinder with no durability",

                    grinder, ingoti);
        }, structureName, "item", "Tests that grinder is spent when done"));
    }
}
