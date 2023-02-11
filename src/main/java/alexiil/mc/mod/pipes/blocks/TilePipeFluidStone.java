package alexiil.mc.mod.pipes.blocks;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;

import alexiil.mc.mod.pipes.pipe.PipeSpFlowFluid;

@Deprecated
public class TilePipeFluidStone extends TilePipe {
    public TilePipeFluidStone(BlockPos pos, BlockState state) {
        super(
            SimplePipeBlocks.STONE_PIPE_FLUID_TILE, pos, state, SimplePipeBlocks.STONE_PIPE_FLUIDS, PipeSpFlowFluid::new
        );
    }
}
