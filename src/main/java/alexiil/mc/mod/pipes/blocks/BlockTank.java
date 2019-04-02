package alexiil.mc.mod.pipes.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockRenderLayer;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.VerticalEntityPosition;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Direction.Axis;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

import alexiil.mc.lib.attributes.AttributeList;
import alexiil.mc.lib.attributes.AttributeProvider;

public class BlockTank extends BlockBase implements BlockEntityProvider, AttributeProvider {

    public static final VoxelShape SHAPE = VoxelShapes.cuboid(2 / 16.0, 0, 2 / 16.0, 14 / 16.0, 12 / 16.0, 14 / 16.0);

    public BlockTank(Block.Settings settings) {
        super(settings);
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
            return false;
        }
        return false;
    }

    @Override
    public void addAllAttributes(World world, BlockPos pos, BlockState state, AttributeList<?> to) {
        BlockEntity be = world.getBlockEntity(pos);
        if (be instanceof TileTank) {
            TileTank tank = (TileTank) be;
            to.offer(tank.fluidInv, SHAPE);
            to.offer(tank.fluidInv.getStatistics(), SHAPE);
            to.offer(tank.fluidInv.getInsertable(), SHAPE);
            to.offer(tank.fluidInv.getExtractable(), SHAPE);
            // tank.addAttributes(to);
        }
    }
}
