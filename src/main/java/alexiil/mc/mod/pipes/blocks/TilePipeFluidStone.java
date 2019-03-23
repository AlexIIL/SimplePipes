package alexiil.mc.mod.pipes.blocks;

public class TilePipeFluidStone extends TilePipe {
    public TilePipeFluidStone() {
        super(SimplePipeBlocks.STONE_PIPE_FLUID_TILE, SimplePipeBlocks.STONE_PIPE_FLUIDS, PipeFlowFluid::new);
    }
}
