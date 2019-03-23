package alexiil.mc.mod.pipes.blocks;

public class TilePipeFluidIron extends TilePipeIron {
    public TilePipeFluidIron() {
        super(SimplePipeBlocks.IRON_PIPE_FLUID_TILE, SimplePipeBlocks.IRON_PIPE_FLUIDS, PipeFlowFluid::new);
    }
}
