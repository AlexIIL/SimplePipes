package alexiil.mc.mod.pipes.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.Tickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

import alexiil.mc.lib.attributes.fluid.FluidAttributes;
import alexiil.mc.lib.attributes.fluid.IFluidInvStats;
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
        Block block = state.getBlock();
        if (block instanceof BlockTrigger) {
            EnumTriggerState previousState = state.get(BlockTrigger.STATE);
            Direction facing = state.get(BlockTrigger.FACING);
            EnumTriggerState newState = getTriggerState(facing);
            if (previousState != newState) {
                world.setBlockState(getPos(), state.with(BlockTrigger.STATE, newState));
                BlockPos offset = getPos().offset(facing.getOpposite());
                world.updateNeighbor(offset, block, getPos());
                world.updateNeighborsExcept(offset, block, facing);
            }
        }
    }

    protected abstract EnumTriggerState getTriggerState(Direction dir);

    public IItemInvStats getNeighbourItemStats(Direction dir) {
        return getNeighbourAttribute(ItemAttributes.INV_STATS, dir);
    }

    public IFluidInvStats getNeighbourFluidStats(Direction dir) {
        return getNeighbourAttribute(FluidAttributes.INV_STATS, dir);
    }
}
