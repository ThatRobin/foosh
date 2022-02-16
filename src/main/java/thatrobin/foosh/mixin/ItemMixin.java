package thatrobin.foosh.mixin;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.util.UseAction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(Item.class)
public class ItemMixin {

    @Inject(method = "finishUsing", at = @At("RETURN"), cancellable = true)
    public void getUseAction(CallbackInfoReturnable<ItemStack> cir) {
        ItemStack itemStack = cir.getReturnValue();
        if(!itemStack.isEmpty()) {
            NbtCompound leftNbt = itemStack.getNbt();
            if (leftNbt.contains("lengths")) {
                NbtList leftNbtList = new NbtList();
                NbtList nbtList = leftNbt.getList("lengths", 5);
                List<NbtElement> leftList = nbtList.subList(0, itemStack.getCount());
                leftNbtList.addAll(leftList);
                leftNbt.remove("lengths");
                leftNbt.put("lengths", leftNbtList);
                itemStack.setNbt(leftNbt);
                cir.setReturnValue(itemStack);
            }
        }
    }
}
