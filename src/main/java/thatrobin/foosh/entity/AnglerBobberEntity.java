package thatrobin.foosh.entity;

import com.mojang.logging.LogUtils;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.*;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.FishingBobberEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.LootTables;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.loot.context.LootContextTypes;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.Packet;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvents;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import thatrobin.foosh.Foosh;
import thatrobin.foosh.registry.FooshEntities;

import java.util.Iterator;
import java.util.List;

public class AnglerBobberEntity extends ProjectileEntity {

    private final Random velocityRandom;
    public boolean caughtFish;
    public int outOfOpenWaterTicks;
    private static final int field_30665 = 10;
    public static final TrackedData<Integer> HOOK_ENTITY_ID;
    public static final TrackedData<Boolean> CAUGHT_FISH;
    public int removalTimer;
    public int hookCountdown;
    public int waitCountdown;
    public int fishTravelCountdown;
    public float fishAngle;
    public boolean inOpenWater;
    @Nullable
    public Entity hookedEntity;
    public FishingBobberEntity.State state;
    public final int luckOfTheSeaLevel;
    public final int lureLevel;

    private AnglerBobberEntity(EntityType<? extends AnglerBobberEntity> type, World world, int luckOfTheSeaLevel, int lureLevel) {
        super(type, world);
        this.velocityRandom = Random.create();
        this.inOpenWater = true;
        this.state = FishingBobberEntity.State.FLYING;
        this.ignoreCameraFrustum = true;
        this.luckOfTheSeaLevel = Math.max(0, luckOfTheSeaLevel);
        this.lureLevel = Math.max(0, lureLevel);
    }

    public AnglerBobberEntity(EntityType<? extends AnglerBobberEntity> entityType, World world) {
        this((EntityType)entityType, world, 0, 0);
    }

    public AnglerBobberEntity(WanderingAnglerEntity thrower, World world, int luckOfTheSeaLevel, int lureLevel) {
        this(FooshEntities.ANGLER_BOBBER, world, luckOfTheSeaLevel, lureLevel);
        this.setOwner(thrower);
        float f = thrower.getPitch();
        float g = thrower.getHeadYaw();
        float h = MathHelper.cos(-g * 0.017453292F - 3.1415927F);
        float i = MathHelper.sin(-g * 0.017453292F - 3.1415927F);
        float j = -MathHelper.cos(-f * 0.017453292F);
        float k = MathHelper.sin(-f * 0.017453292F);
        double d = thrower.getX() - (double)i * 0.3D;
        double e = thrower.getEyeY();
        double l = thrower.getZ() - (double)h * 0.3D;
        this.refreshPositionAndAngles(d, e, l, g, f);
        Vec3d vec3d = new Vec3d((double)(-i), (double)MathHelper.clamp(-(k / j), -5.0F, 5.0F), (double)(-h));
        double m = vec3d.length();
        vec3d = vec3d.multiply(0.6D / m + this.random.nextTriangular(0.5D, 0.0103365D), 0.6D / m + this.random.nextTriangular(0.5D, 0.0103365D), 0.6D / m + this.random.nextTriangular(0.5D, 0.0103365D));
        this.setVelocity(vec3d);
        this.setYaw((float)(MathHelper.atan2(vec3d.x, vec3d.z) * 57.2957763671875D));
        this.setPitch((float)(MathHelper.atan2(vec3d.y, vec3d.horizontalLength()) * 57.2957763671875D));
        this.prevYaw = this.getYaw();
        this.prevPitch = this.getPitch();
    }

    protected void initDataTracker() {
        this.getDataTracker().startTracking(HOOK_ENTITY_ID, 0);
        this.getDataTracker().startTracking(CAUGHT_FISH, false);
    }

    public void onTrackedDataSet(TrackedData<?> data) {
        if (HOOK_ENTITY_ID.equals(data)) {
            int i = (Integer)this.getDataTracker().get(HOOK_ENTITY_ID);
            this.hookedEntity = i > 0 ? this.world.getEntityById(i - 1) : null;
        }

        if (CAUGHT_FISH.equals(data)) {
            this.caughtFish = (Boolean)this.getDataTracker().get(CAUGHT_FISH);
            if (this.caughtFish) {
                this.setVelocity(this.getVelocity().x, (double)(-0.4F * MathHelper.nextFloat(this.velocityRandom, 0.6F, 1.0F)), this.getVelocity().z);
            }
        }

        super.onTrackedDataSet(data);
    }

