package thatrobin.foosh.mixin;

import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
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

}