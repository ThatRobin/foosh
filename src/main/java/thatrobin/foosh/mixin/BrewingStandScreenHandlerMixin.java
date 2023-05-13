package thatrobin.foosh.mixin;

import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import thatrobin.foosh.registry.FooshItems;

@Mixin(targets = "net.minecraft.screen.BrewingStandScreenHandler$PotionSlot")
public class BrewingStandScreenHandlerMixin {

    @Inject(method = "matches", at = @At("RETURN"), cancellable = true)
    private static void matches(ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
        cir.setReturnValue(cir.getReturnValue() || stack.isOf(FooshItems.GLASSFISH) || stack.isOf(FooshItems.GLASSFISH_POTION));
    }

}
