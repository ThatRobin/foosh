package thatrobin.foosh.mixin;

import thatrobin.foosh.client.rendering.ItemOverlayMaps;
import thatrobin.foosh.client.rendering.ItemOverlayRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;

@Mixin(ItemRenderer.class)
public abstract class ItemRendererMixin {
    @Unique private final MatrixStack matrixStack = new MatrixStack();
    @Unique private boolean needPopping = false;
    @Unique private String countLabelTmp;

    @Unique private void setGuiQuadColor(Args args, int color) {
        // renderGuiQuad takes each component separately because :mojank:
        args.set(5, (color >> 16) & 0xFF);
        args.set(6, (color >> 8) & 0xFF);
        args.set(7, color & 0xFF);
        args.set(8, (color >> 24) & 0xFF);
    }

    // calls the pre-renderer, allows for cancelling the rest of the overlay
    @Inject(method = "renderGuiItemOverlay(Lnet/minecraft/client/font/TextRenderer;Lnet/minecraft/item/ItemStack;IILjava/lang/String;)V",
            at = @At("HEAD"), cancellable = true)
    public void preOverlay(TextRenderer renderer, ItemStack stack, int x, int y, String countLabel, CallbackInfo ci) {
        if (stack.isEmpty()) {
            return;
        }

        ItemOverlayRenderer.Pre preRenderer = ItemOverlayMaps.PRE_RENDERER_MAP.get(stack.getItem());

        if (preRenderer == null) {
            return;
        }

        matrixStack.push();
        boolean cancel = preRenderer.renderOverlay(matrixStack, renderer, stack, x, y, countLabel);
        matrixStack.pop();

        if (cancel) {
            ci.cancel();
        }
    }

    // why didn't Mojang just add a MatrixStack parameter? beats me
    @Redirect(method = "renderGuiItemOverlay(Lnet/minecraft/client/font/TextRenderer;Lnet/minecraft/item/ItemStack;IILjava/lang/String;)V",
            at = @At(value = "NEW", target = "net/minecraft/client/util/math/MatrixStack"))
    public MatrixStack reuseMatrixStack() {
        matrixStack.push();
        needPopping = true;
        return matrixStack;
    }

    // hack to make the "countLabel != null" expression in the "is count label visible" condition always evaluate to false
    // this makes count label visibility depend on ItemStack.getCount(), which gets redirected to our isVisible method
    // thanks, @Gimpansor
    @ModifyVariable(method = "renderGuiItemOverlay(Lnet/minecraft/client/font/TextRenderer;Lnet/minecraft/item/ItemStack;IILjava/lang/String;)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;getCount()I", ordinal = 0), ordinal = 0, argsOnly = true)
    public String countVisibleCondHack(String countLabel, TextRenderer renderer, ItemStack stack) {
        countLabelTmp = countLabel;

        return null;
    }

    // undoes the "countLabel != null" expression hack
    @ModifyVariable(method = "renderGuiItemOverlay(Lnet/minecraft/client/font/TextRenderer;Lnet/minecraft/item/ItemStack;IILjava/lang/String;)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/util/math/MatrixStack;translate(DDD)V"), ordinal = 0, argsOnly = true)
    public String countVisibleCondHackUndo(String countLabel) {
        return countLabelTmp;
    }

    // calls the post-renderer
    @Inject(method = "renderGuiItemOverlay(Lnet/minecraft/client/font/TextRenderer;Lnet/minecraft/item/ItemStack;IILjava/lang/String;)V",
            at = @At("TAIL"))
    public void postOverlay(TextRenderer renderer, ItemStack stack, int x, int y, String countLabel, CallbackInfo ci) {
        if (stack.isEmpty()) {
            return;
        }

        if (needPopping) {
            matrixStack.pop();
            needPopping = false;
        }

        ItemOverlayRenderer.Post postRenderer = ItemOverlayMaps.POST_RENDERER_MAP.get(stack.getItem());

        if (postRenderer == null) {
            return;
        }

        matrixStack.push();
        postRenderer.renderOverlay(matrixStack, renderer, stack, x, y, countLabel);
        matrixStack.pop();
    }
}
