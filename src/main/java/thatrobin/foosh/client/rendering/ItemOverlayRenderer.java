package thatrobin.foosh.client.rendering;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public final class ItemOverlayRenderer {
    private ItemOverlayRenderer() { }

    /**
     * This interface allows you to override Minecraft's GUI item overlay rendering (damage bar, item label
     * and cooldown overlay).
     *
     * <p>With this interface, you could customize this overlay to your heart's content.
     *
     * <p>Example:<pre>
     * {@link ItemOverlayRendererRegistry#setPreRenderer(Item, Pre)} ItemOverlayRendererRegistry.setPreRenderer}(Items.DIAMOND, (matrixStack, renderer, stack, x, y, countLabel) -> {
     * 	renderer.drawWithShadow(matrixStack, "?", x + 17 - renderer.getWidth("?"), y + 9, 0xFFFFFF);
     * 	return true;
     * });
     * </pre>
     */
    @Environment(EnvType.CLIENT)
    @FunctionalInterface
    public interface Pre {
        /**
         * Called before Vanilla's overlay rendering. Note that overlay rendering occurs <em>after</em> the
         * enchanted glint is rendered.
         * @return {@code true} to cancel Vanilla's overlay rendering.
         */
        boolean renderOverlay(MatrixStack matrixStack, TextRenderer renderer, ItemStack stack, int x, int y, String countLabel);
    }

    /**
     * This interface allows you to append to Minecraft's GUI item overlay rendering (durability bars, item counts
     * and cooldown overlays).
     */
    @Environment(EnvType.CLIENT)
    @FunctionalInterface
    public interface Post {
        /**
         * Called after Vanilla's overlay rendering. Note that overlay rendering occurs <em>after</em> the
         * enchanted glint is rendered.
         */
        void renderOverlay(MatrixStack matrixStack, TextRenderer renderer, ItemStack stack, int x, int y, String countLabel);
    }
}