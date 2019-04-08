package alexiil.mc.mod.pipes.container;

import net.fabricmc.fabric.api.container.ContainerFactory;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.container.Container;
import net.minecraft.container.Slot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import alexiil.mc.mod.pipes.blocks.TilePipeItemDiamond;

public class ContainerPipeSorter extends ContainerTile<TilePipeItemDiamond> {

    public static final ContainerFactory<Container> FACTORY = (syncId, id, player, buffer) -> {
        BlockPos pos = buffer.readBlockPos();
        BlockEntity be = player.world.getBlockEntity(pos);
        if (be instanceof TilePipeItemDiamond) {
            return new ContainerPipeSorter(syncId, player, (TilePipeItemDiamond) be);
        }
        return null;
    };

    public final int startY = 18;

    public ContainerPipeSorter(int syncId, PlayerEntity player, TilePipeItemDiamond tile) {
        super(syncId, player, tile);
        for (int y = 0; y < 6; y++)
        {
            for (int x = 0; x < 9; x++)
            {
                addSlot(new Slot(tile.filterInv, x + y * 9, 8 + x * 18, startY + y * 18));
            }
        }
        addPlayerInventory(140);
    }

    @Override 
    public ItemStack transferSlot(PlayerEntity player, int slotIndex){
		ItemStack stack = ItemStack.EMPTY;
        Slot slot = slotList.get(slotIndex);
        
		if (slot != null && slot.hasStack()) {
			ItemStack slotStack = slot.getStack();
			stack = slotStack.copy();
			if (slotIndex < tile.filterInv.getInvSize()) { 
                if (!insertItem(slotStack, tile.filterInv.getInvSize(), slotList.size(), true)) { 
                    return ItemStack.EMPTY; 
                }
            } else if (!insertItem(slotStack, 0, tile.filterInv.getInvSize(), false)) {
                return ItemStack.EMPTY;
            }
            if (slotStack.isEmpty()) slot.setStack(ItemStack.EMPTY);
			else slot.markDirty();
        }
        
		return stack;
	}

}
