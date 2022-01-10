package thatrobin.foosh.client.rendering;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;

import java.util.Objects;

@Environment(EnvType.CLIENT)
public class ItemOverlayRendererRegistry {

    public static void setPostRenderer(Item item, ItemOverlayRenderer.Post renderer) {
        Objects.requireNonNull(item);
        Objects.requireNonNull(renderer);
        ItemOverlayMaps.POST_RENDERER_MAP.put(item, renderer);
    }

    public static void setPostRenderer(ItemConvertible itemConvertible, ItemOverlayRenderer.Post renderer) {
        Objects.requireNonNull(itemConvertible);
        setPostRenderer(itemConvertible.asItem(), renderer);
    }

    public static void setPreRenderer(Item item, ItemOverlayRenderer.Pre renderer) {
        Objects.requireNonNull(item);
        Objects.requireNonNull(renderer);
        ItemOverlayMaps.PRE_RENDERER_MAP.put(item, renderer);
    }

    public static void setDefaultPostRenderer(ItemOverlayRenderer.Post renderer) {
        Objects.requireNonNull(renderer);
        ItemOverlayMaps.POST_RENDERER_MAP.defaultReturnValue(renderer);
    }
}
