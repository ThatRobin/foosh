package thatrobin.foosh.item;

import net.minecraft.entity.projectile.FishingBobberEntity;
import net.minecraft.sound.SoundEvents;
import thatrobin.foosh.api.*;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.sound.SoundCategory;
import net.minecraft.stat.Stats;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class AFishingRodItem extends FishingRodItem implements Vanishable {

    private final SoundInstance retrieve;
    private final SoundInstance cast;
    private final int baseLure;
    private final int baseLOTS;
    private final boolean lavaProof;

    public AFishingRodItem(Settings settings, SoundInstance retrieve, SoundInstance cast, int baseLure, int baseLOTS, boolean lavaProof) {
        super(settings);
        this.retrieve = retrieve;
        this.cast = cast;
        this.baseLure = baseLure;
        this.baseLOTS = baseLOTS;
        this.lavaProof = lavaProof;
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack heldStack = user.getStackInHand(hand);
        Random random = world.random;

        if (user.fishHook != null) {
            if (!world.isClient) {
                int damage = user.fishHook.use(heldStack);
                heldStack.damage(damage, user, player -> player.sendToolBreakStatus(hand));
            }

            world.playSound(null, user.getX(), user.getY(), user.getZ(), retrieve.getSound(), SoundCategory.NEUTRAL, retrieve.getVolume(random), retrieve.getPitch(random));
        } else {
            world.playSound(null, user.getX(), user.getY(), user.getZ(), cast.getSound(), SoundCategory.NEUTRAL, cast.getVolume(random), cast.getPitch(random));

            // Summon new fishing bobber
            if (!world.isClient) {
                int bonusLure = 0;
                int bonusLuck = 0;

                // Find buffing items in player inventory
                List<FishingBonus> found = new ArrayList<>();
                for (ItemStack stack : user.getInventory().main) {
                    Item item = stack.getItem();

                    if (item instanceof FishingBonus bonus) {
                        if (!found.contains(bonus)) {
                            if(bonus.shouldApply(world, user)) {
                                found.add(bonus);
                                bonusLure += bonus.getLure();
                                bonusLuck += bonus.getLuckOfTheSea();
                            }
                        }
                    }
                }

                // Calculate lure and luck
                int lure = EnchantmentHelper.getLure(heldStack) + baseLure + bonusLuck + bonusLure;
                int lots = EnchantmentHelper.getLuckOfTheSea(heldStack) + baseLOTS + bonusLuck + bonusLuck;

                // Summon bobber with stats
                FishingBobberEntity bobber = new FishingBobberEntity(user, world, lots, lure);
                world.spawnEntity(bobber);
                ((FireproofEntity) bobber).setFireproof(lavaProof);
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
        return true;
    }

    public static class Builder {

        private Item.Settings settings = new Item.Settings().group(ItemGroup.TOOLS).maxDamage(100);
        private SoundInstance retrieve = new SoundInstance(SoundEvents.ENTITY_FISHING_BOBBER_RETRIEVE, 1.0F, SoundInstance.DEFAULT_PITCH);
        private SoundInstance cast = new SoundInstance(SoundEvents.ENTITY_FISHING_BOBBER_THROW, 0.5F, SoundInstance.DEFAULT_PITCH);
        private int baseLure = 0;
        private int baseLOTS = 0;
        private boolean lavaProof = false;

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

        public Builder withRetrieveSound(SoundInstance sound) {
            this.retrieve = sound;
            return this;
        }

        public Builder withCastSound(SoundInstance sound) {
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

        public AFishingRodItem build() {
            return new AFishingRodItem(settings, retrieve, cast, baseLure, baseLOTS, lavaProof);
        }
    }
}
