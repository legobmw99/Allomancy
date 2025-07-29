package com.legobmw99.allomancy.modules.world.block;

import com.legobmw99.allomancy.modules.world.WorldSetup;
import com.legobmw99.allomancy.modules.world.recipe.InvestingRecipe;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FlowingFluid;

import java.util.function.Supplier;

public class LiquidLerasiumBlock extends LiquidBlock {
    public LiquidLerasiumBlock(Supplier<FlowingFluid> fluid, Properties properties) {
        super(fluid, properties);
    }


    @Override
    public ItemStack pickupBlock(LevelAccessor level, BlockPos pos, BlockState state) {

        return ItemStack.EMPTY;
    }


    @Override
    public void entityInside(BlockState state, Level level, BlockPos pos, Entity entity) {
        super.entityInside(state, level, pos, entity);
        if (entity instanceof ItemEntity item) {
            var input = new InvestingRecipe.InvestingWrapper(item.getItem());
            level
                    .getRecipeManager()
                    .getRecipeFor(WorldSetup.INVESTING_RECIPE.get(), input, level)
                    .ifPresent(recipe -> {
                        item.setItem(recipe.assemble(input, level.registryAccess()));
                        if (!level.isClientSide()) {
                            level.playSound(null, pos, SoundEvents.ENCHANTMENT_TABLE_USE, SoundSource.BLOCKS);
                        }
                        for (int i = 0; i < 16; i++) {
                            float f4 = Mth.cos(i) * 0.3f;
                            float f5 = Mth.sin(i) * 0.3f;
                            level.addParticle(ParticleTypes.ENCHANT, entity.getX() + f4, entity.getY() + 0.4f,
                                              entity.getZ() + f5, 0.0, 0.0, 0.0);
                        }
                    });
        }
    }
}