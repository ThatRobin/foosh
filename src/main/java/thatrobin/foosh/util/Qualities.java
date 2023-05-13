package thatrobin.foosh.util;

import net.minecraft.util.collection.WeightedList;
import org.apache.commons.compress.utils.Lists;

import java.util.List;
import java.util.Random;

public class Qualities {

    public static WeightedCollection<String> getQualities() {
        WeightedCollection<String> baseQualities = new WeightedCollection<>();
        baseQualities.add("common", 40);
        baseQualities.add("uncommon", 30);
        baseQualities.add("rare", 20);
        baseQualities.add("legendary", 10);
        return baseQualities;
    }

    public static WeightedCollection<String> getBiasQualities() {
        WeightedCollection<String> baseQualities = new WeightedCollection<>();
        baseQualities.add("common", 30);
        baseQualities.add("uncommon", 30);
        baseQualities.add("rare", 25);
        baseQualities.add("legendary", 20);
        return baseQualities;
    }

}
