package alexiil.mc.mod.pipes.blocks;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

import alexiil.mc.mod.pipes.pipe.PipeSpFlowFluid;

import alexiil.mc.lib.attributes.fluid.impl.EmptyFluidExtractable;

public class TilePipeFluidWood extends TilePipeWood {
    public TilePipeFluidWood(BlockPos pos, BlockState state) {
        super(
            SimplePipeBlocks.WOODEN_PIPE_FLUID_TILE, pos, state, SimplePipeBlocks.WOODEN_PIPE_FLUIDS, PipeSpFlowFluid::new
        );
    }

    @Override
    public void tryExtract(Direction dir, int pulses) {
        ((PipeSpFlowFluid) getFlow()).tryExtract(dir);
    }

    @Override
    protected boolean canFaceDirection(Direction dir) {
        if (getNeighbourPipe(dir) != null) {
            return false;
        }
        return getFluidExtractable(dir) != EmptyFluidExtractable.NULL;
    }
}
