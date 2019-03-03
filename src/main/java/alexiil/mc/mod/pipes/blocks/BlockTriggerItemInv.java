package alexiil.mc.mod.pipes.blocks;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

import alexiil.mc.lib.attributes.item.impl.EmptyItemInvStats;

public abstract class BlockTriggerItemInv extends BlockTrigger {

    public BlockTriggerItemInv(Settings settings) {
        super(settings);
    }

    @Override
    protected boolean isTriggerBlock(World world, BlockPos pos, Direction dir) {
        return getNeighbourItemInvStats(world, pos, dir) != EmptyItemInvStats.INSTANCE;
    }

}
