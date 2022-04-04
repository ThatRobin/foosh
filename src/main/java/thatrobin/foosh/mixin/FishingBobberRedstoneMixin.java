package thatrobin.foosh.mixin;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.FishingBobberEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.state.property.Property;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import thatrobin.foosh.api.FireproofEntity;
import thatrobin.foosh.api.RedstoneEntity;

@Mixin(FishingBobberEntity.class)
public abstract class FishingBobberRedstoneMixin extends ProjectileEntity implements RedstoneEntity {

    @Unique
    private static final TrackedData<Boolean> REDSTONE_POWER = DataTracker.registerData(FishingBobberEntity.class, TrackedDataHandlerRegistry.BOOLEAN);

    private FishingBobberRedstoneMixin(EntityType<? extends ProjectileEntity> type, World world) {
        super(type, world);
    }

    @Inject(method = "initDataTracker", at = @At("RETURN"))
    private void registerFireImmuneTracker(CallbackInfo ci) {
        dataTracker.startTracking(REDSTONE_POWER, false);
    }

    @Inject(method = "tick", at = @At(value = "TAIL"))
    public void tick(CallbackInfo ci) {
        if(isOnGround()) {
            if (isRedstone()) {
                Block block = world.getBlockState(getBlockPos()).getBlock();
                world.getBlockState(getBlockPos()).getStateForNeighborUpdate(Direction.UP, getBlockStateAtPos(), world, getBlockPos(), getBlockPos());
                world.updateNeighbors(getBlockPos(), block);
            }
        }
    }

    @Inject(method = "use", at = @At(value = "RETURN"))
    public void use(ItemStack usedItem, CallbackInfoReturnable<Integer> cir) {
        if(isOnGround()) {
            if (isRedstone()) {
                Block block = world.getBlockState(getBlockPos()).getBlock();
                world.getBlockState(getBlockPos()).getStateForNeighborUpdate(Direction.UP, getBlockStateAtPos(), world, getBlockPos(), getBlockPos());
                world.updateNeighbors(getBlockPos(), block);
            }
        }
    }

    @Inject(method = "removeIfInvalid", at = @At("RETURN"))
    public void removeIfInvaid(PlayerEntity player, CallbackInfoReturnable<Boolean> cir) {
        if(cir.getReturnValue()) {
            if(isRedstone()) {
                Block block = world.getBlockState(getBlockPos()).getBlock();
                world.getBlockState(getBlockPos()).getStateForNeighborUpdate(Direction.UP, getBlockStateAtPos(), world, getBlockPos(), getBlockPos());
                world.updateNeighbors(getBlockPos(), block);
            }
        }
    }

    @Inject(method = "onRemoved", at = @At("HEAD"))
    public void onRemoved(CallbackInfo ci) {
        if(isRedstone()) {
            Block block = world.getBlockState(getBlockPos()).getBlock();
            world.getBlockState(getBlockPos()).getStateForNeighborUpdate(Direction.UP, getBlockStateAtPos(), world, getBlockPos(), getBlockPos());
            world.updateNeighbors(getBlockPos(), block);
        }
    }


    @Override
    public boolean isRedstone() {
        return dataTracker.get(REDSTONE_POWER);
    }

    @Override
    public void setRedstone(boolean value) {
        dataTracker.set(REDSTONE_POWER, value);
    }
}