    public boolean shouldRender(double distance) {
        double d = 64.0D;
        return distance < 4096.0D;
    }

    public void updateTrackedPositionAndAngles(double x, double y, double z, float yaw, float pitch, int interpolationSteps, boolean interpolate) {
    }

    public void tick() {
        this.velocityRandom.setSeed(this.getUuid().getLeastSignificantBits() ^ this.world.getTime());
        super.tick();
        WanderingAnglerEntity anglerEntity = this.getAnglerOwner();
        if (anglerEntity == null) {
            this.discard();
        } else if (this.world.isClient || !this.removeIfInvalid(anglerEntity)) {
            if (this.onGround) {
                this.discard();
            }

            float f = 0.0F;
            BlockPos blockPos = this.getBlockPos();
            FluidState fluidState = this.world.getFluidState(blockPos);
            if (fluidState.isIn(FluidTags.WATER)) {
                f = fluidState.getHeight(this.world, blockPos);
            }

            boolean bl = f > 0.0F;
            if (this.state == FishingBobberEntity.State.FLYING) {
                if (this.hookedEntity != null) {
                    this.setVelocity(Vec3d.ZERO);
                    this.state = FishingBobberEntity.State.HOOKED_IN_ENTITY;
                    return;
                }

                if (bl) {
                    this.setVelocity(this.getVelocity().multiply(0.3D, 0.2D, 0.3D));
                    this.state = FishingBobberEntity.State.BOBBING;
                    return;
                }

                this.checkForCollision();
            } else {
                if (this.state == FishingBobberEntity.State.HOOKED_IN_ENTITY) {
                    if (this.hookedEntity != null) {
                        if (!this.hookedEntity.isRemoved() && this.hookedEntity.world.getRegistryKey() == this.world.getRegistryKey()) {
                            this.setPosition(this.hookedEntity.getX(), this.hookedEntity.getBodyY(0.8D), this.hookedEntity.getZ());
                        } else {
                            this.updateHookedEntityId((Entity)null);
                            this.state = FishingBobberEntity.State.FLYING;
                        }
                    }

                    return;
                }

                if (this.state == FishingBobberEntity.State.BOBBING) {
                    Vec3d vec3d = this.getVelocity();
                    double d = this.getY() + vec3d.y - (double)blockPos.getY() - (double)f;
                    if (Math.abs(d) < 0.01D) {
                        d += Math.signum(d) * 0.1D;
                    }

                    this.setVelocity(vec3d.x * 0.9D, vec3d.y - d * (double)this.random.nextFloat() * 0.2D, vec3d.z * 0.9D);
                    if (this.hookCountdown <= 0 && this.fishTravelCountdown <= 0) {
                        this.inOpenWater = true;
                    } else {
                        this.inOpenWater = this.inOpenWater && this.outOfOpenWaterTicks < 10 && this.isOpenOrWaterAround(blockPos);
                    }

                    if (bl) {
                        this.outOfOpenWaterTicks = Math.max(0, this.outOfOpenWaterTicks - 1);
                        if (this.caughtFish) {
                            this.setVelocity(this.getVelocity().add(0.0D, -0.1D * (double)this.velocityRandom.nextFloat() * (double)this.velocityRandom.nextFloat(), 0.0D));
                        }

                        if (!this.world.isClient) {
                            this.tickFishingLogic(blockPos);
                        }
                    } else {
                        this.outOfOpenWaterTicks = Math.min(10, this.outOfOpenWaterTicks + 1);
                    }
                }
            }

            if (!fluidState.isIn(FluidTags.WATER)) {
                this.setVelocity(this.getVelocity().add(0.0D, -0.03D, 0.0D));
            }

            this.move(MovementType.SELF, this.getVelocity());
            this.updateRotation();
            if (this.state == FishingBobberEntity.State.FLYING && (this.onGround || this.horizontalCollision)) {
                this.setVelocity(Vec3d.ZERO);
            }

            double e = 0.92D;
            this.setVelocity(this.getVelocity().multiply(0.92D));
            this.refreshPosition();
        }
    }

    private boolean removeIfInvalid(WanderingAnglerEntity player) {
        if (!player.isRemoved() && player.isAlive() && !(this.squaredDistanceTo(player) > 1024.0D)) {
            return false;
        } else {
            return true;
        }
    }

