package thatrobin.foosh.mixin;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.FishingBobberEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootManager;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.LootTables;
import net.minecraft.loot.context.LootContext;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtFloat;
import net.minecraft.nbt.NbtList;
import net.minecraft.tag.ItemTags;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import thatrobin.foosh.api.FireproofEntity;
import thatrobin.foosh.registry.FishRegistry;
import thatrobin.foosh.registry.FooshLootTables;

import java.text.DecimalFormat;
import java.util.Iterator;
import java.util.List;

@Mixin(FishingBobberEntity.class)
public abstract class FishingBobberLootMixin extends Entity {

    public FishingBobberLootMixin(EntityType<?> type, World world) {
        super(type, world);
    }

    @Redirect(method = "use", at = @At(value = "INVOKE", target = "Lnet/minecraft/loot/LootManager;getTable(Lnet/minecraft/util/Identifier;)Lnet/minecraft/loot/LootTable;"))
    private LootTable generateLoot(LootManager instance, Identifier id) throws Exception {
        if(world.getServer() != null) {
            if(world.getRegistryKey() == World.NETHER) {
                return instance.getTable(FooshLootTables.LAVA_FISHING);
            }
            return instance.getTable(LootTables.FISHING_GAMEPLAY);
        }
        throw new Exception("Server is null");
    }
    @Redirect(
            method = "use",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/loot/LootTable;generateLoot(Lnet/minecraft/loot/context/LootContext;)Ljava/util/List;"))
    private List<ItemStack> generateLoot(LootTable instance, LootContext context) {
        List<ItemStack> list = instance.generateLoot(context);

        for (ItemStack itemStack : list) {
            if(itemStack.isIn(ItemTags.FISHES)) {
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
                NbtCompound compound = new NbtCompound();
                compound.put("lengths", nbtList);
                String qualityStr;
                String[] qualities = new String[]{"common", "uncommon", "rare", "legendary"};
                int quality = getRandomInt(0, 3);
                qualityStr = qualities[quality];
                compound.putString("quality", qualityStr);
                itemStack.setNbt(compound);
            }
        }

        return list;
    }

    public float getRandomFloat(float min, float max) {
        return (float) ((Math.random() * (max - min)) + min);
    }

    public int getRandomInt(int min, int max) {
        return (int) ((Math.random() * (max - min)) + min);
    }

    @Inject(
            method = "use",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/ItemEntity;setVelocity(DDD)V"),
            locals = LocalCapture.CAPTURE_FAILHARD)
    private void setFireproof(ItemStack usedItem, CallbackInfoReturnable<Integer> cir, PlayerEntity playerEntity, int i, LootContext.Builder builder, LootTable lootTable, List<?> list, Iterator<?> var7, ItemStack itemStack, ItemEntity itemEntity, double d, double e, double f, double g) {
        ((FireproofEntity) itemEntity).setFireproof(true);
    }
}
