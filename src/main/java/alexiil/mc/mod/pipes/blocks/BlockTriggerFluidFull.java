package alexiil.mc.mod.pipes.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;

public class BlockTriggerFluidFull extends BlockTriggerItemInv {

    public BlockTriggerFluidFull(Block.Settings settings) {
        super(settings);
    }

    @Override
    public TileTrigger createBlockEntity(BlockPos pos, BlockState state) {
        return new TileTriggerFluidFull(pos, state);
    }
}
