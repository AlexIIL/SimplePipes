package alexiil.mc.mod.pipes.pipe;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Identifier;

import alexiil.mc.lib.net.IMsgReadCtx;
import alexiil.mc.lib.net.InvalidInputDataException;
import alexiil.mc.lib.net.NetByteBuf;

import alexiil.mc.lib.attributes.fluid.impl.EmptyFluidExtractable;
import alexiil.mc.lib.attributes.item.impl.EmptyItemExtractable;

import alexiil.mc.lib.multipart.api.AbstractPart;
import alexiil.mc.lib.multipart.api.MultipartHolder;
import alexiil.mc.lib.multipart.api.PartDefinition;

public abstract class PipeSpDef extends PartDefinition {

    public final boolean isExtraction;

    public PipeSpDef(Identifier identifier, boolean isExtraction) {
        super(identifier);
        this.isExtraction = isExtraction;
    }

    @Override
    public AbstractPart readFromNbt(MultipartHolder holder, NbtCompound nbt) {
        PartSpPipe pipe = new PartSpPipe(this, holder);
        pipe.fromNbt(nbt);
        return pipe;
    }

    @Override
    public AbstractPart loadFromBuffer(MultipartHolder holder, NetByteBuf buffer, IMsgReadCtx ctx)
        throws InvalidInputDataException {
        PartSpPipe pipe = new PartSpPipe(this, holder);
        pipe.fromNbt(buffer.readNbt());
        return pipe;
    }

    public PipeSpBehaviour createBehaviour(PartSpPipe pipe) {
        return new PipeSpBehaviour(pipe);
    }

    public abstract PipeSpFlow createFlow(PartSpPipe pipe);

    public abstract Object getEmptyExtractable();

    public static class PipeDefItem extends PipeSpDef {

        public final boolean canBounce;
        public final double speedModifier;

        public PipeDefItem(Identifier identifier, boolean isExtraction, boolean canBounce, double speedModifier) {
            super(identifier, isExtraction);
            this.canBounce = canBounce;
            this.speedModifier = speedModifier;
        }

        @Override
        public PipeSpFlow createFlow(PartSpPipe pipe) {
            return new PipeFlowItem(pipe);
        }

        @Override
        public Object getEmptyExtractable() {
            return EmptyItemExtractable.SUPPLIER;
        }
    }

    public static class PipeDefFluid extends PipeSpDef {

        public PipeDefFluid(Identifier identifier, boolean isExtraction) {
            super(identifier, isExtraction);
        }

        @Override
        public PipeSpFlow createFlow(PartSpPipe pipe) {
            return new PipeFlowFluid(pipe);
        }

        @Override
        public Object getEmptyExtractable() {
            return EmptyFluidExtractable.SUPPLIER;
        }
    }
}
