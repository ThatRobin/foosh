package thatrobin.foosh.networking;

import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.loot.context.LootContextTypes;
import net.minecraft.loot.function.LootFunction;
import net.minecraft.loot.function.LootFunctionManager;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.ItemCommand;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import thatrobin.foosh.Foosh;
import thatrobin.foosh.client.FishCaughtToast;

public class ModPacketsC2S {

    public static void register() {
        ServerPlayNetworking.registerGlobalReceiver(ModPackets.MODIFY_ITEM, ModPacketsC2S::modifyItem);
    }

    private static void modifyItem(MinecraftServer minecraftServer, ServerPlayerEntity player, ServerPlayNetworkHandler serverPlayNetworkHandler, PacketByteBuf packetByteBuf, PacketSender packetSender) {
        ItemStack stack = packetByteBuf.readItemStack();
        int slotIndex = packetByteBuf.readInt();
        LootFunctionManager lootFunctionManager = minecraftServer.getItemModifierManager();
        LootFunction lootFunction = lootFunctionManager.get(Foosh.identifier("set_length"));
        if (lootFunction != null) {
            Foosh.LOGGER.info("loot function is not null");
            LootContext.Builder builder = (new LootContext.Builder(minecraftServer.getOverworld())).parameter(LootContextParameters.ORIGIN, player.getPos()).parameter(LootContextParameters.THIS_ENTITY, player);
            ItemStack mutableStack = stack.copy();
            mutableStack.setCount(1);
            Foosh.LOGGER.info("created stack copy");
            ItemStack newStack = lootFunction.apply(mutableStack, builder.build(LootContextTypes.COMMAND));
            Foosh.LOGGER.info("apply loot function");
            player.giveItemStack(newStack);
            showToast(newStack, stack);
            player.currentScreenHandler.getSlot(slotIndex).takeStack(1);
            player.currentScreenHandler.updateToClient();
            Foosh.LOGGER.info("gave player new stack");
        }
    }

    private static void showToast(ItemStack itemStack, ItemStack displayStack) {
        assert itemStack.getNbt() != null;
        int rarity = switch (itemStack.getNbt().getString("quality")) {
            case "common" -> 16777215;
            case "uncommon" -> 6579300;
            case "rare" -> 16776960;
            case "legendary" -> 13107400;
            default -> 0;
        };
        MinecraftClient.getInstance().getToastManager().add(new FishCaughtToast(displayStack.getItem().getName().getString(), String.valueOf(itemStack.getNbt().getFloat("length")), rarity, displayStack.getItem().getDefaultStack()));

    }
}
