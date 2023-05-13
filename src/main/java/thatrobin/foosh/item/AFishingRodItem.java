package thatrobin.foosh.item;

import net.minecraft.entity.projectile.FishingBobberEntity;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.sound.SoundCategory;
import net.minecraft.stat.Stats;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import thatrobin.foosh.entity.bobbers.AirBobberEntity;
import thatrobin.foosh.entity.bobbers.LavaBobberEntity;
import thatrobin.foosh.entity.bobbers.RedstoneBobberEntity;

public class AFishingRodItem extends FishingRodItem implements Vanishable {

    private final SoundEvent retrieve;
    private final SoundEvent cast;
    private final int baseLure;
    private final int baseLOTS;
    private final boolean lavaProof;
    private final boolean redstone;
    private final boolean shulk;

    public AFishingRodItem(Settings settings, SoundEvent retrieve, SoundEvent cast, int baseLure, int baseLOTS, boolean lavaProof, boolean redstone, boolean shulk) {
        super(settings);
        this.retrieve = retrieve;
        this.cast = cast;
        this.baseLure = baseLure;
        this.baseLOTS = baseLOTS;
        this.lavaProof = lavaProof;
        this.redstone = redstone;
        this.shulk = shulk;
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack heldStack = user.getStackInHand(hand);

        if (user.fishHook != null) {
            if (!world.isClient) {
                int damage = user.fishHook.use(heldStack);
                heldStack.damage(damage, user, player -> player.sendToolBreakStatus(hand));
            }

            world.playSound(null, user.getX(), user.getY(), user.getZ(), retrieve, SoundCategory.NEUTRAL, 1.0F, 0.4F / (world.getRandom().nextFloat() * 0.4F + 0.8F));
        } else {
            world.playSound(null, user.getX(), user.getY(), user.getZ(), cast, SoundCategory.NEUTRAL, 0.5F, 0.4F / (world.getRandom().nextFloat() * 0.4F + 0.8F));

            if (!world.isClient) {
                int lure = EnchantmentHelper.getLure(heldStack) + baseLure;
                int lots = EnchantmentHelper.getLuckOfTheSea(heldStack) + baseLOTS;

                if (lavaProof) {
                    LavaBobberEntity bobber = new LavaBobberEntity(user, world, lots, lure);
                    world.spawnEntity(bobber);
                } else if (redstone) {
                    RedstoneBobberEntity bobber = new RedstoneBobberEntity(user, world, lots, lure);
                    world.spawnEntity(bobber);
                } else if (shulk) {
                    AirBobberEntity bobber = new AirBobberEntity(user, world, lots, lure);
                    world.spawnEntity(bobber);
                } else {
                    FishingBobberEntity bobber = new FishingBobberEntity(user, world, lots, lure);
                    world.spawnEntity(bobber);
                }

            }

            user.incrementStat(Stats.USED.getOrCreateStat(this));
        }

        return TypedActionResult.success(heldStack, world.isClient());
    }

    @Override
    public int getEnchantability() {
        return 1;
    }

    public boolean canFishInLava() {
        return lavaProof;
    }

    public boolean isRedstoneActivating() {
        return redstone;
    }

    public boolean canFishInAir() {
        return shulk;
    }

    @SuppressWarnings("all")
    public static class Builder {

        private Item.Settings settings = new Item.Settings().group(ItemGroup.TOOLS).maxDamage(100);
        private SoundEvent retrieve = SoundEvents.ENTITY_FISHING_BOBBER_RETRIEVE;
        private SoundEvent cast = SoundEvents.ENTITY_FISHING_BOBBER_THROW;
        private int baseLure = 0;
        private int baseLOTS = 0;
        private boolean lavaProof = false;
        private boolean redstone = false;
        private boolean shulk = false;

        public Builder() {

        }

        public Builder withSettings(Item.Settings settings) {
            this.settings = settings;
            return this;
        }

        public Builder durability(int durability) {
            this.settings.maxDamage(durability);
            return this;
        }

        public Builder withRetrieveSound(SoundEvent sound) {
            this.retrieve = sound;
            return this;
        }

        public Builder withCastSound(SoundEvent sound) {
            this.cast = sound;
            return this;
        }

        public Builder withBaseLure(int lure) {
            this.baseLure = lure;
            return this;
        }

        public Builder withBaseLOTS(int lots) {
            this.baseLOTS = lots;
            return this;
        }

        public Builder lavaProof(boolean lavaProof) {
            this.lavaProof = lavaProof;
            return this;
        }

        public Builder redstone(boolean redstone) {
            this.redstone = redstone;
            return this;
        }

        public Builder shulk(boolean shulk) {
            this.shulk = shulk;
            return this;
        }


        public AFishingRodItem build() {
            return new AFishingRodItem(settings, retrieve, cast, baseLure, baseLOTS, lavaProof, redstone, shulk);
        }
    }
}
