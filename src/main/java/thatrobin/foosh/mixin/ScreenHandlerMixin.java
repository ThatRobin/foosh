package thatrobin.foosh.mixin;

import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.StackReference;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ClickType;
import net.minecraft.util.collection.DefaultedList;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import thatrobin.foosh.Foosh;
import thatrobin.foosh.networking.ModPackets;
import thatrobin.foosh.registry.FishRegistry;

@Mixin(ScreenHandler.class)
public abstract class ScreenHandlerMixin {

    @Shadow @Final public DefaultedList<Slot> slots;

    @Inject(method = "internalOnSlotClick", at = @At("HEAD"), cancellable = true)
    private void onClicked(int slotIndex, int button, SlotActionType actionType, PlayerEntity player, CallbackInfo ci) {
        ClickType clickType = button == 1 ? ClickType.RIGHT : ClickType.LEFT;
        if (clickType == ClickType.RIGHT) {
            if (slotIndex < 0) {
                return;
            }
            Slot slot = this.slots.get(slotIndex);
            ItemStack stack = slot.getStack();
            if (stack.hasNbt()) {
                assert stack.getNbt() != null;
                if (!stack.getNbt().contains("length") && FishRegistry.contains(stack)) {
                    if(player.world.isClient) {
                        if(Screen.hasShiftDown()) {
                            Foosh.LOGGER.info("shift is down");
                            PacketByteBuf packetByteBuf2 = new PacketByteBuf(Unpooled.buffer());
                            packetByteBuf2.writeItemStack(stack);
                            packetByteBuf2.writeInt(slotIndex);
                            ClientPlayNetworking.send(ModPackets.MODIFY_ITEM, packetByteBuf2);
                        }
                    }
                    ci.cancel();
                }
            }
        }
    }
}
