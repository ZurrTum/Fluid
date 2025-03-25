package com.zurrtum.fluid.impl;

import net.minecraft.particle.ParticleEffect;

public interface NextParticleProvider {
    void set_nextParticle(ColorParticleEffect effect);
    ParticleEffect get_nextParticle();
}
