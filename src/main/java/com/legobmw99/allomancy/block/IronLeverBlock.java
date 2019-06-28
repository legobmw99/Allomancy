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
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
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
    public boolean onBlockActivatedAllomantically(BlockState state, World world, BlockPos pos, PlayerEntity playerIn,
                                                  boolean isPush) {
        state = state.cycle(POWERED);
        if (world.isRemote) {
            return true;
        }
        if ((!isPush && state.get(POWERED)) || (isPush && !state.get(POWERED))) {

            world.setBlockState(pos, state, 3);
            float f = state.get(POWERED) ? 0.6F : 0.5F;
            world.playSound((PlayerEntity) null, pos, SoundEvents.BLOCK_LEVER_CLICK, SoundCategory.BLOCKS, 0.3F, f);
            this.updateNeighbors(state, world, pos);
            return true;

        }
        return false;
    }

    @Override
    public boolean onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {
        return false;
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
