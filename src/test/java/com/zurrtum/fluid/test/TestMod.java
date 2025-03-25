package com.zurrtum.fluid.test;

import com.zurrtum.fluid.api.DataRegistry;
import com.zurrtum.fluid.api.FluidRegistry;
import com.zurrtum.fluid.api.base.FluidBlock;
import com.zurrtum.fluid.api.base.FluidEntry;
import com.zurrtum.fluid.api.base.WaterFluid;
import net.fabricmc.api.ModInitializer;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Blocks;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;

public class TestMod implements ModInitializer {
    public static final String MOD_ID = "test";
    @Override
    public void onInitialize() {
        // Using api register fluid
        FluidEntry water = FluidRegistry.registerWater(Identifier.of(MOD_ID, "water"), 0x00FFFF);
        FluidEntry lava = FluidRegistry.registerLava(Identifier.of(MOD_ID, "lava"), 0x00FF00);


        // Custom register fluid
        Identifier id = Identifier.of(MOD_ID, "custom");
        FluidEntry entry = new FluidEntry();
        entry.still = new WaterFluid.Still(entry);
        entry.flowing = new WaterFluid.Flowing(entry);
        entry.block = new FluidBlock(
            entry.still,
            AbstractBlock.Settings.copy(Blocks.WATER).registryKey(RegistryKey.of(RegistryKeys.BLOCK, id))
        );
        Registry.register(Registries.FLUID, id, entry.still);
        Registry.register(Registries.FLUID, id.withPrefixedPath("flowing_"), entry.flowing);
        Registry.register(Registries.BLOCK, id, entry.block);

        // Dynamic registration (or use json file)
        DataRegistry.registerWaterState(entry.still);
        DataRegistry.registerWaterTag(entry.still);
        DataRegistry.registerWaterTag(entry.flowing);

        // Using fabric SimpleFluidRenderHandler (or using client register)
        entry.still_texture = FluidEntry.WATER_STILL;
        entry.flowing_texture = FluidEntry.WATER_FLOWING;
        entry.overlay_texture = FluidEntry.WATER_OVERLAY;
        entry.tint = 0xFF0000;
        DataRegistry.registerFluid(entry);

        // Register particle and fog color
        DataRegistry.registerTint(entry.still, entry.tint);

        // Using api register bucket and cell
        entry.bucket = FluidRegistry.registerBucket(entry.still);
        FluidRegistry.registerCell(entry.still);
    }
}