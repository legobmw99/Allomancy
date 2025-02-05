package com.legobmw99.allomancy.modules.world.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.neoforged.neoforge.fluids.BaseFlowingFluid;

public class LerasiumFluid extends BaseFlowingFluid.Source {

    public LerasiumFluid(Properties props) {
        super(props);
    }


    @Override
    protected void spread(ServerLevel level, BlockPos pos, BlockState blockState, FluidState fluidState) {

    }

    @Override
    protected int getSlopeFindDistance(LevelReader level) {
        return 0;
    }

    @Override
    protected boolean canBeReplacedWith(FluidState state,
                                        BlockGetter level,
                                        BlockPos pos,
                                        Fluid fluid,
                                        Direction direction) {
        return false;
    }

    @Override
    public int getTickDelay(LevelReader level) {
        return 0;
    }

    @Override
    protected boolean isRandomlyTicking() {
        return false;
    }

    @Override
    protected void createFluidStateDefinition(StateDefinition.Builder<Fluid, FluidState> builder) {
        super.createFluidStateDefinition(builder);
        builder.add(LEVEL);
    }
}
