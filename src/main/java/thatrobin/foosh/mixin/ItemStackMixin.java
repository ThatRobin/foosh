package thatrobin.foosh.mixin;

import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.text.LiteralText;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import thatrobin.foosh.Foosh;

import java.util.List;

@Mixin(ItemStack.class)
public class ItemStackMixin {

    @Shadow
    @Nullable
    private NbtCompound nbt;

    @Inject(method = "getTooltip", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;hasNbt()Z", ordinal = 1), locals = LocalCapture.CAPTURE_FAILHARD)
    private void addEquipmentPowerTooltips(PlayerEntity player, TooltipContext context, CallbackInfoReturnable<List<Text>> cir, List<Text> list) {
        ItemStack item = (ItemStack) (Object) this;
        if (item.getNbt() != null) {

            if (!item.getNbt().isEmpty()) {
                NbtList lengths = item.getNbt().getList("lengths", 5);
                if (lengths != null && !lengths.isEmpty()) {
                    if (lengths.size() == 1) {
                        String length = lengths.get(0).toString();
                        length = StringUtils.substring(length, 0, length.length() - 1);
                        list.add(new LiteralText("Length: " + length + "cm").formatted(Formatting.GRAY));
                    } else {
                        list.add(new LiteralText("Multiple Fish").formatted(Formatting.GRAY));
                    }
                }
                String quality = item.getNbt().getString("quality");
                if (quality != null && !quality.isEmpty()) {
                    MutableText text1 = new LiteralText("Quality: ").formatted(Formatting.GRAY);
                    Formatting format = switch (quality) {
                        case "uncommon" -> Formatting.DARK_GRAY;
                        case "rare" -> Formatting.YELLOW;
                        case "legendary" -> Formatting.LIGHT_PURPLE;
                        default -> Formatting.GRAY;
                    };
                    quality = quality.substring(0, 1).toUpperCase() + quality.substring(1);
                    MutableText text2 = new LiteralText(quality).formatted(format);
                    list.add(text1.append(text2));
                }
            }
        }
    }

    @Inject(method = "areNbtEqual", at = @At("RETURN"), cancellable = true)
    private static void areNbtEqual(ItemStack left, ItemStack right, CallbackInfoReturnable<Boolean> cir) {
        if(!cir.getReturnValue()) {
            if(left.hasNbt() && right.hasNbt()) {
                NbtCompound leftNbt = left.getNbt();
                NbtCompound rightNbt = right.getNbt();
                if (leftNbt.getString("quality") == rightNbt.getString("quality")) {
                    NbtList leftNbtList = leftNbt.getList("lengths", 5);
                    NbtList rightNbtList = rightNbt.getList("lengths", 5);
                    NbtList mergedList = leftNbtList;
                    mergedList.addAll(rightNbtList.stream().toList());
                    NbtCompound merged = leftNbt;
                    merged.remove("lengths");
                    merged.put("lengths", mergedList);

                    left.setNbt(merged);
                    right.setNbt(merged);
                    cir.setReturnValue(true);
                }
            }
        }
        return;
    }

    @Inject(method = "split", at = @At("RETURN"), cancellable = true)
    private void split(int amount, CallbackInfoReturnable<ItemStack> cir) {
        ItemStack itemStack = cir.getReturnValue();
        ItemStack thisItemStack = ((ItemStack)(Object)this);
        if(!itemStack.isEmpty() && !thisItemStack.isEmpty()) {
            if(itemStack.hasNbt() && thisItemStack.hasNbt()) {
                NbtCompound leftNbt = thisItemStack.getNbt();
                NbtCompound rightNbt = itemStack.getNbt();
                if (leftNbt.contains("lengths")) {
                    NbtList nbtList = leftNbt.getList("lengths", 5);
                    NbtList leftNbtList = new NbtList();
                    NbtList rightNbtList = new NbtList();

                    List<NbtElement> leftList = nbtList.subList(0, thisItemStack.getCount());

                    List<NbtElement> rightList = nbtList.subList(thisItemStack.getCount(), nbtList.size());


                    leftNbtList.addAll(leftList);
                    rightNbtList.addAll(rightList);
                    leftNbt.remove("lengths");
                    leftNbt.put("lengths", leftNbtList);
                    rightNbt.remove("lengths");
                    rightNbt.put("lengths", rightNbtList);
                    thisItemStack.setNbt(leftNbt);
                    itemStack.setNbt(rightNbt);
                    cir.setReturnValue(itemStack);
                }
            }
        }
        return;
    }
}