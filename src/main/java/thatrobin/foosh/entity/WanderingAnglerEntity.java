package thatrobin.foosh.entity;

import com.google.common.collect.ImmutableMap;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ExperienceOrbEntity;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.*;
import net.minecraft.entity.passive.MerchantEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.passive.WanderingTraderEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.FishingBobberEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.potion.PotionUtil;
import net.minecraft.potion.Potions;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.village.TradeOffer;
import net.minecraft.village.TradeOfferList;
import net.minecraft.village.TradeOffers;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import thatrobin.foosh.client.rendering.AnglerBobberEntityRenderer;
import thatrobin.foosh.entity.goal.UseItemGoal;
import thatrobin.foosh.registry.FooshItems;

import java.util.EnumSet;

public class WanderingAnglerEntity  extends MerchantEntity {
    private static final int field_30629 = 5;
    @Nullable
    private BlockPos wanderTarget;
    private int despawnDelay;
    @Nullable
    public AnglerBobberEntity fishHook;
    private static TradeOffers.Factory[] TRADE_OFFERS;

    public WanderingAnglerEntity(EntityType<? extends WanderingAnglerEntity> entityType, World world) {
        super(entityType, world);
    }

    protected void initGoals() {
        this.goalSelector.add(0, new HoldInHandsGoal(this, FooshItems.BAMBOO_ROD.getDefaultStack(), SoundEvents.ENTITY_WANDERING_TRADER_DISAPPEARED, (wanderingAngler) -> {
            return true;
        }));
        this.goalSelector.add(0, new UseItemGoal(this, 0.02F, 8.0F));
        this.goalSelector.add(1, new SwimGoal(this));
        this.goalSelector.add(2, new StopFollowingCustomerGoal(this));
        this.goalSelector.add(2, new FleeEntityGoal(this, ZombieEntity.class, 8.0F, 0.5D, 0.5D));
        this.goalSelector.add(2, new FleeEntityGoal(this, EvokerEntity.class, 12.0F, 0.5D, 0.5D));
        this.goalSelector.add(2, new FleeEntityGoal(this, VindicatorEntity.class, 8.0F, 0.5D, 0.5D));
        this.goalSelector.add(2, new FleeEntityGoal(this, VexEntity.class, 8.0F, 0.5D, 0.5D));
        this.goalSelector.add(2, new FleeEntityGoal(this, PillagerEntity.class, 15.0F, 0.5D, 0.5D));
        this.goalSelector.add(2, new FleeEntityGoal(this, IllusionerEntity.class, 12.0F, 0.5D, 0.5D));
        this.goalSelector.add(2, new FleeEntityGoal(this, ZoglinEntity.class, 10.0F, 0.5D, 0.5D));
        this.goalSelector.add(2, new EscapeDangerGoal(this, 0.5D));
        //this.goalSelector.add(2, new LookAtCustomerGoal(this));
        this.goalSelector.add(3, new WanderingAnglerEntity.WanderToTargetGoal(this, 2.0D, 0.35D));
        this.goalSelector.add(4, new GoToWalkTargetGoal(this, 0.35D));
        this.goalSelector.add(8, new WanderAroundFarGoal(this, 0.35D));
        //this.goalSelector.add(10, new StopAndLookAtEntityGoal(this, PlayerEntity.class, 3.0F, 1.0F));
        //this.goalSelector.add(11, new LookAtEntityGoal(this, MobEntity.class, 8.0F));
    }

    @Nullable
    public PassiveEntity createChild(ServerWorld world, PassiveEntity entity) {
        return null;
    }

    public boolean isLeveledMerchant() {
        return false;
    }

