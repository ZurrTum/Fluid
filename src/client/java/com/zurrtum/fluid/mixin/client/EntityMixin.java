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
import com.zurrtum.fluid.FluidEntry;
import com.zurrtum.fluid.FluidMod;
import com.zurrtum.fluid.FluidRegistry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.particle.Particle;
import net.minecraft.entity.Entity;
import net.minecraft.fluid.FluidState;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
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
	private float particleRed = -1;
	@Unique
	private float particleGreen = -1;
	@Unique
	private float particleBlue = -1;

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
					FluidEntry entry = FluidRegistry.FLUID.get(fluidState.getFluid());
					if (entry != null) {
						particleRed = (entry.tint >> 16 & 0xFF) / 255.0F;
						particleGreen = (entry.tint >> 8 & 0xFF) / 255.0F;
						particleBlue = (entry.tint & 0xFF) / 255.0F;
					} else {
						particleRed = -1;
					}
					return;
				}
			}
		}
	}

	@WrapOperation(method = "onSwimmingStart()V", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;addParticleClient(Lnet/minecraft/particle/ParticleEffect;DDDDDD)V"))
	private void addParticle(World world, ParticleEffect parameters, double x, double y, double z, double velocityX, double velocityY, double velocityZ, Operation<Void> original) {
		if (!world.isClient) return;
		if (particleRed == -1) {
			original.call(world, parameters, x, y, z, velocityX, velocityY, velocityZ);
			return;
		}

		if (parameters == ParticleTypes.BUBBLE) {
			parameters = FluidMod.BUBBLE;
		} else if (parameters == ParticleTypes.SPLASH) {
			parameters = FluidMod.SPLASH;
		}
		Particle particle = MinecraftClient.getInstance().particleManager.addParticle(parameters, x, y, z, velocityX, velocityY, velocityZ);
		if (particle != null) {
			particle.setColor(particleRed, particleGreen, particleBlue);
		}
	}
}
