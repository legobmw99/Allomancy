package com.legobmw99.allomancy.block;

import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * This interface can be used to signify if a block should react to being pushed
 * or pulled, rather than moving the pusher
 * 
 * @see IronLeverBlock
 * @author legobmw99
 *
 */
public interface IAllomanticallyActivatedBlock {

	/**
	 * Called when the block is steelpushed or ironpulled
	 * 
	 * @param isPush
	 *            whether or not the activation is Steel
	 * @return whether or not the block was activated
	 */
	boolean onBlockActivatedAllomantically(World worldIn, BlockPos pos, BlockState state, PlayerEntity playerIn, boolean isPush);
}
