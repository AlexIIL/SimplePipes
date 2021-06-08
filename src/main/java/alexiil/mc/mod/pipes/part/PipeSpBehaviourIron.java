package alexiil.mc.mod.pipes.part;

import net.minecraft.util.math.Direction;

import alexiil.mc.mod.pipes.pipe.ISimplePipe;
import alexiil.mc.mod.pipes.pipe.PartSpPipe;
import alexiil.mc.mod.pipes.pipe.PipeSpFlowItem;
import alexiil.mc.mod.pipes.pipe.PipeSpBehaviourSided;

public class PipeSpBehaviourIron extends PipeSpBehaviourSided {

    public PipeSpBehaviourIron(PartSpPipe pipe) {
        super(pipe);
    }

    @Override
    protected boolean canFaceDirection(Direction dir) {
        ISimplePipe neighbour = pipe.getNeighbourPipe(dir);
        if (neighbour != null) {
            return pipe.flow instanceof PipeSpFlowItem == neighbour.getFlow() instanceof PipeSpFlowItem;
        }
        return pipe.flow.hasInsertable(dir);
    }
}
