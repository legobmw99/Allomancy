package com.legobmw99.allomancy.test.util;

import com.legobmw99.allomancy.api.enums.Metal;
import com.legobmw99.allomancy.modules.combat.CombatSetup;
import com.legobmw99.allomancy.modules.powers.data.AllomancerAttachment;
import com.legobmw99.allomancy.modules.powers.data.AllomancerData;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.FrontAndTop;
import net.minecraft.gametest.framework.GameTestAssertPosException;
import net.minecraft.gametest.framework.GameTestInfo;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.entity.CrafterBlockEntity;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.neoforged.testframework.gametest.ExtendedGameTestHelper;
import net.neoforged.testframework.gametest.GameTestPlayer;

import java.util.Arrays;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Stream;

public class AllomancyTestHelper extends ExtendedGameTestHelper {

    public AllomancyTestHelper(GameTestInfo info) {
        super(info);
    }

    public InteractionResult useItem(ServerPlayer player, ItemLike item) {
        return useItem(player, new ItemStack(item));
    }

    public InteractionResult useItem(ServerPlayer player, ItemStack stack) {
        player.setItemInHand(InteractionHand.MAIN_HAND, stack);
        return player.gameMode.useItem(player, player.serverLevel(), player.getItemInHand(InteractionHand.MAIN_HAND),
                                       InteractionHand.MAIN_HAND);
    }

    private static final BlockPos CRAFTER = new BlockPos(0, 0, 1);
    private static final BlockPos OUTPUT = new BlockPos(0, 0, 0);

    void craft(ItemStack... items) {
        this.setBlock(CRAFTER, Blocks.CRAFTER
                .defaultBlockState()
                .setValue(BlockStateProperties.ORIENTATION,
                          FrontAndTop.fromFrontAndTop(this.getTestRotation().rotate(Direction.NORTH), Direction.UP)));
        this.setBlock(OUTPUT, Blocks.BARREL);

        CrafterBlockEntity crafter_ent = this.getBlockEntity(CRAFTER, CrafterBlockEntity.class);
        for (int i = 0; i < items.length; i++) {
            crafter_ent.setItem(i, items[i]);
        }
        this.pulseRedstone(CRAFTER.above(), 0);
    }

    void craft(ItemLike... items) {
        this.craft(Arrays.stream(items).map(ItemStack::new).toArray(ItemStack[]::new));
    }

    void craft(ItemStack stack1, ItemLike... items) {
        this.craft(
                Stream.concat(Stream.of(stack1), Arrays.stream(items).map(ItemStack::new)).toArray(ItemStack[]::new));
    }

    public void succeedIfCrafts(Predicate<BaseContainerBlockEntity> pred,
                                Supplier<String> exceptionMessage,
                                ItemLike... items) {
        this.startSequence().thenExecute(() -> this.craft(items)).thenExecuteAfter(4, () -> {
            this.assertContainerEmpty(CRAFTER);


            BaseContainerBlockEntity output = this.getBlockEntity(OUTPUT, BaseContainerBlockEntity.class);
            if (!pred.test(output)) {
                throw new GameTestAssertPosException(exceptionMessage.get(), this.absolutePos(CRAFTER), CRAFTER,
                                                     this.getTick());

            }
        }).thenSucceed();
    }

    public void succeedIfCraftingFails(ItemStack stack, ItemLike... items) {
        this.startSequence().thenExecute(() -> this.craft(stack, items)).thenExecuteAfter(4, () -> {
            this.assertContainerEmpty(OUTPUT);
            this.assertContainerContains(CRAFTER, stack.getItem());
            for (var item : items) {
                this.assertContainerContains(CRAFTER, item.asItem());
            }
        }).thenSucceed();

    }

    public GameTestPlayer makeTickingPlayer() {
        return this.makeTickingMockServerPlayerInLevel(GameType.SURVIVAL).moveToCentre();
    }

    public GameTestPlayer makeMistbornPlayer() {
        var player = this.makeTickingPlayer();
        player.setItemSlot(EquipmentSlot.HEAD, CombatSetup.ALUMINUM_HELMET.toStack());
        player.setItemSlot(EquipmentSlot.CHEST, CombatSetup.MISTCLOAK.toStack());
        var data = player.getData(AllomancerAttachment.ALLOMANCY_DATA);

        data.setMistborn();
        for (Metal m : Metal.values()) {
            for (int i = 0; i < AllomancerData.MAX_STORAGE; i++) {
                data.incrementStored(m);
            }
        }

        return player;
    }

}
