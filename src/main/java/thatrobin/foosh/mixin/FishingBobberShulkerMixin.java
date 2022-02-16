package thatrobin.foosh.mixin;

import net.minecraft.block.Block;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.projectile.FishingBobberEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import thatrobin.foosh.api.RedstoneEntity;
import thatrobin.foosh.api.ShulkEntity;

@Mixin(FishingBobberEntity.class)
public abstract class FishingBobberShulkerMixin extends ProjectileEntity implements ShulkEntity {

    @Unique
    private static final TrackedData<Boolean> SHULK = DataTracker.registerData(FishingBobberEntity.class, TrackedDataHandlerRegistry.BOOLEAN);

    private FishingBobberShulkerMixin(EntityType<? extends ProjectileEntity> type, World world) {
        super(type, world);
    }

    @Inject(method = "initDataTracker", at = @At("RETURN"))
    private void registerFireImmuneTracker(CallbackInfo ci) {
        dataTracker.startTracking(SHULK, false);
    }

    //@Inject(method = "tick", at = @At(value = "TAIL"))
    //public void tick(CallbackInfo ci) {
    //    if(isShulk()) {
//
    //    }
    //}

    //@Inject(method = "use", at = @At(value = "RETURN"))
    //public void tick(ItemStack usedItem, CallbackInfoReturnable<Integer> cir) {
    //    if(isShulk()) {
    //        Block block = world.getBlockState(getBlockPos()).getBlock();
    //        world.updateNeighbors(getBlockPos(), block);
    //    }
    //}


    @Override
    public boolean isShulk() {
        return dataTracker.get(SHULK);
    }

    @Override
    public void setShulk(boolean value) {
        dataTracker.set(SHULK, value);
    }
}
