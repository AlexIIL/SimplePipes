package alexiil.mc.mod.pipes.blocks;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;

import alexiil.mc.mod.pipes.pipe.PipeSpFlowFluid;

public class TilePipeFluidIron extends TilePipeIron {
    public TilePipeFluidIron(BlockPos pos, BlockState state) {
        super(SimplePipeBlocks.IRON_PIPE_FLUID_TILE, pos, state, SimplePipeBlocks.IRON_PIPE_FLUIDS, PipeSpFlowFluid::new);
    }
}
