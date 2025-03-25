package com.zurrtum.fluid.api;

import com.zurrtum.fluid.api.base.FluidEntry;
import com.zurrtum.fluid.impl.DataRegistryImpl;
import net.minecraft.fluid.FlowableFluid;
import net.minecraft.fluid.Fluid;

public interface DataRegistry {
    static void registerWaterState(Fluid fluid) {
        DataRegistryImpl.registerWaterState(fluid);
    }

    static void registerWaterTag(Fluid fluid) {
        DataRegistryImpl.registerWaterTag(fluid);
    }

    static void registerLavaState(Fluid fluid) {
        DataRegistryImpl.registerLavaState(fluid);
    }

    static void registerLavaTag(Fluid fluid) {
        DataRegistryImpl.registerLavaTag(fluid);
    }

    static void registerFluid(FluidEntry entry) {
        DataRegistryImpl.registerFluid(entry);
    }

    static void registerTint(FlowableFluid fluid, int tint) {
        DataRegistryImpl.registerTint(fluid, tint);
    }
}
