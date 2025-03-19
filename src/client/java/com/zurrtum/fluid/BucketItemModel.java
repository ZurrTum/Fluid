package com.zurrtum.fluid;

import com.mojang.serialization.MapCodec;
import net.minecraft.fluid.Fluid;
import net.minecraft.util.Identifier;

import java.util.List;

public class BucketItemModel extends FluidItemModel.Unbaked {
    public static final MapCodec<BucketItemModel> CODEC = getCodec(BucketItemModel::new);
    public static final Identifier BUCKET_BASE = Identifier.of(FluidMod.MOD_ID, "item/bucket_base");
    public static final Identifier BUCKET_BACKGROUND = Identifier.of(FluidMod.MOD_ID, "item/bucket_background");
    public BucketItemModel(Fluid fluid) {
        super(BUCKET_BACKGROUND, List.of(BUCKET_BASE), fluid);
    }

    @Override
    public MapCodec<BucketItemModel> getCodec() {
        return CODEC;
    }
}
