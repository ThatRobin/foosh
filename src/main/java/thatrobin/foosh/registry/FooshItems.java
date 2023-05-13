package thatrobin.foosh.registry;

import thatrobin.foosh.Foosh;
import thatrobin.foosh.item.AFishingRodItem;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.item.Item;
import net.minecraft.util.registry.Registry;
import thatrobin.foosh.item.FishingBag;
import thatrobin.foosh.item.GlassBottleFishItem;
import thatrobin.foosh.item.PotionFishItem;

public class FooshItems {

    public static Item BAMBOO_ROD = new AFishingRodItem.Builder().withSettings(new FabricItemSettings().group(Foosh.FOOSH_ITEMS).maxDamage(256)).build();
    public static Item BLAZE_ROD = new AFishingRodItem.Builder().lavaProof(true).withSettings(new FabricItemSettings().group(Foosh.FOOSH_ITEMS).maxDamage(256)).build();
    public static Item BONE_ROD = new AFishingRodItem.Builder().withSettings(new FabricItemSettings().group(Foosh.FOOSH_ITEMS).maxDamage(256)).build();
    public static Item END_ROD = new AFishingRodItem.Builder().shulk(true).withSettings(new FabricItemSettings().group(Foosh.FOOSH_ITEMS).maxDamage(256)).build();
    public static Item PRISMARINE_ROD = new AFishingRodItem.Builder().withSettings(new FabricItemSettings().group(Foosh.FOOSH_ITEMS).maxDamage(256)).build();
    public static Item REDSTONE_ROD = new AFishingRodItem.Builder().redstone(true).withSettings(new FabricItemSettings().group(Foosh.FOOSH_ITEMS).maxDamage(256)).build();

    public static Item FISHING_BAG = new FishingBag(new FabricItemSettings().group(Foosh.FOOSH_ITEMS), 64);
    public static Item FISHING_BAG_2 = new FishingBag(new FabricItemSettings().group(Foosh.FOOSH_ITEMS), 128);
    public static Item FISHING_BAG_3 = new FishingBag(new FabricItemSettings().group(Foosh.FOOSH_ITEMS), 256);

    public static Item OBSTURGEON = new Item(new FabricItemSettings().group(Foosh.FOOSH_ITEMS));
    public static Item PYROYSTER = new Item(new FabricItemSettings().group(Foosh.FOOSH_ITEMS));
    public static Item GOLDFISH = new Item(new FabricItemSettings().group(Foosh.FOOSH_ITEMS));
    public static Item LAPLAICE = new Item(new FabricItemSettings().group(Foosh.FOOSH_ITEMS));
    public static Item END_SNAPPER = new Item(new FabricItemSettings().group(Foosh.FOOSH_ITEMS));
    public static Item CORIS = new Item(new FabricItemSettings().group(Foosh.FOOSH_ITEMS));

    public static Item GLASSFISH = new GlassBottleFishItem(new FabricItemSettings().group(Foosh.FOOSH_ITEMS).maxCount(3));
    public static Item GLASSFISH_POTION = new PotionFishItem(new FabricItemSettings().group(Foosh.FOOSH_ITEMS).maxCount(3));

    public static void register() {
        register("bamboo_rod", BAMBOO_ROD);
        register("blaze_rod", BLAZE_ROD);
        register("bone_rod", BONE_ROD);
        register("end_rod", END_ROD);
        register("prismarine_rod", PRISMARINE_ROD);
        register("redstone_rod", REDSTONE_ROD);

        register("obsturgeon", OBSTURGEON);
        register("pyroyster", PYROYSTER);
        register("goldfish", GOLDFISH);
        register("laplaice", LAPLAICE);
        register("end_snapper", END_SNAPPER);
        register("coris", CORIS);
        register("glassfish", GLASSFISH);
        register("glassfish_potion", GLASSFISH_POTION);

        register("fishing_bag", FISHING_BAG);
        register("fishing_bag_2", FISHING_BAG_2);
        register("fishing_bag_3", FISHING_BAG_3);


    }

    public static Item register(String name, Item item) {
        return Registry.register(Registry.ITEM, Foosh.identifier(name), item);
    }
}
