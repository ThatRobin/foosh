package thatrobin.foosh.client.rendering;

import it.unimi.dsi.fastutil.objects.Reference2ObjectOpenHashMap;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.item.Item;

@Environment(EnvType.CLIENT)
public final class ItemOverlayMaps {
    private ItemOverlayMaps() { }

    // The FabricMC Group is not responsible for any damages caused by directly mutating the following maps.
    // (use ItemOverlayRendererRegistry)
    public static final Reference2ObjectOpenHashMap<Item, ItemOverlayRenderer.Pre> PRE_RENDERER_MAP
            = new Reference2ObjectOpenHashMap<>();
    public static final Reference2ObjectOpenHashMap<Item, ItemOverlayRenderer.Post> POST_RENDERER_MAP
            = new Reference2ObjectOpenHashMap<>();
}