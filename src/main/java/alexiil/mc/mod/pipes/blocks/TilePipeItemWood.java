package alexiil.mc.mod.pipes.blocks;

import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Direction;

import alexiil.mc.lib.attributes.Simulation;
import alexiil.mc.lib.attributes.item.ItemExtractable;
import alexiil.mc.lib.attributes.item.impl.EmptyItemExtractable;

public class TilePipeItemWood extends TilePipeWood {

    public TilePipeItemWood() {
        super(SimplePipeBlocks.WOODEN_PIPE_ITEM_TILE, SimplePipeBlocks.WOODEN_PIPE_ITEMS, PipeFlowItem::new);
    }

    @Override
    protected boolean canFaceDirection(Direction dir) {
        if (getNeighbourPipe(dir) != null) {
            return false;
        }
        return getItemExtractable(dir) != EmptyItemExtractable.NULL;
    }

    @Override
    protected void tryExtract(Direction dir) {
        ItemExtractable extractable = getItemExtractable(dir);
        ItemStack stack = extractable.attemptAnyExtraction(1, Simulation.ACTION);

        if (!stack.isEmpty()) {
            ((PipeFlowItem) flow).insertItemsForce(stack, dir, null, EXTRACT_SPEED);
        }
    }
}
