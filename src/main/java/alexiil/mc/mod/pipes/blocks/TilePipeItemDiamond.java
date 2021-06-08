package alexiil.mc.mod.pipes.blocks;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

import net.minecraft.block.BlockState;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

import alexiil.mc.mod.pipes.pipe.PipeFlowItem;
import alexiil.mc.mod.pipes.pipe.TravellingItem;

import alexiil.mc.lib.attributes.item.ItemAttributes;

public class TilePipeItemDiamond extends TilePipe {

    public final int INV_SIZE = 9 * 6;

    public final SimpleInventory filterInv = new SimpleInventory(INV_SIZE);

    public TilePipeItemDiamond(BlockPos pos, BlockState state) {
        super(
            SimplePipeBlocks.DIAMOND_PIPE_ITEM_TILE, pos, state, SimplePipeBlocks.DIAMOND_PIPE_ITEMS,
            pipe -> new PipeFlowItem(pipe) {
                @Override
                protected List<EnumSet<Direction>> getOrderForItem(
                    TravellingItem item, EnumSet<Direction> validDirections
                ) {
                    List<EnumSet<Direction>> list = new ArrayList<>();
                    EnumSet<Direction> matches = EnumSet.noneOf(Direction.class);
                    EnumSet<Direction> empties = EnumSet.noneOf(Direction.class);

                    for (Direction dir : validDirections) {
                        boolean emptyRow = true;

                        for (int x = 0; x < 9; x++) {
                            ItemStack filter = ((TilePipeItemDiamond) pipe).filterInv.getStack(dir.getId() * 9 + x);

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
                    }

                    list.add(matches);
                    list.add(empties);
                    return list;
                }

                @Override
                protected boolean canBounce() {
                    return true;
                }
            }
        );
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        for (int i = 0; i < INV_SIZE; i++) {
            filterInv.setStack(i, ItemStack.fromNbt(nbt.getCompound("filterStack_" + i)));
        }
    }

    @Override
    public NbtCompound writeNbt(NbtCompound tag) {
        tag = super.writeNbt(tag);
        for (int i = 0; i < INV_SIZE; i++) {
            ItemStack stack = filterInv.getStack(i);
            if (!stack.isEmpty()) {
                tag.put("filterStack_" + i, stack.writeNbt(new NbtCompound()));
            }
        }
        return tag;
    }

    @Override
    public DefaultedList<ItemStack> removeItemsForDrop() {
        DefaultedList<ItemStack> list = super.removeItemsForDrop();
        list.addAll(filterInv.clearToList());
        return list;
    }
}
