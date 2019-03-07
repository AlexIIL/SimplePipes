package alexiil.mc.mod.pipes.blocks;

import java.util.EnumSet;
import java.util.List;

import net.minecraft.util.math.Direction;

public class TilePipeItemIron extends TilePipeIron {

    public TilePipeItemIron() {
        super(SimplePipeBlocks.IRON_PIPE_ITEM_TILE, SimplePipeBlocks.IRON_PIPE_ITEMS, pipe -> new PipeFlowItem(pipe) {
            @Override
            protected List<EnumSet<Direction>> getOrderForItem(TravellingItem item,
                EnumSet<Direction> validDirections) {
                List<EnumSet<Direction>> order = super.getOrderForItem(item, validDirections);
                for (EnumSet<Direction> set : order) {
                    set.remove(((TilePipeSided) pipe).currentDirection());
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
