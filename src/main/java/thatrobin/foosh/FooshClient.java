package thatrobin.foosh;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.fabricmc.fabric.api.object.builder.v1.client.model.FabricModelPredicateProviderRegistry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.SimpleOption;
import net.minecraft.client.option.StickyKeyBinding;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.potion.PotionUtil;
import net.minecraft.util.Identifier;
import thatrobin.foosh.client.ToggleMeasureToast;
import thatrobin.foosh.config.ModConfigs;
import thatrobin.foosh.entity.WanderingAnglerEntity;
import thatrobin.foosh.networking.ModPacketsS2C;
import thatrobin.foosh.registry.FooshEntities;
import thatrobin.foosh.registry.FooshItems;

@Environment(EnvType.CLIENT)
public class FooshClient implements ClientModInitializer {

    public static StickyKeyBinding toggleAutoMeasure;
    public boolean pressed = false;

    @Override
    public void onInitializeClient() {
        FooshEntities.registerClient();
        ModPacketsS2C.register();
        registerFishingRodPredicates(FooshItems.BLAZE_ROD, FooshItems.REDSTONE_ROD, FooshItems.PRISMARINE_ROD, FooshItems.END_ROD, FooshItems.BAMBOO_ROD, FooshItems.BONE_ROD);
        registerColourProvider();

        SimpleOption<Boolean> autoMeasure = SimpleOption.ofBoolean("foosh.toggle_auto_measure", ModConfigs.AUTO_MEASURE);

        toggleAutoMeasure = new StickyKeyBinding("key.foosh.toggle_auto_measure",72,"category." + Foosh.MODID, autoMeasure::getValue);

        KeyBindingHelper.registerKeyBinding(toggleAutoMeasure);

        ClientTickEvents.START_CLIENT_TICK.register(tick -> {
            if(tick.player != null) {
                if (toggleAutoMeasure.isPressed() && !pressed) {
                    pressed = true;
                    ModConfigs.AUTO_MEASURE = !ModConfigs.AUTO_MEASURE;
                    MinecraftClient.getInstance().getToastManager().clear();
                    MinecraftClient.getInstance().getToastManager().add(new ToggleMeasureToast("Auto-Measure", ModConfigs.AUTO_MEASURE));
                }

                if(!toggleAutoMeasure.isPressed()) {
                    pressed = false;
                }


            }
        });
    }

    @SuppressWarnings("all")
    public void registerFishingRodPredicates(Item... item) {
        for (int i = 0; i < item.length; i++) {
            FabricModelPredicateProviderRegistry.register(item[i], new Identifier("cast"), (itemStack, clientWorld, livingEntity, e) -> {
                if (livingEntity == null) {
                    float lit = itemStack.getNbt().getBoolean("lit") ? 1.0F : 0.0F;
                    return lit;
                } else {
                    boolean bl = livingEntity.getMainHandStack() == itemStack;
                    boolean bl2 = livingEntity.getOffHandStack() == itemStack;

                    float playerUsing = (bl || bl2) && livingEntity instanceof PlayerEntity && ((PlayerEntity)livingEntity).fishHook != null ? 1.0F : 0.0F;
                    float anglerUsing = (bl || bl2) && livingEntity instanceof WanderingAnglerEntity && ((WanderingAnglerEntity)livingEntity).fishHook != null ? 1.0F : 0.0F;

                    float result = (playerUsing == 1.0F || anglerUsing == 1.0F) ? 1.0F : 0.0F;
                    return result;
                }
            });
        }
    }

    public void registerColourProvider() {
        ColorProviderRegistry.ITEM.register((stack, tintIndex) -> tintIndex > 0 ? -1 : PotionUtil.getColor(stack), FooshItems.GLASSFISH_POTION);
    }
}
