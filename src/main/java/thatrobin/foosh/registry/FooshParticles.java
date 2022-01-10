package thatrobin.foosh.registry;

import thatrobin.foosh.Foosh;
import thatrobin.foosh.particle.CustomDefaultParticleType;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.util.registry.Registry;

public class FooshParticles {

    public static final DefaultParticleType FLAME_FISHING = register("flame_fishing", false);

    private static DefaultParticleType register(String name, boolean alwaysShow) {
        return Registry.register(Registry.PARTICLE_TYPE, Foosh.identifier(name), new CustomDefaultParticleType(alwaysShow));
    }

    public static void init() {

    }

    private FooshParticles() {

    }
}
