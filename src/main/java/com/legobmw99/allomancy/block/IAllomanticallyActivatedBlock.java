package com.legobmw99.allomancy.block;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface IAllomanticallyActivatedBlock {

	boolean onBlockActivatedAllomantically(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, boolean isPush);
}
