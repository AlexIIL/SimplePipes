package alexiil.mc.mod.pipes.blocks;

import net.minecraft.block.Block;
import net.minecraft.world.BlockView;

public class BlockTriggerFluidFull extends BlockTriggerItemInv {

    public BlockTriggerFluidFull(Block.Settings settings) {
        super(settings);
    }

    @Override
    public TileTrigger createBlockEntity(BlockView view) {
        return new TileTriggerFluidFull();
    }
}
