package thatrobin.foosh.registry;

import com.google.common.collect.Maps;
import net.minecraft.util.Pair;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import thatrobin.foosh.util.FishSettings;

import java.util.Map;

public class FishRegistry {

    public static Map<Identifier, Pair<Item, FishSettings>> fishData = Maps.newHashMap();

    public static void register(Identifier id, Item item, FishSettings fishSettings) {
        Pair<Item, FishSettings> fishPairData = new Pair<>(item, fishSettings);
        fishData.put(id, fishPairData);
    }

    public static Item getItem(Identifier id) {
        return fishData.get(id).getLeft();
    }

    public static Float getMinLength(Identifier id) {
        return fishData.get(id).getRight().getMinLength();
    }

    public static Float getMaxLength(Identifier id) {
        return fishData.get(id).getRight().getMaxLength();
    }

    public static boolean contains(ItemStack itemStack) {
        return fishData.containsKey(Registry.ITEM.getId(itemStack.getItem()));
    }

    public static Identifier getId(ItemStack itemStack) {
        return Registry.ITEM.getId(itemStack.getItem());
    }

}
