package alexiil.mc.mod.pipes.blocks;

import net.minecraft.util.math.Direction;

import alexiil.mc.lib.attributes.item.IItemInvStats;
import alexiil.mc.lib.attributes.item.filter.ConstantItemFilter;
import alexiil.mc.lib.attributes.item.impl.EmptyItemInvStats;

public class TileTriggerInvEmpty extends TileTrigger {
    public TileTriggerInvEmpty() {
        super(SimplePipeBlocks.TRIGGER_ITEM_INV_EMPTY_TILE);
    }

    @Override
    protected EnumTriggerState getTriggerState(Direction dir) {
        IItemInvStats invStats = getNeighbourItemStats(dir);
        if (invStats == EmptyItemInvStats.INSTANCE) {
            return EnumTriggerState.NO_TARGET;
        }
        return EnumTriggerState.of(invStats.getStatistics(ConstantItemFilter.ANYTHING).amount == 0);
    }
}
