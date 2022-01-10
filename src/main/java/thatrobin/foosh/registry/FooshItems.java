package thatrobin.foosh.registry;

import thatrobin.foosh.Foosh;
import thatrobin.foosh.api.SoundInstance;
import thatrobin.foosh.item.AFishingRodItem;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.item.Item;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.registry.Registry;

public class FooshItems {

    //public static Item BLAZE_ROD = register("blaze_rod", new AFishingRodItem(new FabricItemSettings(), new SoundInstance(SoundEvents.ENTITY_FISHING_BOBBER_RETRIEVE, 0.5f, SoundInstance.DEFAULT_PITCH), new SoundInstance(SoundEvents.ENTITY_FISHING_BOBBER_THROW, 0.5f, SoundInstance.DEFAULT_PITCH), 10, 10, true));
    public static Item BLAZE_ROD = register("blaze_rod", new AFishingRodItem.Builder().lavaProof(true).build());

    public static Item register(String name, Item item) {
        return Registry.register(Registry.ITEM, Foosh.identifier(name), item);
    }

    public static void init() {
        // NO-OP
    }
}
