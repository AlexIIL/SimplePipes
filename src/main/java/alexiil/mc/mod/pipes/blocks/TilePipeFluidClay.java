package alexiil.mc.mod.pipes.blocks;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;

import alexiil.mc.mod.pipes.pipe.PipeSpFlowFluid;

public class TilePipeFluidClay extends TilePipe {
    public TilePipeFluidClay(BlockPos pos, BlockState state) {
        super(SimplePipeBlocks.CLAY_PIPE_FLUID_TILE, pos, state, SimplePipeBlocks.CLAY_PIPE_FLUIDS, PipeSpFlowFluid::new);
    }
}
