package com.legobmw99.allomancy.modules.world.block;

import com.legobmw99.allomancy.modules.world.WorldSetup;
import com.legobmw99.allomancy.modules.world.recipe.InvestingRecipe;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.InsideBlockEffectApplier;
import net.minecraft.world.entity.LivingEntity;
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
    public ItemStack pickupBlock(@Nullable LivingEntity entity, LevelAccessor level, BlockPos pos, BlockState state) {
        if (entity instanceof Player p && p.getAbilities().instabuild) {
            return super.pickupBlock(entity, level, pos, state);
        }
        return ItemStack.EMPTY;
    }


    @Override
    protected void entityInside(BlockState state,
                                Level level,
                                BlockPos pos,
                                Entity entity,
                                InsideBlockEffectApplier eff,
                                boolean intersects) {
        super.entityInside(state, level, pos, entity, eff, intersects);
        if (level instanceof ServerLevel serverLevel && entity instanceof ItemEntity item) {
            var input = new InvestingRecipe.InvestingWrapper(item.getItem());
            serverLevel
                    .recipeAccess()
                    .getRecipeFor(WorldSetup.INVESTING_RECIPE.get(), input, serverLevel)
                    .ifPresent(recipe -> {
                        item.setItem(recipe.value().assemble(input, serverLevel.registryAccess()));
                        serverLevel.playSound(null, pos.getX(), pos.getY(), pos.getZ(),
                                              SoundEvents.ENCHANTMENT_TABLE_USE, SoundSource.BLOCKS);
                        for (int i = 0; i < 16; i++) {
                            float f4 = Mth.cos(i) * 0.3f;
                            float f5 = Mth.sin(i) * 0.3f;
                            serverLevel.sendParticles(ParticleTypes.ENCHANT, entity.getX() + f4, entity.getY() + 0.4f,
                                                      entity.getZ() + f5, 1, 0.0, 0.0, 0.0, 0.0);
                        }
                    });
        }
    }
}
