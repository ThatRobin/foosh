package thatrobin.foosh.mixin;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.FishingBobberEntity;
import net.minecraft.util.TypeFilter;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.apache.commons.compress.utils.Lists;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import thatrobin.foosh.api.RedstoneEntity;

import java.util.List;

@Mixin(AbstractBlock.class)
public class BlockMixin {

    @Inject(method = "getWeakRedstonePower", at = @At("RETURN"), cancellable = true)
    public void getRedstonePower(BlockState state, BlockView world, BlockPos pos, Direction direction, CallbackInfoReturnable<Integer> cir) {
        TypeFilter test = TypeFilter.instanceOf(FishingBobberEntity.class);
        List<Entity> entities = Lists.newArrayList();
        Direction[] directions = {Direction.NORTH, Direction.SOUTH, Direction.EAST, Direction.WEST};

        entities.addAll(((World)world).getEntitiesByType(test, Box.from(new BlockBox(pos)), (entity) -> {
            if(entity instanceof FishingBobberEntity bobber) {
                return ((RedstoneEntity)bobber).isRedstone();
            }
            return false;
        }));
        if(entities.isEmpty()) {
            for (Direction dir : directions) {
                BlockPos tempPos = pos.offset(dir);
                BlockBox blockBox = new BlockBox(tempPos);
                entities.addAll(((World) world).getEntitiesByType(test, Box.from(blockBox), (entity) -> {
                    if (entity instanceof FishingBobberEntity bobber) {
                        return ((RedstoneEntity) bobber).isRedstone();
                    }
                    return false;
                }));
            }
        }
        if(!entities.isEmpty()) {
            cir.setReturnValue(15);
        }
    }
}
