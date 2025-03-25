package com.zurrtum.fluid.impl;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.fabricmc.fabric.api.client.render.fluid.v1.FluidRenderHandlerRegistry;
import net.fabricmc.fabric.api.client.render.fluid.v1.SimpleFluidRenderHandler;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.minecraft.client.particle.WaterBubbleParticle;
import net.minecraft.client.particle.WaterSplashParticle;
import net.minecraft.client.render.RenderLayer;

public class FluidClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        FluidItemModel.register();
        ParticleFactoryRegistry particle = ParticleFactoryRegistry.getInstance();
        particle.register(FluidMod.PARTICLE, new ColorParticleEffect.Factory());
        particle.register(FluidMod.SPLASH, WaterSplashParticle.SplashFactory::new);
        particle.register(FluidMod.BUBBLE, WaterBubbleParticle.Factory::new);
        ClientLifecycleEvents.CLIENT_STARTED.register(client -> {
            DataRegistryImpl.ENTRY_LIST.forEach((entry) -> {
                FluidRenderHandlerRegistry.INSTANCE.register(entry.still, entry.flowing, new SimpleFluidRenderHandler(
                        entry.still_texture,
                        entry.flowing_texture,
                        entry.overlay_texture,
                        entry.tint
                ));
                BlockRenderLayerMap.INSTANCE.putFluids(RenderLayer.getTranslucent(), entry.still, entry.flowing);
            });
        });
    }
}
