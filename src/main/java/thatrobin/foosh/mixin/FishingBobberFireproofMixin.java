package thatrobin.foosh.mixin;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.projectile.FishingBobberEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import thatrobin.foosh.api.FireproofEntity;

@Mixin(FishingBobberEntity.class)
public abstract class FishingBobberFireproofMixin extends Entity implements FireproofEntity {

    @Unique
    private static final TrackedData<Boolean> FIRE_IMMUNE = DataTracker.registerData(FishingBobberEntity.class, TrackedDataHandlerRegistry.BOOLEAN);

    private FishingBobberFireproofMixin(EntityType<?> type, World world) {
        super(type, world);
    }

    @Inject(
            method = "initDataTracker",
            at = @At("RETURN"))
    private void registerFireImmuneTracker(CallbackInfo ci) {
        dataTracker.startTracking(FIRE_IMMUNE, false);
    }
    
    @Override
    public boolean isOnFire() {
        if(dataTracker.get(FIRE_IMMUNE)) {
            return false;
        }

        return super.isOnFire();
    }

    @Override
    public boolean isFireproof() {
        return dataTracker.get(FIRE_IMMUNE);
    }

    @Override
    public void setFireproof(boolean value) {
        dataTracker.set(FIRE_IMMUNE, value);
    }
}
