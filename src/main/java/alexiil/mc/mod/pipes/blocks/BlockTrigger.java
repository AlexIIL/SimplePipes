package alexiil.mc.mod.pipes.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.entity.ai.pathing.NavigationType;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

import alexiil.mc.lib.attributes.SearchOptions;
import alexiil.mc.lib.attributes.fluid.FluidAttributes;
import alexiil.mc.lib.attributes.fluid.GroupedFluidInvView;
import alexiil.mc.lib.attributes.item.GroupedItemInvView;
import alexiil.mc.lib.attributes.item.ItemAttributes;

public abstract class BlockTrigger extends BlockBase implements BlockEntityProvider {
    public static final DirectionProperty FACING = Properties.FACING;
    public static final EnumProperty<EnumTriggerState> STATE = EnumProperty.of("state", EnumTriggerState.class);

    public BlockTrigger(Block.Settings settings) {
        super(settings);
        setDefaultState(getDefaultState().with(STATE, EnumTriggerState.NO_TARGET));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        super.appendProperties(builder);
        builder.add(FACING, STATE);
    }

    @Override
    public boolean canPathfindThrough(BlockState state, BlockView view, BlockPos pos, NavigationType env) {
        return super.canPathfindThrough(state, view, pos, env);
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return getDefaultState().with(FACING, ctx.getPlayerLookDirection());
    }

    @Override
    public boolean emitsRedstonePower(BlockState state) {
        return true;
    }

    @Override
    public int getWeakRedstonePower(BlockState state, BlockView view, BlockPos pos, Direction from) {
        if (from == state.get(FACING)) {
            return state.get(STATE) == EnumTriggerState.ON ? 15 : 0;
        }
        return 0;
    }

    @Override
    public int getStrongRedstonePower(BlockState state, BlockView view, BlockPos pos, Direction from) {
        if (from == state.get(FACING)) {
            return state.get(STATE) == EnumTriggerState.ON ? 15 : 0;
        }
        return 0;
    }

    @Override
    public abstract TileTrigger createBlockEntity(BlockView view);

    /** @param pos This position
     * @param dir the direction to look in. */
    protected abstract boolean isTriggerBlock(World world, BlockPos pos, Direction dir);

    static GroupedItemInvView getNeighbourGroupedItemInvView(World world, BlockPos pos, Direction dir) {
        return ItemAttributes.GROUPED_INV_VIEW.get(world, pos.offset(dir), SearchOptions.inDirection(dir));
    }

    static GroupedFluidInvView getNeighbourGroupedFluidInvView(World world, BlockPos pos, Direction dir) {
        return FluidAttributes.GROUPED_INV_VIEW.get(world, pos.offset(dir), SearchOptions.inDirection(dir));
    }
}
