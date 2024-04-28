package alexiil.mc.mod.pipes.pipe;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
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

    private ItemStack pickStack = ItemStack.EMPTY;

    public PipeSpDef(Identifier identifier, boolean isExtraction) {
        super(identifier);
        this.isExtraction = isExtraction;
    }

    @Override
    public AbstractPart readFromNbt(MultipartHolder holder, NbtCompound nbt, RegistryWrapper.WrapperLookup lookup) {
        PartSpPipe pipe = new PartSpPipe(this, holder);
        pipe.fromNbt(nbt, lookup);
        return pipe;
    }

    @Override
    public AbstractPart loadFromBuffer(MultipartHolder holder, NetByteBuf buffer, IMsgReadCtx ctx)
        throws InvalidInputDataException {
        PartSpPipe pipe = new PartSpPipe(this, holder);
        pipe.fromBuffer(buffer, ctx);
        return pipe;
    }

    public PipeSpBehaviour createBehaviour(PartSpPipe pipe) {
        return new PipeSpBehaviour(pipe);
    }

    public abstract PipeSpFlow createFlow(PartSpPipe pipe);

    public abstract Object getEmptyExtractable();

    public void setPickStack(ItemStack pickStack) {
        if (this.pickStack.isEmpty()) {
            this.pickStack = pickStack;
        }
    }

    public ItemStack getPickStack() {
        return this.pickStack.copy();
    }

    public static class PipeDefItem extends PipeSpDef {

        public final boolean canBounce;
        public final double speedModifier;

        public PipeDefItem(Identifier identifier, boolean isExtraction, boolean canBounce, double speedModifier) {
            super(identifier, isExtraction);
            this.canBounce = canBounce;
            this.speedModifier = speedModifier;
        }

        @Override
        public PipeSpFlowItem createFlow(PartSpPipe pipe) {
            return new PipeSpFlowItem(pipe);
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
        public PipeSpFlowFluid createFlow(PartSpPipe pipe) {
            return new PipeSpFlowFluid(pipe);
        }

        @Override
        public Object getEmptyExtractable() {
            return EmptyFluidExtractable.SUPPLIER;
        }
    }
}
