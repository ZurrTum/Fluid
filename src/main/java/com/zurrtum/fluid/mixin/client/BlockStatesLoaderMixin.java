package com.zurrtum.fluid.mixin.client;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.zurrtum.fluid.impl.DataRegistryImpl;
import net.minecraft.client.render.model.BlockStatesLoader;
import net.minecraft.resource.Resource;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.function.Function;

@Mixin(BlockStatesLoader.class)
public class BlockStatesLoaderMixin {
    @WrapOperation(method = "load(Lnet/minecraft/resource/ResourceManager;Ljava/util/concurrent/Executor;)Ljava/util/concurrent/CompletableFuture;", at = @At(value = "INVOKE", target = "Ljava/util/concurrent/CompletableFuture;thenCompose(Ljava/util/function/Function;)Ljava/util/concurrent/CompletableFuture;"))
    private static CompletableFuture<BlockStatesLoader.LoadedModels> loadBlockStates(CompletableFuture<Map<Identifier, List<Resource>>> instance, Function<Map<Identifier, List<Resource>>, CompletionStage<BlockStatesLoader.LoadedModels>> fn, Operation<CompletableFuture<BlockStatesLoader.LoadedModels>> original) {
        return original.call(instance, (Function<Map<Identifier, List<Resource>>, CompletionStage<BlockStatesLoader.LoadedModels>>) (map -> {
            DataRegistryImpl.changeBlockStates(map);
            return fn.apply(map);
        }));
    }
}
