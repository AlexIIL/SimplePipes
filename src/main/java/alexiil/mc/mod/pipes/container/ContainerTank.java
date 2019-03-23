package alexiil.mc.mod.pipes.container;

import net.fabricmc.fabric.api.container.ContainerFactory;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.container.Container;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;

import alexiil.mc.mod.pipes.blocks.TileTank;

public class ContainerTank extends ContainerTile<TileTank> {

    public static final ContainerFactory<Container> FACTORY = (syncId, id, player, buffer) -> {
        BlockPos pos = buffer.readBlockPos();
        BlockEntity be = player.world.getBlockEntity(pos);
        if (be instanceof TileTank) {
            return new ContainerTank(syncId, player, (TileTank) be);
        }
        return null;
    };

    protected ContainerTank(int syncId, PlayerEntity player, TileTank tile) {
        super(syncId, player, tile);
    }
}
