package thatrobin.foosh.mixin;

import thatrobin.foosh.registry.FooshParticles;
import net.minecraft.client.particle.FishingParticle;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ParticleManager.class)
public abstract class ParticleManagerMixin {

    @Shadow protected abstract <T extends ParticleEffect> void registerFactory(ParticleType<T> particleType, ParticleManager.SpriteAwareFactory<T> spriteAwareFactory);

    @Inject(
            method = "registerDefaultFactories",
            at = @At("RETURN")
    )
    private void registerParticles(CallbackInfo ci) {
        this.registerFactory(FooshParticles.FLAME_FISHING, FishingParticle.Factory::new);
    }
}
