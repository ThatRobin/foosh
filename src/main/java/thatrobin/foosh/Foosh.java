package thatrobin.foosh;
import thatrobin.foosh.registry.*;
import net.fabricmc.api.ModInitializer;
import net.minecraft.item.Items;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Foosh implements ModInitializer {

    public static final Logger LOGGER = LogManager.getLogger();

    @Override
    public void onInitialize() {
        FooshItems.init();
        FooshParticles.init();

        FishRegistry.register(new Identifier("cod"), Items.COD, 76.20f, 129.54f);
        FishRegistry.register(new Identifier("salmon"), Items.SALMON, 60.96f, 167.64f);
        FishRegistry.register(new Identifier("pufferfish"), Items.PUFFERFISH, 2.54f, 93.98f);
        FishRegistry.register(new Identifier("tropical_fish"), Items.TROPICAL_FISH, 7.62f, 33.02f);
    }

    public static Identifier identifier(String id) {
        return new Identifier("foosh", id);
    }
}
