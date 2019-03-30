package alexiil.mc.mod.pipes.container;

import net.fabricmc.fabric.api.container.ContainerFactory;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.container.Container;
import net.minecraft.container.Slot;
import net.minecraft.entity.player.PlayerEntity;
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
}
