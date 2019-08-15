package alexiil.mc.mod.pipes.part;

import net.minecraft.nbt.CompoundTag;

import alexiil.mc.lib.multipart.api.MultipartHolder;
import alexiil.mc.lib.multipart.api.PartDefinition;
import alexiil.mc.lib.net.IMsgReadCtx;
import alexiil.mc.lib.net.InvalidInputDataException;
import alexiil.mc.lib.net.NetByteBuf;

public class PartPipeItemClay extends PartPipe {

    public PartPipeItemClay(PartDefinition definition, MultipartHolder holder) {
        super(definition, holder, p -> new PipeFlowItem(p));
    }

    public PartPipeItemClay(PartDefinition definition, MultipartHolder holder, CompoundTag tag) {
        super(definition, holder, p -> new PipeFlowItem(p), tag);
    }

    public PartPipeItemClay(PartDefinition definition, MultipartHolder holder, NetByteBuf buf, IMsgReadCtx ctx)
        throws InvalidInputDataException {
        super(definition, holder, p -> new PipeFlowItem(p), buf, ctx);
    }
}
