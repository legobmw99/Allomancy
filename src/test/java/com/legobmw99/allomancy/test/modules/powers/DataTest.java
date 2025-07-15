package com.legobmw99.allomancy.test.modules.powers;

import com.legobmw99.allomancy.api.enums.Metal;
import com.legobmw99.allomancy.modules.powers.data.AllomancerAttachment;
import com.legobmw99.allomancy.test.util.AllomancyTestHelper;
import com.legobmw99.allomancy.util.AllomancyTags;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;
import net.neoforged.testframework.annotation.ForEachTest;
import net.neoforged.testframework.annotation.TestHolder;
import net.neoforged.testframework.gametest.EmptyTemplate;
import net.neoforged.testframework.gametest.ExtendedGameTestHelper;
import net.neoforged.testframework.gametest.GameTest;

@ForEachTest(groups = "data")
public class DataTest {
    @GameTest
    @EmptyTemplate("1x3x1")
    @TestHolder(description = "Tests that an empty cap is uninvested")
    public static void emptyDataTest(ExtendedGameTestHelper helper) {
        var player = helper.makeMockPlayer(GameType.SURVIVAL);
        var data = AllomancerAttachment.get(player);

        helper.assertTrue(data.isUninvested(), "Default data is invested");
        helper.succeed();
    }

    @GameTest
    @EmptyTemplate("1x3x1")
    @TestHolder(description = "Tests that a new player is made a Misting and given a flake")
    public static void randomMistingTest(AllomancyTestHelper helper) {
        var player = helper.makeTickingPlayer();
        var data = AllomancerAttachment.get(player);
        helper.succeedWhen(() -> {
            helper.assertFalse(data.isUninvested(), "Player is not misting");
            helper.assertFalse(data.isMistborn(), "Player is full mistborn");
            helper.assertTrue(player.getItemInHand(InteractionHand.MAIN_HAND).is(AllomancyTags.FLAKES_TAG),
                              "Misting wasn't given flake");
        });
    }


    @GameTest
    @EmptyTemplate("1x3x1")
    @TestHolder(description = "Tests that data is handled correctly during various respawns")
    public static void dataOnRespawnTest(AllomancyTestHelper helper) {
        var player = helper.makeMistbornPlayer();

        player.setRespawnPosition(new ServerPlayer.RespawnConfig(Level.OVERWORLD, player.blockPosition(), 0.0f, true),
                                  true);

        var returningPlayer =
                player.getServer().getPlayerList().respawn(player, true, Entity.RemovalReason.CHANGED_DIMENSION);

        helper.assertTrue(AllomancerAttachment.get(returningPlayer).isMistborn(),
                          "Player lost investment on teleport");
        helper.assertTrue(AllomancerAttachment.get(returningPlayer).getStored(Metal.STEEL) == 10,
                          "Player lost inventory on teleport");

        var respawnedPlayer =
                player.getServer().getPlayerList().respawn(returningPlayer, false, Entity.RemovalReason.KILLED);

        helper.assertTrue(AllomancerAttachment.get(respawnedPlayer).isMistborn(), "Player lost investment on death");
        helper.assertTrue(AllomancerAttachment.get(respawnedPlayer).getStored(Metal.STEEL) == 0,
                          "Player kept inventory on death");


        var respawnedPlayerKeepInv =
                player.getServer().getPlayerList().respawn(returningPlayer, true, Entity.RemovalReason.KILLED);

        helper.assertTrue(AllomancerAttachment.get(respawnedPlayerKeepInv).isMistborn(),
                          "Player lost investment on death");
        helper.assertTrue(AllomancerAttachment.get(respawnedPlayerKeepInv).getStored(Metal.STEEL) == 10,
                          "Player lost inventory with KeepInventory");

        helper.succeed();
    }
}
