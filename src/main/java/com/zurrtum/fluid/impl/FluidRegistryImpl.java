package com.zurrtum.fluid.impl;

import com.zurrtum.fluid.api.base.FluidBlock;
import com.zurrtum.fluid.api.base.FluidEntry;
import com.zurrtum.fluid.api.base.LavaFluid;
import com.zurrtum.fluid.api.base.WaterFluid;
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

public class FluidRegistryImpl {
    public static FluidEntry registerWater(Identifier id, int tint) {
        FluidEntry entry = new FluidEntry();
        entry.still = new WaterFluid.Still(entry);
        entry.flowing = new WaterFluid.Flowing(entry);
        entry.still_texture = FluidEntry.WATER_STILL;
        entry.flowing_texture = FluidEntry.WATER_FLOWING;
        entry.overlay_texture = FluidEntry.WATER_OVERLAY;
        entry.tint = tint;
        DataRegistryImpl.registerWaterState(entry.still);
        DataRegistryImpl.registerWaterTag(entry.still);
        DataRegistryImpl.registerWaterTag(entry.flowing);
        return register(id, entry, AbstractBlock.Settings.copy(Blocks.WATER));
    }

    public static FluidEntry registerLava(Identifier id, int tint) {
        FluidEntry entry = new FluidEntry();
        entry.still = new LavaFluid.Still(entry);
        entry.flowing = new LavaFluid.Flowing(entry);
        entry.still_texture = FluidEntry.LAVA_STILL;
        entry.flowing_texture = FluidEntry.LAVA_FLOWING;
        entry.tint = tint;
        DataRegistryImpl.registerLavaState(entry.still);
        DataRegistryImpl.registerLavaTag(entry.still);
        DataRegistryImpl.registerLavaTag(entry.flowing);
        return register(id, entry, AbstractBlock.Settings.copy(Blocks.LAVA));
    }

    private static FluidEntry register(Identifier id, FluidEntry entry, AbstractBlock.Settings blockSettings) {
        RegistryKey<Fluid> still_key = RegistryKey.of(RegistryKeys.FLUID, id);
        RegistryKey<Fluid> flowing_key = RegistryKey.of(RegistryKeys.FLUID, id.withPrefixedPath("flowing_"));
        RegistryKey<Block> block_key = RegistryKey.of(RegistryKeys.BLOCK, id);
        Registry.register(Registries.FLUID, still_key, entry.still);
        Registry.register(Registries.FLUID, flowing_key, entry.flowing);
        entry.bucket = registerBucket(entry.still);
        entry.block = new FluidBlock(entry.still, blockSettings.registryKey(block_key));
        Registry.register(Registries.BLOCK, block_key, entry.block);
        registerCell(entry.still);
        DataRegistryImpl.registerFluid(entry);
        DataRegistryImpl.registerTint(entry.still, entry.tint);
        return entry;
    }

    public static BucketItem registerBucket(Fluid fluid) {
        Identifier id = Registries.FLUID.getId(fluid);
        RegistryKey<Item> bucket_key = RegistryKey.of(RegistryKeys.ITEM, id.withSuffixedPath("_bucket"));
        Item.Settings bucketSettings = new Item.Settings().recipeRemainder(Items.BUCKET).maxCount(1).registryKey(bucket_key);
        BucketItem bucket = new BucketItem(fluid, bucketSettings);
        DataRegistryImpl.registerBucket(fluid, bucket);
        return Registry.register(Registries.ITEM, bucket_key, bucket);
    }

    public static BucketItem registerCell(Fluid fluid) {
        return registerCell(fluid, Registries.FLUID.getId(fluid).withSuffixedPath("_cell"));
    }

    protected static BucketItem registerCell(Fluid fluid, Identifier id) {
        RegistryKey<Item> cell_key = RegistryKey.of(RegistryKeys.ITEM, id);
        Item.Settings settings = new Item.Settings().maxCount(16).registryKey(cell_key);
        BucketItem cell = new BucketItem(fluid, settings);
        DataRegistryImpl.registerCell(fluid, cell);
        return Registry.register(Registries.ITEM, cell_key, cell);
    }
}
