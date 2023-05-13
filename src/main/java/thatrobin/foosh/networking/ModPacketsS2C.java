package thatrobin.foosh.networking;

import io.netty.buffer.Unpooled;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.loot.context.LootContextTypes;
import net.minecraft.loot.function.LootFunction;
import net.minecraft.loot.function.LootFunctionManager;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import thatrobin.foosh.Foosh;
import thatrobin.foosh.client.FishCaughtToast;

public class ModPacketsS2C {

    @Environment(EnvType.CLIENT)
    public static void register() {
        ClientPlayConnectionEvents.INIT.register((clientPlayNetworkHandler, minecraftClient) -> {
            ClientPlayNetworking.registerReceiver(ModPackets.MODIFY_ITEM, ModPacketsS2C::modifyItem);
        });
    }

    private static void modifyItem(MinecraftClient minecraftClient, ClientPlayNetworkHandler clientPlayNetworkHandler, PacketByteBuf packetByteBuf, PacketSender packetSender) {
        ItemStack stack = packetByteBuf.readItemStack();
        if(Screen.hasShiftDown()) {
            Foosh.LOGGER.info("shift is down");
            PacketByteBuf packetByteBuf2 = new PacketByteBuf(Unpooled.buffer());
            packetByteBuf2.writeItemStack(stack);
            ClientPlayNetworking.send(ModPackets.MODIFY_ITEM, packetByteBuf2);
        }
    }

    private static void showToast(ItemStack itemStack) {
        assert itemStack.getNbt() != null;
        int rarity = switch (itemStack.getNbt().getString("quality")) {
            case "common" -> 16777215;
            case "uncommon" -> 6579300;
            case "rare" -> 16776960;
            case "legendary" -> 13107400;
            default -> 0;
        };
        MinecraftClient.getInstance().getToastManager().add(new FishCaughtToast(itemStack.getName().getString(), String.valueOf(itemStack.getNbt().getFloat("length")), rarity, itemStack.getItem().getDefaultStack()));

    }

}
