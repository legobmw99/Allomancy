package com.legobmw99.allomancy.block;

import com.legobmw99.allomancy.Allomancy;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.LeverBlock;
import net.minecraft.block.material.Material;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.translation.LanguageMap;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

public class IronLeverBlock extends LeverBlock implements IAllomanticallyActivatedBlock {

    public IronLeverBlock() {
        super(Block.Properties.create(Material.IRON).doesNotBlockMovement().hardnessAndResistance(1.0F));
        this.setRegistryName(Allomancy.MODID, "iron_lever");
    }

    @Override
    public boolean onBlockActivatedAllomantically(World worldIn, BlockPos pos, BlockState state, PlayerEntity playerIn,
                                                  boolean isPush) {
        state = state.cycle(POWERED);
        if (worldIn.isRemote) {
            return true;
        }
        if ((!isPush && isLeverPointedAway(state, playerIn)) || (isPush && !isLeverPointedAway(state, playerIn))) {

            worldIn.setBlockState(pos, state, 3);
            float f = state.get(POWERED) ? 0.6F : 0.5F;
            worldIn.playSound((PlayerEntity) null, pos, SoundEvents.BLOCK_LEVER_CLICK, SoundCategory.BLOCKS, 0.3F, f);
            this.updateNeighbors(state, worldIn, pos);
            return true;

        }
        return false;
    }

    /**
     * <strong>Currently not functional</strong> Determines if the lever is
     * facing away from the player
     *
     * @return true if the lever should be treated as facing away
     */
    public boolean isLeverPointedAway(BlockState state, PlayerEntity player) {
//		TODO: Maybe reconsider this one day
//		switch(state.getValue(FACING)){
//		case UP_Z: case UP_X: case DOWN_Z: case DOWN_X: //Lever is placed with the stick pointing SOUTH or EAST
//			return (player.getHorizontalFacing() == EnumFacing.SOUTH || player.getHorizontalFacing() == EnumFacing.EAST) ^ state.getValue(POWERED).booleanValue();
//		default: //Lever is facing up or down
//			return state.getValue(POWERED).booleanValue();
//		}
        return state.get(POWERED).booleanValue();

    }

    @Override
    public boolean onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {
        return false;
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable IBlockReader worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        super.addInformation(stack, worldIn, tooltip, flagIn);
        tooltip.add(new StringTextComponent(String.format(LanguageMap.getInstance().translateKey("block.allomancy.iron_lever.lore"))));
    }

    private void updateNeighbors(BlockState state, World world, BlockPos pos) {
        world.notifyNeighborsOfStateChange(pos, this);
        world.notifyNeighborsOfStateChange(pos.offset(getFacing(state).getOpposite()), this);
    }

}
