package com.zurrtum.fluid;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.fabricmc.fabric.api.client.render.fluid.v1.FluidRenderHandlerRegistry;
import net.fabricmc.fabric.api.client.render.fluid.v1.SimpleFluidRenderHandler;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.minecraft.client.particle.WaterBubbleParticle;
import net.minecraft.client.particle.WaterSplashParticle;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.item.model.ItemModelTypes;

public class FluidModClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ItemModelTypes.ID_MAPPER.put(FluidMod.CELL_MODEL_ID, CellItemModel.CODEC);
        ItemModelTypes.ID_MAPPER.put(FluidMod.BUCKET_MODEL_ID, BucketItemModel.CODEC);
        ParticleFactoryRegistry.getInstance().register(FluidMod.SPLASH, WaterSplashParticle.SplashFactory::new);
        ParticleFactoryRegistry.getInstance().register(FluidMod.BUBBLE, WaterBubbleParticle.Factory::new);
        ClientLifecycleEvents.CLIENT_STARTED.register(client -> {
            FluidRegistry.FLUID.forEach((fluid, entry) -> {
                FluidRenderHandlerRegistry.INSTANCE.register(fluid, new SimpleFluidRenderHandler(
                        entry.still_texture,
                        entry.flowing_texture,
                        entry.overlay_texture,
                        entry.tint
                ));
                BlockRenderLayerMap.INSTANCE.putFluids(RenderLayer.getTranslucent(), fluid);
            });
        });
    }
}
