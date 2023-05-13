package thatrobin.foosh.util;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.projectile.FishingBobberEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.stat.Stats;
import net.minecraft.tag.ItemTags;
import thatrobin.foosh.client.FishCaughtToast;
import thatrobin.foosh.item.FishingBag;
import thatrobin.foosh.registry.FooshTags;

public class FishManager {

    public static void setFishDataOnCatch(PlayerEntity playerEntity, ItemStack itemStack) {
        String name = playerEntity.getDisplayName().getString();

        NbtCompound compound = itemStack.getOrCreateNbt();
        compound.putString("caughtBy", name);

        itemStack.setNbt(compound);

        if (itemStack.getNbt() != null && itemStack.getNbt().contains("length")) {
            int rarity = 0;
            switch (itemStack.getNbt().getString("quality")) {
                case "common":
                    rarity = 16777215;
                    break;
                case "uncommon":
                    rarity = 6579300;
                    break;
                case "rare":
                    rarity = 16776960;
                    break;
                case "legendary":
                    rarity = 13107400;
            }
            MinecraftClient.getInstance().getToastManager().add(new FishCaughtToast(itemStack.getName().getString(), String.valueOf(itemStack.getNbt().getFloat("length")), rarity, itemStack));
        }
    }

    public static void addFishToBag(PlayerEntity playerEntity, ItemStack itemStack, FishingBobberEntity bobber) {
        PlayerInventory inventory = playerEntity.getInventory();
        boolean inserted = false;
        for (ItemStack stack: inventory.main) {
            if(!inserted) {
                if (stack.isIn(FooshTags.FISHING_BAGS)) {
                    if (itemStack.isIn(ItemTags.FISHES)) {
                        inserted = true;
                        ((FishingBag) stack.getItem()).addToBundle(stack, itemStack);
                        playerEntity.increaseStat(Stats.FISH_CAUGHT, 1);
                        bobber.discard();
                    }
                }
            }
        }
    }
}
