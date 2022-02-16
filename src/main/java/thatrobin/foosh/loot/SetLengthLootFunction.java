package thatrobin.foosh.loot;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.function.ConditionalLootFunction;
import net.minecraft.loot.function.LootFunctionType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtFloat;
import net.minecraft.nbt.NbtList;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import thatrobin.foosh.Foosh;
import thatrobin.foosh.registry.FishRegistry;

import java.text.DecimalFormat;

public class SetLengthLootFunction extends ConditionalLootFunction {
    private static final Logger LOGGER = LogManager.getLogger();

    SetLengthLootFunction(LootCondition[] lootConditions) {
        super(lootConditions);
    }

    public LootFunctionType getType() {
        return Foosh.SET_QUALITY;
    }

    public ItemStack process(ItemStack itemStack, LootContext context) {
        Float minLength = 1.0f;
        Float maxLength = 100.0f;
        if (FishRegistry.contains(itemStack)) {
            Identifier item = FishRegistry.getId(itemStack);
            minLength = FishRegistry.getMinLength(item);
            maxLength = FishRegistry.getMaxLength(item);
        }
        float num = getRandomFloat(minLength, maxLength);
        DecimalFormat df = new DecimalFormat("##.##");
        num = Float.parseFloat(df.format(num));

        NbtFloat nbtFloat = NbtFloat.of(num);

        NbtList nbtList = new NbtList();
        nbtList.add(nbtFloat);
        NbtCompound compound = itemStack.getOrCreateNbt();
        compound.put("lengths", nbtList);

        itemStack.setNbt(compound);
        return itemStack;
    }

    public float getRandomFloat(float min, float max) {
        return (float) ((Math.random() * (max - min)) + min);
    }

    public int getRandomInt(int min, int max) {
        return (int) ((Math.random() * (max - min)) + min);
    }

    public static Builder<?> builder() {
        return builder(SetLengthLootFunction::new);
    }

    public static class Serializer extends ConditionalLootFunction.Serializer<SetLengthLootFunction> {
        public Serializer() {
        }

        public SetLengthLootFunction fromJson(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext, LootCondition[] lootConditions) {
            return new SetLengthLootFunction(lootConditions);
        }
    }
}