    public ActionResult interactMob(PlayerEntity player, Hand hand) {
        ItemStack itemStack = player.getStackInHand(hand);
        if (!itemStack.isOf(Items.VILLAGER_SPAWN_EGG) && this.isAlive() && !this.hasCustomer() && !this.isBaby()) {
            if (hand == Hand.MAIN_HAND) {
                player.incrementStat(Stats.TALKED_TO_VILLAGER);
            }

            if (this.getOffers().isEmpty()) {
                return ActionResult.success(this.world.isClient);
            } else {
                if (!this.world.isClient) {
                    this.setCustomer(player);
                    this.sendOffers(player, this.getDisplayName(), 1);
                }

                return ActionResult.success(this.world.isClient);
            }
        } else {
            return super.interactMob(player, hand);
        }
    }

    @Override
    public boolean canGather(ItemStack stack) {
        return this.canPickupItem(stack);
    }

    protected void fillRecipes() {
        if (TRADE_OFFERS != null) {
            TradeOfferList tradeOfferList = this.getOffers();
            this.fillRecipesFromPool(tradeOfferList, TRADE_OFFERS, 5);
        }
    }

    public void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);
        nbt.putInt("DespawnDelay", this.despawnDelay);
        if (this.wanderTarget != null) {
            nbt.put("WanderTarget", NbtHelper.fromBlockPos(this.wanderTarget));
        }

    }

    public void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);
        if (nbt.contains("DespawnDelay", 99)) {
            this.despawnDelay = nbt.getInt("DespawnDelay");
        }

        if (nbt.contains("WanderTarget")) {
            this.wanderTarget = NbtHelper.toBlockPos(nbt.getCompound("WanderTarget"));
        }

        this.setBreedingAge(Math.max(0, this.getBreedingAge()));
    }

    public boolean canImmediatelyDespawn(double distanceSquared) {
        return false;
    }

    protected void afterUsing(TradeOffer offer) {
        if (offer.shouldRewardPlayerExperience()) {
            int i = 3 + this.random.nextInt(4);
            this.world.spawnEntity(new ExperienceOrbEntity(this.world, this.getX(), this.getY() + 0.5D, this.getZ(), i));
        }

    }

    protected SoundEvent getAmbientSound() {
        return this.hasCustomer() ? SoundEvents.ENTITY_WANDERING_TRADER_TRADE : SoundEvents.ENTITY_WANDERING_TRADER_AMBIENT;
    }

    protected SoundEvent getHurtSound(DamageSource source) {
        return SoundEvents.ENTITY_WANDERING_TRADER_HURT;
    }

    protected SoundEvent getDeathSound() {
        return SoundEvents.ENTITY_WANDERING_TRADER_DEATH;
    }

    protected SoundEvent getDrinkSound(ItemStack stack) {
        return stack.isOf(Items.MILK_BUCKET) ? SoundEvents.ENTITY_WANDERING_TRADER_DRINK_MILK : SoundEvents.ENTITY_WANDERING_TRADER_DRINK_POTION;
    }

    protected SoundEvent getTradingSound(boolean sold) {
        return sold ? SoundEvents.ENTITY_WANDERING_TRADER_YES : SoundEvents.ENTITY_WANDERING_TRADER_NO;
    }

    public SoundEvent getYesSound() {
        return SoundEvents.ENTITY_WANDERING_TRADER_YES;
    }

    public void tickMovement() {
        super.tickMovement();
        if (!this.world.isClient) {
            this.tickDespawnDelay();
        }

    }

    private void tickDespawnDelay() {
        if (this.despawnDelay > 0 && !this.hasCustomer() && --this.despawnDelay == 0) {
            this.discard();
        }

    }

    static {
        TRADE_OFFERS = new TradeOffers.Factory[]{new WanderingAnglerEntity.SellItemFactory(FooshItems.BAMBOO_ROD, Items.EMERALD, 2, 1, 5, 1),
                new WanderingAnglerEntity.SellItemFactory(FooshItems.PRISMARINE_ROD, Items.EMERALD, 12, 1, 16, 1),
                new WanderingAnglerEntity.SellItemFactory(FooshItems.FISHING_BAG, Items.EMERALD, 8, 1, 3, 1),
                new WanderingAnglerEntity.SellItemFactory(FooshItems.FISHING_BAG_2, Items.EMERALD, 16, 1, 3, 1),
                new WanderingAnglerEntity.SellItemFactory(FooshItems.FISHING_BAG_3, Items.EMERALD, 32, 1, 3, 1)};
    }


    public void setWanderTarget(@Nullable BlockPos wanderTarget) {
        this.wanderTarget = wanderTarget;
    }

    @Nullable
    BlockPos getWanderTarget() {
        return this.wanderTarget;
    }

    class WanderToTargetGoal extends Goal {
        final WanderingAnglerEntity trader;
        final double proximityDistance;
        final double speed;

        WanderToTargetGoal(WanderingAnglerEntity trader, double proximityDistance, double speed) {
            this.trader = trader;
            this.proximityDistance = proximityDistance;
            this.speed = speed;
            this.setControls(EnumSet.of(Control.MOVE));
        }

        public void stop() {
            this.trader.setWanderTarget((BlockPos)null);
            WanderingAnglerEntity.this.navigation.stop();
        }

        public boolean canStart() {
            BlockPos blockPos = this.trader.getWanderTarget();
            return blockPos != null && this.isTooFarFrom(blockPos, this.proximityDistance);
        }

        public void tick() {
            BlockPos blockPos = this.trader.getWanderTarget();
            if (blockPos != null && WanderingAnglerEntity.this.navigation.isIdle()) {
                if (this.isTooFarFrom(blockPos, 10.0D)) {
                    Vec3d vec3d = (new Vec3d((double)blockPos.getX() - this.trader.getX(), (double)blockPos.getY() - this.trader.getY(), (double)blockPos.getZ() - this.trader.getZ())).normalize();
                    Vec3d vec3d2 = vec3d.multiply(10.0D).add(this.trader.getX(), this.trader.getY(), this.trader.getZ());
                    WanderingAnglerEntity.this.navigation.startMovingTo(vec3d2.x, vec3d2.y, vec3d2.z, this.speed);
                } else {
                    WanderingAnglerEntity.this.navigation.startMovingTo((double)blockPos.getX(), (double)blockPos.getY(), (double)blockPos.getZ(), this.speed);
                }
            }

        }

        private boolean isTooFarFrom(BlockPos pos, double proximityDistance) {
            return !pos.isWithinDistance(this.trader.getPos(), proximityDistance);
        }
    }

    static class SellItemFactory implements TradeOffers.Factory {
        private final ItemStack sell;
        private final ItemStack buy;
        private final int price;
        private final int count;
        private final int maxUses;
        private final int experience;
        private final float multiplier;

        public SellItemFactory(Block sell, Block buy, int price, int count, int maxUses, int experience) {
            this(new ItemStack(sell), new ItemStack(buy), price, count, maxUses, experience);
        }

        public SellItemFactory(Item sell, Item buy, int price, int count, int experience) {
            this(new ItemStack(sell), new ItemStack(buy), price, count, 12, experience);
        }

        public SellItemFactory(Item sell, Item buy, int price, int count, int maxUses, int experience) {
            this(new ItemStack(sell), new ItemStack(buy), price, count, maxUses, experience);
        }

        public SellItemFactory(ItemStack sell, ItemStack buy, int price, int count, int maxUses, int experience) {
            this(sell, buy, price, count, maxUses, experience, 0.05F);
        }

        public SellItemFactory(ItemStack sell, ItemStack buy, int price, int count, int maxUses, int experience, float multiplier) {
            this.sell = sell;
            this.buy = buy;
            this.price = price;
            this.count = count;
            this.maxUses = maxUses;
            this.experience = experience;
            this.multiplier = multiplier;
        }

        public TradeOffer create(Entity entity, Random random) {
            return new TradeOffer(new ItemStack(this.buy.getItem(), this.price), new ItemStack(this.sell.getItem(), this.count), this.maxUses, this.experience, this.multiplier);
        }
    }

}