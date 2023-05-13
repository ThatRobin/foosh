package thatrobin.foosh.entity.bobbers;

import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.HoneyBlock;
import net.minecraft.entity.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.projectile.FishingBobberEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.LootTables;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.loot.context.LootContextTypes;
import net.minecraft.network.Packet;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.tag.FluidTags;
import net.minecraft.tag.ItemTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import org.jetbrains.annotations.Nullable;
import thatrobin.foosh.Foosh;
import thatrobin.foosh.item.AFishingRodItem;
import thatrobin.foosh.registry.FooshEntities;
import thatrobin.foosh.registry.FooshTags;
import thatrobin.foosh.util.FishManager;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class RedstoneBobberEntity extends FishingBobberEntity {

    private final Random velocityRandom = Random.create();

    public boolean caughtFish;
    public int outOfOpenWaterTicks;

    public int removalTimer;
    public int hookCountdown;
    public int waitCountdown;
    public int fishTravelCountdown;
    public float fishAngle;
    public boolean inOpenWater;
    @Nullable
    public Entity hookedEntity;
    public State state;
    public final int luckOfTheSeaLevel;
    public final int lureLevel;

    private RedstoneBobberEntity(EntityType<? extends RedstoneBobberEntity> type, World world, int luckOfTheSeaLevel, int lureLevel) {
        super(type, world);
        this.inOpenWater = true;
        this.state = State.FLYING;
        this.ignoreCameraFrustum = true;
        this.luckOfTheSeaLevel = Math.max(0, luckOfTheSeaLevel);
        this.lureLevel = Math.max(0, lureLevel);
    }

    public RedstoneBobberEntity(EntityType<? extends RedstoneBobberEntity> entityType, World world) {
        this(entityType, world, 0, 0);
    }

    public RedstoneBobberEntity(PlayerEntity thrower, World world, int luckOfTheSeaLevel, int lureLevel) {
        this(FooshEntities.REDSTONE_BOBBER, world, luckOfTheSeaLevel, lureLevel);
        this.setOwner(thrower);
        float f = thrower.getPitch();
        float g = thrower.getYaw();
        float h = MathHelper.cos(-g * 0.017453292F - 3.1415927F);
        float i = MathHelper.sin(-g * 0.017453292F - 3.1415927F);
        float j = -MathHelper.cos(-f * 0.017453292F);
        float k = MathHelper.sin(-f * 0.017453292F);
        double d = thrower.getX() - (double)i * 0.3D;
        double e = thrower.getEyeY();
        double l = thrower.getZ() - (double)h * 0.3D;
        this.refreshPositionAndAngles(d, e, l, g, f);
        Vec3d vec3d = new Vec3d(-i, MathHelper.clamp(-(k / j), -5.0F, 5.0F), -h);
        double m = vec3d.length();
        vec3d = vec3d.multiply(0.6D / m + this.random.nextTriangular(0.5D, 0.0103365D), 0.6D / m + this.random.nextTriangular(0.5D, 0.0103365D), 0.6D / m + this.random.nextTriangular(0.5D, 0.0103365D));
        this.setVelocity(vec3d);
        this.setYaw((float)(MathHelper.atan2(vec3d.x, vec3d.z) * 57.2957763671875D));
        this.setPitch((float)(MathHelper.atan2(vec3d.y, vec3d.horizontalLength()) * 57.2957763671875D));
        this.prevYaw = this.getYaw();
        this.prevPitch = this.getPitch();
    }

    @Override
    public void setOwner(@Nullable Entity entity) {
        if (entity != null) {
            this.ownerUuid = entity.getUuid();
            this.owner = entity;
        }
        this.setPlayerFishHook(this);
    }


    public final void setPlayerFishHook(@Nullable RedstoneBobberEntity fishingBobber) {
        PlayerEntity playerEntity = this.getPlayerOwner();
        if (playerEntity != null) {
            playerEntity.fishHook  = fishingBobber;
        }

    }

    @Override
    public void tick() {
        this.velocityRandom.setSeed(this.getUuid().getLeastSignificantBits() ^ this.world.getTime());

        if (!this.shot) {
            this.emitGameEvent(GameEvent.PROJECTILE_SHOOT, this.getOwner());
            this.shot = true;
        }

        if (!this.leftOwner) {
            this.leftOwner = this.shouldLeaveOwner();
        }

        if (!this.world.isClient) {
            this.setFlag(6, this.isGlowing());
        }
        this.baseTick();

        PlayerEntity playerEntity = this.getPlayerOwner();

        if (playerEntity == null) {
            this.discard();
        } else if (this.world.isClient || !this.removeIfInvalid(playerEntity)) {

            if (this.onGround) {
                ++this.removalTimer;
                if (this.removalTimer >= 1200) {
                    this.discard();
                    return;
                }
            } else {
                this.removalTimer = 0;
            }

            float f = 0.0F;
            BlockPos blockPos = this.getBlockPos();
            FluidState fluidState = this.world.getFluidState(blockPos);
            if (fluidState.isIn(FluidTags.WATER)) {
                f = fluidState.getHeight(this.world, blockPos);
            }

            boolean bl = f > 0.0F;
            if (this.state == State.FLYING) {
                if (this.hookedEntity != null) {
                    this.setVelocity(Vec3d.ZERO);
                    this.state = State.HOOKED_IN_ENTITY;
                    return;
                }

                if (bl) {
                    this.setVelocity(this.getVelocity().multiply(0.3D, 0.2D, 0.3D));
                    this.state = State.BOBBING;
                    return;
                }

                this.checkForCollision();
            } else {
                if (this.state == State.HOOKED_IN_ENTITY) {
                    if (this.hookedEntity != null) {
                        if (!this.hookedEntity.isRemoved() && this.hookedEntity.world.getRegistryKey() == this.world.getRegistryKey()) {
                            this.setPosition(this.hookedEntity.getX(), this.hookedEntity.getBodyY(0.8D), this.hookedEntity.getZ());
                        } else {
                            this.updateHookedEntityId(null);
                            this.state = State.FLYING;
                        }
                    }

                    return;
                }

                if (this.state == State.BOBBING) {
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
            if (this.state == State.FLYING && (this.onGround || this.horizontalCollision)) {
                this.setVelocity(Vec3d.ZERO);
            }

            this.setVelocity(this.getVelocity().multiply(0.92D));
            this.refreshPosition();
        }

        if(isOnGround()) {
            Block block = world.getBlockState(getBlockPos()).getBlock();
            world.getBlockState(getBlockPos()).getStateForNeighborUpdate(Direction.UP, getBlockStateAtPos(), world, getBlockPos(), getBlockPos());
            world.updateNeighbors(getBlockPos(), block);
        }
    }

    @SuppressWarnings("all")
    private boolean removeIfInvalid(PlayerEntity player) {
        ItemStack itemStack = player.getMainHandStack();
        ItemStack itemStack2 = player.getOffHandStack();
        boolean bl = false;
        boolean bl2 = false;
        if(itemStack.getItem() instanceof AFishingRodItem rodItem) {
            bl = rodItem.isRedstoneActivating();
        }
        if(itemStack2.getItem() instanceof AFishingRodItem rodItem2) {
            bl2 = rodItem2.isRedstoneActivating();
        }

        if (!player.isRemoved() && player.isAlive() && (bl || bl2) && !(this.squaredDistanceTo(player) > 1024.0D)) {
            return false;
        } else {
            this.discard();
            Block block = world.getBlockState(getBlockPos()).getBlock();
            world.getBlockState(getBlockPos()).getStateForNeighborUpdate(Direction.UP, getBlockStateAtPos(), world, getBlockPos(), getBlockPos());
            world.updateNeighbors(getBlockPos(), block);
            return true;
        }
    }

    @Override
    public Packet<?> createSpawnPacket() {
        Entity entity = this.getOwner();
        return new EntitySpawnS2CPacket(this, entity == null ? this.getId() : entity.getId());
    }

    @Override
    public void onSpawnPacket(EntitySpawnS2CPacket packet) {
        int i = packet.getId();
        double d = packet.getX();
        double e = packet.getY();
        double f = packet.getZ();
        this.updateTrackedPosition(d, e, f);
        this.refreshPositionAfterTeleport(d, e, f);
        this.setPitch(packet.getPitch());
        this.setYaw(packet.getYaw());
        this.setId(i);
        this.setUuid(packet.getUuid());
        Entity entity = this.world.getEntityById(packet.getEntityData());
        if (entity != null) {
            this.setOwner(entity);
        }
        if (this.getPlayerOwner() == null) {
            int g = packet.getEntityData();
            Foosh.LOGGER.error("Failed to recreate fishing hook on client. {} (id: {}) is not a valid owner.", this.world.getEntityById(g), g);
            this.kill();
        }
    }

    @Nullable
    @Override
    public PlayerEntity getPlayerOwner() {
        Entity entity = this.getOwner();
        return entity instanceof PlayerEntity ? (PlayerEntity)entity : null;
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
                    e = ((float)MathHelper.floor(this.getY()) + 1.0F);
                    j = this.getZ() + (double)(h * (float)this.fishTravelCountdown * 0.1F);
                    blockState = serverWorld.getBlockState(new BlockPos(d, e - 1.0D, j));
                    if (blockState.isOf(Blocks.WATER)) {
                        if (this.random.nextFloat() < 0.15F) {
                            serverWorld.spawnParticles(ParticleTypes.BUBBLE, d, e - 0.10000000149011612D, j, 1, g, 0.1D, h, 0.0D);
                        }

                        float k = g * 0.04F;
                        float l = h * 0.04F;
                        serverWorld.spawnParticles(ParticleTypes.FISHING, d, e, j, 0, l, 0.01D, (-k), 1.0D);
                        serverWorld.spawnParticles(ParticleTypes.FISHING, d, e, j, 0, -l, 0.01D, k, 1.0D);
                    }
                } else {
                    blockState = serverWorld.getBlockState(this.getBlockPos().down());

                    this.playSound(SoundEvents.ENTITY_FISHING_BOBBER_SPLASH, 0.25F, 1.0F + (this.random.nextFloat() - this.random.nextFloat()) * 0.4F);
                    double m = this.getY() + 0.5D;
                    if (blockState.isOf(Blocks.WATER)) {
                        serverWorld.spawnParticles(ParticleTypes.BUBBLE, this.getX(), m, this.getZ(), (int) (1.0F + this.getWidth() * 20.0F), this.getWidth(), 0.0D, this.getWidth(), 0.20000000298023224D);
                        serverWorld.spawnParticles(ParticleTypes.FISHING, this.getX(), m, this.getZ(), (int) (1.0F + this.getWidth() * 20.0F), this.getWidth(), 0.0D, this.getWidth(), 0.20000000298023224D);
                    }
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
                    e = ((float)MathHelper.floor(this.getY()) + 1.0F);
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
        PositionType positionType = PositionType.INVALID;

        for(int i = -1; i <= 2; ++i) {
            PositionType positionType2 = this.getPositionType(pos.add(-2, i, -2), pos.add(2, i, 2));
            switch(positionType2) {
                case INVALID:
                    return false;
                case ABOVE_WATER:
                    if (positionType == PositionType.INVALID) {
                        return false;
                    }
                    break;
                case INSIDE_WATER:
                    if (positionType == PositionType.ABOVE_WATER) {
                        return false;
                    }
            }

            positionType = positionType2;
        }

        return true;
    }

    private PositionType getPositionType(BlockPos start, BlockPos end) {
        return BlockPos.stream(start, end).map(this::getPositionType).reduce((positionType, positionType2) ->
            positionType == positionType2 ? positionType : PositionType.INVALID
        ).orElse(PositionType.INVALID);
    }

    private PositionType getPositionType(BlockPos pos) {
        BlockState blockState = this.world.getBlockState(pos);

        if (!blockState.isAir() && !blockState.isOf(Blocks.LILY_PAD)) {
            FluidState fluidState = blockState.getFluidState();
            return fluidState.isIn(FluidTags.WATER) && fluidState.isStill() && blockState.getCollisionShape(this.world, pos).isEmpty() ? PositionType.INSIDE_WATER : PositionType.INVALID;
        } else {
            return PositionType.ABOVE_WATER;
        }
    }

    public boolean isInOpenWater() {
        return this.inOpenWater;
    }

    @Override
    public int use(ItemStack usedItem) {
        PlayerEntity playerEntity = this.getPlayerOwner();
        if (!this.world.isClient && playerEntity != null && !this.removeIfInvalid(playerEntity)) {
            int i = 0;
            if (this.hookedEntity != null) {
                this.pullHookedEntity(this.hookedEntity);
                Criteria.FISHING_ROD_HOOKED.trigger((ServerPlayerEntity)playerEntity, usedItem, this, Collections.emptyList());
                this.world.sendEntityStatus(this, (byte)31);
                i = this.hookedEntity instanceof ItemEntity ? 3 : 5;
            } else if (this.hookCountdown > 0) {
                LootContext.Builder builder = (new LootContext.Builder((ServerWorld)this.world)).parameter(LootContextParameters.ORIGIN, this.getPos()).parameter(LootContextParameters.TOOL, usedItem).parameter(LootContextParameters.THIS_ENTITY, this).random(this.random).luck((float)this.luckOfTheSeaLevel + playerEntity.getLuck());

                LootTable table = Objects.requireNonNull(this.world.getServer()).getLootManager().getTable(LootTables.FISHING_GAMEPLAY);
                List<ItemStack> list = table.generateLoot(builder.build(LootContextTypes.FISHING));
                Criteria.FISHING_ROD_HOOKED.trigger((ServerPlayerEntity)playerEntity, usedItem, this, list);

                for (ItemStack itemStack : list) {
                    ItemEntity itemEntity = new ItemEntity(this.world, this.getX(), this.getY(), this.getZ(), itemStack);
                    double d = playerEntity.getX() - this.getX();
                    double e = playerEntity.getY() - this.getY();
                    double f = playerEntity.getZ() - this.getZ();
                    itemEntity.setVelocity(d * 0.1D, e * 0.1D + Math.sqrt(Math.sqrt(d * d + e * e + f * f)) * 0.08D, f * 0.1D);

                    FishManager.setFishDataOnCatch(playerEntity, itemStack);

                    PlayerInventory playerInventory = playerEntity.getInventory();
                    if(!playerInventory.main.stream().filter(itemStack1 -> itemStack1.isIn(FooshTags.FISHING_BAGS)).toList().isEmpty()) {
                        FishManager.addFishToBag(playerEntity, itemStack, this);
                    } else {
                        this.world.spawnEntity(itemEntity);
                    }

                    playerEntity.world.spawnEntity(new ExperienceOrbEntity(playerEntity.world, playerEntity.getX(), playerEntity.getY() + 0.5D, playerEntity.getZ() + 0.5D, this.random.nextInt(6) + 1));
                    if (itemStack.isIn(ItemTags.FISHES)) {
                        playerEntity.increaseStat(Stats.FISH_CAUGHT, 1);
                    }
                }

                i = 1;
            }

            if (this.onGround) {
                i = 2;
            }

            this.discard();
            if(isOnGround()) {
                Block block = world.getBlockState(getBlockPos()).getBlock();
                world.getBlockState(getBlockPos()).getStateForNeighborUpdate(Direction.UP, getBlockStateAtPos(), world, getBlockPos(), getBlockPos());
                world.updateNeighbors(getBlockPos(), block);
            }
            return i;
        } else {
            if(isOnGround()) {
                Block block = world.getBlockState(getBlockPos()).getBlock();
                world.getBlockState(getBlockPos()).getStateForNeighborUpdate(Direction.UP, getBlockStateAtPos(), world, getBlockPos(), getBlockPos());
                world.updateNeighbors(getBlockPos(), block);
            }
            return 0;
        }
    }

    @Override
    public void handleStatus(byte status) {
        if (status == 31 && this.world.isClient && this.hookedEntity instanceof PlayerEntity && ((PlayerEntity)this.hookedEntity).isMainPlayer()) {
            this.pullHookedEntity(this.hookedEntity);
        }

        switch(status) {
            case 53:
                HoneyBlock.addRegularParticles(this);
            default:
        }
    }

    @Override
    public void remove(RemovalReason reason) {
        this.setPlayerFishHook(null);
        this.setRemoved(reason);
    }

    @Override
    public void onRemoved() {
        Block block = world.getBlockState(getBlockPos()).getBlock();
        world.getBlockState(getBlockPos()).getStateForNeighborUpdate(Direction.UP, getBlockStateAtPos(), world, getBlockPos(), getBlockPos());
        world.updateNeighbors(getBlockPos(), block);
        this.setPlayerFishHook(null);
    }

}