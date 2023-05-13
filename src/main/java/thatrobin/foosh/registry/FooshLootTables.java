package thatrobin.foosh.registry;

import net.minecraft.loot.LootTables;
import net.minecraft.util.Identifier;

import java.util.List;

public class FooshLootTables {

    public static Identifier LAVA_FISHING_NETHER = new Identifier("gameplay/lava_fishing");
    public static Identifier LAVA_FISHING_OVERWORLD = new Identifier("gameplay/lava_fishing");
    public static Identifier END_FISHING = new Identifier("gameplay/end_fishing");


    public static List<Identifier> FOOSH_LOOT_TABLES = List.of(LootTables.FISHING_FISH_GAMEPLAY, LAVA_FISHING_NETHER, LAVA_FISHING_OVERWORLD, END_FISHING);
}
