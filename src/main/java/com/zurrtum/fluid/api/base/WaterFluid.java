package com.zurrtum.fluid.api.base;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.FluidBlock;
import net.minecraft.block.FluidFillable;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.Item;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.WorldAccess;

public abstract class WaterFluid extends net.minecraft.fluid.WaterFluid {
    private final FluidEntry entry;
    WaterFluid(FluidEntry entry) {
        this.entry = entry;
    }

    @Override
    public Fluid getFlowing() {
        return entry.flowing;
    }

    @Override
    public Fluid getStill() {
        return entry.still;
    }

    @Override
    public Item getBucketItem() {
        return entry.bucket;
    }

    @Override
    public BlockState toBlockState(FluidState state) {
        return entry.block.getDefaultState().with(FluidBlock.LEVEL, getBlockStateLevel(state));
    }

    @Override
    public boolean matchesType(Fluid fluid) {
        return fluid == entry.still || fluid == entry.flowing;
    }

    public static class Flowing extends WaterFluid {
        public Flowing(FluidEntry entry) {
            super(entry);
        }

        @Override
        protected void appendProperties(StateManager.Builder<Fluid, FluidState> builder) {
            super.appendProperties(builder);
            builder.add(LEVEL);
        }

        @Override
        public int getLevel(FluidState state) {
            return state.get(LEVEL);
        }

        @Override
        public boolean isStill(FluidState state) {
            return false;
        }
    }

    public static class Still extends WaterFluid {
        public Still(FluidEntry entry) {
            super(entry);
        }

        @Override
        public int getLevel(FluidState state) {
            return 8;
        }

        @Override
        public boolean isStill(FluidState state) {
            return true;
        }
    }

    @Override
    protected void flow(WorldAccess world, BlockPos pos, BlockState state, Direction direction, FluidState fluidState) {
        if (state.getBlock() instanceof FluidFillable fluidFillable) {
            fluidFillable.tryFillWithFluid(world, pos, state, fluidState);
        } else if (state.getFluidState().isEmpty()){
            world.setBlockState(pos, fluidState.getBlockState(), Block.NOTIFY_ALL);
        }
    }

    @Override
    protected boolean isInfinite(ServerWorld world) {
        return false;
    }

    @Override
    public boolean canBeReplacedWith(FluidState state, BlockView world, BlockPos pos, Fluid fluid, Direction direction) {
        return false;
    }
}
