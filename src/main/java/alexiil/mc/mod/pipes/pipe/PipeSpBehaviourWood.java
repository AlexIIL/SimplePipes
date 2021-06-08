package alexiil.mc.mod.pipes.pipe;

import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

import alexiil.mc.lib.attributes.Simulation;
import alexiil.mc.lib.attributes.fluid.impl.EmptyFluidExtractable;
import alexiil.mc.lib.attributes.item.ItemExtractable;
import alexiil.mc.lib.attributes.item.impl.EmptyItemExtractable;

public class PipeSpBehaviourWood extends PipeSpBehaviourSided {

    private boolean lastRecv = true;

    public PipeSpBehaviourWood(PartSpPipe pipe) {
        super(pipe);
    }

    @Override
    protected boolean canFaceDirection(Direction dir) {
        if (pipe.getNeighbourPipe(dir) != null) {
            return false;
        }
        return pipe.flow instanceof PipeFlowItem //
            ? pipe.getItemExtractable(dir) != EmptyItemExtractable.NULL //
            : pipe.getFluidExtractable(dir) != EmptyFluidExtractable.NULL;
    }

    @Override
    public void tick() {
        super.tick();
        World world = pipe.getWorld();
        if (world.isClient) {
            return;
        }
        Direction dir = currentDirection();
        if (dir == null) {
            return;
        }

        if (world.isReceivingRedstonePower(pipe.getPos())) {
            if (!lastRecv) {
                lastRecv = true;
                tryExtract(dir);
            }
        } else {
            lastRecv = false;
        }
    }

    private void tryExtract(Direction dir) {
        if (pipe.flow instanceof PipeFlowItem) {
            tryExtractItems(dir, 1);
        } else {
            tryExtractFluids(dir);
        }
    }

    public void tryExtractItems(Direction dir, int count) {
        ItemExtractable extractable = pipe.getItemExtractable(dir);
        ItemStack stack = extractable.attemptAnyExtraction(1, Simulation.ACTION);

        if (!stack.isEmpty()) {
            ((PipeFlowItem) pipe.getFlow()).insertItemsForce(stack, dir, null, 0.08);
        }
    }

    public void tryExtractFluids(Direction dir) {
        ((PipeFlowFluid) pipe.getFlow()).tryExtract(dir);
    }
}