    private void checkForCollision() {
        HitResult hitResult = ProjectileUtil.getCollision(this, this::canHit);
        this.onCollision(hitResult);
    }

    protected boolean canHit(Entity entity) {
        return super.canHit(entity) || entity.isAlive() && entity instanceof ItemEntity;
    }

    protected void onEntityHit(EntityHitResult entityHitResult) {
        super.onEntityHit(entityHitResult);
        if (!this.world.isClient) {
            this.updateHookedEntityId(entityHitResult.getEntity());
        }

    }

    protected void onBlockHit(BlockHitResult blockHitResult) {
        super.onBlockHit(blockHitResult);
        this.setVelocity(this.getVelocity().normalize().multiply(blockHitResult.squaredDistanceTo(this)));
    }

    private void updateHookedEntityId(@Nullable Entity entity) {
        this.hookedEntity = entity;
        this.getDataTracker().set(HOOK_ENTITY_ID, entity == null ? 0 : entity.getId() + 1);
    }

    private void tickFishingLogic(BlockPos pos) {
        ServerWorld serverWorld = (ServerWorld)this.world;
        int i = 1;
        BlockPos blockPos = pos.up();
        if (this.random.nextFloat() < 0.25F && this.world.hasRain(blockPos)) {
            ++i;
        }

        if (this.random.nextFloat() < 0.5F && !this.world.isSkyVisible(blockPos)) {
            --i;
        }

        if (this.hookCountdown > 0) {
            --this.hookCountdown;
            if (this.hookCountdown <= 0) {
                this.waitCountdown = 0;
                this.fishTravelCountdown = 0;
                this.getDataTracker().set(CAUGHT_FISH, false);
            }
        } else {
            float f;
            float g;
            float h;
            double d;
            double e;
            double j;
            BlockState blockState;
            if (this.fishTravelCountdown > 0) {
                this.fishTravelCountdown -= i;
                if (this.fishTravelCountdown > 0) {
                    this.fishAngle += (float)this.random.nextTriangular(0.0D, 9.188D);
                    f = this.fishAngle * 0.017453292F;
                    g = MathHelper.sin(f);
                    h = MathHelper.cos(f);
                    d = this.getX() + (double)(g * (float)this.fishTravelCountdown * 0.1F);
                    e = (double)((float)MathHelper.floor(this.getY()) + 1.0F);
                    j = this.getZ() + (double)(h * (float)this.fishTravelCountdown * 0.1F);
                    blockState = serverWorld.getBlockState(new BlockPos(d, e - 1.0D, j));
                    if (blockState.isOf(Blocks.WATER)) {
                        if (this.random.nextFloat() < 0.15F) {
                            serverWorld.spawnParticles(ParticleTypes.BUBBLE, d, e - 0.10000000149011612D, j, 1, (double)g, 0.1D, (double)h, 0.0D);
                        }

                        float k = g * 0.04F;
                        float l = h * 0.04F;
                        serverWorld.spawnParticles(ParticleTypes.FISHING, d, e, j, 0, (double)l, 0.01D, (double)(-k), 1.0D);
                        serverWorld.spawnParticles(ParticleTypes.FISHING, d, e, j, 0, (double)(-l), 0.01D, (double)k, 1.0D);
                    }
                } else {
                    this.playSound(SoundEvents.ENTITY_FISHING_BOBBER_SPLASH, 0.25F, 1.0F + (this.random.nextFloat() - this.random.nextFloat()) * 0.4F);
                    double m = this.getY() + 0.5D;
                    serverWorld.spawnParticles(ParticleTypes.BUBBLE, this.getX(), m, this.getZ(), (int)(1.0F + this.getWidth() * 20.0F), (double)this.getWidth(), 0.0D, (double)this.getWidth(), 0.20000000298023224D);
                    serverWorld.spawnParticles(ParticleTypes.FISHING, this.getX(), m, this.getZ(), (int)(1.0F + this.getWidth() * 20.0F), (double)this.getWidth(), 0.0D, (double)this.getWidth(), 0.20000000298023224D);
                    this.hookCountdown = MathHelper.nextInt(this.random, 20, 40);
                    this.getDataTracker().set(CAUGHT_FISH, true);
                }
            } else if (this.waitCountdown > 0) {
                this.waitCountdown -= i;
                f = 0.15F;
                if (this.waitCountdown < 20) {
                    f += (float)(20 - this.waitCountdown) * 0.05F;
                } else if (this.waitCountdown < 40) {
                    f += (float)(40 - this.waitCountdown) * 0.02F;
                } else if (this.waitCountdown < 60) {
                    f += (float)(60 - this.waitCountdown) * 0.01F;
                }

                if (this.random.nextFloat() < f) {
                    g = MathHelper.nextFloat(this.random, 0.0F, 360.0F) * 0.017453292F;
                    h = MathHelper.nextFloat(this.random, 25.0F, 60.0F);
                    d = this.getX() + (double)(MathHelper.sin(g) * h) * 0.1D;
                    e = (double)((float)MathHelper.floor(this.getY()) + 1.0F);
                    j = this.getZ() + (double)(MathHelper.cos(g) * h) * 0.1D;
                    blockState = serverWorld.getBlockState(new BlockPos(d, e - 1.0D, j));
                    if (blockState.isOf(Blocks.WATER)) {
                        serverWorld.spawnParticles(ParticleTypes.SPLASH, d, e, j, 2 + this.random.nextInt(2), 0.10000000149011612D, 0.0D, 0.10000000149011612D, 0.0D);
                    }
                }

                if (this.waitCountdown <= 0) {
                    this.fishAngle = MathHelper.nextFloat(this.random, 0.0F, 360.0F);
                    this.fishTravelCountdown = MathHelper.nextInt(this.random, 20, 80);
                }
            } else {
                this.waitCountdown = MathHelper.nextInt(this.random, 100, 600);
                this.waitCountdown -= this.lureLevel * 20 * 5;
            }
        }
    }

