package com.legobmw99.allomancy.block;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * This interface can be used to signify if a block should react to being pushed
 * or pulled, rather than moving the pusher
 * 
 * @see BlockIronLever
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
	boolean onBlockActivatedAllomantically(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, boolean isPush);
}
