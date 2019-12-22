package alexiil.mc.mod.pipes.blocks;

import java.util.Collections;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.EntityContext;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Direction.Axis;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

import alexiil.mc.lib.attributes.AttributeList;
import alexiil.mc.lib.attributes.AttributeProvider;
import alexiil.mc.lib.multipart.api.MultipartContainer.MultipartCreator;
import alexiil.mc.lib.multipart.api.NativeMultipart;
import alexiil.mc.mod.pipes.part.PartTank;
import alexiil.mc.mod.pipes.part.SimplePipeParts;

public class BlockTank extends BlockBase implements BlockEntityProvider, AttributeProvider, NativeMultipart {

    public static final VoxelShape SHAPE = VoxelShapes.cuboid(2 / 16.0, 0, 2 / 16.0, 14 / 16.0, 12 / 16.0, 14 / 16.0);

    public BlockTank(Block.Settings settings) {
        super(settings);
    }

    @Override
    public BlockEntity createBlockEntity(BlockView var1) {
        return new TileTank();
    }

    @Override
    public VoxelShape getOutlineShape(
        BlockState blockState_1, BlockView blockView_1, BlockPos blockPos_1, EntityContext verticalEntityPosition_1
    ) {
        return SHAPE;
    }

    @Override
    public boolean isSideInvisible(BlockState thisState, BlockState otherState, Direction side) {
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
        }
    }

    @Override
    public List<MultipartCreator> getMultipartConversion(World world, BlockPos pos, BlockState state) {
        BlockEntity be = world.getBlockEntity(pos);
        if (!(be instanceof TileTank)) {
            return null;
        }
        TileTank tank = (TileTank) be;
        MultipartCreator creator = holder -> {
            PartTank part = new PartTank(SimplePipeParts.TANK, holder);
            part.fluidInv.forceSetInvFluid(0, tank.fluidInv.getInvFluid(0));
            return part;
        };
        return Collections.singletonList(creator);
    }
}
