package thatrobin.foosh.item;

import net.minecraft.client.item.BundleTooltipData;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.client.item.TooltipData;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.StackReference;
import net.minecraft.item.*;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.screen.slot.Slot;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.tag.ItemTags;
import net.minecraft.text.Text;
import net.minecraft.util.ClickType;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import thatrobin.foosh.Foosh;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

public class FishingBag extends BundleItem {

    private static final int ITEM_BAR_COLOR = MathHelper.packRgb(0.4F, 0.4F, 1.0F);

    public int max_storage;

    public FishingBag(Settings settings) {
        this(settings, 64);
    }

    public FishingBag(Settings settings, int max_storage) {
        super(settings);
        setBundleMax(max_storage);
    }


    public boolean onStackClicked(ItemStack stack, Slot slot, ClickType clickType, PlayerEntity player) {
        if (clickType != ClickType.RIGHT) {
            return false;
        } else {
            ItemStack itemStack = slot.getStack();
            if (itemStack.isEmpty()) {
                this.playRemoveOneSound(player);
                removeFirstStack(stack).ifPresent((removedStack) -> {
                    addToBundle(stack, slot.insertStack(removedStack));
                });
            } else if (itemStack.getItem().canBeNested() && slot.getStack().isIn(ItemTags.FISHES)) {
                int i = (getBundleMax() - getBundleOccupancy(stack)) / getItemOccupancy(itemStack);
                int j = addToBundle(stack, slot.takeStackRange(itemStack.getCount(), i, player));
                if (j > 0) {
                    this.playInsertSound(player);
                }
            }

            return true;
        }
    }

