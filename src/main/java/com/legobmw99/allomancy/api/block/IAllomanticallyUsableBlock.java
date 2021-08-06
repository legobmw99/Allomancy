package com.legobmw99.allomancy.api.block;

import com.legobmw99.allomancy.modules.extras.block.IronLeverBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

/**
 * This interface can be used to signify if a block should react to being pushed
 * or pulled, rather than moving the pusher
 *
 * @author legobmw99
 * @see IronLeverBlock
 */
public interface IAllomanticallyUsableBlock {

    /**
     * Called when the block is steelpushed or ironpulled
     *
     * @param isPush whether or not the activation is Steel
     * @return whether or not the block was activated
     */
    boolean useAllomantically(BlockState state, Level world, BlockPos pos, Player playerIn, boolean isPush);
}
