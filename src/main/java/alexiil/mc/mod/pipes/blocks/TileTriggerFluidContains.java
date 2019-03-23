package alexiil.mc.mod.pipes.blocks;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.math.Direction;

import alexiil.mc.lib.attributes.fluid.IFluidInvStats;
import alexiil.mc.lib.attributes.fluid.filter.ConstantFluidFilter;
import alexiil.mc.lib.attributes.fluid.filter.ExactFluidFilter;
import alexiil.mc.lib.attributes.fluid.filter.IFluidFilter;
import alexiil.mc.lib.attributes.fluid.impl.EmptyFluidInvStats;
import alexiil.mc.lib.attributes.fluid.volume.FluidKey;
import alexiil.mc.lib.attributes.fluid.volume.FluidKeys;

public class TileTriggerFluidContains extends TileTrigger {

    public FluidKey filter = FluidKeys.EMPTY;

    public TileTriggerFluidContains() {
        super(SimplePipeBlocks.TRIGGER_FLUID_INV_CONTAINS_TILE);
    }

    @Override
    public void fromTag(CompoundTag tag) {
        super.fromTag(tag);
        filter = FluidKey.fromTag(tag.getCompound("filter"));
    }

    @Override
    public CompoundTag toTag(CompoundTag tag) {
        tag = super.toTag(tag);
        if (!filter.isEmpty()) {
            tag.put("filter", filter.toTag());
        }
        return tag;
    }

    @Override
    protected EnumTriggerState getTriggerState(Direction dir) {
        IFluidInvStats invStats = getNeighbourFluidStats(dir);
        if (invStats == EmptyFluidInvStats.INSTANCE) {
            return EnumTriggerState.NO_TARGET;
        }
        final IFluidFilter fluidFilter;
        if (filter.isEmpty()) {
            fluidFilter = ConstantFluidFilter.ANYTHING;
        } else {
            fluidFilter = new ExactFluidFilter(filter);
        }
        return EnumTriggerState.of(invStats.getStatistics(fluidFilter).amount > 0);
    }
}
