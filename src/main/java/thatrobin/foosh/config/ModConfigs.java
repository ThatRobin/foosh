package thatrobin.foosh.config;

import com.mojang.datafixers.util.Pair;
import thatrobin.foosh.Foosh;

public class ModConfigs {
    public static SimpleConfig CONFIG;
    private static ModConfigProvider configs;

    public static boolean AUTO_MEASURE;


    public static void registerConfigs() {
        configs = new ModConfigProvider();
        createConfigs();

        CONFIG = SimpleConfig.of(Foosh.MODID + "_config").provider(configs).request();

        assignConfigs();
    }

    private static void createConfigs() {
        configs.addKeyValuePair(new Pair<>("foosh.auto_measure", false), "boolean");


    }

    private static void assignConfigs() {
        AUTO_MEASURE = CONFIG.getOrDefault("foosh.auto_measure", false);

        Foosh.LOGGER.info("All " + configs.getConfigsList().size() + " have been assigned properly");
    }
}