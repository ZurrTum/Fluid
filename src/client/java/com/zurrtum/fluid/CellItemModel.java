package com.zurrtum.fluid;

import com.mojang.serialization.MapCodec;
import net.minecraft.fluid.Fluid;
import net.minecraft.util.Identifier;

import java.util.List;

public class CellItemModel extends FluidItemModel.Unbaked {
    public static final MapCodec<CellItemModel> CODEC = getCodec(CellItemModel::new);
    public static final Identifier CELL_BASE = Identifier.of(FluidMod.MOD_ID, "item/cell_base");
    public static final Identifier CELL_BACKGROUND = Identifier.of(FluidMod.MOD_ID, "item/cell_background");
    public static final Identifier CELL_GLASS = Identifier.of(FluidMod.MOD_ID, "item/cell_glass");
    public CellItemModel(Fluid fluid) {
        super(CELL_BACKGROUND, List.of(CELL_BASE, CELL_GLASS), fluid);
    }

    @Override
    public MapCodec<CellItemModel> getCodec() {
        return CODEC;
    }
}
