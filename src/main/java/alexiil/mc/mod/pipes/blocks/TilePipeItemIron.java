package alexiil.mc.mod.pipes.blocks;

import java.util.EnumSet;
import java.util.Iterator;
import java.util.List;

import net.minecraft.util.math.Direction;

public class TilePipeItemIron extends TilePipeIron {

    public TilePipeItemIron() {
        super(SimplePipeBlocks.IRON_PIPE_ITEM_TILE, SimplePipeBlocks.IRON_PIPE_ITEMS, pipe -> new PipeFlowItem(pipe) {
            @Override
            protected List<EnumSet<Direction>> getOrderForItem(TravellingItem item,
                EnumSet<Direction> validDirections) {
                List<EnumSet<Direction>> order = super.getOrderForItem(item, validDirections);
                Direction currentDirection = ((TilePipeSided) pipe).currentDirection();
                Iterator<EnumSet<Direction>> iterator = order.iterator();
                while (iterator.hasNext()) {
                    EnumSet<Direction> set = iterator.next();
                    if (set.contains(currentDirection)) {
                        set.clear();
                        set.add(currentDirection);
                    } else {
                        iterator.remove();
                    }
                }
                return order;
            }

            @Override
            protected boolean canBounce() {
                return true;
            }
        });
    }
}
