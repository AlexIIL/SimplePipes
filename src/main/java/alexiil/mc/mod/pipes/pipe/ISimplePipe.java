package alexiil.mc.mod.pipes.pipe;

import javax.annotation.Nullable;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

import alexiil.mc.lib.net.NetIdData;

import alexiil.mc.lib.attributes.CombinableAttribute;
import alexiil.mc.lib.attributes.fluid.FluidAttributes;
import alexiil.mc.lib.attributes.fluid.FluidExtractable;
import alexiil.mc.lib.attributes.fluid.FluidInsertable;
import alexiil.mc.lib.attributes.item.ItemAttributes;
import alexiil.mc.lib.attributes.item.ItemExtractable;
import alexiil.mc.lib.attributes.item.ItemInsertable;

public interface ISimplePipe {

    PipeSpDef getDefinition();

    PipeSpFlow getFlow();

    BlockPos getPipePos();

    World getPipeWorld();

    default long getWorldTime() {
        World w = getPipeWorld();
        return w == null ? 0 : w.getTime();
    }

    @Nullable
    ISimplePipe getNeighbourPipe(Direction dir);

    <T> T getNeighbourAttribute(CombinableAttribute<T> attr, Direction dir);

    default ItemExtractable getItemExtractable(Direction dir) {
        return getNeighbourAttribute(ItemAttributes.EXTRACTABLE, dir);
    }

    default ItemInsertable getItemInsertable(Direction dir) {
        return getNeighbourAttribute(ItemAttributes.INSERTABLE, dir);
    }

    default FluidExtractable getFluidExtractable(Direction dir) {
        return getNeighbourAttribute(FluidAttributes.EXTRACTABLE, dir);
    }

    default FluidInsertable getFluidInsertable(Direction dir) {
        return getNeighbourAttribute(FluidAttributes.INSERTABLE, dir);
    }

    boolean isConnected(Direction dir);

    void connect(Direction dir);

    void disconnect(Direction dir);

    double getPipeLength(Direction side);

    void sendFlowPacket(NetIdData.IMsgDataWriter writer);
}
