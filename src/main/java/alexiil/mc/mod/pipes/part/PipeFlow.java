package alexiil.mc.mod.pipes.part;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.DefaultedList;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

import alexiil.mc.lib.net.IMsgReadCtx;
import alexiil.mc.lib.net.IMsgWriteCtx;
import alexiil.mc.lib.net.InvalidInputDataException;
import alexiil.mc.lib.net.NetByteBuf;

public abstract class PipeFlow {
    public final PartPipe pipe;

    public PipeFlow(PartPipe pipe) {
        this.pipe = pipe;
    }

    protected World world() {
        return pipe.getWorld();
    }

    public abstract void fromTag(CompoundTag tag);

    public abstract CompoundTag toTag();

    public void writeCreationData(NetByteBuf buf, IMsgWriteCtx ctx) {
        // nothing by default
    }

    public void readCreationData(NetByteBuf buf, IMsgReadCtx ctx) throws InvalidInputDataException {
        // nothing by default
    }

    protected abstract boolean canConnect(Direction dir);

    protected abstract void tick();

    public void removeItemsForDrop(DefaultedList<ItemStack> all) {}

    protected abstract Object getInsertable(Direction searchDirection);
}
