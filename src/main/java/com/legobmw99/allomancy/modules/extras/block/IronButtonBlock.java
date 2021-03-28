package com.legobmw99.allomancy.modules.extras.block;

import com.legobmw99.allomancy.Allomancy;
import com.legobmw99.allomancy.api.block.IAllomanticallyUsableBlock;
import net.minecraft.block.AbstractButtonBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.common.ToolType;

import javax.annotation.Nullable;
import java.util.List;

public class IronButtonBlock extends AbstractButtonBlock implements IAllomanticallyUsableBlock {

    public IronButtonBlock() {
        super(false, Block.Properties.of(Material.METAL).noCollission().strength(1.0F).harvestLevel(2).harvestTool(ToolType.PICKAXE));
    }

    @Override
    public boolean useAllomantically(BlockState state, World world, BlockPos pos, PlayerEntity player, boolean isPush) {
        if (state.getValue(POWERED) || world.isClientSide) {
            return true;
        } else if (isPush) {
            world.setBlock(pos, state.setValue(POWERED, true), 3);
            this.playSound(player, world, pos, true);
            this.updateNeighbors(state, world, pos);
            world.getBlockTicks().scheduleTick(pos, this, 20);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public ActionResultType use(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {
        return ActionResultType.FAIL;
    }


    @Override
    protected SoundEvent getSound(boolean on) {
        return on ? SoundEvents.METAL_PRESSURE_PLATE_CLICK_ON : SoundEvents.METAL_PRESSURE_PLATE_CLICK_OFF;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable IBlockReader worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        super.appendHoverText(stack, worldIn, tooltip, flagIn);
        IFormattableTextComponent lore = Allomancy.addColorToText("block.allomancy.iron_activation.lore", TextFormatting.GRAY);
        tooltip.add(lore);
    }


    private void updateNeighbors(BlockState state, World world, BlockPos pos) {
        world.updateNeighborsAt(pos, this);
        world.updateNeighborsAt(pos.relative(getConnectedDirection(state).getOpposite()), this);
    }
}
