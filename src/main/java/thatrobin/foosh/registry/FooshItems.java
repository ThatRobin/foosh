package thatrobin.foosh.registry;

import net.minecraft.item.ItemGroup;
import thatrobin.foosh.Foosh;
import thatrobin.foosh.api.SoundInstance;
import thatrobin.foosh.item.AFishingRodItem;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.item.Item;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.registry.Registry;
import thatrobin.foosh.item.FishingBag;

public class FooshItems {

    public static Item BAMBOO_ROD = new AFishingRodItem.Builder().withSettings(new FabricItemSettings().group(Foosh.FOOSH_ITEMS)).build();
    public static Item BLAZE_ROD = new AFishingRodItem.Builder().lavaProof(true).withSettings(new FabricItemSettings().group(Foosh.FOOSH_ITEMS)).build();
    public static Item BONE_ROD = new AFishingRodItem.Builder().withSettings(new FabricItemSettings().group(Foosh.FOOSH_ITEMS)).build();
    public static Item END_ROD = new AFishingRodItem.Builder().shulk(true).withSettings(new FabricItemSettings().group(Foosh.FOOSH_ITEMS)).build();
    public static Item PRISMARINE_ROD = new AFishingRodItem.Builder().withSettings(new FabricItemSettings().group(Foosh.FOOSH_ITEMS)).build();
    public static Item REDSTONE_ROD = new AFishingRodItem.Builder().redstone(true).withSettings(new FabricItemSettings().group(Foosh.FOOSH_ITEMS)).build();

    public static Item FISHING_BAG = new FishingBag(new FabricItemSettings().group(Foosh.FOOSH_ITEMS));

    public static Item OBSIDIFISH = new Item(new FabricItemSettings().group(Foosh.FOOSH_ITEMS));
    public static Item PYROYSTER = new Item(new FabricItemSettings().group(Foosh.FOOSH_ITEMS));
    public static Item GOLDFISH = new Item(new FabricItemSettings().group(Foosh.FOOSH_ITEMS));
    public static Item LAPLAICE = new Item(new FabricItemSettings().group(Foosh.FOOSH_ITEMS));
    public static Item END_SNAPPER = new Item(new FabricItemSettings().group(Foosh.FOOSH_ITEMS));
    public static Item CORIS = new Item(new FabricItemSettings().group(Foosh.FOOSH_ITEMS));

    public static void register() {
        register("bamboo_rod", BAMBOO_ROD);
        register("blaze_rod", BLAZE_ROD);
        register("bone_rod", BONE_ROD);
        register("end_rod", END_ROD);
        register("prismarine_rod", PRISMARINE_ROD);
        register("redstone_rod", REDSTONE_ROD);

        register("obsidifish", OBSIDIFISH);
        register("pyroyster", PYROYSTER);
        register("goldfish", GOLDFISH);
        register("laplaice", LAPLAICE);
        register("end_snapper", END_SNAPPER);
        register("coris", CORIS);


        register("fishing_bag", FISHING_BAG);
    }

    public static Item register(String name, Item item) {
        return Registry.register(Registry.ITEM, Foosh.identifier(name), item);
    }
}