    private boolean isOpenOrWaterAround(BlockPos pos) {
        FishingBobberEntity.PositionType positionType = FishingBobberEntity.PositionType.INVALID;

        for(int i = -1; i <= 2; ++i) {
            FishingBobberEntity.PositionType positionType2 = this.getPositionType(pos.add(-2, i, -2), pos.add(2, i, 2));
            switch(positionType2) {
                case INVALID:
                    return false;
                case ABOVE_WATER:
                    if (positionType == FishingBobberEntity.PositionType.INVALID) {
                        return false;
                    }
                    break;
                case INSIDE_WATER:
                    if (positionType == FishingBobberEntity.PositionType.ABOVE_WATER) {
                        return false;
                    }
            }

            positionType = positionType2;
        }

        return true;
    }

    private FishingBobberEntity.PositionType getPositionType(BlockPos start, BlockPos end) {
        return (FishingBobberEntity.PositionType)BlockPos.stream(start, end).map(this::getPositionType).reduce((positionType, positionType2) -> {
            return positionType == positionType2 ? positionType : FishingBobberEntity.PositionType.INVALID;
        }).orElse(FishingBobberEntity.PositionType.INVALID);
    }

    private FishingBobberEntity.PositionType getPositionType(BlockPos pos) {
        BlockState blockState = this.world.getBlockState(pos);
        if (!blockState.isAir() && !blockState.isOf(Blocks.LILY_PAD)) {
            FluidState fluidState = blockState.getFluidState();
            return fluidState.isIn(FluidTags.WATER) && fluidState.isStill() && blockState.getCollisionShape(this.world, pos).isEmpty() ? FishingBobberEntity.PositionType.INSIDE_WATER : FishingBobberEntity.PositionType.INVALID;
        } else {
            return FishingBobberEntity.PositionType.ABOVE_WATER;
        }
    }

    public boolean isInOpenWater() {
        return this.inOpenWater;
    }

    public void writeCustomDataToNbt(NbtCompound nbt) {
    }

    public void readCustomDataFromNbt(NbtCompound nbt) {
    }

