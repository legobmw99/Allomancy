package com.legobmw99.allomancy.modules.extras.block;

import com.legobmw99.allomancy.Allomancy;
import com.legobmw99.allomancy.api.block.IAllomanticallyUsableBlock;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LeverBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.common.ToolType;

import javax.annotation.Nullable;
import java.util.List;

public class IronLeverBlock extends LeverBlock implements IAllomanticallyUsableBlock {

    public IronLeverBlock() {
        super(Block.Properties.of(Material.METAL).noCollission().strength(1.0F).harvestLevel(2).harvestTool(ToolType.PICKAXE));
    }

    @Override
    public boolean useAllomantically(BlockState state, Level world, BlockPos pos, Player playerIn, boolean isPush) {
        state = state.cycle(POWERED); // formerly cycle
        if (world.isClientSide) {
            return true;
        }
        if ((!isPush && state.getValue(POWERED)) || (isPush && !state.getValue(POWERED))) {

            world.setBlock(pos, state, 3);
            float f = state.getValue(POWERED) ? 0.6F : 0.5F;
            world.playSound(null, pos, SoundEvents.LEVER_CLICK, SoundSource.BLOCKS, 0.3F, f);
            this.updateNeighbors(state, world, pos);
            return true;

        }
        return false;
    }

    @Override
    public InteractionResult use(BlockState state, Level worldIn, BlockPos pos, Player player, InteractionHand handIn, BlockHitResult hit) {
        return InteractionResult.FAIL;
    }


    @Override
    public void appendHoverText(ItemStack stack, @Nullable BlockGetter worldIn, List<Component> tooltip, TooltipFlag flagIn) {
        super.appendHoverText(stack, worldIn, tooltip, flagIn);
        Component lore = Allomancy.addColorToText("block.allomancy.iron_activation.lore", ChatFormatting.GRAY);
        tooltip.add(lore);
    }

    private void updateNeighbors(BlockState state, Level world, BlockPos pos) {
        world.updateNeighborsAt(pos, this);
        world.updateNeighborsAt(pos.relative(getConnectedDirection(state).getOpposite()), this);
    }

}
