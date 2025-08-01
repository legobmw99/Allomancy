package com.legobmw99.allomancy.modules.extras.block;

import com.legobmw99.allomancy.api.block.IAllomanticallyUsable;
import com.legobmw99.allomancy.modules.extras.ExtrasSetup;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.ButtonBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.capabilities.IBlockCapabilityProvider;
import org.jetbrains.annotations.Nullable;

import java.util.function.BiConsumer;

public class IronButtonBlock extends ButtonBlock {


    private final boolean activatedOnPush;

    public IronButtonBlock(boolean activatedOnPush, Properties props) {
        super(BlockSetType.IRON, 35, props);
        this.activatedOnPush = activatedOnPush;
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
                                  ServerLevel pLevel,
                                  BlockPos pPos,
                                  Explosion pExplosion,
                                  BiConsumer<ItemStack, BlockPos> pDropConsumer) {
    }

    @Override
    public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        if (!this.activatedOnPush) {
            return super.getShape(pState.cycle(POWERED), pLevel, pPos, pContext);
        }
        return super.getShape(pState, pLevel, pPos, pContext);
    }


    public static class AllomanticUseCapabilityProvider implements IBlockCapabilityProvider<IAllomanticallyUsable,
            Void> {

        @Override
        public IAllomanticallyUsable getCapability(Level level,
                                                   BlockPos pos,
                                                   BlockState state,
                                                   @Nullable BlockEntity blockEntity,
                                                   @Nullable Void context) {
            return ((player, isPush) -> {
                if (player instanceof ServerPlayer sp) {
                    ExtrasSetup.ALLOMANTICALLY_ACTIVATED_BLOCK_TRIGGER.get().trigger(sp, pos, isPush);
                }

                if (state.getValue(POWERED) || level.isClientSide()) {
                    return true;
                }

                IronButtonBlock block = (IronButtonBlock) state.getBlock();
                if (isPush == block.activatedOnPush) {
                    block.press(state, level, pos, player);
                    block.playSound(player, level, pos, false);
                    level.gameEvent(player, GameEvent.BLOCK_ACTIVATE, pos);
                    return true;
                } else {
                    return false;
                }
            });
        }
    }

}
