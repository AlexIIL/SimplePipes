package alexiil.mc.mod.pipes.blocks;

import net.minecraft.util.math.Direction;

import alexiil.mc.lib.attributes.item.ItemInvStats;
import alexiil.mc.lib.attributes.item.ItemInvStats.ItemInvStatistic;
import alexiil.mc.lib.attributes.item.filter.ConstantItemFilter;
import alexiil.mc.lib.attributes.item.impl.EmptyItemInvStats;
import alexiil.mc.lib.attributes.misc.LibBlockAttributes;

public class TileTriggerInvFull extends TileTrigger {
    public TileTriggerInvFull() {
        super(SimplePipeBlocks.TRIGGER_ITEM_INV_FULL_TILE);
    }

    @Override
    protected EnumTriggerState getTriggerState(Direction dir) {
        ItemInvStats invStats = getNeighbourItemStats(dir);
        if (invStats == EmptyItemInvStats.INSTANCE) {
            return EnumTriggerState.NO_TARGET;
        }
        ItemInvStatistic stats = invStats.getStatistics(ConstantItemFilter.ANYTHING);
        if (stats.spaceTotal == -1) {
            // Not good!
            LibBlockAttributes.LOGGER.warn("Found an ItemInvStats implementation that doesn't correctly "
                + "calculate the 'ItemInvStatistic.spaceTotal' value from 'ConstantItemFilter.ANYTHING'!\n"
                + invStats.getClass() + " for block " + world.getBlockState(getPos()) + ", block entity = "
                + world.getBlockEntity(getPos()));
        }
        return EnumTriggerState.of(stats.spaceAddable + stats.spaceTotal == 0);
    }
}
