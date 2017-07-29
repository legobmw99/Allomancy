package com.legobmw99.allomancy.block;

import java.util.List;

import javax.annotation.Nullable;

import com.legobmw99.allomancy.Allomancy;
import com.legobmw99.allomancy.util.AllomancyCapability;

import net.minecraft.block.BlockLever;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockIronLever extends BlockLever implements IAllomanticallyActivatedBlock {

	public BlockIronLever() {
		super();
		this.setHarvestLevel("pickaxe", 2);
		this.setRegistryName(Allomancy.MODID,"iron_lever");
		this.setUnlocalizedName("iron_lever");
		this.setHardness(1.0F);
	}
	
	@Override
	public boolean onBlockActivatedAllomantically(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, boolean isPush){
		if (worldIn.isRemote) {
			return true;
		}
		if ((!isPush && isLeverPointedAway(state,playerIn)) || 
			(isPush && !isLeverPointedAway(state,playerIn))) {
			state = state.cycleProperty(POWERED);
			worldIn.setBlockState(pos, state, 3);
			float f = ((Boolean) state.getValue(POWERED)).booleanValue() ? 0.6F : 0.5F;
			worldIn.playSound((EntityPlayer) null, pos, SoundEvents.BLOCK_LEVER_CLICK, SoundCategory.BLOCKS, 0.3F,f);
			worldIn.notifyNeighborsOfStateChange(pos, this, false);
			EnumFacing enumfacing = ((BlockLever.EnumOrientation) state.getValue(FACING)).getFacing();
			worldIn.notifyNeighborsOfStateChange(pos.offset(enumfacing.getOpposite()), this, false);
			return true;
		}
		return false;
	}
	
	public boolean isLeverPointedAway(IBlockState state,EntityPlayer player){
//		TODO: Maybe reconsider this one day
//		switch(state.getValue(FACING)){
//		case UP_Z: case UP_X: case DOWN_Z: case DOWN_X: //Lever is placed with the stick pointing SOUTH or EAST
//			return (player.getHorizontalFacing() == EnumFacing.SOUTH || player.getHorizontalFacing() == EnumFacing.EAST) ^ state.getValue(POWERED).booleanValue();
//		default: //Lever is facing up or down
//			return state.getValue(POWERED).booleanValue();
//		}
		return state.getValue(POWERED).booleanValue();

	}

	@Override
	public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn,
			EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		return false;
	}
	
	@Override
    public void addInformation(ItemStack stack, @Nullable World playerIn, List<String> tooltip, ITooltipFlag advanced){
		tooltip.add("\u00A77" + I18n.translateToLocal("tile.iron_lever.lore"));
	}
	

}
