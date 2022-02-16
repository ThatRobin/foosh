package thatrobin.foosh.mixin;

import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.FishingBobberEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.TypeFilter;
import net.minecraft.util.math.*;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import thatrobin.foosh.Foosh;
import thatrobin.foosh.api.RedstoneEntity;

import java.util.List;
import java.util.function.Predicate;

@Mixin(World.class)
public abstract class WorldMixin {

    @Shadow public abstract <T extends Entity> List<T> getEntitiesByType(TypeFilter<FishingBobberEntity, ?> filter, Box box, Predicate<? super T> predicate);

    @Inject(method = "getReceivedRedstonePower", at = @At(value = "HEAD"), cancellable = true)
    private void getPower(BlockPos pos, CallbackInfoReturnable<Integer> cir) {
        TypeFilter<FishingBobberEntity, ?> test = TypeFilter.instanceOf(FishingBobberEntity.class);
        BlockBox blockBox = new BlockBox(pos);
        List<Entity> entities = this.getEntitiesByType(test, Box.from(blockBox.expand(2)), (entity) -> {
            if(entity instanceof FishingBobberEntity bobber) {
                return ((RedstoneEntity)bobber).isRedstone();
            }
            return false;
        });
        if(!entities.isEmpty()) {
            cir.setReturnValue(15);
            return;
        }
    }
}
