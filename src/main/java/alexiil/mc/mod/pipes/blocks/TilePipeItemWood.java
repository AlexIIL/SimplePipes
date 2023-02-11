package alexiil.mc.mod.pipes.blocks;

import net.minecraft.block.BlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

import alexiil.mc.mod.pipes.pipe.PipeSpFlowItem;

import alexiil.mc.lib.attributes.Simulation;
import alexiil.mc.lib.attributes.item.ItemExtractable;
import alexiil.mc.lib.attributes.item.impl.EmptyItemExtractable;

@Deprecated
public class TilePipeItemWood extends TilePipeWood {

    public TilePipeItemWood(BlockPos pos, BlockState state) {
        super(
            SimplePipeBlocks.WOODEN_PIPE_ITEM_TILE, pos, state, SimplePipeBlocks.WOODEN_PIPE_ITEMS, PipeSpFlowItem::new
        );
    }

    @Override
    protected boolean canFaceDirection(Direction dir) {
        if (getNeighbourPipe(dir) != null) {
            return false;
        }
        return getItemExtractable(dir) != EmptyItemExtractable.NULL;
    }

    @Override
    public void tryExtract(Direction dir, int pulses) {
        ItemExtractable extractable = getItemExtractable(dir);
        ItemStack stack = extractable.attemptAnyExtraction(pulses, Simulation.ACTION);

        if (!stack.isEmpty()) {
            ((PipeSpFlowItem) getFlow()).insertItemsForce(stack, dir, null, EXTRACT_SPEED);
        }
    }
}
