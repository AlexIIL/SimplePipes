package alexiil.mc.mod.pipes.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockRenderLayer;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.VerticalEntityPosition;
import net.minecraft.state.StateFactory.Builder;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Direction.Axis;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

import alexiil.mc.lib.attributes.AttributeList;
import alexiil.mc.lib.attributes.IAttributeBlock;

public class BlockTank extends BlockBase implements BlockEntityProvider, IAttributeBlock {

    public static final VoxelShape SHAPE = VoxelShapes.cube(2 / 16.0, 0, 2 / 16.0, 14 / 16.0, 1, 14 / 16.0);
    public static final BooleanProperty JOINED_BELOW = BooleanProperty.create("joined_below");

    public BlockTank(Block.Settings settings) {
        super(settings);
    }

    @Override
    protected void appendProperties(Builder<Block, BlockState> builder) {
        super.appendProperties(builder);
        builder.with(JOINED_BELOW);
    }

    @Override
    public BlockEntity createBlockEntity(BlockView var1) {
        return new TileTank();
    }

    @Override
    public VoxelShape getOutlineShape(BlockState blockState_1, BlockView blockView_1, BlockPos blockPos_1,
        VerticalEntityPosition verticalEntityPosition_1) {
        return SHAPE;
    }

    @Override
    public BlockRenderLayer getRenderLayer() {
        return BlockRenderLayer.CUTOUT;
    }

    @Override
    public boolean skipRenderingSide(BlockState thisState, BlockState otherState, Direction side) {
        if (otherState.getBlock() == this && side.getAxis() == Axis.Y) {
            return true;
        }
        return false;
    }

    @Override
    public <T> void addAllAttributes(World world, BlockPos pos, BlockState state, AttributeList<T> to) {
        BlockEntity be = world.getBlockEntity(pos);
        if (be instanceof TileTank) {
            TileTank tank = (TileTank) be;
            to.offer(tank.fluidInv, SHAPE);
            to.offer(tank.fluidInv.getStatistics(), SHAPE);
        }
    }
}
