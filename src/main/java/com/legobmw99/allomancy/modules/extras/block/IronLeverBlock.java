package com.legobmw99.allomancy.modules.extras.block;

import com.legobmw99.allomancy.api.block.IAllomanticallyUsable;
import com.legobmw99.allomancy.modules.extras.ExtrasSetup;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.LeverBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.neoforge.capabilities.IBlockCapabilityProvider;
import org.jetbrains.annotations.Nullable;

import java.util.function.BiConsumer;

public class IronLeverBlock extends LeverBlock {

    public IronLeverBlock(Properties props) {
        super(props);
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
                if (level.isClientSide()) {
                    return true;
                }
                if (isPush == state.getValue(POWERED)) {
                    ((LeverBlock) state.getBlock()).pull(state, level, pos, player);
                    float f = state.getValue(POWERED) ? 0.6F : 0.5F;
                    level.playSound(null, pos, SoundEvents.LEVER_CLICK, SoundSource.BLOCKS, 0.3F, f);
                    level.gameEvent(player,
                                    state.getValue(POWERED) ? GameEvent.BLOCK_ACTIVATE : GameEvent.BLOCK_DEACTIVATE,
                                    pos);
                    return true;

                }
                return false;
            });
        }
    }

}
