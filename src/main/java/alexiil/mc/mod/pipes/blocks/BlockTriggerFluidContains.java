package alexiil.mc.mod.pipes.blocks;

import alexiil.mc.mod.pipes.container.ContainerTriggerFluidContains;
import alexiil.mc.mod.pipes.container.SimplePipeContainerFactory;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BlockTriggerFluidContains extends BlockTriggerFluidInv {

    public BlockTriggerFluidContains(Block.Settings settings) {
        super(settings);
    }

    @Override
    public TileTrigger createBlockEntity(BlockPos pos, BlockState state) {
        return new TileTriggerFluidContains(pos, state);
    }

    @Override
    public ActionResult onUse(
        BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit
    ) {
        BlockEntity be = world.getBlockEntity(pos);

        if (be instanceof TileTriggerFluidContains tile) {
            if (!world.isClient) {
                player.openHandledScreen(new SimplePipeContainerFactory(getName(),
                        (syncId, inv, player1) -> new ContainerTriggerFluidContains(syncId, player1, tile),
                        (player1, buf) -> buf.writeBlockPos(pos)));
            }
            return ActionResult.SUCCESS;
        }

        return super.onUse(state, world, pos, player, hand, hit);
    }
}
