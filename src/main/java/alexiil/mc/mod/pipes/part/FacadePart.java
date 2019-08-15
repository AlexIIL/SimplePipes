package alexiil.mc.mod.pipes.part;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.shape.VoxelShape;

import alexiil.mc.lib.multipart.api.AbstractPart;
import alexiil.mc.lib.multipart.api.MultipartHolder;
import alexiil.mc.lib.multipart.api.PartDefinition;
import alexiil.mc.lib.multipart.api.render.PartModelKey;
import alexiil.mc.lib.net.IMsgReadCtx;
import alexiil.mc.lib.net.IMsgWriteCtx;
import alexiil.mc.lib.net.InvalidInputDataException;
import alexiil.mc.lib.net.NetByteBuf;
import alexiil.mc.mod.pipes.client.model.part.FacadePartKey;
import alexiil.mc.mod.pipes.items.SimplePipeItems;

public class FacadePart extends AbstractPart {

    public final FacadeBlockStateInfo state;
    public final FacadeShape shape;

    public FacadePart(PartDefinition definition, MultipartHolder holder, FacadeBlockStateInfo states,
        FacadeShape shape) {
        super(definition, holder);
        this.state = states;
        this.shape = shape;
    }

    public FacadePart(PartDefinition definition, MultipartHolder holder, CompoundTag tag) {
        super(definition, holder);
        this.state = FacadeBlockStateInfo.fromTag(tag.getCompound("states"));
        shape = FacadeShape.fromTag(tag.getCompound("shape"));
    }

    @Override
    public CompoundTag toTag() {
        CompoundTag tag = super.toTag();
        tag.put("states", state.toTag());
        tag.put("shape", shape.toTag());
        return tag;
    }

    public FacadePart(PartDefinition definition, MultipartHolder holder, NetByteBuf buffer, IMsgReadCtx ctx)
        throws InvalidInputDataException {
        super(definition, holder);
        this.state = FacadeBlockStateInfo.readFromBuffer(buffer);
        shape = FacadeShape.fromBuffer(buffer);
    }

    @Override
    public void writeCreationData(NetByteBuf buffer, IMsgWriteCtx ctx) {
        super.writeCreationData(buffer, ctx);
        state.writeToBuffer(buffer);
        shape.toBuffer(buffer);
    }

    @Override
    public VoxelShape getShape() {
        return shape.shape;
    }

    @Override
    public ItemStack getPickStack() {
        return SimplePipeItems.FACADE.createItemStack(new FullFacade(state, shape));
    }

    @Override
    public boolean canOverlapWith(AbstractPart other) {
        // The "no fully overlapping" rule of PartContainer allows us to do this
        return other instanceof FacadePart;
    }

    @Override
    public PartModelKey getModelKey() {
        return new FacadePartKey(shape, state.state);
    }
}
