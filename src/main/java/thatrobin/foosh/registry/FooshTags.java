package thatrobin.foosh.registry;

import net.minecraft.item.Item;
import net.minecraft.tag.TagKey;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class FooshTags {

    public static final TagKey<Item> BARTERING_ITEMS = TagKey.of(Registry.ITEM_KEY, new Identifier("foosh", "bartering_items"));
    public static final TagKey<Item> FISHING_BAGS = TagKey.of(Registry.ITEM_KEY, new Identifier("foosh", "fishing_bags"));

}
