package thatrobin.foosh.mixin;

import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.client.util.InputUtil;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.StackReference;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.LootTables;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.loot.context.LootContextTypes;
import net.minecraft.loot.function.LootFunction;
import net.minecraft.loot.function.LootFunctionManager;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.slot.Slot;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.ClickType;
import net.minecraft.util.Formatting;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import thatrobin.foosh.Foosh;
import thatrobin.foosh.client.FishCaughtToast;
import thatrobin.foosh.networking.ModPackets;
import thatrobin.foosh.registry.FishRegistry;

import java.util.List;

@Mixin(ItemStack.class)
public abstract class ItemStackMixin {

    @Shadow public abstract Item getItem();

    @Inject(method = "getTooltip", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;hasNbt()Z", ordinal = 1), locals = LocalCapture.CAPTURE_FAILHARD)
    private void addLengthTooltip(PlayerEntity player, TooltipContext context, CallbackInfoReturnable<List<Text>> cir, List<Text> list) {
        ItemStack item = (ItemStack) (Object) this;
        if (item.getNbt() != null  && !item.getNbt().isEmpty()) {
            if (item.getNbt().contains("length")) {
                float length = item.getNbt().getFloat("length");
                list.add(Text.literal("Length: " + length + "cm").formatted(Formatting.GRAY));
            } else if (FishRegistry.contains(item)) {
                list.add(Text.literal("Length: (Shift + Right Click to measure)").formatted(Formatting.GRAY));
            }
            if (item.getNbt().contains("quality")) {
                String quality = item.getNbt().getString("quality");
                MutableText text1 = Text.literal("Quality: ").formatted(Formatting.GRAY);
                Formatting format = switch (quality) {
                    case "uncommon" -> Formatting.DARK_GRAY;
                    case "rare" -> Formatting.YELLOW;
                    case "legendary" -> Formatting.LIGHT_PURPLE;
                    default -> Formatting.GRAY;
                };
                quality = quality.substring(0, 1).toUpperCase() + quality.substring(1);
                MutableText text2 = Text.literal(quality).formatted(format);
                list.add(text1.append(text2));
            }
            if (item.getNbt().contains("caughtBy")) {
                String name = item.getNbt().getString("caughtBy");
                list.add(Text.literal("Caught by: " + name).formatted(Formatting.GRAY));
            }
        }
    }

}