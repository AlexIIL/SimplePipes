package alexiil.mc.mod.pipes.blocks;

import net.minecraft.util.math.Direction;

import alexiil.mc.lib.attributes.fluid.FluidInvStats;
import alexiil.mc.lib.attributes.fluid.FluidInvStats.FluidInvStatistic;
import alexiil.mc.lib.attributes.fluid.filter.ConstantFluidFilter;
import alexiil.mc.lib.attributes.fluid.impl.EmptyFluidInvStats;
import alexiil.mc.lib.attributes.misc.LibBlockAttributes;

public class TileTriggerFluidFull extends TileTrigger {
    public TileTriggerFluidFull() {
        super(SimplePipeBlocks.TRIGGER_FLUID_INV_FULL_TILE);
    }

    @Override
    protected EnumTriggerState getTriggerState(Direction dir) {
        FluidInvStats invStats = getNeighbourFluidStats(dir);
        if (invStats == EmptyFluidInvStats.INSTANCE) {
            return EnumTriggerState.NO_TARGET;
        }
        FluidInvStatistic stats = invStats.getStatistics(ConstantFluidFilter.ANYTHING);
        if (stats.spaceTotal == -1) {
            // Not good!
            LibBlockAttributes.LOGGER.warn("Found an FluidInvStats implementation that doesn't correctly "
                + "calculate the 'FluidInvStatistic.spaceTotal' value from 'ConstantFluidFilter.ANYTHING'!\n"
                + invStats.getClass() + " for block " + world.getBlockState(getPos()) + ", block entity = "
                + world.getBlockEntity(getPos()));
        }
        return EnumTriggerState.of(stats.spaceAddable + stats.spaceTotal == 0);
    }
}
