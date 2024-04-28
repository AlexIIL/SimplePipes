package alexiil.mc.mod.pipes.pipe;

import net.minecraft.item.ItemStack;
import net.minecraft.loot.context.LootContextParameterSet;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.DirectionTransformation;
import net.minecraft.world.World;

import alexiil.mc.lib.net.IMsgReadCtx;
import alexiil.mc.lib.net.IMsgWriteCtx;
import alexiil.mc.lib.net.InvalidInputDataException;
import alexiil.mc.lib.net.NetByteBuf;

import alexiil.mc.lib.multipart.api.AbstractPart.ItemDropTarget;

public abstract class PipeSpFlow {
    public final ISimplePipe pipe;

    public PipeSpFlow(ISimplePipe pipe) {
        this.pipe = pipe;
    }

    protected World world() {
        return pipe.getPipeWorld();
    }

    public abstract void fromTag(NbtCompound tag, RegistryWrapper.WrapperLookup lookup);

    public abstract NbtCompound toTag(RegistryWrapper.WrapperLookup lookup);
    
    public abstract void fromBuffer(NetByteBuf buffer, IMsgReadCtx ctx) throws InvalidInputDataException;
    
    public abstract void writeToBuffer(NetByteBuf buffer, IMsgWriteCtx ctx);

    public abstract void fromClientTag(NbtCompound tag, RegistryWrapper.WrapperLookup lookup);

    public void fromInitialClientTag(NbtCompound tag, RegistryWrapper.WrapperLookup lookup) {
        // nothing by default
    }

    public void toInitialClientTag(NbtCompound tag, RegistryWrapper.WrapperLookup lookup) {
        // nothing by default
    }

    public boolean canConnect(Direction dir) {
        return hasExtractable(dir) || hasInsertable(dir);
    }

    public abstract boolean hasExtractable(Direction dir);

    public abstract boolean hasInsertable(Direction dir);

    public abstract void tick();

    public void addDrops(ItemDropTarget target, LootContextParameterSet context) {}

    public void removeItemsForDrop(DefaultedList<ItemStack> all) {}

    public abstract Object getInsertable(Direction searchDirection);

    public void transform(DirectionTransformation transformation) {}
}
