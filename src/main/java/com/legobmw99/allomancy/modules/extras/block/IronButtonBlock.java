package com.legobmw99.allomancy.modules.extras.block;

import com.legobmw99.allomancy.api.block.IAllomanticallyUsableBlock;
import com.legobmw99.allomancy.util.ItemDisplay;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.ButtonBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.Nullable;
import java.util.List;

public class IronButtonBlock extends ButtonBlock implements IAllomanticallyUsableBlock {


    private final boolean activatedOnPush;

    public IronButtonBlock(boolean activatedOnPush) {
        super(Block.Properties.of().noCollission().strength(1.0F), BlockSetType.IRON, 35, false);
        this.activatedOnPush = activatedOnPush;
    }

    @Override
    public boolean useAllomantically(BlockState state, Level level, BlockPos pos, Player player, boolean isPush) {


        if (state.getValue(POWERED) || level.isClientSide()) {
            return true;
        } else if (isPush == this.activatedOnPush) {
            this.press(state, level, pos);
            this.playSound(null, level, pos, true);
            level.gameEvent(player, GameEvent.BLOCK_ACTIVATE, pos);
            return true;
        } else {
            return false;
        }
    }

    public boolean activatedOnPush() {
        return this.activatedOnPush;
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
    public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        if (!this.activatedOnPush) {
            return super.getShape(pState.cycle(POWERED), pLevel, pPos, pContext);
        }
        return super.getShape(pState, pLevel, pPos, pContext);
    }

    @Override
    public void appendHoverText(ItemStack stack,
                                @Nullable BlockGetter worldIn,
                                List<Component> tooltip,
                                TooltipFlag flagIn) {
        super.appendHoverText(stack, worldIn, tooltip, flagIn);
        MutableComponent lore =
                ItemDisplay.addColorToText("block.allomancy.iron_activation.lore", ChatFormatting.GRAY);
        tooltip.add(lore);
    }

}