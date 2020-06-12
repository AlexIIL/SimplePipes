package alexiil.mc.mod.pipes.util;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.network.PacketByteBuf;

public final class MessageUtil {
    private MessageUtil() {}

    /** Writes a block state using the block ID and its metadata. Not suitable for full states. */
    public static void writeBlockState(PacketByteBuf buf, BlockState state) {
        buf.writeInt(Block.STATE_IDS.getId(state));
    }

    public static BlockState readBlockState(PacketByteBuf buf) {
        return Block.STATE_IDS.get(buf.readInt());
    }

}
