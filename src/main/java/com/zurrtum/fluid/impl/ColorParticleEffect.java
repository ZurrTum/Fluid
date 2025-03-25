package com.zurrtum.fluid.impl;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleFactory;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleType;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.particle.SimpleParticleType;
import net.minecraft.registry.Registries;

import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Map;

public class ColorParticleEffect implements ParticleEffect {
    private static SimpleParticleType getType(int index) {
        return (SimpleParticleType) Registries.PARTICLE_TYPE.get(index);
    }
    public static final MapCodec<ColorParticleEffect> CODEC = RecordCodecBuilder.mapCodec(
            instance -> instance.group(
                    Codec.INT.xmap(ColorParticleEffect::getType, Registries.PARTICLE_TYPE::getRawId)
                            .fieldOf("type").forGetter(ColorParticleEffect::getRawType),
                    Codec.INT.fieldOf("color").forGetter(ColorParticleEffect::getTint)
            ).apply(instance, ColorParticleEffect::new)
    );
    public static final PacketCodec<ByteBuf, ColorParticleEffect> PACKET_CODEC = PacketCodec.tuple(
            PacketCodecs.INTEGER.xmap(ColorParticleEffect::getType, Registries.PARTICLE_TYPE::getRawId),
            ColorParticleEffect::getRawType,
            PacketCodecs.INTEGER, ColorParticleEffect::getTint,
            ColorParticleEffect::new
    );

    private static final Map<Integer, Map<ParticleEffect, ColorParticleEffect>> CACHE = new HashMap<>();
    private static final Map<SimpleParticleType, SimpleParticleType> MAP = Map.of(
        ParticleTypes.BUBBLE, FluidMod.BUBBLE,
        ParticleTypes.SPLASH, FluidMod.SPLASH
    );
    public static ColorParticleEffect create(ParticleEffect parameters, int tint) {
        return CACHE.computeIfAbsent(tint, i -> new IdentityHashMap<>())
            .computeIfAbsent(parameters, effect -> new ColorParticleEffect(effect, tint));
    }

    private final SimpleParticleType type;
    private final int tint;

    public ColorParticleEffect(ParticleEffect type, int tint) {
        this.type = (SimpleParticleType) type;
        this.tint = tint;
    }

    private SimpleParticleType getRawType() {
        return type;
    }

    @Override
    public ParticleType<ColorParticleEffect> getType() {
        return FluidMod.PARTICLE;
    }

    public int getTint() {
        return tint;
    }

    public static class Factory implements ParticleFactory<ColorParticleEffect> {
        public Particle createParticle(ColorParticleEffect parameters, ClientWorld clientWorld, double d, double e, double f, double g, double h, double i) {
            SimpleParticleType type = parameters.getRawType();
            int tint = parameters.getTint();
            if (tint != -1) {
                type = MAP.getOrDefault(type, type);
            }
            Particle particle = MinecraftClient.getInstance().particleManager.addParticle(type, d, e, f, g, h, i);
            if (particle != null && tint != -1) {
                particle.setColor(
                    (tint >> 16 & 0xFF) / 255.0F,
                    (tint >> 8 & 0xFF) / 255.0F,
                    (tint & 0xFF) / 255.0F
                );
                if (particle instanceof NextParticleProvider provider) {
                    provider.set_nextParticle(create(provider.get_nextParticle(), tint));
                }
            }
            return particle;
        }
    }
}
