package com.zurrtum.fluid.mixin.client;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.zurrtum.fluid.impl.ColorParticleEffect;
import com.zurrtum.fluid.impl.DataRegistryImpl;
import net.minecraft.block.PointedDripstoneBlock;
import net.minecraft.fluid.Fluid;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(PointedDripstoneBlock.class)
public class PointedDripstoneBlockMixin {
    @WrapOperation(
        method = "createParticle(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;Lnet/minecraft/fluid/Fluid;)V",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;addParticleClient(Lnet/minecraft/particle/ParticleEffect;DDDDDD)V")
    )
    private static void addParticle(World world, ParticleEffect parameters, double x, double y, double z, double velocityX, double velocityY, double velocityZ, Operation<Void> original, @Local(argsOnly = true) Fluid fluid) {
        if (!world.isClient) return;
        original.call(world, ColorParticleEffect.create(parameters, DataRegistryImpl.TINT_LIST.getOrDefault(fluid, -1)), x, y, z, velocityX, velocityY, velocityZ);
    }
}
