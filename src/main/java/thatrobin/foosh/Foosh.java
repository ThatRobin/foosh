package thatrobin.foosh;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.fabricmc.fabric.api.loot.v1.FabricLootPoolBuilder;
import net.fabricmc.fabric.api.loot.v1.FabricLootSupplierBuilder;
import net.fabricmc.fabric.api.loot.v1.event.LootTableLoadingCallback;
import net.minecraft.entity.EntityType;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.LootTables;
import net.minecraft.loot.function.LootFunction;
import net.minecraft.loot.function.LootFunctionType;
import net.minecraft.loot.function.SetCountLootFunction;
import net.minecraft.loot.provider.number.ConstantLootNumberProvider;
import net.minecraft.recipe.RecipeType;
import net.minecraft.recipe.SmeltingRecipe;
import net.minecraft.tag.EntityTypeTags;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.JsonSerializer;
import net.minecraft.util.registry.Registry;
import org.apache.commons.compress.utils.Lists;
import thatrobin.foosh.loot.SetLengthLootFunction;
import thatrobin.foosh.loot.SetQualityLootFunction;
import thatrobin.foosh.registry.*;
import net.fabricmc.api.ModInitializer;
import net.minecraft.item.Items;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Foosh implements ModInitializer {

    public static final ItemGroup FOOSH_ITEMS = FabricItemGroupBuilder.create(
            identifier("foosh_items"))
            .icon(() -> new ItemStack(FooshItems.OBSIDIFISH))
            .build();

    public static final Logger LOGGER = LogManager.getLogger();

    public static final LootFunctionType SET_QUALITY = register("set_quality", new SetQualityLootFunction.Serializer());

    private static LootFunctionType register(String id, JsonSerializer<? extends LootFunction> jsonSerializer) {
        return (LootFunctionType) Registry.register(Registry.LOOT_FUNCTION_TYPE, identifier(id), new LootFunctionType(jsonSerializer));
    }

    @Override
    public void onInitialize() {
        FooshItems.register();

        FishRegistry.register(new Identifier("cod"), Items.COD, 75f, 130f);
        FishRegistry.register(new Identifier("salmon"), Items.SALMON, 60f, 167f);
        FishRegistry.register(new Identifier("pufferfish"), Items.PUFFERFISH, 2.5f, 94f);
        FishRegistry.register(new Identifier("tropical_fish"), Items.TROPICAL_FISH, 7.6f, 33f);

        FishRegistry.register(identifier("goldfish"), FooshItems.GOLDFISH, 21f, 58f);
        FishRegistry.register(identifier("obsidifish"), FooshItems.OBSIDIFISH, 21f, 58f);
        FishRegistry.register(identifier("pyroyster"), FooshItems.PYROYSTER, 21f, 58f);
        FishRegistry.register(identifier("laplaice"), FooshItems.LAPLAICE, 21f, 58f);
        FishRegistry.register(identifier("end_snapper"), FooshItems.END_SNAPPER, 21f, 58f);

        LootTableLoadingCallback.EVENT.register((resourceManager, lootManager, id, table, setter) -> {
            if (LootTables.FISHING_FISH_GAMEPLAY.equals(id)) {
                table.withFunction(SetQualityLootFunction.builder().build()).withFunction(SetLengthLootFunction.builder().build());
            }
        });
    }

    public static Identifier identifier(String id) {
        return new Identifier("foosh", id);
    }
}
