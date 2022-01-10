package thatrobin.foosh.registry;

import com.google.common.collect.Maps;
import thatrobin.foosh.client.rendering.ItemOverlayRendererRegistry;
import thatrobin.foosh.client.rendering.QualityIcon;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.apache.commons.lang3.tuple.Triple;

import java.util.Map;

public class FishRegistry {

    public static Map<Identifier, Triple<Item, Float, Float>> fishData = Maps.newHashMap();

    public static void register(Identifier id, Item item, Float minLength, Float maxLength) {
        ItemOverlayRendererRegistry.setPostRenderer(item, new QualityIcon());
        Triple<Item, Float, Float> itemLength = Triple.of(item, minLength, maxLength);
        fishData.put(id, itemLength);
    }

    public static Item getItem(Identifier id) {
        return fishData.get(id).getLeft();
    }

    public static Float getMinLength(Identifier id) {
        return fishData.get(id).getMiddle();
    }

    public static Float getMaxLength(Identifier id) {
        return fishData.get(id).getRight();
    }

    public static boolean contains(ItemStack itemStack) {
        return fishData.containsKey(Registry.ITEM.getId(itemStack.getItem()));
    }

    public static Identifier getId(ItemStack itemStack) {
        return Registry.ITEM.getId(itemStack.getItem());
    }

}
