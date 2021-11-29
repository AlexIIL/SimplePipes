package alexiil.mc.mod.pipes.blocks;

import javax.annotation.Nonnull;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

import alexiil.mc.lib.net.IMsgReadCtx;
import alexiil.mc.lib.net.NetByteBuf;
import alexiil.mc.lib.net.NetIdDataK;
import alexiil.mc.lib.net.ParentNetIdSingle;
import alexiil.mc.lib.net.impl.ActiveMinecraftConnection;
import alexiil.mc.lib.net.impl.CoreMinecraftNetUtil;
import alexiil.mc.lib.net.impl.McNetworkStack;

import alexiil.mc.lib.attributes.CombinableAttribute;
import alexiil.mc.lib.attributes.SearchOptions;

public abstract class TileBase extends BlockEntity {

    public static final ParentNetIdSingle<TileBase> NET_PARENT;
    public static final NetIdDataK<TileBase> NET_DATA;

    static {
        NET_PARENT = McNetworkStack.BLOCK_ENTITY.subType(TileBase.class, "simplepipes:tile_base");
        NET_DATA = NET_PARENT.idData("data").toClientOnly().setReceiver(TileBase::receiveData);
    }

    public TileBase(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Nonnull
    public <T> T getNeighbourAttribute(CombinableAttribute<T> attr, Direction dir) {
        return attr.get(getWorld(), getPos().offset(dir), SearchOptions.inDirection(dir));
    }

    public DefaultedList<ItemStack> removeItemsForDrop() {
        return DefaultedList.of();
    }

    protected void sendPacket(ServerWorld w, NbtCompound tag) {
        for (ActiveMinecraftConnection c : CoreMinecraftNetUtil.getNearbyActiveConnections(this, 24)) {
            NET_DATA.send(c, this, (t, buf, ctx) -> buf.writeNbt(tag));
        }
    }

    @Override
    public NbtCompound toInitialChunkDataNbt() {
        return toClientTag(super.toInitialChunkDataNbt());
    }

    private void receiveData(NetByteBuf buffer, IMsgReadCtx ctx) {
        readPacket(buffer.readNbt());
    }

    public NbtCompound toClientTag(NbtCompound tag) {
        return tag;
    }

    public void readPacket(NbtCompound tag) {}

    public void onPlacedBy(LivingEntity placer, ItemStack stack) {}

    public ActionResult onUse(PlayerEntity player, Hand hand, BlockHitResult hit) {
        return ActionResult.PASS;
    }
}
