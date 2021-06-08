package alexiil.mc.mod.pipes.blocks;

import net.minecraft.block.BlockState;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

import alexiil.mc.lib.attributes.item.GroupedItemInvView;
import alexiil.mc.lib.attributes.item.filter.ConstantItemFilter;
import alexiil.mc.lib.attributes.item.filter.ExactItemStackFilter;
import alexiil.mc.lib.attributes.item.filter.ItemFilter;
import alexiil.mc.lib.attributes.item.impl.EmptyGroupedItemInv;

public class TileTriggerInvContains extends TileTrigger {

    public final SimpleInventory filterInv = new SimpleInventory(1);

    public TileTriggerInvContains(BlockPos pos, BlockState state) {
        super(SimplePipeBlocks.TRIGGER_ITEM_INV_CONTAINS_TILE, pos, state);
    }

    @Override
    public void readNbt(NbtCompound tag) {
        super.readNbt(tag);
        filterInv.setStack(0, ItemStack.fromNbt(tag.getCompound("filterStack")));
    }

    @Override
    public NbtCompound writeNbt(NbtCompound tag) {
        tag = super.writeNbt(tag);
        ItemStack stack = filterInv.getStack(0);
        if (!stack.isEmpty()) {
            tag.put("filterStack", stack.writeNbt(new NbtCompound()));
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
        return EnumTriggerState.of(invStats.getStatistics(filter).amount > 0);
    }
}
