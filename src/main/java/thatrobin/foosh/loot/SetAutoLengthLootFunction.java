package thatrobin.foosh.loot;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.function.ConditionalLootFunction;
import net.minecraft.loot.function.LootFunctionType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Identifier;
import thatrobin.foosh.Foosh;
import thatrobin.foosh.config.ModConfigs;
import thatrobin.foosh.registry.FishRegistry;

import java.text.DecimalFormat;

public class SetAutoLengthLootFunction extends ConditionalLootFunction {

    SetAutoLengthLootFunction(LootCondition[] lootConditions) {
        super(lootConditions);
    }

    public LootFunctionType getType() {
        return Foosh.SET_AUTO_LENGTH;
    }

    public ItemStack process(ItemStack itemStack, LootContext context) {
        if(ModConfigs.AUTO_MEASURE && FishRegistry.contains(itemStack)) {
            Identifier item = FishRegistry.getId(itemStack);
            float minLength = FishRegistry.getMinLength(item);
            float maxLength = FishRegistry.getMaxLength(item);

            float num = getRandomFloat(minLength, maxLength);
            DecimalFormat df = new DecimalFormat("##.##");
            num = Float.parseFloat(df.format(num));

            NbtCompound compound = itemStack.getOrCreateNbt();
            compound.putFloat("length", num);

            itemStack.setNbt(compound);
        }
        return itemStack;
    }

    public float getRandomFloat(float min, float max) {
        return (float) ((Math.random() * (max - min)) + min);
    }

    public int getRandomInt(int min, int max) {
        return (int) ((Math.random() * (max - min)) + min);
    }

    public static Builder<?> builder() {
        return builder(SetAutoLengthLootFunction::new);
    }

    public static class Serializer extends ConditionalLootFunction.Serializer<SetAutoLengthLootFunction> {
        public Serializer() {
        }

        public SetAutoLengthLootFunction fromJson(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext, LootCondition[] lootConditions) {
            return new SetAutoLengthLootFunction(lootConditions);
        }
    }
}
