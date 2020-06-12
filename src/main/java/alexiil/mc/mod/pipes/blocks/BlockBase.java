package alexiil.mc.mod.pipes.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public abstract class BlockBase extends Block {

    public BlockBase(Block.Settings settings) {
        super(settings);
    }

    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
        super.onPlaced(world, pos, state, placer, stack);
        BlockEntity be = world.getBlockEntity(pos);
        if (be instanceof TileBase) {
            ((TileBase) be).onPlacedBy(placer, stack);
        }
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand,
        BlockHitResult hit) {
        BlockEntity be = world.getBlockEntity(pos);
        if (be instanceof TileBase) {
            return ((TileBase) be).onUse(player, hand, hit);
        }
        return super.onUse(state, world, pos, player, hand, hit);
    }

    @Override
    public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState,
        boolean _unknown_boolean) {

        if (this != newState.getBlock()) {
            BlockEntity be = world.getBlockEntity(pos);
            if (be instanceof TileBase) {
                ItemScatterer.spawn(world, pos, ((TileBase) be).removeItemsForDrop());
            }
        }

        super.onStateReplaced(state, world, pos, newState, _unknown_boolean);
    }
}
