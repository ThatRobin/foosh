package thatrobin.foosh.loot;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.FishingBobberEntity;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.loot.function.ConditionalLootFunction;
import net.minecraft.loot.function.FurnaceSmeltLootFunction;
import net.minecraft.loot.function.LootFunctionType;
import net.minecraft.loot.function.LootFunctionTypes;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.recipe.RecipeType;
import net.minecraft.recipe.SmeltingRecipe;
import net.minecraft.util.registry.Registry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import thatrobin.foosh.Foosh;
import thatrobin.foosh.util.Qualities;
import thatrobin.foosh.util.WeightedCollection;

import java.util.Optional;

public class SetQualityLootFunction extends ConditionalLootFunction {

    SetQualityLootFunction(LootCondition[] lootConditions) {
        super(lootConditions);
    }

    public LootFunctionType getType() {
        return Foosh.SET_QUALITY;
    }

    public ItemStack process(ItemStack itemStack, LootContext context) {
        NbtCompound compound = itemStack.getOrCreateNbt();
        Entity entity = context.get(LootContextParameters.THIS_ENTITY);
        String qualityStr;
        if(entity instanceof FishingBobberEntity fishingBobberEntity && fishingBobberEntity.owner instanceof LivingEntity livingEntity && livingEntity.hasStatusEffect(Foosh.FISHERMENS_LUCK)) {
            qualityStr = Qualities.getBiasQualities().next();
        } else {
            qualityStr = Qualities.getQualities().next();
        }
        compound.putString("quality", qualityStr);
        itemStack.setNbt(compound);
        return itemStack;
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
