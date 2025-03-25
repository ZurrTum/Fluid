package com.zurrtum.fluid.api.base;

import net.minecraft.block.FluidBlock;
import net.minecraft.fluid.FlowableFluid;
import net.minecraft.item.BucketItem;
import net.minecraft.util.Identifier;

public class FluidEntry {
    public FlowableFluid flowing = null;
    public FlowableFluid still = null;
    public BucketItem bucket = null;
    public FluidBlock block = null;
    public int tint = -1;
    public Identifier still_texture = null;
    public Identifier flowing_texture = null;
    public Identifier overlay_texture = null;
    public static final Identifier WATER_STILL = Identifier.ofVanilla("block/water_still");
    public static final Identifier WATER_FLOWING = Identifier.ofVanilla("block/water_flow");
    public static final Identifier WATER_OVERLAY = Identifier.ofVanilla("block/water_overlay");
    public static final Identifier LAVA_STILL = Identifier.of("fluid", "block/lava_still");
    public static final Identifier LAVA_FLOWING = Identifier.of("fluid", "block/lava_flow");
}
