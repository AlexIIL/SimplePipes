package alexiil.mc.mod.pipes.container;

import net.fabricmc.fabric.api.container.ContainerFactory;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.container.Container;
import net.minecraft.container.Slot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;

import alexiil.mc.mod.pipes.blocks.TileTriggerInvSpace;

public class ContainerTriggerInvSpace extends ContainerTile<TileTriggerInvSpace> {

    public static final ContainerFactory<Container> FACTORY = (syncId, id, player, buffer) -> {
        BlockPos pos = buffer.readBlockPos();
        BlockEntity be = player.world.getBlockEntity(pos);
        if (be instanceof TileTriggerInvSpace) {
            return new ContainerTriggerInvSpace(syncId, player, (TileTriggerInvSpace) be);
        }
        return null;
    };

    public ContainerTriggerInvSpace(int syncId, PlayerEntity player, TileTriggerInvSpace tile) {
        super(syncId, player, tile);
        addPlayerInventory(71);
        addSlot(new Slot(tile.filterInv, 0, 80, 26));
    }
}
