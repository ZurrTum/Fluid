package com.zurrtum.fluid.mixin.client;

import com.zurrtum.fluid.impl.ColorParticleEffect;
import com.zurrtum.fluid.impl.NextParticleProvider;
import net.minecraft.particle.ParticleEffect;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(targets = "net.minecraft.client.particle.BlockLeakParticle$ContinuousFalling")
public class ParticleContinuousFallingMixin implements NextParticleProvider {
    @Final
    @Mutable
    @Shadow
    protected ParticleEffect nextParticle;

    @Override
    public void set_nextParticle(ColorParticleEffect effect) {
        this.nextParticle = effect;
    }

    @Override
    public ParticleEffect get_nextParticle() {
        return nextParticle;
    }
}
