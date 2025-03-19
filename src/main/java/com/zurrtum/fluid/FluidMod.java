package com.zurrtum.fluid;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.*;
import net.minecraft.particle.SimpleParticleType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class FluidMod implements ModInitializer {
    public static final String MOD_ID = "fluid";
    public static final Identifier BUCKET_MODEL_ID = Identifier.of(FluidMod.MOD_ID, "model/bucket");
    public static final Identifier CELL_MODEL_ID = Identifier.of(FluidMod.MOD_ID, "model/cell");
    public static final SimpleParticleType SPLASH = FabricParticleTypes.simple();
    public static final SimpleParticleType BUBBLE = FabricParticleTypes.simple();
    public static final Item CELL_EMPTY = FluidRegistry.registerCell(Fluids.EMPTY, Identifier.of(MOD_ID, "cell"));
    public static final RegistryKey<ItemGroup> ITEM_GROUP = RegistryKey.of(RegistryKeys.ITEM_GROUP, Identifier.of(MOD_ID, "item_group"));

    @Override
    public void onInitialize() {
        Registry.register(Registries.PARTICLE_TYPE, Identifier.of(MOD_ID, "splash"), SPLASH);
        Registry.register(Registries.PARTICLE_TYPE, Identifier.of(MOD_ID, "bubble"), BUBBLE);
        FluidRegistry.registerCompatCell(Fluids.WATER);
        FluidRegistry.registerCompatCell(Fluids.LAVA);
        Registry.register(Registries.ITEM_GROUP, ITEM_GROUP, FabricItemGroup.builder()
                .displayName(Text.translatable("fluid.item_group"))
                .icon(() -> new ItemStack(CELL_EMPTY))
                .build());
        ItemGroupEvents.modifyEntriesEvent(ITEM_GROUP).register(content -> {
            content.add(CELL_EMPTY);
            FluidRegistry.COMPAT_CELL.values().forEach(content::add);
            FluidRegistry.FLUID.forEach((fluid, entry) -> {
                if (fluid == entry.still) {
                    content.add(entry.bucket);
                    content.add(entry.cell);
                }
            });
        });
    }
}
