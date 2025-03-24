package com.zurrtum.fluid;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.BucketItem;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;

import java.util.LinkedHashMap;
import java.util.Map;

public class FluidRegistry {
    public static final Map<Fluid, FluidEntry> FLUID = new LinkedHashMap<>();
    public static final Map<Fluid, Item> COMPAT_CELL = new LinkedHashMap<>();

    public static BucketItem registerCompatCell(Fluid fluid) {
        BucketItem cell = registerCell(fluid);
        COMPAT_CELL.put(fluid, cell);
        return cell;
    }

    public static FluidEntry registerWater(Identifier id, int tint) {
        FluidEntry entry = new FluidEntry();
        entry.still = new WaterFluid.Still(entry);
        entry.flowing = new WaterFluid.Flowing(entry);
        entry.still_texture = FluidEntry.WATER_STILL;
        entry.flowing_texture = FluidEntry.WATER_FLOWING;
        entry.overlay_texture = FluidEntry.WATER_OVERLAY;
        DataRegistry.registerWaterState(entry.still);
        DataRegistry.registerWaterTag(entry.still);
        DataRegistry.registerWaterTag(entry.flowing);
        return register(id, entry, AbstractBlock.Settings.copy(Blocks.WATER), tint);
    }

    public static FluidEntry registerLava(Identifier id, int tint) {
        FluidEntry entry = new FluidEntry();
        entry.still = new LavaFluid.Still(entry);
        entry.flowing = new LavaFluid.Flowing(entry);
        entry.still_texture = FluidEntry.LAVA_STILL;
        entry.flowing_texture = FluidEntry.LAVA_FLOWING;
        DataRegistry.registerLavaState(entry.still);
        DataRegistry.registerLavaTag(entry.still);
        DataRegistry.registerLavaTag(entry.flowing);
        return register(id, entry, AbstractBlock.Settings.copy(Blocks.LAVA), tint);
    }

    public static FluidEntry register(Identifier id, FluidEntry entry, AbstractBlock.Settings blockSettings, int tint) {
        RegistryKey<Fluid> still_key = RegistryKey.of(RegistryKeys.FLUID, id);
        RegistryKey<Fluid> flowing_key = RegistryKey.of(RegistryKeys.FLUID, id.withPrefixedPath("flowing_"));
        RegistryKey<Block> block_key = RegistryKey.of(RegistryKeys.BLOCK, id);
        Registry.register(Registries.FLUID, still_key, entry.still);
        Registry.register(Registries.FLUID, flowing_key, entry.flowing);
        entry.tint = tint;
        entry.cell = registerCell(entry.still);
        entry.bucket = registerBucket(entry.still);
        entry.block = Registry.register(Registries.BLOCK, block_key, new FluidBlock(entry.still, blockSettings.registryKey(block_key)));
        FLUID.put(entry.still, entry);
        FLUID.put(entry.flowing, entry);
        return entry;
    }

    public static BucketItem registerBucket(Fluid fluid) {
        Identifier id = Registries.FLUID.getId(fluid);
        RegistryKey<Item> bucket_key = RegistryKey.of(RegistryKeys.ITEM, id.withSuffixedPath("_bucket"));
        Item.Settings bucketSettings = new Item.Settings().recipeRemainder(Items.BUCKET).maxCount(1).registryKey(bucket_key);
        DataRegistry.registerBucketModel(fluid);
        return Registry.register(Registries.ITEM, bucket_key, new BucketItem(fluid, bucketSettings));
    }

    public static BucketItem registerCell(Fluid fluid) {
        Identifier id = Registries.FLUID.getId(fluid);
        RegistryKey<Item> cell_key = RegistryKey.of(RegistryKeys.ITEM, id.withSuffixedPath("_cell"));
        Item.Settings cellSettings = new Item.Settings().maxCount(16).registryKey(cell_key);
        DataRegistry.registerCellModel(fluid);
        return Registry.register(Registries.ITEM, cell_key, new BucketItem(fluid, cellSettings));
    }

    public static BucketItem registerCell(Fluid fluid, Identifier id) {
        RegistryKey<Item> cell_key = RegistryKey.of(RegistryKeys.ITEM, id);
        Item.Settings cellSettings = new Item.Settings().maxCount(16).registryKey(cell_key);
        DataRegistry.registerCellModel(fluid);
        return Registry.register(Registries.ITEM, cell_key, new BucketItem(fluid, cellSettings));
    }
}
