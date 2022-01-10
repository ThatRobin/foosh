package thatrobin.foosh.mixin;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.FishingBobberEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import thatrobin.foosh.item.AFishingRodItem;

@Mixin(FishingBobberEntity.class)
public abstract class FishingBobberValidityMixin extends Entity {

    private FishingBobberValidityMixin(EntityType<?> type, World world) {
        super(type, world);
    }

    @Inject(
            method = "removeIfInvalid",
            at = @At("HEAD"),
            cancellable = true
    )
    private void removeIfInvalid(PlayerEntity playerEntity, CallbackInfoReturnable<Boolean> cir) {
        ItemStack mainHandStack = playerEntity.getMainHandStack();
        ItemStack offHandStack = playerEntity.getOffHandStack();

        boolean mainHandHasRod = mainHandStack.getItem() instanceof AFishingRodItem;
        boolean offHandHasRod = offHandStack.getItem() instanceof AFishingRodItem;

        if (!playerEntity.isRemoved() && playerEntity.isAlive() && (mainHandHasRod || offHandHasRod) && this.squaredDistanceTo(playerEntity) <= 1024.0D) {
            cir.setReturnValue(false);
        }
    }
}