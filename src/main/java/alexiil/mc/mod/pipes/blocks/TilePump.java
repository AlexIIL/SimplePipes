package alexiil.mc.mod.pipes.blocks;

import net.minecraft.block.BlockState;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

import alexiil.mc.lib.attributes.Simulation;
import alexiil.mc.lib.attributes.fluid.FluidAttributes;
import alexiil.mc.lib.attributes.fluid.FluidInsertable;
import alexiil.mc.lib.attributes.fluid.FluidVolumeUtil;
import alexiil.mc.lib.attributes.fluid.volume.FluidVolume;
import alexiil.mc.lib.attributes.fluid.world.FluidWorldUtil;

public class TilePump extends TileBase {

    // private FluidKey fluidKey;
    private FluidVolume stored = FluidVolumeUtil.EMPTY;

    public TilePump(BlockPos pos, BlockState state) {
        super(SimplePipeBlocks.PUMP_TILE, pos, state);
    }

    @Override
    public void readNbt(NbtCompound tag) {
        super.readNbt(tag);
        stored = FluidVolume.fromTag(tag.getCompound("fluid"));
    }

    @Override
    public NbtCompound writeNbt(NbtCompound tag) {
        tag = super.writeNbt(tag);
        tag.put("fluid", stored.toTag());
        return tag;
    }

    public void serverTick() {
        final World w = world;
        if (w == null) {
            return;
        }
        BlockState state = getCachedState();
        if (state.getBlock() != SimplePipeBlocks.PUMP) {
            return;
        }
        Direction facing = state.get(BlockPump.FACING);
        if (!stored.isEmpty()) {
            FluidInsertable insertable = getNeighbourAttribute(FluidAttributes.INSERTABLE, facing.getOpposite());
            stored = insertable.attemptInsertion(stored, Simulation.ACTION);
            if (!stored.isEmpty()) {
                return;
            }
        }
        if (!w.isReceivingRedstonePower(getPos())) {
            return;
        }
        FluidVolume drained = FluidWorldUtil.drain(getWorld(), getPos().offset(facing), Simulation.ACTION);
        if (!drained.isEmpty()) {
            stored = drained;
        }
    }
}
