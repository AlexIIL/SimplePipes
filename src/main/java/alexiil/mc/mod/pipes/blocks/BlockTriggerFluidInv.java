package alexiil.mc.mod.pipes.blocks;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

import alexiil.mc.lib.attributes.fluid.impl.EmptyFluidInvStats;

public abstract class BlockTriggerFluidInv extends BlockTrigger {

    public BlockTriggerFluidInv(Settings settings) {
        super(settings);
    }

    @Override
    protected boolean isTriggerBlock(World world, BlockPos pos, Direction dir) {
        return getNeighbourFluidInvStats(world, pos, dir) != EmptyFluidInvStats.INSTANCE;
    }
}
