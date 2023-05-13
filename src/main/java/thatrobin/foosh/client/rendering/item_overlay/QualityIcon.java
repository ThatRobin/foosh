package thatrobin.foosh.client.rendering.item_overlay;

import com.mojang.blaze3d.systems.RenderSystem;
import thatrobin.foosh.registry.FishRegistry;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;

public class QualityIcon extends DrawableHelper {
    private static final Identifier UNCOMMON = new Identifier("textures/item/uncommon.png");
    private static final Identifier RARE = new Identifier("textures/item/rare.png");
    private static final Identifier LEGENDARY = new Identifier("textures/item/legendary.png");

    public static void renderOverlay(MatrixStack matrixStack, ItemStack stack, int x, int y) {
        if (!stack.isEmpty()) {
            if (FishRegistry.contains(stack)) {
                if (stack.getNbt() != null) {
                    if (!stack.getNbt().isEmpty()) {
                        String quality = stack.getNbt().getString("quality");
                        if (quality != null && !quality.isEmpty()) {
                            Identifier tex = switch (quality) {
                                case "uncommon" -> UNCOMMON;
                                case "rare" -> RARE;
                                case "legendary" -> LEGENDARY;
                                default -> null;
                            };
                            if(tex != null) {
                                RenderSystem.disableDepthTest();
                                RenderSystem.enableTexture();
                                RenderSystem.enableBlend();
                                RenderSystem.defaultBlendFunc();
                                RenderSystem.setShaderTexture(0, tex);
                                drawTexture(matrixStack, x, y, 0, 0, 16, 16, 16, 16);
                                RenderSystem.enableDepthTest();
                            }
                        }
                    }
                }
            }
        }

    }
}
