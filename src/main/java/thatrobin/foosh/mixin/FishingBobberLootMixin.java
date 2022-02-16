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
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
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

    @Shadow @Nullable public abstract PlayerEntity getPlayerOwner();

    public FishingBobberLootMixin(EntityType<?> type, World world) {
        super(type, world);
    }

    @Redirect(method = "use", at = @At(value = "INVOKE", target = "Lnet/minecraft/loot/LootManager;getTable(Lnet/minecraft/util/Identifier;)Lnet/minecraft/loot/LootTable;"))
    private LootTable generateLoot(LootManager instance, Identifier id) throws Exception {
        if(world.getRegistryKey() == World.NETHER) {
            return instance.getTable(FooshLootTables.LAVA_FISHING);
        }
        return instance.getTable(LootTables.FISHING_GAMEPLAY);
    }

    @Inject(
            method = "use",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/ItemEntity;setVelocity(DDD)V"),
            locals = LocalCapture.CAPTURE_FAILHARD)
    private void setFireproof(ItemStack usedItem, CallbackInfoReturnable<Integer> cir, PlayerEntity playerEntity, int i, LootContext.Builder builder, LootTable lootTable, List<?> list, Iterator<?> var7, ItemStack itemStack, ItemEntity itemEntity, double d, double e, double f, double g) {
        ((FireproofEntity) itemEntity).setFireproof(true);
    }
}
