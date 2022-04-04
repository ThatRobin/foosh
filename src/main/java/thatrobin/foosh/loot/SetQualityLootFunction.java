package thatrobin.foosh.loot;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.function.ConditionalLootFunction;
import net.minecraft.loot.function.FurnaceSmeltLootFunction;
import net.minecraft.loot.function.LootFunctionType;
import net.minecraft.loot.function.LootFunctionTypes;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.recipe.RecipeType;
import net.minecraft.recipe.SmeltingRecipe;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import thatrobin.foosh.Foosh;

import java.util.Optional;

public class SetQualityLootFunction extends ConditionalLootFunction {
    private static final Logger LOGGER = LogManager.getLogger();

    SetQualityLootFunction(LootCondition[] lootConditions) {
        super(lootConditions);
    }

    public LootFunctionType getType() {
        return Foosh.SET_QUALITY;
    }

    public ItemStack process(ItemStack itemStack, LootContext context) {
        NbtCompound compound = itemStack.getOrCreateNbt();
        String qualityStr;
        String[] qualities = new String[]{"common", "uncommon", "rare", "legendary"};
        int quality = getRandomInt(0, 3);
        qualityStr = qualities[quality];
        compound.putString("quality", qualityStr);
        itemStack.setNbt(compound);
        return itemStack;
    }

    public int getRandomInt(int min, int max) {
        return (int)Math.round((Math.random() * (max - min)) + min);
    }

    public static Builder<?> builder() {
        return builder(SetQualityLootFunction::new);
    }

    public static class Serializer extends ConditionalLootFunction.Serializer<SetQualityLootFunction> {
        public Serializer() {
        }

        public SetQualityLootFunction fromJson(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext, LootCondition[] lootConditions) {
            return new SetQualityLootFunction(lootConditions);
        }
    }
}
