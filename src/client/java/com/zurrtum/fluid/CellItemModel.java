package com.zurrtum.fluid;

import com.mojang.serialization.MapCodec;
import net.minecraft.fluid.Fluid;
import net.minecraft.util.Identifier;

public class CellItemModel extends FluidItemModel.Unbaked {
    public static final MapCodec<CellItemModel> CODEC = getCodec(CellItemModel::new);
    public static final Identifier CELL_BASE = Identifier.of(FluidMod.MOD_ID, "item/cell_base");
    public CellItemModel(Fluid fluid) {
        super(CELL_BASE, fluid);
    }

    @Override
    public MapCodec<CellItemModel> getCodec() {
        return CODEC;
    }
}
