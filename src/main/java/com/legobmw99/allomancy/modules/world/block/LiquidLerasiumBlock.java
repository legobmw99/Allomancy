package com.legobmw99.allomancy.modules.world.block;

import com.legobmw99.allomancy.modules.consumables.ConsumeSetup;
import com.legobmw99.allomancy.util.AllomancyTags;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FlowingFluid;
import org.jetbrains.annotations.Nullable;

public class LiquidLerasiumBlock extends LiquidBlock {
    public LiquidLerasiumBlock(FlowingFluid fluid, Properties properties) {
        super(fluid, properties);
    }

    @Override
    public ItemStack pickupBlock(@Nullable Player player, LevelAccessor level, BlockPos pos, BlockState state) {
        if (player != null && player.getAbilities().instabuild) {
            return super.pickupBlock(player, level, pos, state);
        }
        return ItemStack.EMPTY;
    }

    @Override
    protected void entityInside(BlockState state, Level level, BlockPos pos, Entity entity) {
        super.entityInside(state, level, pos, entity);
        if (entity instanceof ItemEntity item) {
            if (item.getItem().is(AllomancyTags.LERASIUM_CONVERSION)) {
                item.setItem(new ItemStack(ConsumeSetup.LERASIUM_NUGGET.get(), item.getItem().getCount()));
                if (!level.isClientSide()) {
                    level.playSound(null, pos.getX(), pos.getY(), pos.getZ(), SoundEvents.ENCHANTMENT_TABLE_USE,
                                    SoundSource.BLOCKS);
                }
                for (int i = 0; i < 16; i++) {
                    float f4 = Mth.cos(i) * 0.3f;
                    float f5 = Mth.sin(i) * 0.3f;
                    level.addParticle(ParticleTypes.ENCHANT, entity.getX() + f4, entity.getY() + 0.4f,
                                      entity.getZ() + f5, 0.0, 0.0, 0.0);
                }
            }
        }
    }
}
