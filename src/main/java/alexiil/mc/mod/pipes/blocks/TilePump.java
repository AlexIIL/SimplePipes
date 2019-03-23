package alexiil.mc.mod.pipes.blocks;

import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Tickable;
import net.minecraft.util.math.Direction;

import alexiil.mc.lib.attributes.Simulation;
import alexiil.mc.lib.attributes.fluid.FluidAttributes;
import alexiil.mc.lib.attributes.fluid.IFluidInsertable;
import alexiil.mc.lib.attributes.fluid.volume.FluidKeys;
import alexiil.mc.lib.attributes.fluid.volume.FluidVolume;
import alexiil.mc.lib.attributes.fluid.world.FluidWorldUtil;

public class TilePump extends TileBase implements Tickable {

    // private FluidKey fluidKey;
    private FluidVolume stored = FluidKeys.EMPTY.withAmount(0);

    public TilePump() {
        super(SimplePipeBlocks.PUMP_TILE);
    }

    @Override
    public void fromTag(CompoundTag tag) {
        super.fromTag(tag);
        stored = FluidVolume.fromTag(tag.getCompound("fluid"));
    }

    @Override
    public CompoundTag toTag(CompoundTag tag) {
        tag = super.toTag(tag);
        tag.put("fluid", stored.toTag());
        return tag;
    }

    @Override
    public void tick() {
        if (world.isClient) {
            return;
        }
        BlockState state = getCachedState();
        if (state.getBlock() != SimplePipeBlocks.PUMP) {
            return;
        }
        Direction facing = state.get(BlockPump.FACING);
        if (!stored.isEmpty()) {
            IFluidInsertable insertable = getNeighbourAttribute(FluidAttributes.INSERTABLE, facing.getOpposite());
            stored = insertable.attemptInsertion(stored, Simulation.ACTION);
            if (!stored.isEmpty()) {
                return;
            }
        }
        if (!world.isReceivingRedstonePower(getPos())) {
            return;
        }
        FluidVolume drained = FluidWorldUtil.drain(getWorld(), getPos().offset(facing), Simulation.ACTION);
        if (!drained.isEmpty()) {
            stored = drained;
        }
    }
}
