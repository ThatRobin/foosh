package thatrobin.foosh.registry;

import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.client.render.entity.FishingBobberEntityRenderer;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.entity.projectile.FishingBobberEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import thatrobin.foosh.Foosh;
import thatrobin.foosh.client.rendering.*;
import thatrobin.foosh.entity.AnglerBobberEntity;
import thatrobin.foosh.entity.WanderingAnglerEntity;
import thatrobin.foosh.entity.bobbers.AirBobberEntity;
import thatrobin.foosh.entity.bobbers.LavaBobberEntity;
import thatrobin.foosh.entity.bobbers.RedstoneBobberEntity;

public class FooshEntities {

    public static final EntityType<WanderingAnglerEntity> WANDERING_ANGLER = FabricEntityTypeBuilder.create(SpawnGroup.CREATURE, WanderingAnglerEntity::new).dimensions(EntityDimensions.fixed(0.6F, 1.95F)).build();
    public static final EntityType<AnglerBobberEntity> ANGLER_BOBBER = FabricEntityTypeBuilder.<AnglerBobberEntity>create(SpawnGroup.CREATURE, AnglerBobberEntity::new).disableSaving().disableSummon().dimensions(EntityDimensions.fixed(0.25F, 0.25F)).build();

    public static final EntityType<LavaBobberEntity> LAVA_BOBBER = FabricEntityTypeBuilder.<LavaBobberEntity>create(SpawnGroup.CREATURE, LavaBobberEntity::new).disableSaving().disableSummon().dimensions(EntityDimensions.fixed(0.25F, 0.25F)).build();
    public static final EntityType<RedstoneBobberEntity> REDSTONE_BOBBER = FabricEntityTypeBuilder.<RedstoneBobberEntity>create(SpawnGroup.CREATURE, RedstoneBobberEntity::new).disableSaving().disableSummon().dimensions(EntityDimensions.fixed(0.25F, 0.25F)).build();
    public static final EntityType<AirBobberEntity> AIR_BOBBER = FabricEntityTypeBuilder.<AirBobberEntity>create(SpawnGroup.CREATURE, AirBobberEntity::new).disableSaving().disableSummon().dimensions(EntityDimensions.fixed(0.25F, 0.25F)).build();

    public static void register() {
        Registry.register(Registry.ENTITY_TYPE, Foosh.identifier("wandering_angler"), WANDERING_ANGLER);
        Registry.register(Registry.ENTITY_TYPE, Foosh.identifier("angler_bobber"), ANGLER_BOBBER);
        Registry.register(Registry.ENTITY_TYPE, Foosh.identifier("lava_bobber"), LAVA_BOBBER);
        Registry.register(Registry.ENTITY_TYPE, Foosh.identifier("redstone_bobber"), REDSTONE_BOBBER);
        Registry.register(Registry.ENTITY_TYPE, Foosh.identifier("air_bobber"), AIR_BOBBER);
        FabricDefaultAttributeRegistry.register(WANDERING_ANGLER, WanderingAnglerEntity.createMobAttributes());
    }

    public static void registerClient() {
        EntityRendererRegistry.register(FooshEntities.WANDERING_ANGLER, WanderingAnglerEntityRenderer::new);
        EntityRendererRegistry.register(FooshEntities.ANGLER_BOBBER, AnglerBobberEntityRenderer::new);
        EntityRendererRegistry.register(FooshEntities.LAVA_BOBBER, LavaBobberEntityRenderer::new);
        EntityRendererRegistry.register(FooshEntities.AIR_BOBBER, AirBobberEntityRenderer::new);
        EntityRendererRegistry.register(FooshEntities.REDSTONE_BOBBER, RedstoneBobberEntityRenderer::new);
    }
}
