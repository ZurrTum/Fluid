package com.zurrtum.fluid.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.zurrtum.fluid.DataRegistry;
import net.minecraft.registry.tag.TagGroupLoader;
import net.minecraft.resource.Resource;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

import java.util.List;
import java.util.Map;
import java.util.Set;

@Mixin(TagGroupLoader.class)
public class TagGroupLoaderMixin {
    @Shadow
    @Final
    private String dataType;
    @Unique
    private static final String fluidType = "tags/fluid";
    @Unique
    private static final Identifier water = Identifier.ofVanilla("tags/fluid/water.json");
    @Unique
    private static final Identifier lava = Identifier.ofVanilla("tags/fluid/lava.json");

    @WrapOperation(method = "loadTags(Lnet/minecraft/resource/ResourceManager;)Ljava/util/Map;", at = @At(value = "INVOKE", target = "Ljava/util/Map;entrySet()Ljava/util/Set;"))
    private Set<Map.Entry<Identifier, List<Resource>>> loadTags(Map<Identifier, List<Resource>> instance, Operation<Set<Map.Entry<Identifier, List<Resource>>>> original) {
        if (this.dataType.equals(fluidType)) {
            DataRegistry.changeFluidTags(instance.get(water), instance.get(lava));
        }
        return original.call(instance);
    }
}
