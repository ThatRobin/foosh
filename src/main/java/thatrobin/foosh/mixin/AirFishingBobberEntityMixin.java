package thatrobin.foosh.mixin;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.FishingBobberEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvents;
import net.minecraft.tag.FluidTags;
import net.minecraft.tag.Tag;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3f;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import thatrobin.foosh.api.ShulkEntity;
import thatrobin.foosh.item.AFishingRodItem;

@Mixin(FishingBobberEntity.class)
public abstract class AirFishingBobberEntityMixin extends Entity implements ShulkEntity {

    @Shadow public abstract PlayerEntity getPlayerOwner();

    @Shadow public abstract void remove(RemovalReason reason);

    private AirFishingBobberEntityMixin(EntityType<?> type, World world) {
        super(type, world);
    }

    public boolean isInAir(BlockState blockState) {
        return blockState.getBlock() == Blocks.AIR || blockState.getBlock() == Blocks.CAVE_AIR || blockState.getBlock() == Blocks.VOID_AIR;
    }

    @Inject(
            method = "tickFishingLogic",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/block/BlockState;isOf(Lnet/minecraft/block/Block;)Z", ordinal = 0),
            locals = LocalCapture.CAPTURE_FAILHARD
    )
    private void fishingParticleReplacement(BlockPos pos, CallbackInfo ci, ServerWorld serverWorld, int i, float n, float o, float p, double q, double r, double s, BlockState blockState) {
        if (isInAir(blockState) && world.getRegistryKey() == World.END && this.isShulk()) {
            if (this.random.nextFloat() < 0.15F) {
                serverWorld.spawnParticles(ParticleTypes.DRAGON_BREATH, q, r - 0.10000000149011612D, s, 1, o, 0.1D, p, 0.0D);
            }

            float k = o * 0.04F;
            float l = p * 0.04F;
            serverWorld.spawnParticles(ParticleTypes.DRAGON_BREATH, q, r, s, 0, l,  0.01D, -k, 1.0D);
            serverWorld.spawnParticles(ParticleTypes.DRAGON_BREATH, q, r, s, 0, -l, 0.01D, k, 1.0D);
        }
    }

    @Inject(
            method = "tickFishingLogic",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/block/BlockState;isOf(Lnet/minecraft/block/Block;)Z", ordinal = 1),
            locals = LocalCapture.CAPTURE_FAILHARD
    )
    private void splashParticleReplacement(BlockPos pos, CallbackInfo ci, ServerWorld serverWorld, int i, float n, float o, float p, double q, double r, double s, BlockState blockState2) {
        if (isInAir(blockState2) && world.getRegistryKey() == World.END && this.isShulk()) {
            float g = MathHelper.nextFloat(this.random, 0.0F, 360.0F) * 0.017453292F;
            float h = MathHelper.nextFloat(this.random, 25.0F, 60.0F);
            double j = this.getY() + (double)(MathHelper.sin(g) * h * 0.1F);
            serverWorld.spawnParticles(ParticleTypes.END_ROD, q, j, s, 2 + this.random.nextInt(4), 0.10000000149011612D, 0.10000000149011612D, 0.10000000149011612D, 0.0D);
        }
    }

    @Inject(method = "pullHookedEntity", at = @At(value = "HEAD"))
    protected void pullHookedEntity(Entity entity, CallbackInfo ci) {
        if(this.isShulk()) {
            double posX = this.getX() + MathHelper.nextFloat(this.random, -4.0F, 4.0F);
            double posY = this.getY() + MathHelper.nextFloat(this.random, -4.0F, 4.0F);
            double posZ = this.getZ() + MathHelper.nextFloat(this.random, -4.0F, 4.0F);

            this.requestTeleport(posX, posY, posZ);
            this.refreshPositionAfterTeleport(posX, posY, posZ);
        }
    }
    
}
