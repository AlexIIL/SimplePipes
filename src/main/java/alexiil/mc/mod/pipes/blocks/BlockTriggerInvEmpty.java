package alexiil.mc.mod.pipes.blocks;

import net.minecraft.block.Block;
import net.minecraft.world.BlockView;

public class BlockTriggerInvEmpty extends BlockTriggerItemInv {

    public BlockTriggerInvEmpty(Block.Settings settings) {
        super(settings);
    }

    @Override
    public TileTrigger createBlockEntity(BlockView view) {
        return new TileTriggerInvEmpty();
    }
}
