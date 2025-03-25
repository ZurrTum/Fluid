package com.zurrtum.fluid.impl;

import com.google.common.base.Suppliers;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.fabricmc.fabric.api.client.render.fluid.v1.FluidRenderHandler;
import net.fabricmc.fabric.api.client.render.fluid.v1.FluidRenderHandlerRegistry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.item.ItemModelManager;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.TexturedRenderLayers;
import net.minecraft.client.render.item.ItemRenderState;
import net.minecraft.client.render.item.model.ItemModel;
import net.minecraft.client.render.item.model.ItemModelTypes;
import net.minecraft.client.render.model.*;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.LivingEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.ItemDisplayContext;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;

import static com.zurrtum.fluid.impl.FluidMod.MOD_ID;
import static com.zurrtum.fluid.impl.FluidMod.CELL_MODEL_ID;
import static com.zurrtum.fluid.impl.FluidMod.BUCKET_MODEL_ID;

public class FluidItemModel implements ItemModel {
    private final RenderLayer layer;
    private final ModelSettings settings;
    private final Supplier<Triple<List<BakedQuad>, Supplier<Vector3f[]>, Integer>> bake;

    FluidItemModel(ModelSettings modelSettings, Supplier<Triple<List<BakedQuad>, Supplier<Vector3f[]>, Integer>> quadsProvider) {
        layer = TexturedRenderLayers.getItemEntityTranslucentCull();
        settings = modelSettings;
        bake = Suppliers.memoize(quadsProvider::get);
    }

    @Override
    public void update(
            ItemRenderState state,
            ItemStack stack,
            ItemModelManager resolver,
            ItemDisplayContext displayContext,
            @Nullable ClientWorld world,
            @Nullable LivingEntity user,
            int seed
    ) {
        ItemRenderState.LayerRenderState layerRenderState = state.newLayer();
        layerRenderState.setRenderLayer(layer);
        Triple<List<BakedQuad>, Supplier<Vector3f[]>, Integer> baked = bake.get();
        layerRenderState.getQuads().addAll(baked.getLeft());
        layerRenderState.setVector(baked.getMiddle());
        layerRenderState.initTints(1)[0] = baked.getRight();
        settings.addSettings(layerRenderState, displayContext);
    }

    static abstract class Unbaked implements ItemModel.Unbaked {
        static <T extends Unbaked> MapCodec<T> getCodec(Function<Fluid, T> factory) {
            return RecordCodecBuilder.mapCodec(
            instance -> instance.group(
                    Identifier.CODEC.xmap(Registries.FLUID::get, Registries.FLUID::getId)
                            .fieldOf("fluid").forGetter(Unbaked::getFluid)
            ).apply(instance, factory));
        }

        private final Identifier base;
        private final Fluid fluid;
        Unbaked(Identifier base, Fluid fluid) {
            this.base = base;
            this.fluid = fluid;
        }

        private Fluid getFluid() {
            return fluid;
        }

        @Override
        public void resolve(Resolver resolver) {
            resolver.markDependency(base);
        }

        @Override
        public ItemModel bake(ItemModel.BakeContext context) {
            Baker baker = context.blockModelBaker();
            BakedSimpleModel model = baker.getModel(base);
            ModelTextures modelTextures = model.getTextures();
            ModelSettings modelSettings = ModelSettings.resolveSettings(baker, model, modelTextures);
            return new FluidItemModel(modelSettings, () -> {
                Pair<Sprite, Integer> pair = parseFluid(fluid);
                Sprite sprite = pair.getLeft();
                ModelTextures textures;
                if (sprite != null) {
                    SpriteIdentifier texture = new SpriteIdentifier(sprite.getAtlasId(), sprite.getContents().getId());
                    textures = new ModelTextures.Builder()
                        .addLast(new ModelTextures.Textures.Builder().addSprite("fluid", texture).build())
                        .addLast(model.getModel().textures()).build(model);
                } else {
                    textures = modelTextures;
                }
                List<BakedQuad> list = model.getGeometry().bake(textures, baker, ModelRotation.X0_Y0, model).getAllQuads();
                return Triple.of(list, bakeVector(list), pair.getRight());
            });
        }

        private static Pair<Sprite, Integer> parseFluid(Fluid fluid) {
            FluidRenderHandler handler = FluidRenderHandlerRegistry.INSTANCE.get(fluid);
            if (handler == null) {
                return Pair.of(null, -1);
            }
            MinecraftClient client = MinecraftClient.getInstance();
            if (client.player == null) {
                return Pair.of(null, -1);
            }
            FluidState state = fluid.getDefaultState();
            int tint = handler.getFluidColor(client.world, client.player.getBlockPos(), state) | 0xFF000000;
            Sprite sprite = handler.getFluidSprites(client.world, BlockPos.ORIGIN, state)[0];
            return Pair.of(sprite, tint);
        }

        private static Supplier<Vector3f[]> bakeVector(List<BakedQuad> quads) {
            Set<Vector3f> set = new HashSet<>();
            for (BakedQuad bakedQuad : quads) {
                BakedQuadFactory.calculatePosition(bakedQuad.vertexData(), set::add);
            }
            Vector3f[] vector = set.toArray(Vector3f[]::new);
            return () -> vector;
        }
    }

    private static class CellUnbaked extends Unbaked {
        public static final MapCodec<CellUnbaked> CODEC = getCodec(CellUnbaked::new);
        public CellUnbaked(Fluid fluid) {
            super(Identifier.of(MOD_ID, "item/cell_base"), fluid);
        }

        @Override
        public MapCodec<CellUnbaked> getCodec() {
            return CODEC;
        }
    }

    private static class BucketUnbaked extends Unbaked {
        public static final MapCodec<BucketUnbaked> CODEC = getCodec(BucketUnbaked::new);
        public BucketUnbaked(Fluid fluid) {
            super(Identifier.of(MOD_ID, "item/bucket_base"), fluid);
        }

        @Override
        public MapCodec<BucketUnbaked> getCodec() {
            return CODEC;
        }
    }

    protected static void register() {
        ItemModelTypes.ID_MAPPER.put(CELL_MODEL_ID, CellUnbaked.CODEC);
        ItemModelTypes.ID_MAPPER.put(BUCKET_MODEL_ID, BucketUnbaked.CODEC);
    }
}
