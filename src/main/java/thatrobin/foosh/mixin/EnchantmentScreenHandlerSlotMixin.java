package thatrobin.foosh.mixin;

import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import thatrobin.foosh.registry.FooshItems;

@Mixin(targets = {"net.minecraft.screen.EnchantmentScreenHandler$3"})
public class EnchantmentScreenHandlerSlotMixin {

    @Inject(method = "canInsert", at = @At(value = "RETURN"), cancellable = true)
    public void modifySlot(ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
        cir.setReturnValue(cir.getReturnValue() || stack.isOf(FooshItems.LAPLAICE));
    }
}
