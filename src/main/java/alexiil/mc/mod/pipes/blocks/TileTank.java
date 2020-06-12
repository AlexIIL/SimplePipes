package alexiil.mc.mod.pipes.blocks;

import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Tickable;

import alexiil.mc.lib.attributes.Simulation;
import alexiil.mc.lib.attributes.fluid.impl.SimpleFixedFluidInv;
import alexiil.mc.lib.attributes.fluid.volume.FluidVolume;
import alexiil.mc.lib.multipart.api.MultipartUtil;

public class TileTank extends TileBase implements Tickable {

    private static final int SINGLE_TANK_CAPACITY = 16 * FluidVolume.BUCKET;

    public final SimpleFixedFluidInv fluidInv;

    public TileTank() {
        super(SimplePipeBlocks.TANK_TILE);
        fluidInv = new SimpleFixedFluidInv(1, SINGLE_TANK_CAPACITY);
    }

    @Override
    public CompoundTag toTag(CompoundTag tag) {
        tag = super.toTag(tag);
        FluidVolume invFluid = fluidInv.getInvFluid(0);
        if (!invFluid.isEmpty()) {
            tag.put("fluid", invFluid.toTag());
        }
        return tag;
    }

    @Override
    public void fromTag(BlockState state, CompoundTag tag) {
        super.fromTag(state, tag);
        if (tag.contains("fluid")) {
            FluidVolume fluid = FluidVolume.fromTag(tag.getCompound("fluid"));
            fluidInv.setInvFluid(0, fluid, Simulation.ACTION);
        }
    }

    @Override
    public void tick() {
        MultipartUtil.turnIntoMultipart(world, pos);
    }
}
