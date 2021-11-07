package com.legobmw99.allomancy.modules.extras.block;

import com.legobmw99.allomancy.Allomancy;
import com.legobmw99.allomancy.api.block.IAllomanticallyUsableBlock;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.ButtonBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.BlockHitResult;

import javax.annotation.Nullable;
import java.util.List;

public class IronButtonBlock extends ButtonBlock implements IAllomanticallyUsableBlock {

    public IronButtonBlock() {
        super(false, Block.Properties.of(Material.METAL).noCollission().strength(1.0F));
    }

    @Override
    public boolean useAllomantically(BlockState state, Level world, BlockPos pos, Player player, boolean isPush) {
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
    public InteractionResult use(BlockState state, Level worldIn, BlockPos pos, Player player, InteractionHand handIn, BlockHitResult hit) {
        return InteractionResult.FAIL;
    }


    @Override
    protected SoundEvent getSound(boolean on) {
        return on ? SoundEvents.METAL_PRESSURE_PLATE_CLICK_ON : SoundEvents.METAL_PRESSURE_PLATE_CLICK_OFF;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable BlockGetter worldIn, List<Component> tooltip, TooltipFlag flagIn) {
        super.appendHoverText(stack, worldIn, tooltip, flagIn);
        MutableComponent lore = Allomancy.addColorToText("block.allomancy.iron_activation.lore", ChatFormatting.GRAY);
        tooltip.add(lore);
    }


    private void updateNeighbors(BlockState state, Level world, BlockPos pos) {
        world.updateNeighborsAt(pos, this);
        world.updateNeighborsAt(pos.relative(getConnectedDirection(state).getOpposite()), this);
    }
}
