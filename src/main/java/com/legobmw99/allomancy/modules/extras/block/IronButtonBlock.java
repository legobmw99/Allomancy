package com.legobmw99.allomancy.modules.extras.block;

import com.legobmw99.allomancy.api.block.IAllomanticallyUsableBlock;
import com.legobmw99.allomancy.util.ItemDisplay;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
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
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.phys.BlockHitResult;

import javax.annotation.Nullable;
import java.util.List;

public class IronButtonBlock extends ButtonBlock implements IAllomanticallyUsableBlock {

    public IronButtonBlock() {
        super(BlockSetType.IRON, 35, Block.Properties.of().noCollission().strength(1.0F));
    }

    @Override
    public boolean useAllomantically(BlockState state, Level level, BlockPos pos, Player player, boolean isPush) {
        if (state.getValue(POWERED) || level.isClientSide) {
            return true;
        } else if (isPush) {
            this.press(state, level, pos);
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
    public void appendHoverText(ItemStack stack, @Nullable BlockGetter worldIn, List<Component> tooltip, TooltipFlag flagIn) {
        super.appendHoverText(stack, worldIn, tooltip, flagIn);
        MutableComponent lore = ItemDisplay.addColorToText("block.allomancy.iron_activation.lore", ChatFormatting.GRAY);
        tooltip.add(lore);
    }

}
