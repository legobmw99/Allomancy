package com.legobmw99.allomancy.test.modules.consumables.recipe;

import com.legobmw99.allomancy.api.enums.Metal;
import com.legobmw99.allomancy.modules.consumables.ConsumeSetup;
import com.legobmw99.allomancy.modules.consumables.item.VialItem;
import com.legobmw99.allomancy.modules.consumables.item.component.FlakeStorage;
import com.legobmw99.allomancy.modules.world.WorldSetup;
import com.legobmw99.allomancy.test.util.AllomancyTestHelper;
import net.minecraft.world.item.ItemStack;
import net.neoforged.testframework.annotation.ForEachTest;
import net.neoforged.testframework.annotation.TestHolder;
import net.neoforged.testframework.gametest.EmptyTemplate;
import net.neoforged.testframework.gametest.GameTest;

@ForEachTest(groups = "item")
public class VialCraftingTest {

    @GameTest
    @EmptyTemplate("1x2x2")
    @TestHolder(description = "Tests that vial crafting works for multiple flakes")
    public static void vialCraftingWorks(AllomancyTestHelper helper) {
        helper.succeedIfCrafts(barrel -> {
                                   ItemStack out = barrel.getItem(0);
                                   return out.getCount() == 1 && out.is(ConsumeSetup.VIAL.get()) &&
                                          out.get(ConsumeSetup.FLAKE_STORAGE).contains(Metal.IRON) &&
                                          out.get(ConsumeSetup.FLAKE_STORAGE).contains(Metal.GOLD);
                               }, "Didn't craft vial!", ConsumeSetup.VIAL,
                               WorldSetup.FLAKES.get(Metal.IRON.getIndex()),
                               WorldSetup.FLAKES.get(Metal.GOLD.getIndex()));
    }


    @GameTest
    @EmptyTemplate("1x2x2")
    @TestHolder(description = "Tests that vial crafting is prevented if a specific metal is already present")
    public static void vialCraftingPreventDupes(AllomancyTestHelper helper) {
        var vial = ConsumeSetup.VIAL.toStack();
        VialItem.fillVial(vial, new FlakeStorage.Mutable().add(Metal.IRON).toImmutable());

        helper.succeedIfCraftingFails(vial, WorldSetup.FLAKES.get(Metal.IRON.getIndex()),
                                      WorldSetup.FLAKES.get(Metal.GOLD.getIndex()));

    }
}
