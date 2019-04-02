/*
 * Copyright (c) 2019 SpaceToad and the BuildCraft team
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0. If a copy of the MPL was not
 * distributed with this file, You can obtain one at https://mozilla.org/MPL/2.0/
 */
package alexiil.mc.mod.pipes.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockRenderLayer;
import net.minecraft.block.BlockState;
import net.minecraft.block.Waterloggable;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.VerticalEntityPosition;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateFactory.Builder;
import net.minecraft.state.property.Properties;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.BooleanBiFunction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Direction.Axis;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

import alexiil.mc.lib.attributes.AttributeList;
import alexiil.mc.lib.attributes.AttributeProvider;
import alexiil.mc.lib.attributes.fluid.FluidAttributes;
import alexiil.mc.lib.attributes.fluid.impl.EmptyFluidExtractable;
import alexiil.mc.lib.attributes.fluid.impl.RejectingFluidInsertable;
import alexiil.mc.lib.attributes.item.ItemAttributes;
import alexiil.mc.lib.attributes.item.impl.EmptyItemExtractable;
import alexiil.mc.lib.attributes.item.impl.RejectingItemInsertable;

public abstract class BlockPipe extends BlockBase implements BlockEntityProvider, AttributeProvider, Waterloggable {

    public static final VoxelShape CENTER_SHAPE;
    private static final VoxelShape[] FACE_SHAPES;
    private static final VoxelShape[] FACE_CENTER_SHAPES;
    private static final VoxelShape[] SHAPES;

    static {
        CENTER_SHAPE = VoxelShapes.cuboid(0.25, 0.25, 0.25, 0.75, 0.75, 0.75);
        FACE_SHAPES = new VoxelShape[6];
        FACE_CENTER_SHAPES = new VoxelShape[6];
        for (Direction dir : Direction.values()) {
            double x = 0.5 + dir.getOffsetX() * 0.375;
            double y = 0.5 + dir.getOffsetY() * 0.375;
            double z = 0.5 + dir.getOffsetZ() * 0.375;
            double rx = dir.getAxis() == Axis.X ? 0.125 : 0.25;
            double ry = dir.getAxis() == Axis.Y ? 0.125 : 0.25;
            double rz = dir.getAxis() == Axis.Z ? 0.125 : 0.25;
            VoxelShape faceShape = VoxelShapes.cuboid(x - rx, y - ry, z - rz, x + rx, y + ry, z + rz);
            FACE_SHAPES[dir.ordinal()] = faceShape;
            FACE_CENTER_SHAPES[dir.ordinal()] = VoxelShapes.union(faceShape, CENTER_SHAPE);
        }

        SHAPES = new VoxelShape[2 * 2 * 2 * 2 * 2 * 2];
        for (int c = 0; c <= 0b111_111; c++) {
            VoxelShape shape = CENTER_SHAPE;
            for (Direction dir : Direction.values()) {
                if ((c & (1 << dir.ordinal())) != 0) {
                    shape = VoxelShapes.combine(shape, FACE_SHAPES[dir.ordinal()], BooleanBiFunction.OR);
                }
            }
            SHAPES[c] = shape.simplify();
        }
    }

    public BlockPipe(Settings settings) {
        super(settings);
    }

    @Override
    protected void appendProperties(Builder<Block, BlockState> builder) {
        super.appendProperties(builder);
        builder.with(Properties.WATERLOGGED);
    }

    @Override
    public boolean isTranslucent(BlockState state, BlockView view, BlockPos pos) {
        return !(Boolean) state.get(Properties.WATERLOGGED);
    }

    @Override
    public FluidState getFluidState(BlockState blockState_1) {
        return blockState_1.get(Properties.WATERLOGGED) ? Fluids.WATER.getState(false)
            : super.getFluidState(blockState_1);
    }

    @Override
    public BlockState getStateForNeighborUpdate(BlockState blockState_1, Direction direction_1, BlockState blockState_2,
        IWorld iWorld_1, BlockPos blockPos_1, BlockPos blockPos_2) {
        if (blockState_1.get(Properties.WATERLOGGED)) {
            iWorld_1.getFluidTickScheduler().schedule(blockPos_1, Fluids.WATER, Fluids.WATER.getTickRate(iWorld_1));
        }

        return super.getStateForNeighborUpdate(blockState_1, direction_1, blockState_2, iWorld_1, blockPos_1,
            blockPos_2);
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        FluidState fluidState_1 = ctx.getWorld().getFluidState(ctx.getBlockPos());
        return this.getDefaultState().with(Properties.WATERLOGGED,
            fluidState_1.matches(FluidTags.WATER) && fluidState_1.getLevel() == 8);
    }

    @Override
    public abstract TilePipe createBlockEntity(BlockView var1);

    @Override
    public BlockRenderLayer getRenderLayer() {
        return BlockRenderLayer.CUTOUT;
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView view, BlockPos pos,
        VerticalEntityPosition entityPos) {
        BlockEntity be = view.getBlockEntity(pos);
        if (be instanceof TilePipe) {
            TilePipe pipe = (TilePipe) be;
            if (pipe.connections == 0) {
                return CENTER_SHAPE;
            }
            return SHAPES[pipe.connections & 0b111111];
        }

        return CENTER_SHAPE;
    }

    // TODO: Last Parameter important?
    @Override
    public void neighborUpdate(BlockState state, World world, BlockPos thisPos, Block neighbourBlock,
        BlockPos neighbourPos, boolean idunno) {
        BlockEntity be = world.getBlockEntity(thisPos);
        if (be instanceof TilePipe) {
            TilePipe pipe = (TilePipe) be;
            pipe.setWorld(world);
            pipe.onNeighbourChange();
        }
    }

    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, LivingEntity entity, ItemStack stack) {
        super.onPlaced(world, pos, state, entity, stack);
        BlockEntity be = world.getBlockEntity(pos);
        if (be instanceof TilePipe) {
            TilePipe pipe = (TilePipe) be;
            pipe.onNeighbourChange();
        }
    }

    @Override
    public void addAllAttributes(World world, BlockPos pos, BlockState state, AttributeList<?> to) {

        Direction searchDirection = to.getSearchDirection();
        if (searchDirection == null) {
            // Pipes only work with physical connections
            return;
        }
        if (to.attribute != ItemAttributes.EXTRACTABLE || to.attribute != ItemAttributes.INSERTABLE
            || to.attribute != FluidAttributes.EXTRACTABLE || to.attribute != FluidAttributes.INSERTABLE) {
            return;
        }
        BlockEntity be = world.getBlockEntity(pos);
        if (!(be instanceof TilePipe)) {
            return;
        }
        Direction pipeSide = searchDirection.getOpposite();
        TilePipe pipe = (TilePipe) be;
        VoxelShape pipeShape = pipe.isConnected(pipeSide) ? FACE_CENTER_SHAPES[pipeSide.ordinal()] : CENTER_SHAPE;

        if (this instanceof BlockPipeItemWooden) {
            if (to.attribute == ItemAttributes.INSERTABLE || to.attribute == FluidAttributes.INSERTABLE) {
                to.offer(pipe.flow.getInsertable(searchDirection), pipeShape);
            } else {
                to.offer(RejectingItemInsertable.EXTRACTOR, pipeShape);
                to.offer(RejectingFluidInsertable.EXTRACTOR, pipeShape);
            }
        } else {
            to.offer(EmptyItemExtractable.SUPPLIER, pipeShape);
            to.offer(EmptyFluidExtractable.SUPPLIER, pipeShape);
        }
    }
}
