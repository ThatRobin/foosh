package thatrobin.foosh.mixin;

import net.minecraft.entity.projectile.FishingBobberEntity;
import thatrobin.foosh.api.FireproofEntity;
import thatrobin.foosh.api.FishingBonus;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.FishingRodItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.List;

@Mixin(FishingRodItem.class)
public class FishingRodPropertyMixin {

    @Unique private PlayerEntity player;
    @Unique private ItemStack heldStack;

    @Inject(method = "use", at = @At("HEAD"))
    private void storeContext(World world, PlayerEntity user, Hand hand, CallbackInfoReturnable<TypedActionResult<ItemStack>> cir) {
        this.heldStack = user.getStackInHand(hand);
        this.player = user;
    }

    @Redirect(method = "use", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;spawnEntity(Lnet/minecraft/entity/Entity;)Z"))
    private boolean modifyBobber(World world, Entity entity) {
        if(entity instanceof FishingBobberEntity bobber) {
            modifyBobber(world, bobber);
        }

        return world.spawnEntity(entity);
    }

    @Unique
    private void modifyBobber(World world, FishingBobberEntity bobber) {
        int bonusLure = 0;
        int bonusLuck = 0;

        // Find buffing items in player inventory
        List<FishingBonus> found = new ArrayList<>();
        for (ItemStack stack : player.getInventory().main) {
            Item item = stack.getItem();

            if (item instanceof FishingBonus bonus) {
                if (!found.contains(bonus)) {
                    if(bonus.shouldApply(world, player)) {
                        found.add(bonus);
                        bonusLure += bonus.getLure();
                        bonusLuck += bonus.getLuckOfTheSea();
                    }
                }
            }
        }

        // Modify bobber statistics
        ((FireproofEntity) bobber).setFireproof(false);
        FishingBobberEntityAccessor accessor = (FishingBobberEntityAccessor) bobber;
        accessor.setLureLevel(accessor.getLureLevel() + bonusLure);
        accessor.setLuckOfTheSeaLevel(accessor.getLuckOfTheSeaLevel() + bonusLuck);
    }

}
