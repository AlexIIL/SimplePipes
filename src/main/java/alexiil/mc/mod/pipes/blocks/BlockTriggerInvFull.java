package alexiil.mc.mod.pipes.blocks;

import net.minecraft.block.Block;
import net.minecraft.world.BlockView;

public class BlockTriggerInvFull extends BlockTriggerItemInv {

    public BlockTriggerInvFull(Block.Settings settings) {
        super(settings);
    }

    @Override
    public TileTrigger createBlockEntity(BlockView view) {
        return new TileTriggerInvFull();
    }
}
