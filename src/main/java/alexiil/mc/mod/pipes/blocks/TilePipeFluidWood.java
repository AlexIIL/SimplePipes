package alexiil.mc.mod.pipes.blocks;

import net.minecraft.util.math.Direction;

import alexiil.mc.lib.attributes.fluid.impl.EmptyFluidExtractable;

public class TilePipeFluidWood extends TilePipeWood {
    public TilePipeFluidWood() {
        super(SimplePipeBlocks.WOODEN_PIPE_FLUID_TILE, SimplePipeBlocks.WOODEN_PIPE_FLUIDS, PipeFlowFluid::new);
    }

    @Override
    protected void tryExtract(Direction dir) {
        ((PipeFlowFluid) flow).tryExtract(dir);
    }

    @Override
    protected boolean canFaceDirection(Direction dir) {
        if (getNeighbourPipe(dir) != null) {
            return false;
        }
        return getFluidExtractable(dir) != EmptyFluidExtractable.NULL;
    }
}
