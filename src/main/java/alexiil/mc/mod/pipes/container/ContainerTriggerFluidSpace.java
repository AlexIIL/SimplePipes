package alexiil.mc.mod.pipes.container;

import net.fabricmc.fabric.api.container.ContainerFactory;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.container.Container;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;

import alexiil.mc.mod.pipes.blocks.TileTriggerFluidSpace;

public class ContainerTriggerFluidSpace extends ContainerTile<TileTriggerFluidSpace> {

    public static final ContainerFactory<Container> FACTORY = (syncId, id, player, buffer) -> {
        BlockPos pos = buffer.readBlockPos();
        BlockEntity be = player.world.getBlockEntity(pos);
        if (be instanceof TileTriggerFluidSpace) {
            return new ContainerTriggerFluidSpace(syncId, player, (TileTriggerFluidSpace) be);
        }
        return null;
    };

    public ContainerTriggerFluidSpace(int syncId, PlayerEntity player, TileTriggerFluidSpace tile) {
        super(syncId, player, tile);
        addPlayerInventory(71);
        // addSlot(new Slot(tile.filterInv, 0, 80, 26));
    }
}
