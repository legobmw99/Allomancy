package com.legobmw99.allomancy.modules.extras.block;

import com.legobmw99.allomancy.api.block.IAllomanticallyUsableBlock;
import com.legobmw99.allomancy.modules.extras.ExtrasSetup;
import com.legobmw99.allomancy.util.ItemDisplay;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.ButtonBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.List;
import java.util.function.BiConsumer;

public class IronButtonBlock extends ButtonBlock implements IAllomanticallyUsableBlock {


    private final boolean activatedOnPush;

    public IronButtonBlock(boolean activatedOnPush) {
        super(BlockSetType.IRON, 35, Block.Properties.of().noCollission().strength(1.0F));
        this.activatedOnPush = activatedOnPush;
    }

    @Override
    public boolean useAllomantically(BlockState state, Level level, BlockPos pos, Player player, boolean isPush) {
        if (player instanceof ServerPlayer sp) {
            ExtrasSetup.ALLOMANTICALLY_ACTIVATED_BLOCK_TRIGGER.get().trigger(sp, pos, isPush);
        }

        if (state.getValue(POWERED) || level.isClientSide()) {
            return true;
        } else if (isPush == this.activatedOnPush) {
            this.press(state, level, pos, player);
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
    protected InteractionResult useWithoutItem(BlockState pState,
                                               Level pLevel,
                                               BlockPos pPos,
                                               Player pPlayer,
                                               BlockHitResult pHitResult) {
        return InteractionResult.FAIL;
    }

    @Override
    protected void onExplosionHit(BlockState pState,
                                  Level pLevel,
                                  BlockPos pPos,
                                  Explosion pExplosion,
                                  BiConsumer<ItemStack, BlockPos> pDropConsumer) {
        super.onExplosionHit(pState, pLevel, pPos, pExplosion, pDropConsumer);
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
                                Item.TooltipContext ctx,
                                List<Component> tooltip,
                                TooltipFlag flagIn) {
        super.appendHoverText(stack, ctx, tooltip, flagIn);
        MutableComponent lore =
                ItemDisplay.addColorToText("block.allomancy.iron_activation.lore", ChatFormatting.GRAY);
        tooltip.add(lore);
    }
}
