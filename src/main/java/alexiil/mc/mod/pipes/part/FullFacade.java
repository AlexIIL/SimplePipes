package alexiil.mc.mod.pipes.part;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;

import alexiil.mc.lib.net.InvalidInputDataException;
import alexiil.mc.lib.net.NetByteBuf;

/** This fully describes the information about a facade when in {@link ItemStack} form. */
public final class FullFacade {
    public final FacadeBlockStateInfo state;
    public final FacadeShape shape;

    public FullFacade(FacadeBlockStateInfo state, FacadeShape shape) {
        this.state = state;
        this.shape = shape;
    }

    public FullFacade(CompoundTag tag) {
        this.state = FacadeBlockStateInfo.fromTag(tag.getCompound("state"));
        this.shape = FacadeShape.fromTag(tag.getCompound("shape"));
    }

    public CompoundTag toTag() {
        CompoundTag tag = new CompoundTag();
        tag.put("state", state.toTag());
        CompoundTag shapeTag = shape.toTag();
        // Remove all unnecessary information
        shapeTag.remove("side");
        shapeTag.remove("edge");
        shapeTag.remove("corner");
        tag.put("shape", shapeTag);
        return tag;
    }

    public FullFacade(NetByteBuf buffer) throws InvalidInputDataException {
        this.state = FacadeBlockStateInfo.readFromBuffer(buffer);
        this.shape = FacadeShape.fromBuffer(buffer);
    }

    public void toBuffer(NetByteBuf buffer) {
        state.writeToBuffer(buffer);
        shape.toBuffer(buffer);
    }
}
