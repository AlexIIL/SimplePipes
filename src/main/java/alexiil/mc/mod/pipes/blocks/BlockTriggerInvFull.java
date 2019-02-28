package alexiil.mc.mod.pipes.blocks;

import net.minecraft.block.Block;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

import alexiil.mc.lib.attributes.item.IItemInvStats;
import alexiil.mc.lib.attributes.item.ItemInvUtil;
import alexiil.mc.lib.attributes.item.filter.IItemFilter;
import alexiil.mc.lib.attributes.item.impl.EmptyItemInvStats;

public class BlockTriggerInvFull extends BlockTrigger {

    public BlockTriggerInvFull(Block.Settings settings) {
        super(settings);
    }

    @Override
    protected boolean isTriggerBlock(World world, BlockPos pos, Direction dir) {
        pos = pos.offset(dir);
        return ItemInvUtil.getItemInvStats(world, pos) != EmptyItemInvStats.INSTANCE;
    }

    @Override
    protected boolean isTriggerActive(World world, BlockPos pos, Direction dir) {
        pos = pos.offset(dir);
        IItemInvStats invStats = ItemInvUtil.getItemInvStats(world, pos);
        if (invStats == EmptyItemInvStats.INSTANCE) {
            return false;
        }
        return invStats.getStatistics(IItemFilter.ANY_STACK).spaceAddable == 0;
    }
}
