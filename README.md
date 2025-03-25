### Using api register fluid
```java
import com.zurrtum.fluid.api.FluidRegistry;
import com.zurrtum.fluid.api.base.FluidEntry;

FluidEntry water = FluidRegistry.registerWater(Identifier.of(MOD_ID, "water"), 0x00FFFF);
FluidEntry lava = FluidRegistry.registerLava(Identifier.of(MOD_ID, "lava"), 0x00FF00);
// Auto register bucket and cell.
```

### Custom register fluid
- Override default fluid flow behavior using the extended `WaterFluid` and `FluidBlock` classes.
```java
import com.zurrtum.fluid.api.base.FluidEntry;
import com.zurrtum.fluid.api.base.FluidBlock;
import com.zurrtum.fluid.api.base.WaterFluid;

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
```
- Dynamic registration (or use json file)
```java
DataRegistry.registerWaterState(entry.still);
DataRegistry.registerWaterTag(entry.still);
DataRegistry.registerWaterTag(entry.flowing);
```
- Using fabric SimpleFluidRenderHandler (or using client register)
```java
entry.still_texture = FluidEntry.WATER_STILL;
entry.flowing_texture = FluidEntry.WATER_FLOWING;
entry.overlay_texture = FluidEntry.WATER_OVERLAY;
entry.tint = 0xFF0000;
DataRegistry.registerFluid(entry);
```
- Register particle and fog color
```java
DataRegistry.registerTint(entry.still, entry.tint);
```

### Using api register bucket and cell
```java
// Save the variable to entry because getBucketItem needs it.
entry.bucket = FluidRegistry.registerBucket(entry.still);
FluidRegistry.registerCell(entry.still);
```