package alexiil.mc.mod.pipes.blocks;

import net.minecraft.util.math.Direction;

import alexiil.mc.lib.attributes.item.IItemInvStats;
import alexiil.mc.lib.attributes.item.ItemInvUtil;
import alexiil.mc.lib.attributes.item.filter.IItemFilter;
import alexiil.mc.lib.attributes.item.impl.EmptyItemInvStats;

public class TileTriggerInvFull extends TileTrigger {
    public TileTriggerInvFull() {
        super(SimplePipeBlocks.TRIGGER_ITEM_INV_FULL_TILE);
    }

    @Override
    protected boolean isTriggerActive(Direction dir) {
        IItemInvStats invStats = ItemInvUtil.getItemInvStats(world, getPos().offset(dir));
        if (invStats == EmptyItemInvStats.INSTANCE) {
            return false;
        }
        return invStats.getStatistics(IItemFilter.ANY_STACK).spaceAddable == 0;
    }
}