    public boolean onClicked(ItemStack stack, ItemStack otherStack, Slot slot, ClickType clickType, PlayerEntity player, StackReference cursorStackReference) {
        if (clickType == ClickType.RIGHT && slot.canTakePartial(player)) {

            if (otherStack.isEmpty()) {
                removeFirstStack(stack).ifPresent((itemStack) -> {
                    this.playRemoveOneSound(player);
                    cursorStackReference.set(itemStack);
                });
            } else {
                if (otherStack.isIn(ItemTags.FISHES)) {
                    int i = addToBundle(stack, otherStack);
                    if (i > 0) {
                        this.playInsertSound(player);
                        otherStack.decrement(i);
                    }
                }
            }
            return true;
        } else {
            return false;
        }
    }

    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack itemStack = user.getStackInHand(hand);
        if (dropAllBundledItems(itemStack, user)) {
            this.playDropContentsSound(user);
            user.incrementStat(Stats.USED.getOrCreateStat(this));
            return TypedActionResult.success(itemStack, world.isClient());
        } else {
            return TypedActionResult.fail(itemStack);
        }
    }

    public boolean isItemBarVisible(ItemStack stack) {
        return getBundleOccupancy(stack) > 0;
    }

    public int getItemBarStep(ItemStack stack) {
        return Math.min(1 + 12 * getBundleOccupancy(stack) / getBundleMax(), 13);
    }

    public int getItemBarColor(ItemStack stack) {
        return ITEM_BAR_COLOR;
    }

    public int addToBundle(ItemStack bundle, ItemStack stack) {
        if (!stack.isEmpty() && stack.getItem().canBeNested()) {
            NbtCompound nbtCompound = bundle.getOrCreateNbt();
            if (!nbtCompound.contains("Items")) {
                nbtCompound.put("Items", new NbtList());
            }

            int i = getBundleOccupancy(bundle);
            int j = getItemOccupancy(stack);
            int k = Math.min(stack.getCount(), (getBundleMax() - i) / j);
            if (k == 0) {
                return 0;
            } else {
                NbtList nbtList = nbtCompound.getList("Items", 10);
                Optional<NbtCompound> optional = canMergeStack(stack, nbtList);
                if (optional.isPresent()) {
                    NbtCompound nbtCompound2 = (NbtCompound) optional.get();
                    ItemStack itemStack = ItemStack.fromNbt(nbtCompound2);
                    itemStack.increment(k);
                    itemStack.writeNbt(nbtCompound2);
                    nbtList.remove(nbtCompound2);
                    nbtList.add(0, nbtCompound2);
                } else {
                    ItemStack nbtCompound2 = stack.copy();
                    nbtCompound2.setCount(k);
                    NbtCompound itemStack = new NbtCompound();
                    nbtCompound2.writeNbt(itemStack);
                    nbtList.add(0, itemStack);
                }

                return k;
            }
        } else {
            return 0;
        }
    }

    private static Optional<NbtCompound> canMergeStack(ItemStack stack, NbtList items) {
        if (stack.isOf(Items.BUNDLE)) {
            return Optional.empty();
        } else {
            Stream<NbtElement> var10000 = items.stream();
            Objects.requireNonNull(NbtCompound.class);
            var10000 = var10000.filter(NbtCompound.class::isInstance);
            Objects.requireNonNull(NbtCompound.class);
            return var10000.map(NbtCompound.class::cast).filter((item) -> {
                return ItemStack.canCombine(ItemStack.fromNbt(item), stack)  && (ItemStack.fromNbt((NbtCompound) item).getCount() + stack.getCount() <= stack.getMaxCount());
            }).findFirst();
        }
    }

    private int getItemOccupancy(ItemStack stack) {
        return 1;
    }

    private int getBundleOccupancy(ItemStack stack) {
        return getBundledStacks(stack).mapToInt((itemStack) -> {
            return getItemOccupancy(itemStack) * itemStack.getCount();
        }).sum();
    }

    private static Optional<ItemStack> removeFirstStack(ItemStack stack) {
        NbtCompound nbtCompound = stack.getOrCreateNbt();
        if (!nbtCompound.contains("Items")) {
            return Optional.empty();
        } else {
            NbtList nbtList = nbtCompound.getList("Items", 10);
            if (nbtList.isEmpty()) {
                return Optional.empty();
            } else {
                NbtCompound nbtCompound2 = nbtList.getCompound(0);
                ItemStack itemStack = ItemStack.fromNbt(nbtCompound2);
                nbtList.remove(0);
                if (nbtList.isEmpty()) {
                    stack.removeSubNbt("Items");
                }

                return Optional.of(itemStack);
            }
        }
    }

    private static boolean dropAllBundledItems(ItemStack stack, PlayerEntity player) {
        NbtCompound nbtCompound = stack.getOrCreateNbt();
        if (!nbtCompound.contains("Items")) {
            return false;
        } else {
            if (player instanceof ServerPlayerEntity) {
                NbtList nbtList = nbtCompound.getList("Items", 10);

                for (int i = 0; i < nbtList.size(); ++i) {
                    NbtCompound nbtCompound2 = nbtList.getCompound(i);
                    ItemStack itemStack = ItemStack.fromNbt(nbtCompound2);
                    player.dropItem(itemStack, true);
                }
            }

            stack.removeSubNbt("Items");
            return true;
        }
    }

    private static Stream<ItemStack> getBundledStacks(ItemStack stack) {
        NbtCompound nbtCompound = stack.getNbt();
        if (nbtCompound == null) {
            return Stream.empty();
        } else {
            NbtList nbtList = nbtCompound.getList("Items", 10);
            Stream<NbtElement> var10000 = nbtList.stream();
            Objects.requireNonNull(NbtCompound.class);
            return var10000.map(NbtCompound.class::cast).map(ItemStack::fromNbt);
        }
    }

    public Optional<TooltipData> getTooltipData(ItemStack stack) {
        DefaultedList<ItemStack> defaultedList = DefaultedList.of();
        Stream<ItemStack> var10000 = getBundledStacks(stack);
        Objects.requireNonNull(defaultedList);
        var10000.forEach((stack2) -> defaultedList.add(stack2));
        return Optional.of(new BundleTooltipData(defaultedList, getBundleOccupancy(stack)));
    }

    public void appendTooltip(ItemStack stack, World world, List<Text> tooltip, TooltipContext context) {
        tooltip.add((Text.translatable("item.minecraft.bundle.fullness", new Object[]{getBundleOccupancy(stack), getBundleMax()})).formatted(Formatting.GRAY));
    }

    public void onItemEntityDestroyed(ItemEntity entity) {
        ItemUsage.spawnItemContents(entity, getBundledStacks(entity.getStack()));
    }

    private void playRemoveOneSound(Entity entity) {
        entity.playSound(SoundEvents.ITEM_BUNDLE_REMOVE_ONE, 0.8F, 0.8F + entity.getWorld().getRandom().nextFloat() * 0.4F);
    }

    private void playInsertSound(Entity entity) {
        entity.playSound(SoundEvents.ITEM_BUNDLE_INSERT, 0.8F, 0.8F + entity.getWorld().getRandom().nextFloat() * 0.4F);
    }

    private void playDropContentsSound(Entity entity) {
        entity.playSound(SoundEvents.ITEM_BUNDLE_DROP_CONTENTS, 0.8F, 0.8F + entity.getWorld().getRandom().nextFloat() * 0.4F);
    }

    public void setBundleMax(int amount) {
        this.max_storage = amount;
    }

    public int getBundleMax() {
        return this.max_storage;
    }
}
