package thatrobin.foosh.mixin;

import net.minecraft.block.entity.BrewingStandBlockEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.recipe.BrewingRecipeRegistry;
import net.minecraft.util.collection.DefaultedList;
import org.checkerframework.checker.units.qual.A;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import thatrobin.foosh.Foosh;
import thatrobin.foosh.item.GlassBottleFishItem;
import thatrobin.foosh.item.PotionFishItem;
import thatrobin.foosh.registry.FooshItems;

@Mixin(BrewingStandBlockEntity.class)
public abstract class BrewingStandBlockEntityMixin {

    @Shadow private DefaultedList<ItemStack> inventory;

    @Shadow public abstract ItemStack getStack(int slot);

    @Inject(method = "isValid", at = @At(value = "RETURN"), cancellable = true)
    public void isValid(int slot, ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
        if (slot != 3 && slot != 4) {
            cir.setReturnValue(cir.getReturnValue() || (stack.isOf(FooshItems.GLASSFISH) || stack.isOf(FooshItems.GLASSFISH_POTION) && this.getStack(slot).isEmpty()));
        }
    }
}