    public int use(ItemStack usedItem) {
        Foosh.LOGGER.info("Used Rod");
        WanderingAnglerEntity anglerEntity = this.getAnglerOwner();
        if (!this.world.isClient && anglerEntity != null && !this.removeIfInvalid(anglerEntity)) {
            int i = 0;
            if (this.hookedEntity != null) {
                this.pullHookedEntity(this.hookedEntity);
                this.world.sendEntityStatus(this, (byte) 31);
                i = this.hookedEntity instanceof ItemEntity ? 3 : 5;
            } else if (this.hookCountdown > 0) {
                LootContext.Builder builder = (new LootContext.Builder((ServerWorld) this.world)).parameter(LootContextParameters.ORIGIN, this.getPos()).parameter(LootContextParameters.TOOL, usedItem).parameter(LootContextParameters.THIS_ENTITY, this).random(this.random).luck((float) this.luckOfTheSeaLevel);
                LootTable lootTable = this.world.getServer().getLootManager().getTable(LootTables.FISHING_GAMEPLAY);
                List<ItemStack> list = lootTable.generateLoot(builder.build(LootContextTypes.FISHING));
                Iterator var7 = list.iterator();

                while (var7.hasNext()) {
                    ItemStack itemStack = (ItemStack) var7.next();
                    ItemEntity itemEntity = new ItemEntity(this.world, this.getX(), this.getY(), this.getZ(), itemStack);
                    double d = anglerEntity.getX() - this.getX();
                    double e = anglerEntity.getY() - this.getY();
                    double f = anglerEntity.getZ() - this.getZ();
                    double g = 0.1D;
                    itemEntity.setVelocity(d * 0.1D, e * 0.1D + Math.sqrt(Math.sqrt(d * d + e * e + f * f)) * 0.08D, f * 0.1D);
                    this.world.spawnEntity(itemEntity);
                    anglerEntity.world.spawnEntity(new ExperienceOrbEntity(anglerEntity.world, anglerEntity.getX(), anglerEntity.getY() + 0.5D, anglerEntity.getZ() + 0.5D, this.random.nextInt(6) + 1));
                }

                i = 1;
            }

            if (this.onGround) {
                i = 2;
            }

            this.discard();
            return i;
        } else {
            return 0;
        }
    }

    public void handleStatus(byte status) {
        if (status == 31 && this.world.isClient && this.hookedEntity instanceof PlayerEntity && ((PlayerEntity)this.hookedEntity).isMainPlayer()) {
            this.pullHookedEntity(this.hookedEntity);
        }

        super.handleStatus(status);
    }

    protected void pullHookedEntity(Entity entity) {
        Entity entity2 = this.getOwner();
        if (entity2 != null) {
            Vec3d vec3d = (new Vec3d(entity2.getX() - this.getX(), entity2.getY() - this.getY(), entity2.getZ() - this.getZ())).multiply(0.1D);
            entity.setVelocity(entity.getVelocity().add(vec3d));
        }
    }

    protected MoveEffect getMoveEffect() {
        return MoveEffect.NONE;
    }

    public void remove(RemovalReason reason) {
        this.setAnglerFishHook(null);
        super.remove(reason);
    }

    public void onRemoved() {
        this.setAnglerFishHook(null);
    }

    @Override
    public void setOwner(@Nullable Entity entity) {
        if (entity != null) {
            this.ownerUuid = entity.getUuid();
            this.owner = entity;
        }
        this.setAnglerFishHook(this);
    }

    private void setAnglerFishHook(@Nullable AnglerBobberEntity fishingBobber) {
        WanderingAnglerEntity anglerEntity = this.getAnglerOwner();
        if (anglerEntity != null) {
            anglerEntity.fishHook = fishingBobber;
        }

    }

    @Nullable
    public WanderingAnglerEntity getAnglerOwner() {
        Entity entity = this.getOwner();
        return entity instanceof WanderingAnglerEntity ? (WanderingAnglerEntity)entity : null;
    }

    @Nullable
    public Entity getHookedEntity() {
        return this.hookedEntity;
    }

    public boolean canUsePortals() {
        return false;
    }

    public Packet<?> createSpawnPacket() {
        Entity entity = this.getOwner();
        return new EntitySpawnS2CPacket(this, entity == null ? this.getId() : entity.getId());
    }

    @Override
    public void onSpawnPacket(EntitySpawnS2CPacket packet) {
        super.onSpawnPacket(packet);
        if (this.getAnglerOwner() == null) {
            int data = packet.getEntityData();
            Foosh.LOGGER.error("Failed to recreate fishing hook on client. {} (id: {}) is not a valid owner.", this.world.getEntityById(data), data);
            this.kill();
        }
    }

    static {
        HOOK_ENTITY_ID = DataTracker.registerData(AnglerBobberEntity.class, TrackedDataHandlerRegistry.INTEGER);
        CAUGHT_FISH = DataTracker.registerData(AnglerBobberEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    }
}
