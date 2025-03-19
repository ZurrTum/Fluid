package com.zurrtum.fluid;

import com.google.common.base.Suppliers;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.fabricmc.fabric.api.client.render.fluid.v1.FluidRenderHandler;
import net.fabricmc.fabric.api.client.render.fluid.v1.FluidRenderHandlerRegistry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.data.TextureKey;
import net.minecraft.client.item.ItemModelManager;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.TexturedRenderLayers;
import net.minecraft.client.render.item.ItemRenderState;
import net.minecraft.client.render.item.model.ItemModel;
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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;

public class FluidItemModel implements ItemModel {
    private final RenderLayer layer;
    private final ModelSettings settings;
    private final Supplier<Triple<List<BakedQuad>, Supplier<Vector3f[]>, Integer>> bake;

    public FluidItemModel(ModelSettings modelSettings, Supplier<Triple<List<BakedQuad>, Supplier<Vector3f[]>, Integer>> quadsProvider) {
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

    public static abstract class Unbaked implements ItemModel.Unbaked {
        public static <T extends Unbaked> MapCodec<T> getCodec(Function<Fluid, T> factory) {
            return RecordCodecBuilder.mapCodec(
            instance -> instance.group(
                    Identifier.CODEC.xmap(Registries.FLUID::get, Registries.FLUID::getId)
                            .fieldOf("fluid").forGetter(Unbaked::getFluid)
            ).apply(instance, factory));
        }

        private final Identifier background;
        private final List<Identifier> cover;
        private final Fluid fluid;
        Unbaked(Identifier background, List<Identifier> cover, Fluid fluid) {
            this.background = background;
            this.cover = cover;
            this.fluid = fluid;
        }

        public Fluid getFluid() {
            return fluid;
        }

        @Override
        public void resolve(Resolver resolver) {
            resolver.markDependency(background);
            cover.forEach(resolver::markDependency);
        }

        @Override
        public ItemModel bake(ItemModel.BakeContext context) {
            Baker baker = context.blockModelBaker();
            BakedSimpleModel backgroundModel = baker.getModel(background);
            List<BakedQuad> backgroundQuads = backgroundModel.bakeGeometry(backgroundModel.getTextures(), baker, ModelRotation.X0_Y0).getAllQuads();
            List<BakedQuad> coverQuads = new ArrayList<>();
            BakedSimpleModel[] models = new BakedSimpleModel[cover.size()];
            ModelTextures[] textures = new ModelTextures[cover.size()];
            for (int i = 0; i < cover.size(); i++) {
                Identifier id = cover.get(i);
                models[i] = baker.getModel(id);
                textures[i] = models[i].getTextures();
                coverQuads.addAll(models[i].bakeGeometry(textures[i], baker, ModelRotation.X0_Y0).getAllQuads());
            }
            ModelSettings modelSettings = ModelSettings.resolveSettings(baker, models[0], textures[0]);
            return new FluidItemModel(modelSettings, () -> {
                List<BakedQuad> list = new ArrayList<>(backgroundQuads);
                Pair<Sprite, Integer> pair = parseFluid(fluid);
                if (pair != null) {
                    list.addAll(bakeFluidQuads(baker, backgroundModel, pair));
                    list.addAll(coverQuads);
                    return Triple.of(list, bakeVector(list), pair.getRight());
                } else {
                    list.addAll(coverQuads);
                    return Triple.of(list, bakeVector(list), -1);
                }
            });
        }

        @Nullable
        public static Pair<Sprite, Integer> parseFluid(Fluid fluid) {
            FluidRenderHandler handler = FluidRenderHandlerRegistry.INSTANCE.get(fluid);
            if (handler == null) {
                return null;
            }
            MinecraftClient client = MinecraftClient.getInstance();
            if (client.player == null) {
                return null;
            }
            FluidState state = fluid.getDefaultState();
            int tint = handler.getFluidColor(client.world, client.player.getBlockPos(), state) | 0xFF000000;
            Sprite sprite = handler.getFluidSprites(client.world, BlockPos.ORIGIN, state)[0];
            return Pair.of(sprite, tint);
        }

        public static List<BakedQuad> bakeFluidQuads(Baker baker, BakedSimpleModel model, Pair<Sprite, Integer> pair) {
            Sprite sprite = pair.getLeft();
            SpriteIdentifier texture = new SpriteIdentifier(sprite.getAtlasId(), sprite.getContents().getId());
            ModelTextures.Textures textures = new ModelTextures.Textures.Builder()
                    .addSprite(TextureKey.TEXTURE.getName(), texture).build();
            ModelTextures sprites = new ModelTextures.Builder().addLast(textures).build(null);
            List<BakedQuad> quads = model.getGeometry().bake(sprites, baker, ModelRotation.X0_Y0, model).getAllQuads();
            if (pair.getRight() == -1) {
                return quads;
            }
            List<BakedQuad> list = new ArrayList<>(quads.size());
            for (BakedQuad quad : quads) {
                list.add(new BakedQuad(quad.vertexData(), 0, quad.face(), quad.sprite(), quad.shade(), quad.lightEmission()));
            }
            return list;
        }

        public static Supplier<Vector3f[]> bakeVector(List<BakedQuad> quads) {
            Set<Vector3f> set = new HashSet<>();
            for (BakedQuad bakedQuad : quads) {
                BakedQuadFactory.calculatePosition(bakedQuad.vertexData(), set::add);
            }
            Vector3f[] vector = set.toArray(Vector3f[]::new);
            return () -> vector;
        }
    }
}
