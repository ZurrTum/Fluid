package com.zurrtum.fluid.mixin.client;

import com.zurrtum.fluid.impl.NextParticleProvider;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.world.ClientWorld;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(targets = "net.minecraft.client.particle.BlockLeakParticle$DrippingLava")
public abstract class ParticleDrippingLavaMixin extends Particle implements NextParticleProvider {
    protected ParticleDrippingLavaMixin(ClientWorld world, double x, double y, double z) {
        super(world, x, y, z);
    }

    @Unique
    private float particleRed = -1;
    @Unique
    private float particleGreen = -1;
    @Unique
    private float particleBlue = -1;
    @Unique
    private float diffRed = -1;
    @Unique
    private float diffGreen = -1;
    @Unique
    private float diffBlue = -1;

    @Override
    public void setColor(float red, float green, float blue) {
        particleRed = Math.min(red + 0.4F, 1F);
        particleGreen = Math.min(green + 0.4F, 1F);
        particleBlue = Math.min(blue + 0.4F, 1F);
        diffRed = particleRed - red;
        diffGreen = particleGreen - green;
        diffBlue = particleBlue - blue;
        super.setColor(particleRed, particleGreen, particleBlue);
    }

    @Inject(method = "updateAge", at = @At("TAIL"))
    private void updateAge(CallbackInfo ci) {
        if (particleRed != -1) {
            float i = (float) (40 - maxAge) / 40;
            red = particleRed - i * diffRed;
            green = particleGreen - i * diffGreen;
            blue = particleBlue - i * diffBlue;
        }
    }
}
