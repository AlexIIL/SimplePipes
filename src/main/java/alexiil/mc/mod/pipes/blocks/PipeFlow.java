package alexiil.mc.mod.pipes.blocks;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public abstract class PipeFlow {
    public final TilePipe pipe;

    public PipeFlow(TilePipe pipe) {
        this.pipe = pipe;
    }

    protected World world() {
        return pipe.getWorld();
    }

    public abstract void fromTag(CompoundTag tag);

    public abstract CompoundTag toTag();

    protected abstract void fromClientTag(CompoundTag tag);

    protected void fromInitialClientTag(CompoundTag tag) {
        // nothing by default
    }

    public void toInitialClientTag(CompoundTag tag) {
        // nothing by default
    }

    protected abstract boolean canConnect(Direction dir);

    protected abstract void tick();

    public void removeItemsForDrop(DefaultedList<ItemStack> all) {}

    protected abstract Object getInsertable(Direction searchDirection);

}
