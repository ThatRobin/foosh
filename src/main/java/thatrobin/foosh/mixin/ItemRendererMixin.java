package thatrobin.foosh.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import thatrobin.foosh.client.rendering.item_overlay.QualityIcon;
import thatrobin.foosh.registry.FishRegistry;

@Mixin(ItemRenderer.class)
public abstract class ItemRendererMixin {
    @Unique private final MatrixStack matrixStack = new MatrixStack();

    @Inject(method = "renderGuiItemOverlay(Lnet/minecraft/client/font/TextRenderer;Lnet/minecraft/item/ItemStack;IILjava/lang/String;)V",
            at = @At("TAIL"))
    public void postOverlay(TextRenderer renderer, ItemStack stack, int x, int y, String countLabel, CallbackInfo ci) {
        if (stack.isEmpty()) {
            return;
        }

        if(FishRegistry.contains(stack)) {
            matrixStack.push();
            QualityIcon.renderOverlay(matrixStack, stack, x, y);
            matrixStack.pop();
        }
    }
}
