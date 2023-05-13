package thatrobin.foosh.entity.goal;

import com.mojang.datafixers.util.Pair;
import net.minecraft.block.Blocks;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.fluid.Fluids;
import net.minecraft.server.command.LocateCommand;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.tag.BiomeTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import thatrobin.foosh.Foosh;
import thatrobin.foosh.entity.AnglerBobberEntity;
import thatrobin.foosh.entity.WanderingAnglerEntity;

import java.util.Optional;

public class UseItemGoal extends Goal {

    private final WanderingAnglerEntity actor;

    protected BlockPos target;
    private int lookTime;
    protected final float chance;
    protected final float range;

    public UseItemGoal(WanderingAnglerEntity actor, float chance, float range) {
        this.actor = actor;
        this.chance = chance;
        this.range = range;
    }

    @Override
    public boolean canStart() {
        if (this.actor.getRandom().nextFloat() >= this.chance) {
            return false;
        } else {
            Pair<BlockPos, RegistryEntry<Biome>> pair = ((ServerWorld)this.actor.getWorld()).locateBiome((biomeRegistryEntry -> biomeRegistryEntry.isIn(BiomeTags.IS_OCEAN)), this.actor.getBlockPos(), 32, 32, 64);
            if(pair != null) {
                this.target = pair.getFirst();
                this.actor.setPositionTarget(this.target, 12);
                return this.target != null;
            } else {
                return false;
            }
        }
    }

    @Override
    public boolean shouldContinue() {
        if (this.target == null) {
            return false;
        } else if (this.actor.squaredDistanceTo(this.target.getX(), this.target.getY(), this.target.getZ()) > (double)(this.range * this.range)) {
            return false;
        } else {
            return this.lookTime > 0 && this.actor.isUsingItem();
        }
    }

    @Override
    public void start() {
        this.lookTime = this.getTickCount(80 + this.actor.getRandom().nextInt(40));
        this.actor.getLookControl().lookAt(this.target.getX(), this.actor.getEyeY(), this.target.getZ());
        if(this.lookTime > 0) {
            WanderingAnglerEntity user = this.actor;
            World world = user.world;

            if (user.fishHook == null) {
                world.playSound(null, user.getX(), user.getY(), user.getZ(), SoundEvents.ENTITY_FISHING_BOBBER_THROW, SoundCategory.NEUTRAL, 0.5F, 0.4F / (world.getRandom().nextFloat() * 0.4F + 0.8F));

                if (!world.isClient) {
                    AnglerBobberEntity bobber = new AnglerBobberEntity(user, world, 0, 0);
                    world.spawnEntity(bobber);
                }
            }
        }
    }

    @Override
    public void stop() {
        this.target = null;
    }

    @Override
    public void tick() {
        if (this.target != null) {
            this.actor.getLookControl().lookAt(this.target.getX(), this.actor.getEyeY(), this.target.getZ());
            --this.lookTime;
            if (this.actor.fishHook != null && this.actor.fishHook.hookedEntity != null) {
                if (!this.actor.world.isClient) {
                    this.actor.fishHook.use(this.actor.getMainHandStack());
                    this.actor.world.playSound(null, this.actor.getX(), this.actor.getY(), this.actor.getZ(), SoundEvents.ENTITY_FISHING_BOBBER_RETRIEVE, SoundCategory.NEUTRAL, 1.0F, 0.4F / (this.actor.world.getRandom().nextFloat() * 0.4F + 0.8F));
                }
            }
        }
    }
}