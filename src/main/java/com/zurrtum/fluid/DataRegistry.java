package com.zurrtum.fluid;

import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.registry.Registries;
import net.minecraft.resource.Resource;
import net.minecraft.util.Identifier;

import java.util.*;
import java.util.stream.Collectors;

public class DataRegistry {
    private static final Set<Fluid> WATER_STATES_LIST = new HashSet<>();
    private static final Set<Fluid> LAVA_STATES_LIST = new HashSet<>();
    private static final Set<Fluid> BUCKET_MODELS_LIST = new HashSet<>();
    private static final Set<Fluid> CELL_MODELS_LIST = new HashSet<>();

    public static void registerWaterState(Fluid fluid) {
        WATER_STATES_LIST.add(fluid);
    }

    public static void registerLavaState(Fluid fluid) {
        LAVA_STATES_LIST.add(fluid);
    }

    public static void registerBucketModel(Fluid fluid) {
        BUCKET_MODELS_LIST.add(fluid);
    }

    public static void registerCellModel(Fluid fluid) {
        CELL_MODELS_LIST.add(fluid);
    }

    private static List<Resource> getBlockState(String type) {
        return List.of(new StringResource(String.format("""
        {
          "variants": {
            "": {
              "model": "minecraft:block/%s"
            }
          }
        }
        """.trim(), type)));
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
        return new StringResource(String.format("""
        {
          "model": {
            "type": "%s",
            "fluid": "%s"
          }
        }
        """.trim(), model, fluid));
    }

    public static void changeItemModels(Map<Identifier, Resource> map) {
        BUCKET_MODELS_LIST.forEach(fluid -> {
            Identifier id = Registries.FLUID.getId(fluid);
            map.put(
                id.withPath(path -> "items/" + path + "_bucket.json"),
                getFluidModel(FluidMod.BUCKET_MODEL_ID, id)
            );
        });
        CELL_MODELS_LIST.forEach(fluid -> {
            Identifier id = Registries.FLUID.getId(fluid);
            Identifier key;
            if (fluid != Fluids.EMPTY) {
                key = id.withPath(name -> "items/" + name + "_cell.json");
            } else {
                key = Registries.ITEM.getId(FluidMod.CELL_EMPTY).withPath(name -> "items/" + name + ".json");
            }
            map.put(key, getFluidModel(FluidMod.CELL_MODEL_ID, id));
        });
    }

    private static Resource getFluidTags(Set<Fluid> fluids) {
        if (fluids.isEmpty()) {
            return null;
        }
        String values = fluids.stream()
            .map(Registries.FLUID::getId).map(Identifier::toString)
            .collect(Collectors.joining("\",\""));
        return new StringResource(String.format("""
        {
          "replace": false,
          "values": ["%s"]
        }
        """.trim(), values));
    }

    public static void changeFluidTags(List<Resource> waters, List<Resource> lavas) {
        if (!waters.isEmpty()) {
            Resource resource = getFluidTags(WATER_STATES_LIST);
            if (resource != null) {
                waters.add(resource);
            }
        }
        if (!lavas.isEmpty()) {
            Resource resource = getFluidTags(LAVA_STATES_LIST);
            if (resource != null) {
                lavas.add(resource);
            }
        }
    }
}
