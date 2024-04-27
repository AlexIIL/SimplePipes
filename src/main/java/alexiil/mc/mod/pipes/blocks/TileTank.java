package alexiil.mc.mod.pipes.blocks;

import net.minecraft.block.BlockState;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.math.BlockPos;

import alexiil.mc.lib.attributes.Simulation;
import alexiil.mc.lib.attributes.fluid.amount.FluidAmount;
import alexiil.mc.lib.attributes.fluid.impl.SimpleFixedFluidInv;
import alexiil.mc.lib.attributes.fluid.volume.FluidVolume;

public class TileTank extends TileBase {

    private static final FluidAmount SINGLE_TANK_CAPACITY = FluidAmount.BUCKET.mul(16);

    public final SimpleFixedFluidInv fluidInv;

    public TileTank(BlockPos pos, BlockState state) {
        super(SimplePipeBlocks.TANK_TILE, pos, state);
        fluidInv = new SimpleFixedFluidInv(1, SINGLE_TANK_CAPACITY);
    }

    @Override
    public void writeNbt(NbtCompound tag, RegistryWrapper.WrapperLookup lookup) {
        super.writeNbt(tag, lookup);
        FluidVolume invFluid = fluidInv.getInvFluid(0);
        if (!invFluid.isEmpty()) {
            tag.put("fluid", invFluid.toTag());
        }
    }

    @Override
    public void readNbt(NbtCompound tag, RegistryWrapper.WrapperLookup lookup) {
        super.readNbt(tag, lookup);
        if (tag.contains("fluid")) {
            FluidVolume fluid = FluidVolume.fromTag(tag.getCompound("fluid"));
            fluidInv.setInvFluid(0, fluid, Simulation.ACTION);
        }
    }
}
