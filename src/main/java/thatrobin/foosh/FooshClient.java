package thatrobin.foosh;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.object.builder.v1.client.model.FabricModelPredicateProviderRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.FishingRodItem;
import net.minecraft.item.Item;
import net.minecraft.util.Identifier;
import thatrobin.foosh.registry.FooshItems;

@Environment(EnvType.CLIENT)
public class FooshClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        registerFishingRodPredicates(FooshItems.BLAZE_ROD, FooshItems.REDSTONE_ROD, FooshItems.PRISMARINE_ROD, FooshItems.END_ROD, FooshItems.BONE_ROD, FooshItems.BAMBOO_ROD);
    }

    public void registerFishingRodPredicates(Item... item) {
        for (int i = 0; i < item.length; i++) {
            FabricModelPredicateProviderRegistry.register(item[i], new Identifier("cast"), (itemStack, clientWorld, livingEntity, e) -> {
                if (livingEntity == null) {
                    return 0.0F;
                } else {
                    boolean bl = livingEntity.getMainHandStack() == itemStack;
                    boolean bl2 = livingEntity.getOffHandStack() == itemStack;
                    if (livingEntity.getMainHandStack().getItem() instanceof FishingRodItem) {
                        bl2 = false;
                    }

                    return (bl || bl2) && livingEntity instanceof PlayerEntity && ((PlayerEntity)livingEntity).fishHook != null ? 1.0F : 0.0F;
                }
            });
        }
    }
}
