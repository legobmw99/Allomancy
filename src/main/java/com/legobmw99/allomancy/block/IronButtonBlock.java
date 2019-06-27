package com.legobmw99.allomancy.block;

import com.legobmw99.allomancy.Allomancy;
import net.minecraft.block.AbstractButtonBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

public class IronButtonBlock extends AbstractButtonBlock implements IAllomanticallyActivatedBlock {

    public IronButtonBlock() {
        super(false, Block.Properties.create(Material.IRON).doesNotBlockMovement().hardnessAndResistance(1.0F));
        this.setRegistryName(Allomancy.MODID, "iron_button");
    }

    @Override
    public boolean onBlockActivatedAllomantically(BlockState state, BlockPos pos, World worldIn, PlayerEntity player, boolean isPush) {
        if (state.get(POWERED) || worldIn.isRemote) {
            return true;
        } else if(isPush) {
            worldIn.setBlockState(pos, state.with(POWERED, Boolean.valueOf(true)), 3);
            this.playSound(player, worldIn, pos, true);
            this.updateNeighbors(state, worldIn, pos);
            worldIn.getPendingBlockTicks().scheduleTick(pos, this, this.tickRate(worldIn));
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {
        return false;
    }


    @Override
    protected SoundEvent getSoundEvent(boolean on) {
        return on ? SoundEvents.BLOCK_METAL_PRESSURE_PLATE_CLICK_ON : SoundEvents.BLOCK_METAL_PRESSURE_PLATE_CLICK_OFF;
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable IBlockReader worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        super.addInformation(stack, worldIn, tooltip, flagIn);
        ITextComponent lore = new TranslationTextComponent("block.allomancy.iron_activation.lore");
        lore.setStyle(lore.getStyle().setColor(TextFormatting.GRAY));
        tooltip.add(lore);
    }


    private void updateNeighbors(BlockState state, World world, BlockPos pos) {
        world.notifyNeighborsOfStateChange(pos, this);
        world.notifyNeighborsOfStateChange(pos.offset(getFacing(state).getOpposite()), this);
    }
}
