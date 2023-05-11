package alexiil.mc.mod.pipes.pipe;

import net.minecraft.class_8567;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.context.LootContext;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.DirectionTransformation;
import net.minecraft.world.World;

import alexiil.mc.lib.multipart.api.AbstractPart.ItemDropTarget;

public abstract class PipeSpFlow {
    public final ISimplePipe pipe;

    public PipeSpFlow(ISimplePipe pipe) {
        this.pipe = pipe;
    }

    protected World world() {
        return pipe.getPipeWorld();
    }

    public abstract void fromTag(NbtCompound tag);

    public abstract NbtCompound toTag();

    public abstract void fromClientTag(NbtCompound tag);

    public void fromInitialClientTag(NbtCompound tag) {
        // nothing by default
    }

    public void toInitialClientTag(NbtCompound tag) {
        // nothing by default
    }

    public boolean canConnect(Direction dir) {
        return hasExtractable(dir) || hasInsertable(dir);
    }

    public abstract boolean hasExtractable(Direction dir);

    public abstract boolean hasInsertable(Direction dir);

    public abstract void tick();

    public void addDrops(ItemDropTarget target, class_8567 context) {}

    public void removeItemsForDrop(DefaultedList<ItemStack> all) {}

    public abstract Object getInsertable(Direction searchDirection);

    public void transform(DirectionTransformation transformation) {}
}
