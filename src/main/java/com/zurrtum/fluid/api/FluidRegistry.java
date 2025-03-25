package com.zurrtum.fluid.api;

import com.zurrtum.fluid.api.base.FluidEntry;
import com.zurrtum.fluid.impl.FluidRegistryImpl;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.BucketItem;
import net.minecraft.util.Identifier;

public interface FluidRegistry {
    static FluidEntry registerWater(Identifier id, int tint) {
        return FluidRegistryImpl.registerWater(id, tint);
    }

    static FluidEntry registerLava(Identifier id, int tint) {
        return FluidRegistryImpl.registerLava(id, tint);
    }

    static BucketItem registerBucket(Fluid fluid) {
        return FluidRegistryImpl.registerBucket(fluid);
    }

    static BucketItem registerCell(Fluid fluid) {
        return FluidRegistryImpl.registerCell(fluid);
    }
}
