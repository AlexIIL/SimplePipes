package alexiil.mc.mod.pipes.container;

import net.fabricmc.fabric.api.container.ContainerFactory;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.container.Container;
import net.minecraft.container.Slot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;

import alexiil.mc.mod.pipes.blocks.TileTriggerInvContains;

public class ContainerTriggerInvContains extends ContainerTile<TileTriggerInvContains> {

    public static final ContainerFactory<Container> FACTORY = (syncId, id, player, buffer) -> {
        BlockPos pos = buffer.readBlockPos();
        BlockEntity be = player.world.getBlockEntity(pos);
        if (be instanceof TileTriggerInvContains) {
            return new ContainerTriggerInvContains(syncId, player, (TileTriggerInvContains) be);
        }
        return null;
    };

    public ContainerTriggerInvContains(int syncId, PlayerEntity player, TileTriggerInvContains tile) {
        super(syncId, player, tile);
        addPlayerInventory(71);
        addSlot(new Slot(tile.filterInv, 0, 80, 26));
    }

    @Override 
    public ItemStack transferSlot(PlayerEntity player, int slotIndex){
		return ItemStack.EMPTY;
	}
}
