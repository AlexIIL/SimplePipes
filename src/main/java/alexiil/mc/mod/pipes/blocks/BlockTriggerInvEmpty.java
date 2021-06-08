package alexiil.mc.mod.pipes.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;

public class BlockTriggerInvEmpty extends BlockTriggerItemInv {

    public BlockTriggerInvEmpty(Block.Settings settings) {
        super(settings);
    }

    @Override
    public TileTrigger createBlockEntity(BlockPos pos, BlockState state) {
        return new TileTriggerInvEmpty(pos, state);
    }
}
