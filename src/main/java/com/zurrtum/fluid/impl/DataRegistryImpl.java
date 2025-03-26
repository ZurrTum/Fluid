package com.zurrtum.fluid.impl;

import com.zurrtum.fluid.api.base.FluidEntry;
import net.minecraft.fluid.FlowableFluid;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.BucketItem;
import net.minecraft.registry.Registries;
import net.minecraft.resource.Resource;
import net.minecraft.util.Identifier;

import java.io.ByteArrayInputStream;
import java.util.*;
import java.util.stream.Collectors;

import static com.zurrtum.fluid.impl.FluidMod.*;

public class DataRegistryImpl {
    private static final Set<Fluid> WATER_STATES_LIST = new HashSet<>();
    private static final Set<Fluid> WATER_TAG_LIST = new HashSet<>();
    private static final Set<Fluid> LAVA_STATES_LIST = new HashSet<>();
    private static final Set<Fluid> LAVA_TAG_LIST = new HashSet<>();
    protected static final Set<FluidEntry> ENTRY_LIST = new HashSet<>();
    protected static final Map<Fluid, BucketItem> BUCKET_LIST = new LinkedHashMap<>();
    public static final Map<Fluid, Integer> TINT_LIST = new HashMap<>();
    public static final Map<Fluid, BucketItem> CELL_LIST = new LinkedHashMap<>();

    public static void registerWaterState(Fluid fluid) {
        WATER_STATES_LIST.add(fluid);
    }

    public static void registerWaterTag(Fluid fluid) {
        WATER_TAG_LIST.add(fluid);
    }

    public static void registerLavaState(Fluid fluid) {
        LAVA_STATES_LIST.add(fluid);
    }

    public static void registerLavaTag(Fluid fluid) {
        LAVA_TAG_LIST.add(fluid);
    }

    public static void registerFluid(FluidEntry entry) {
        ENTRY_LIST.add(entry);
    }

    public static void registerTint(FlowableFluid fluid, int tint) {
        TINT_LIST.put(fluid.getStill(), tint);
        TINT_LIST.put(fluid.getFlowing(), tint);
    }

    protected static void registerBucket(Fluid fluid, BucketItem bucket) {
        BUCKET_LIST.put(fluid, bucket);
    }

    protected static void registerCell(Fluid fluid, BucketItem bucket) {
        CELL_LIST.put(fluid, bucket);
    }

    private static List<Resource> getBlockState(String type) {
        return List.of(new StringResource("""
        {
          "variants": {
            "": {
              "model": "minecraft:block/%s"
            }
          }
        }
        """, type));
    }

    public static void changeBlockStates(Map<Identifier, List<Resource>> map) {
        WATER_STATES_LIST.forEach((fluid) -> {
            map.put(
                Registries.FLUID.getId(fluid).withPath(path -> "blockstates/" + path + ".json"),
                getBlockState("water")
            );
        });
        LAVA_STATES_LIST.forEach((fluid) -> {
            map.put(
                Registries.FLUID.getId(fluid).withPath(path -> "blockstates/" + path + ".json"),
                getBlockState("lava")
            );
        });
    }

    private static Resource getFluidModel(Identifier model, Identifier fluid) {
        return new StringResource("""
        {
          "model": {
            "type": "%s",
            "fluid": "%s"
          }
        }
        """, model, fluid);
    }

    public static void changeItemModels(Map<Identifier, Resource> map) {
        BUCKET_LIST.keySet().forEach(fluid -> {
            Identifier id = Registries.FLUID.getId(fluid);
            map.put(
                id.withPath(path -> "items/" + path + "_bucket.json"),
                getFluidModel(BUCKET_MODEL_ID, id)
            );
        });
        CELL_LIST.keySet().forEach(fluid -> {
            Identifier id = Registries.FLUID.getId(fluid);
            Identifier key;
            if (fluid != Fluids.EMPTY) {
                key = id.withPath(name -> "items/" + name + "_cell.json");
            } else {
                key = CELL_ID.withPath(name -> "items/" + name + ".json");
            }
            map.put(key, getFluidModel(CELL_MODEL_ID, id));
        });
    }

    private static Resource getFluidTags(Set<Fluid> fluids) {
        if (fluids.isEmpty()) {
            return null;
        }
        String values = fluids.stream()
            .map(Registries.FLUID::getId).map(Identifier::toString)
            .collect(Collectors.joining("\",\""));
        return new StringResource("""
        {
          "replace": false,
          "values": ["%s"]
        }
        """, values);
    }

    public static void changeFluidTags(List<Resource> waters, List<Resource> lavas) {
        if (!waters.isEmpty()) {
            Resource resource = getFluidTags(WATER_TAG_LIST);
            if (resource != null) {
                waters.add(resource);
            }
        }
        if (!lavas.isEmpty()) {
            Resource resource = getFluidTags(LAVA_TAG_LIST);
            if (resource != null) {
                lavas.add(resource);
            }
        }
    }

    private static class StringResource extends Resource {
        public StringResource(String format, Object... args) {
            super(null, () -> new ByteArrayInputStream(new Formatter().format(format.trim(), args).toString().getBytes()));
        }
        @Override
        public String getPackId() {
            return MOD_ID;
        }
    }
}
