package alexiil.mc.mod.pipes.blocks;

import java.util.EnumSet;
import java.util.List;

import java.util.ArrayList;

import net.minecraft.util.math.Direction;

import alexiil.mc.lib.attributes.item.ItemAttributes;

import net.minecraft.inventory.BasicInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DefaultedList;
import net.minecraft.nbt.CompoundTag;

public class TilePipeItemDiamond extends TilePipe {

    public final int INV_SIZE = 9 * 6;

    public final BasicInventory filterInv = new BasicInventory(INV_SIZE);

    public TilePipeItemDiamond() {
        super(SimplePipeBlocks.DIAMOND_PIPE_ITEM_TILE, SimplePipeBlocks.DIAMOND_PIPE_ITEMS, pipe -> new PipeFlowItem(pipe) {
            @Override
            protected List<EnumSet<Direction>> getOrderForItem(TravellingItem item, EnumSet<Direction> validDirections) {
                List<EnumSet<Direction>> list = new ArrayList<>();
                EnumSet<Direction> matches = EnumSet.noneOf(Direction.class);
                EnumSet<Direction> empties = EnumSet.noneOf(Direction.class);

                for (Direction dir : validDirections) {
                    boolean emptyRow = true;

                    for (int x = 0; x < 9; x++) {
                        ItemStack filter = ((TilePipeItemDiamond)pipe).filterInv.getInvStack(dir.getId() * 9 + x);
                    
                        if (!filter.isEmpty()) {
                            emptyRow = false;

                            if (filter.getItem().equals(item.stack.getItem())) {
                                matches.add(dir);
                            } else if (ItemAttributes.FILTER.get(filter).matches(item.stack)) {
                                matches.add(dir);
                            }
                        }
                    }

                    if (emptyRow) {
                        empties.add(dir);
                    }

                    list.add(matches);
                    list.add(empties);
                }
                return list;
            }

            @Override
            protected boolean canBounce() {
                return true;
            }
        });
    }

    @Override
    public void fromTag(CompoundTag tag) {
        super.fromTag(tag);
        for (int i = 0; i < INV_SIZE; i++) {
            filterInv.setInvStack(i, ItemStack.fromTag(tag.getCompound("filterStack_" + i)));
        }
    }

    @Override
    public CompoundTag toTag(CompoundTag tag) {
        tag = super.toTag(tag);
        for (int i = 0; i < INV_SIZE; i++) {
            ItemStack stack = filterInv.getInvStack(i);
            if (!stack.isEmpty()) {
                tag.put("filterStack_" + i, stack.toTag(new CompoundTag()));
            }
        }
        return tag;
    }

    @Override
    public DefaultedList<ItemStack> removeItemsForDrop() {
        DefaultedList<ItemStack> list = super.removeItemsForDrop();
        list.add(filterInv.removeInvStack(0));
        return list;
    }
}
