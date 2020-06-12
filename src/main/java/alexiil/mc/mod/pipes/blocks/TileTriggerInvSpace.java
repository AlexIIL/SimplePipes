package alexiil.mc.mod.pipes.blocks;

import net.minecraft.block.BlockState;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.Direction;

import alexiil.mc.lib.attributes.item.GroupedItemInvView;
import alexiil.mc.lib.attributes.item.GroupedItemInvView.ItemInvStatistic;
import alexiil.mc.lib.attributes.item.filter.ConstantItemFilter;
import alexiil.mc.lib.attributes.item.filter.ExactItemStackFilter;
import alexiil.mc.lib.attributes.item.filter.ItemFilter;
import alexiil.mc.lib.attributes.item.impl.EmptyGroupedItemInv;

public class TileTriggerInvSpace extends TileTrigger {

    public final SimpleInventory filterInv = new SimpleInventory(1);

    public TileTriggerInvSpace() {
        super(SimplePipeBlocks.TRIGGER_ITEM_INV_SPACE_TILE);
    }

    @Override
    public void fromTag(BlockState state, CompoundTag tag) {
        super.fromTag(state, tag);
        filterInv.setStack(0, ItemStack.fromTag(tag.getCompound("filterStack")));
    }

    @Override
    public CompoundTag toTag(CompoundTag tag) {
        tag = super.toTag(tag);
        ItemStack stack = filterInv.getStack(0);
        if (!stack.isEmpty()) {
            tag.put("filterStack", stack.toTag(new CompoundTag()));
        }
        return tag;
    }

    @Override
    public DefaultedList<ItemStack> removeItemsForDrop() {
        DefaultedList<ItemStack> list = super.removeItemsForDrop();
        list.add(filterInv.removeStack(0));
        return list;
    }

    @Override
    protected EnumTriggerState getTriggerState(Direction dir) {
        GroupedItemInvView invStats = getNeighbourItemStats(dir);
        if (invStats == EmptyGroupedItemInv.INSTANCE) {
            return EnumTriggerState.NO_TARGET;
        }
        final ItemFilter filter;
        ItemStack stack = filterInv.getStack(0);
        if (stack.isEmpty()) {
            filter = ConstantItemFilter.ANYTHING;
        } else {
            filter = new ExactItemStackFilter(stack);
        }
        ItemInvStatistic stats = invStats.getStatistics(filter);
        assert stats.spaceTotal >= 0 : "ItemInvStatistic should have checked this for ExactItemStackFilter and ConstantItemFilter!";
        return EnumTriggerState.of(stats.spaceAddable + stats.spaceTotal > 0);
    }
}
