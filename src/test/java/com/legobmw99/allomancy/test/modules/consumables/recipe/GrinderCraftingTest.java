package com.legobmw99.allomancy.test.modules.consumables.recipe;

import com.legobmw99.allomancy.api.enums.Metal;
import com.legobmw99.allomancy.modules.consumables.ConsumeSetup;
import com.legobmw99.allomancy.modules.materials.MaterialsSetup;
import com.legobmw99.allomancy.test.AllomancyTest;
import com.legobmw99.allomancy.test.util.CallbackTest;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.testframework.Test;
import net.neoforged.testframework.annotation.RegisterStructureTemplate;
import net.neoforged.testframework.gametest.StructureTemplateBuilder;

import java.util.function.Consumer;
import java.util.function.Supplier;

// for automation, this class is done in a more vanilla GameTest style.
@GameTestHolder(AllomancyTest.MODID)
public class GrinderCraftingTest {

    @RegisterStructureTemplate(AllomancyTest.MODID + ":crafter")
    public static final StructureTemplate CRAFTER = StructureTemplateBuilder.empty(1, 2, 2);


    private static Item getIngotItem(int metal_idx) {
        Supplier<Item> res = MaterialsSetup.INGOTS.get(metal_idx);
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
        if (metal_idx == MaterialsSetup.LEAD) {
            return "lead";
        }
        if (metal_idx == MaterialsSetup.SILVER) {
            return "silver";
        }
        return Metal.values()[metal_idx].getName();
    }


    public static void register(Consumer<Test> add) {
        String structureName = AllomancyTest.MODID + ":crafter";
        for (int i = 0; i < MaterialsSetup.METAL_ITEM_LEN; i++) {
            int I = i;
            add.accept(new CallbackTest("crafting_" + getMetalName(i) + "_flakes", helper -> {
                var ingot = getIngotItem(I);
                var flake = MaterialsSetup.FLAKES.get(I).get();
                helper.succeedIfCrafts(barrel -> {
                    var craftedFlake = barrel.getItem(0).is(flake) && barrel.getItem(0).getCount() == 2;
                    var retainedGrinder = barrel.getItem(1).is(ConsumeSetup.ALLOMANTIC_GRINDER.get());
                    var damagedGrinder = barrel.getItem(1).getDamageValue() == 1;
                    return craftedFlake && retainedGrinder && damagedGrinder;
                }, () -> "Failed to craft flakes", ConsumeSetup.ALLOMANTIC_GRINDER, ingot);
            }, structureName, "crafting"));
        }
    }
}
