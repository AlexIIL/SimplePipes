package alexiil.mc.mod.pipes.blocks;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

import alexiil.mc.lib.attributes.item.GroupedItemInvView;
import alexiil.mc.lib.attributes.item.filter.ConstantItemFilter;
import alexiil.mc.lib.attributes.item.impl.EmptyGroupedItemInv;

public class TileTriggerInvEmpty extends TileTrigger {
    public TileTriggerInvEmpty(BlockPos pos, BlockState state) {
        super(SimplePipeBlocks.TRIGGER_ITEM_INV_EMPTY_TILE, pos, state);
    }

    @Override
    protected EnumTriggerState getTriggerState(Direction dir) {
        GroupedItemInvView invStats = getNeighbourItemStats(dir);
        if (invStats == EmptyGroupedItemInv.INSTANCE) {
            return EnumTriggerState.NO_TARGET;
        }
        return EnumTriggerState.of(invStats.getStatistics(ConstantItemFilter.ANYTHING).amount == 0);
    }
}
