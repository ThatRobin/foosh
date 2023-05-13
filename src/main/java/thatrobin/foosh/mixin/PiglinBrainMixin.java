package thatrobin.foosh.mixin;

import net.minecraft.entity.mob.PiglinBrain;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import thatrobin.foosh.registry.FooshTags;

@Mixin(PiglinBrain.class)
public class PiglinBrainMixin {

    @Inject(
            method = "acceptsForBarter",
            at = @At("RETURN"),
            cancellable = true)
    private static void acceptsForBarter(ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
        cir.setReturnValue(stack.isIn(FooshTags.BARTERING_ITEMS));
    }
}
