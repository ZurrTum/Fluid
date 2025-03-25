package com.zurrtum.fluid.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.zurrtum.fluid.impl.DataRegistryImpl;
import com.zurrtum.fluid.impl.FluidMod;
import net.minecraft.block.*;
import net.minecraft.entity.LivingEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.BucketItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldAccess;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(BucketItem.class)
public class BucketItemMixin {
    @Shadow @Final private Fluid fluid;

    @WrapOperation(method = "use(Lnet/minecraft/world/World;Lnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/util/Hand;)Lnet/minecraft/util/ActionResult;", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/FluidDrainable;tryDrainFluid(Lnet/minecraft/entity/LivingEntity;Lnet/minecraft/world/WorldAccess;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;)Lnet/minecraft/item/ItemStack;"))
    private ItemStack tryDrainFluid(FluidDrainable instance, LivingEntity livingEntity, WorldAccess worldAccess, BlockPos blockPos, BlockState blockState, Operation<ItemStack> original, @Local ItemStack stack) {
        if (stack.getItem() == FluidMod.CELL_EMPTY && blockState.get(FluidBlock.LEVEL) == 0) {
            Fluid fluid = worldAccess.getFluidState(blockPos).getFluid();
            Item cell = DataRegistryImpl.CELL_LIST.get(fluid);
            if (cell != null) {
                worldAccess.setBlockState(blockPos, Blocks.AIR.getDefaultState(), Block.NOTIFY_ALL_AND_REDRAW);
                return new ItemStack(cell);
            }
        }
        return original.call(instance, livingEntity, worldAccess, blockPos, blockState);
    }

    @WrapOperation(method = "getEmptiedStack(Lnet/minecraft/item/ItemStack;Lnet/minecraft/entity/player/PlayerEntity;)Lnet/minecraft/item/ItemStack;", at = @At(value = "NEW", target = "Lnet/minecraft/item/ItemStack;"))
    private static ItemStack getEmptiedStack(ItemConvertible item, Operation<ItemStack> original, @Local(argsOnly = true) ItemStack stack) {
        if (stack.getItem() instanceof BucketItem bucket) {
            Fluid fluid = ((BucketItemMixin)(Object) bucket).fluid;
            Item cell = DataRegistryImpl.CELL_LIST.get(fluid);
            if (cell == bucket) {
                return new ItemStack(FluidMod.CELL_EMPTY);
            }
        }
        return original.call(item);
    }
}
