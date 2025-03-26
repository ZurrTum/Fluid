package com.zurrtum.fluid.mixin.client;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.zurrtum.fluid.impl.ColorParticleEffect;
import com.zurrtum.fluid.impl.DataRegistryImpl;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.WaterFluid;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(WaterFluid.class)
public class WaterFluidMixin {
    @WrapOperation(
        method = "randomDisplayTick(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/fluid/FluidState;Lnet/minecraft/util/math/random/Random;)V",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;addParticleClient(Lnet/minecraft/particle/ParticleEffect;DDDDDD)V")
    )
    public void addParticle(
        World world,
        ParticleEffect parameters,
        double x, double y, double z,
        double velocityX, double velocityY, double velocityZ,
        Operation<Void> original,
        @Local(argsOnly = true) FluidState state
    ) {
        if (!world.isClient()) return;
        int tint = DataRegistryImpl.TINT_LIST.getOrDefault(state.getFluid(), -1);
        original.call(world, ColorParticleEffect.create(parameters, tint), x, y, z, velocityX, velocityY, velocityZ);
    }
}
