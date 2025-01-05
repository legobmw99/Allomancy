package com.legobmw99.allomancy.test.modules.powers;

import com.legobmw99.allomancy.api.enums.Metal;
import com.legobmw99.allomancy.modules.materials.MaterialsSetup;
import com.legobmw99.allomancy.modules.powers.data.AllomancerAttachment;
import com.legobmw99.allomancy.test.AllomancyTestHelper;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;
import net.neoforged.testframework.annotation.ForEachTest;
import net.neoforged.testframework.annotation.TestHolder;
import net.neoforged.testframework.gametest.EmptyTemplate;
import net.neoforged.testframework.gametest.ExtendedGameTestHelper;

@ForEachTest(groups = "data")
public class DataTest {
    @GameTest
    @EmptyTemplate("1x3x1")
    @TestHolder
    public static void emptyDataTest(ExtendedGameTestHelper helper) {
        var player = helper.makeMockPlayer(GameType.SURVIVAL);
        var data = player.getData(AllomancerAttachment.ALLOMANCY_DATA);

        helper.assertTrue(data.isUninvested(), "Default data is invested");
        helper.succeed();
    }

    @GameTest
    @EmptyTemplate("1x3x1")
    @TestHolder
    public static void randomMistingTest(AllomancyTestHelper helper) {
        var player = helper.makeTickingPlayer();
        var data = player.getData(AllomancerAttachment.ALLOMANCY_DATA);

        helper.assertFalse(data.isUninvested(), "Player is not misting");
        helper.assertFalse(data.isMistborn(), "Player is full mistborn");
        helper.assertTrue(player.getItemInHand(InteractionHand.MAIN_HAND).is(MaterialsSetup.FLAKES_TAG),
                          "Misting wasn't given flake");
        helper.succeed();
    }


    @GameTest
    @EmptyTemplate("1x3x1")
    @TestHolder
    public static void dataOnRespawnTest(AllomancyTestHelper helper) {
        var player = helper.makeMistbornPlayer();

        player.setRespawnPosition(Level.OVERWORLD, player.blockPosition(), 0.0f, true, true);

        var returningPlayer =
                player.getServer().getPlayerList().respawn(player, true, Entity.RemovalReason.CHANGED_DIMENSION);

        helper.assertTrue(returningPlayer.getData(AllomancerAttachment.ALLOMANCY_DATA).isMistborn(),
                          "Player lost investment on teleport");
        helper.assertTrue(returningPlayer.getData(AllomancerAttachment.ALLOMANCY_DATA).getStored(Metal.STEEL) == 10,
                          "Player lost inventory on teleport");

        var respawnedPlayer =
                player.getServer().getPlayerList().respawn(returningPlayer, false, Entity.RemovalReason.KILLED);

        helper.assertTrue(respawnedPlayer.getData(AllomancerAttachment.ALLOMANCY_DATA).isMistborn(),
                          "Player lost investment on death");
        helper.assertTrue(respawnedPlayer.getData(AllomancerAttachment.ALLOMANCY_DATA).getStored(Metal.STEEL) == 0,
                          "Player kept inventory on death");

        helper.succeed();
    }
}