/*
 * This file is part of RebornCore, licensed under the MIT License (MIT).
 *
 * Copyright (c) 2025 TeamReborn
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.zurrtum.fluid.mixin.client;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.zurrtum.fluid.impl.ColorParticleEffect;
import com.zurrtum.fluid.impl.DataRegistryImpl;
import net.minecraft.entity.Entity;
import net.minecraft.fluid.FluidState;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Entity.class)
public class EntityMixin {
	@Shadow
	private World world;
	@Shadow
	private Box boundingBox;
	@Unique
	private int tint;

	@Inject(method = "onSwimmingStart()V", at = @At("HEAD"))
	private void refreshParticleColor(CallbackInfo ci) {
		if (!world.isClient) return;
		Box box = boundingBox.contract(0.001);
		int minX = MathHelper.floor(box.minX);
		int maxX = MathHelper.ceil(box.maxX);
		int y = MathHelper.floor(box.minY);
		int minZ = MathHelper.floor(box.minZ);
		int maxZ = MathHelper.ceil(box.maxZ);
		BlockPos.Mutable mutable = new BlockPos.Mutable();
		for(int x = minX; x < maxX; ++x) {
			for(int z = minZ; z < maxZ; ++z) {
				mutable.set(x, y, z);
				FluidState fluidState = world.getFluidState(mutable);
				if (fluidState.isIn(FluidTags.WATER)) {
					tint = DataRegistryImpl.TINT_LIST.getOrDefault(fluidState.getFluid(), -1);
					return;
				}
			}
		}
		tint = -1;
	}

	@WrapOperation(
		method = "onSwimmingStart()V",
		at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;addParticleClient(Lnet/minecraft/particle/ParticleEffect;DDDDDD)V")
	)
	private void addParticle(
		World world,
		ParticleEffect parameters,
		double x, double y, double z,
		double velocityX, double velocityY, double velocityZ,
		Operation<Void> original
	) {
		if (!world.isClient) return;
		original.call(world, ColorParticleEffect.create(parameters, tint), x, y, z, velocityX, velocityY, velocityZ);
	}
}
