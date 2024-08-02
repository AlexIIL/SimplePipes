package alexiil.mc.mod.pipes.part;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.DecoderException;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.component.ComponentType;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;

import alexiil.mc.mod.pipes.SimplePipes;

import alexiil.mc.lib.net.InvalidInputDataException;
import alexiil.mc.lib.net.NetByteBuf;

/**
 * This fully describes the information about a facade when in {@link ItemStack} form.
 */
public final class FullFacade {
    public static final Codec<FullFacade> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        FacadeBlockStateInfo.CODEC.fieldOf("state").forGetter(it -> it.state),
        FacadeShape.ITEM_CODEC.fieldOf("shape").forGetter(it -> it.shape)
    ).apply(instance, FullFacade::new));
    public static final PacketCodec<ByteBuf, FullFacade> PACKET_CODEC = new PacketCodec<>() {
        @Override
        public FullFacade decode(ByteBuf buf) {
            try {
                return new FullFacade(NetByteBuf.asNetByteBuf(buf));
            } catch (InvalidInputDataException e) {
                throw new DecoderException(e);
            }
        }

        @Override
        public void encode(ByteBuf buf, FullFacade value) {
            value.toBuffer(NetByteBuf.asNetByteBuf(buf));
        }
    };
    public static final FullFacade DEFAULT = new FullFacade(FacadeStateManager.getDefaultState(),
        FacadeShape.Sided.get(FacadeSize.SLAB, Direction.WEST, false));
    public static final Identifier TYPE_ID = SimplePipes.id("full_facade");
    public static final ComponentType<FullFacade> TYPE = ComponentType.<FullFacade>builder().codec(CODEC).packetCodec(PACKET_CODEC).build();

    public final FacadeBlockStateInfo state;
    public final FacadeShape shape;

    public FullFacade(FacadeBlockStateInfo state, FacadeShape shape) {
        this.state = state;
        this.shape = shape;
    }

    public FullFacade(NbtCompound tag) {
        this.state = FacadeBlockStateInfo.fromTag(tag.getCompound("state"));
        this.shape = FacadeShape.fromTag(tag.getCompound("shape"));
    }

    @Deprecated
    public NbtCompound toTag() {
        NbtCompound tag = new NbtCompound();
        tag.put("state", state.toTag());
        NbtCompound shapeTag = shape.toTag();
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        FullFacade that = (FullFacade) o;
        return state.equals(that.state) && shape.equals(that.shape);
    }

    @Override
    public int hashCode() {
        int result = state.hashCode();
        result = 31 * result + shape.hashCode();
        return result;
    }
}
