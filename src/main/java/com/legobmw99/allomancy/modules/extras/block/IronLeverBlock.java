package com.legobmw99.allomancy.modules.extras.block;

import com.legobmw99.allomancy.api.block.IAllomanticallyActivatedBlock;
import com.legobmw99.allomancy.setup.AllomancySetup;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.LeverBlock;
import net.minecraft.block.material.Material;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.common.ToolType;

import javax.annotation.Nullable;
import java.util.List;

public class IronLeverBlock extends LeverBlock implements IAllomanticallyActivatedBlock {

    public IronLeverBlock() {
        super(Block.Properties.of(Material.METAL).noCollission().strength(1.0F).harvestLevel(2).harvestTool(ToolType.PICKAXE));
    }

    @Override
    public boolean onBlockActivatedAllomantically(BlockState state, World world, BlockPos pos, PlayerEntity playerIn, boolean isPush) {
        state = state.cycle(POWERED); // formerly cycle
        if (world.isClientSide) {
            return true;
        }
        if ((!isPush && state.getValue(POWERED)) || (isPush && !state.getValue(POWERED))) {

            world.setBlock(pos, state, 3);
            float f = state.getValue(POWERED) ? 0.6F : 0.5F;
            world.playSound(null, pos, SoundEvents.LEVER_CLICK, SoundCategory.BLOCKS, 0.3F, f);
            this.updateNeighbors(state, world, pos);
            return true;

        }
        return false;
    }

    @Override
    public ActionResultType use(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {
        return ActionResultType.FAIL;
    }


    @Override
    public void appendHoverText(ItemStack stack, @Nullable IBlockReader worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        super.appendHoverText(stack, worldIn, tooltip, flagIn);
        ITextComponent lore = AllomancySetup.addColorToText("block.allomancy.iron_activation.lore", TextFormatting.GRAY);
        tooltip.add(lore);
    }

    private void updateNeighbors(BlockState state, World world, BlockPos pos) {
        world.updateNeighborsAt(pos, this);
        world.updateNeighborsAt(pos.relative(getConnectedDirection(state).getOpposite()), this);
    }

}
