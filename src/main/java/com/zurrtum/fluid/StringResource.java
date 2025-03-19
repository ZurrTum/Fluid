package com.zurrtum.fluid;

import java.io.ByteArrayInputStream;

public class StringResource extends net.minecraft.resource.Resource {
    public StringResource(String data) {
        super(null, () -> new ByteArrayInputStream(data.getBytes()));
    }

    @Override
    public String getPackId() {
        return FluidMod.MOD_ID;
    }
}
