package alexiil.mc.mod.pipes.blocks;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.Tickable;
import net.minecraft.util.math.Direction;

public abstract class TileTrigger extends BlockEntity implements Tickable {

    public TileTrigger(BlockEntityType<?> type) {
        super(type);
    }

    @Override
    public void tick() {
        if (world.isClient) {
            return;
        }
        BlockState state = world.getBlockState(getPos());
        if (state.getBlock() instanceof BlockTrigger) {
            boolean wasActive = state.get(BlockTrigger.ACTIVE);
            boolean nowActive = isTriggerActive(state.get(BlockTrigger.FACING));
            if (wasActive != nowActive) {
                world.setBlockState(getPos(), state.with(BlockTrigger.ACTIVE, nowActive));
            }
        }
    }

    protected abstract boolean isTriggerActive(Direction dir);
}
