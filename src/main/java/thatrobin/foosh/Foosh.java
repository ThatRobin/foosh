package thatrobin.foosh;
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.fabricmc.fabric.api.loot.v2.LootTableEvents;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootTables;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.loot.entry.LootPoolEntry;
import net.minecraft.loot.function.LootFunction;
import net.minecraft.loot.function.LootFunctionType;
import net.minecraft.loot.function.SetPotionLootFunction;
import net.minecraft.potion.Potion;
import net.minecraft.recipe.BrewingRecipeRegistry;
import net.minecraft.util.JsonSerializer;
import net.minecraft.util.registry.Registry;
import thatrobin.foosh.config.ModConfigs;
import thatrobin.foosh.loot.SetAutoLengthLootFunction;
import thatrobin.foosh.loot.SetLengthLootFunction;
import thatrobin.foosh.loot.SetQualityLootFunction;
import thatrobin.foosh.networking.ModPacketsC2S;
import thatrobin.foosh.potion.FishermansLuckEffect;
import thatrobin.foosh.registry.*;
import net.fabricmc.api.ModInitializer;
import net.minecraft.item.Items;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import thatrobin.foosh.util.FishSettings;

public class Foosh implements ModInitializer {

    public static final String MODID = "foosh";

    public static final ItemGroup FOOSH_ITEMS = FabricItemGroupBuilder.create(
            identifier("foosh_items"))
            .icon(() -> new ItemStack(FooshItems.OBSTURGEON))
            .build();

    public static final Logger LOGGER = LogManager.getLogger();

    public static final LootFunctionType SET_QUALITY = register("set_quality", new SetQualityLootFunction.Serializer());
    public static final LootFunctionType SET_LENGTH = register("set_length", new SetLengthLootFunction.Serializer());
    public static final LootFunctionType SET_AUTO_LENGTH = register("set_auto_length", new SetAutoLengthLootFunction.Serializer());

    public static final StatusEffect FISHERMENS_LUCK = new FishermansLuckEffect(StatusEffectCategory.BENEFICIAL, 38655);
    public static final Potion FISHERMENS_LUCK_POTION = new Potion(new StatusEffectInstance(FISHERMENS_LUCK, 3600));

    private static LootFunctionType register(String id, JsonSerializer<? extends LootFunction> jsonSerializer) {
        return Registry.register(Registry.LOOT_FUNCTION_TYPE, identifier(id), new LootFunctionType(jsonSerializer));
    }

    @Override
    public void onInitialize() {
        ModConfigs.registerConfigs();
        ModPacketsC2S.register();

        Registry.register(Registry.STATUS_EFFECT, identifier("fishermens_luck"), FISHERMENS_LUCK);
        Registry.register(Registry.POTION, identifier("fishermens_luck"), FISHERMENS_LUCK_POTION);

        FooshItems.register();
        FooshEntities.register();

        BrewingRecipeRegistry.registerPotionType(FooshItems.GLASSFISH_POTION);

        FishRegistry.register(new Identifier("cod"), Items.COD, new FishSettings().minLength(75f).maxLength(130f));
        FishRegistry.register(new Identifier("salmon"), Items.SALMON, new FishSettings().minLength(60f).maxLength(167f));
        FishRegistry.register(new Identifier("pufferfish"), Items.PUFFERFISH, new FishSettings().minLength(2.5f).maxLength(94f));
        FishRegistry.register(new Identifier("tropical_fish"), Items.TROPICAL_FISH, new FishSettings().minLength(7.6f).maxLength(33f));

        FishRegistry.register(identifier("goldfish"), FooshItems.GOLDFISH, new FishSettings().minLength(21f).maxLength(58f));
        FishRegistry.register(identifier("obsturgeon"), FooshItems.OBSTURGEON, new FishSettings().minLength(21f).maxLength(58f));
        FishRegistry.register(identifier("pyroyster"), FooshItems.PYROYSTER, new FishSettings().minLength(21f).maxLength(58f));
        FishRegistry.register(identifier("laplaice"), FooshItems.LAPLAICE, new FishSettings().minLength(21f).maxLength(58f));
        FishRegistry.register(identifier("end_snapper"), FooshItems.END_SNAPPER, new FishSettings().minLength(21f).maxLength(58f));
        FishRegistry.register(identifier("coris"), FooshItems.CORIS, new FishSettings().minLength(21f).maxLength(58f));

        LootTableEvents.MODIFY.register((resourceManager, lootManager, id, builder, source) -> {
            if (FooshLootTables.FOOSH_LOOT_TABLES.contains(id)) {
                builder.apply(SetQualityLootFunction.builder().build());
                builder.apply(SetAutoLengthLootFunction.builder().build());
            }
            if(LootTables.FISHING_TREASURE_GAMEPLAY.equals(id)) {
                LootPoolEntry entry = ItemEntry.builder(FooshItems.GLASSFISH_POTION).build();
                builder.modifyPools((pool) -> pool.with(entry).apply(SetPotionLootFunction.builder(FISHERMENS_LUCK_POTION)));
            }
        });
    }

    public static Identifier identifier(String id) {
        return new Identifier("foosh", id);
    }
}
