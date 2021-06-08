package alexiil.mc.mod.pipes.util;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;

/** An {@link BlockView} for getting the properties of a single {@link BlockState} at the {@link SingleBlockView#POS} */
public class SingleBlockView implements BlockView {
    public static final BlockPos POS = BlockPos.ORIGIN;
    public final BlockState state;

    public SingleBlockView(BlockState state) {
        this.state = state;
    }

    @Override
    public BlockEntity getBlockEntity(BlockPos pos) {
        return null;
    }

    @Override
    public BlockState getBlockState(BlockPos pos) {
        return POS.equals(pos) ? state : Blocks.AIR.getDefaultState();
    }

    @Override
    public FluidState getFluidState(BlockPos pos) {
        return POS.equals(pos) ? state.getFluidState() : Blocks.AIR.getDefaultState().getFluidState();
    }

    @Override
    public int getHeight() {
        return 16;
    }

    @Override
    public int getBottomY() {
        return 0;
    }
}
