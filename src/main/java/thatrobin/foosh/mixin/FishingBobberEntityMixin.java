package thatrobin.foosh.mixin;

import net.minecraft.entity.projectile.FishingBobberEntity;
import net.minecraft.nbt.NbtCompound;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(FishingBobberEntity.class)
public class FishingBobberEntityMixin {

    @Inject(method = "writeCustomDataToNbt", at = @At("HEAD"))
    public void writeNBT(NbtCompound nbt, CallbackInfo ci) {
        FishingBobberEntity bobber = (FishingBobberEntity) (Object) this;
        if (bobber.ownerUuid != null) {
            nbt.putUuid("Owner", bobber.ownerUuid);
        }

        if (bobber.leftOwner) {
            nbt.putBoolean("LeftOwner", true);
        }

        nbt.putBoolean("HasBeenShot", bobber.shot);
    }

    @Inject(method = "readCustomDataFromNbt", at = @At("HEAD"))
    public void readNBT(NbtCompound nbt, CallbackInfo ci) {
        FishingBobberEntity bobber = (FishingBobberEntity) (Object) this;
        if (nbt.containsUuid("Owner")) {
            bobber.ownerUuid = nbt.getUuid("Owner");
        }

        bobber.leftOwner = nbt.getBoolean("LeftOwner");
        bobber.shot = nbt.getBoolean("HasBeenShot");
    }
}
