package com.legobmw99.allomancy.modules.extras.block;

import com.legobmw99.allomancy.api.block.IAllomanticallyUsableBlock;
import com.legobmw99.allomancy.util.ItemDisplay;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LeverBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.BlockHitResult;

import javax.annotation.Nullable;
import java.util.List;

public class IronLeverBlock extends LeverBlock implements IAllomanticallyUsableBlock {

    public IronLeverBlock() {
        super(Block.Properties.of().noCollission().strength(1.0F));
    }

    @Override
    public boolean useAllomantically(BlockState state, Level level, BlockPos pos, Player player, boolean isPush) {

        if (level.isClientSide()) {
            return true;
        }
        if (isPush == state.getValue(POWERED)) {
            BlockState blockstate = this.pull(state, level, pos);
            float f = blockstate.getValue(POWERED) ? 0.6F : 0.5F;
            level.playSound(null, pos, SoundEvents.LEVER_CLICK, SoundSource.BLOCKS, 0.3F, f);
            level.gameEvent(player,
                            blockstate.getValue(POWERED) ? GameEvent.BLOCK_ACTIVATE : GameEvent.BLOCK_DEACTIVATE,
                            pos);
            return true;

        }
        return false;
    }

    @Override
    public InteractionResult use(BlockState state,
                                 Level worldIn,
                                 BlockPos pos,
                                 Player player,
                                 InteractionHand handIn,
                                 BlockHitResult hit) {
        return InteractionResult.FAIL;
    }


    @Override
    public void appendHoverText(ItemStack stack,
                                @Nullable BlockGetter worldIn,
                                List<Component> tooltip,
                                TooltipFlag flagIn) {
        super.appendHoverText(stack, worldIn, tooltip, flagIn);
        Component lore = ItemDisplay.addColorToText("block.allomancy.iron_activation.lore", ChatFormatting.GRAY);
        tooltip.add(lore);
    }

    private void updateNeighbors(BlockState state, Level world, BlockPos pos) {
        world.updateNeighborsAt(pos, this);
        world.updateNeighborsAt(pos.relative(getConnectedDirection(state).getOpposite()), this);
    }

}