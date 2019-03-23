package alexiil.mc.mod.pipes.blocks;

public class TilePipeFluidClay extends TilePipe {
    public TilePipeFluidClay() {
        super(SimplePipeBlocks.CLAY_PIPE_FLUID_TILE, SimplePipeBlocks.CLAY_PIPE_FLUIDS, PipeFlowFluid::new);
    }
}
