package alexiil.mc.mod.pipes.pipe;

import alexiil.mc.mod.pipes.blocks.TilePipeFluidSponge;

public class PipeSpBehaviourSponge extends PipeSpBehaviour {

    public PipeSpBehaviourSponge(PartSpPipe pipe) {
        super(pipe);
    }

    @Override
    public void tick() {
        super.tick();
        TilePipeFluidSponge.tickFluid((PipeSpFlowFluid) pipe.getFlow());
    }
}
