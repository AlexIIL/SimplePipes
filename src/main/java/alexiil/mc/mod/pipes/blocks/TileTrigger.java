package alexiil.mc.mod.pipes.blocks;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.Tickable;
import net.minecraft.util.math.Direction;

import alexiil.mc.lib.attributes.item.IItemInvStats;
import alexiil.mc.lib.attributes.item.ItemAttributes;

public abstract class TileTrigger extends TileBase implements Tickable {

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
            EnumTriggerState previousState = state.get(BlockTrigger.STATE);
            EnumTriggerState newState = getTriggerState(state.get(BlockTrigger.FACING));
            if (previousState != newState) {
                world.setBlockState(getPos(), state.with(BlockTrigger.STATE, newState));
            }
        }
    }

    protected abstract EnumTriggerState getTriggerState(Direction dir);

    public IItemInvStats getNeighbourStats(Direction dir) {
        return getNeighbourAttribute(ItemAttributes.INV_STATS, dir);
    }
}
