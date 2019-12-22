package alexiil.mc.mod.pipes.container;

import net.fabricmc.fabric.api.container.ContainerFactory;

import net.minecraft.container.Container;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;

import alexiil.mc.lib.multipart.api.MultipartContainer;
import alexiil.mc.lib.multipart.api.MultipartUtil;
import alexiil.mc.mod.pipes.part.PartTank;

public class ContainerTank extends ContainerPart<PartTank> {

    public static final ContainerFactory<Container> FACTORY = (syncId, id, player, buffer) -> {
        BlockPos pos = buffer.readBlockPos();
        MultipartContainer c = MultipartUtil.get(player.world, pos);
        if (c == null) {
            return null;
        }
        for (PartTank tank : c.getParts(PartTank.class)) {
            return new ContainerTank(syncId, player, tank);
        }
        return null;
    };

    protected ContainerTank(int syncId, PlayerEntity player, PartTank tank) {
        super(syncId, player, tank);
        addPlayerInventory(94);
    }
}
