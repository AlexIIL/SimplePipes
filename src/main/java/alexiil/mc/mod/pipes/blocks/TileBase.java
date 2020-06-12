package alexiil.mc.mod.pipes.blocks;

import javax.annotation.Nonnull;

import net.fabricmc.fabric.api.block.entity.BlockEntityClientSerializable;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

import alexiil.mc.lib.attributes.CombinableAttribute;
import alexiil.mc.lib.attributes.SearchOptions;

public abstract class TileBase extends BlockEntity implements BlockEntityClientSerializable {

    public TileBase(BlockEntityType<?> blockEntityType_1) {
        super(blockEntityType_1);
    }

    @Nonnull
    public <T> T getNeighbourAttribute(CombinableAttribute<T> attr, Direction dir) {
        return attr.get(getWorld(), getPos().offset(dir), SearchOptions.inDirection(dir));
    }

    public DefaultedList<ItemStack> removeItemsForDrop() {
        return DefaultedList.of();
    }

    protected void sendPacket(ServerWorld w, CompoundTag tag) {
        tag.putString("id", BlockEntityType.getId(getType()).toString());
        sendPacket(w, new BlockEntityUpdateS2CPacket(getPos(), 127, tag));
    }

    protected void sendPacket(ServerWorld w, BlockEntityUpdateS2CPacket packet) {
        w.getPlayers(player -> player.squaredDistanceTo(Vec3d.of(getPos())) < 24 * 24)
            .forEach(player -> player.networkHandler.sendPacket(packet));
    }

    @Override
    public CompoundTag toClientTag(CompoundTag tag) {
        return tag;
    }

    @Override
    public void fromClientTag(CompoundTag tag) {}

    public void onPlacedBy(LivingEntity placer, ItemStack stack) {}

    public ActionResult onUse(PlayerEntity player, Hand hand, BlockHitResult hit) {
        return ActionResult.PASS;
    }
}
