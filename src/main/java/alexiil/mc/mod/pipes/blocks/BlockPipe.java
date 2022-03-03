/*
 * Copyright (c) 2019 SpaceToad and the BuildCraft team
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0. If a copy of the MPL was not
 * distributed with this file, You can obtain one at https://mozilla.org/MPL/2.0/
 */
package alexiil.mc.mod.pipes.blocks;

import java.util.Collections;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.Waterloggable;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.Properties;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;

import alexiil.mc.mod.pipes.pipe.ISimplePipe;
import alexiil.mc.mod.pipes.pipe.PartSpPipe;
import alexiil.mc.mod.pipes.pipe.PipeSpDef;

import alexiil.mc.lib.attributes.AttributeList;
import alexiil.mc.lib.attributes.AttributeProvider;
import alexiil.mc.lib.attributes.fluid.impl.EmptyFluidExtractable;
import alexiil.mc.lib.attributes.item.impl.EmptyItemExtractable;

import alexiil.mc.lib.multipart.api.MultipartContainer.MultipartCreator;
import alexiil.mc.lib.multipart.api.NativeMultipart;

public abstract class BlockPipe extends BlockBase
    implements BlockEntityProvider, AttributeProvider, Waterloggable, NativeMultipart {

    public static final VoxelShape CENTER_SHAPE = PartSpPipe.CENTER_SHAPE;
    private static final VoxelShape[] FACE_SHAPES = PartSpPipe.FACE_SHAPES;
    private static final VoxelShape[] FACE_CENTER_SHAPES = PartSpPipe.FACE_CENTER_SHAPES;
    private static final VoxelShape[] SHAPES = PartSpPipe.SHAPES;

    public final PipeSpDef pipeDef;

    public BlockPipe(Settings settings, PipeSpDef pipeDef) {
        super(settings);
        this.pipeDef = pipeDef;
        pipeDef.pipeBlock = this;
    }

    @Override
    public List<MultipartCreator> getMultipartConversion(World world, BlockPos pos, BlockState state) {
        BlockEntity be = world.getBlockEntity(pos);
        if (be instanceof TilePipe) {
            return Collections.singletonList(((TilePipe) be)::getMultipartConversion);
        } else {
            return Collections.emptyList();
        }
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        super.appendProperties(builder);
        builder.add(Properties.WATERLOGGED);
    }

    @Override
    public boolean isTranslucent(BlockState state, BlockView view, BlockPos pos) {
        return !(Boolean) state.get(Properties.WATERLOGGED);
    }

    @Override
    public FluidState getFluidState(BlockState blockState_1) {
        return blockState_1.get(Properties.WATERLOGGED) ? Fluids.WATER.getStill(false)
            : super.getFluidState(blockState_1);
    }

    @Override
    public BlockState getStateForNeighborUpdate(
        BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos,
        BlockPos neighborPos
    ) {
        if (state.get(Properties.WATERLOGGED)) {
            world.createAndScheduleFluidTick(pos, Fluids.WATER, Fluids.WATER.getTickRate(world));
        }

        return super.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos);
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        FluidState fluidState_1 = ctx.getWorld().getFluidState(ctx.getBlockPos());
        return this.getDefaultState()
            .with(Properties.WATERLOGGED, ctx.getWorld().isWater(ctx.getBlockPos()) && fluidState_1.getLevel() == 8);
    }

    @Override
    public abstract TilePipe createBlockEntity(BlockPos pos, BlockState state);

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(
        World world, BlockState state, BlockEntityType<T> type
    ) {
        return (w, p, s, e) -> ((TilePipe) e).tick();
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView view, BlockPos pos, ShapeContext entityPos) {
        BlockEntity be = view.getBlockEntity(pos);
        if (be instanceof ISimplePipe) {
            TilePipe pipe = (TilePipe) be;
            if (pipe.getConnections() == 0) {
                return CENTER_SHAPE;
            }
            return SHAPES[pipe.getConnections() & 0b111111];
        }

        return CENTER_SHAPE;
    }

    // TODO: Last Parameter important?
    @Override
    public void neighborUpdate(
        BlockState state, World world, BlockPos thisPos, Block neighbourBlock, BlockPos neighbourPos, boolean idunno
    ) {
        BlockEntity be = world.getBlockEntity(thisPos);
        if (be instanceof ISimplePipe) {
            TilePipe pipe = (TilePipe) be;
            pipe.setWorld(world);
            pipe.onNeighbourChange();
        }
    }

    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, LivingEntity entity, ItemStack stack) {
        super.onPlaced(world, pos, state, entity, stack);
        BlockEntity be = world.getBlockEntity(pos);
        if (be instanceof ISimplePipe) {
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
        BlockEntity be = world.getBlockEntity(pos);
        if (!(be instanceof ISimplePipe)) {
            return;
        }
        Direction pipeSide = searchDirection.getOpposite();
        ISimplePipe pipe = (ISimplePipe) be;
        VoxelShape pipeShape = pipe.isConnected(pipeSide) ? FACE_CENTER_SHAPES[pipeSide.ordinal()] : CENTER_SHAPE;

        boolean isItems = this instanceof BlockPipeItem;
        assert isItems != this instanceof BlockPipeFluid;

        if (isExtractionPipe() && be instanceof TilePipeSided && ((TilePipeSided) be).currentDirection() == pipeSide) {
            to.offer(pipe.getFlow().getInsertable(searchDirection), pipeShape);
        } else {
            to.offer(isItems ? EmptyItemExtractable.SUPPLIER : EmptyFluidExtractable.SUPPLIER, pipeShape);
        }
    }

    protected boolean isExtractionPipe() {
        return this instanceof BlockPipeItemWooden || this instanceof BlockPipeFluidWooden;
    }
}
