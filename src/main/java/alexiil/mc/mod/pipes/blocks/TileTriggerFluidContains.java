package alexiil.mc.mod.pipes.blocks;

import net.minecraft.block.BlockState;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

import alexiil.mc.lib.attributes.fluid.GroupedFluidInvView;
import alexiil.mc.lib.attributes.fluid.filter.ConstantFluidFilter;
import alexiil.mc.lib.attributes.fluid.filter.ExactFluidFilter;
import alexiil.mc.lib.attributes.fluid.filter.FluidFilter;
import alexiil.mc.lib.attributes.fluid.impl.EmptyGroupedFluidInv;
import alexiil.mc.lib.attributes.fluid.volume.FluidKey;
import alexiil.mc.lib.attributes.fluid.volume.FluidKeys;

public class TileTriggerFluidContains extends TileTrigger {

    public FluidKey filter = FluidKeys.EMPTY;

    public TileTriggerFluidContains(BlockPos pos, BlockState state) {
        super(SimplePipeBlocks.TRIGGER_FLUID_INV_CONTAINS_TILE, pos, state);
    }

    @Override
    public void readNbt(NbtCompound tag) {
        super.readNbt(tag);
        filter = FluidKey.fromTag(tag.getCompound("filter"));
    }

    @Override
    public void writeNbt(NbtCompound tag) {
        super.writeNbt(tag);
        if (!filter.isEmpty()) {
            tag.put("filter", filter.toTag());
        }
    }

    @Override
    protected EnumTriggerState getTriggerState(Direction dir) {
        GroupedFluidInvView invStats = getNeighbourFluidStats(dir);
        if (invStats == EmptyGroupedFluidInv.INSTANCE) {
            return EnumTriggerState.NO_TARGET;
        }
        final FluidFilter fluidFilter;
        if (filter.isEmpty()) {
            fluidFilter = ConstantFluidFilter.ANYTHING;
        } else {
            fluidFilter = new ExactFluidFilter(filter);
        }
        return EnumTriggerState.of(invStats.getStatistics(fluidFilter).amount_F.as1620() > 0);
    }
}
