package alexiil.mc.mod.pipes.blocks;

import net.minecraft.block.BlockState;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

import alexiil.mc.lib.attributes.item.GroupedItemInvView;
import alexiil.mc.lib.attributes.item.GroupedItemInvView.ItemInvStatistic;
import alexiil.mc.lib.attributes.item.ItemStackUtil;
import alexiil.mc.lib.attributes.item.filter.ConstantItemFilter;
import alexiil.mc.lib.attributes.item.filter.ExactItemStackFilter;
import alexiil.mc.lib.attributes.item.filter.ItemFilter;
import alexiil.mc.lib.attributes.item.impl.EmptyGroupedItemInv;

public class TileTriggerInvSpace extends TileTrigger {

    public final SimpleInventory filterInv = new SimpleInventory(1);

    public TileTriggerInvSpace(BlockPos pos, BlockState state) {
        super(SimplePipeBlocks.TRIGGER_ITEM_INV_SPACE_TILE, pos, state);
    }

    @Override
    public void readNbt(NbtCompound tag, RegistryWrapper.WrapperLookup lookup) {
        super.readNbt(tag, lookup);
        filterInv.setStack(0, ItemStackUtil.fromNbt(tag.getCompound("filterStack"), lookup));
    }

    @Override
    public void writeNbt(NbtCompound tag, RegistryWrapper.WrapperLookup lookup) {
        super.writeNbt(tag, lookup);
        ItemStack stack = filterInv.getStack(0);
        if (!stack.isEmpty()) {
            tag.put("filterStack", ItemStackUtil.writeNbt(stack, lookup));
        }
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
        assert stats.spaceTotal
            >= 0 : "ItemInvStatistic should have checked this for ExactItemStackFilter and ConstantItemFilter!";
        return EnumTriggerState.of(stats.spaceAddable + stats.spaceTotal > 0);
    }
}
