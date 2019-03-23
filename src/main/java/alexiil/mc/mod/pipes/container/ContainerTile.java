package alexiil.mc.mod.pipes.container;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.container.Container;
import net.minecraft.container.Slot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class ContainerTile<T extends BlockEntity> extends Container {

    public final PlayerEntity player;
    public final T tile;

    protected ContainerTile(int syncId, PlayerEntity player, T tile) {
        super(/* Custom containers don't use the ContainerType system */null, syncId);
        this.player = player;
        this.tile = tile;
    }

    @Override
    public boolean canUse(PlayerEntity p) {
        if (player != p) {
            return false;
        }
        World w = tile.getWorld();
        return w != null && w.getBlockEntity(tile.getPos()) == tile && p.squaredDistanceTo(new Vec3d(tile.getPos())) < 8 * 8;
    }

    protected void addPlayerInventory(int startY) {
        for (int y = 0; y < 3; y++) {
            for (int x = 0; x < 9; ++x) {
                addSlot(new Slot(player.inventory, x + y * 9 + 9, 8 + x * 18, startY + y * 18));
            }
        }
        for (int x = 0; x < 9; x++) {
            addSlot(new Slot(player.inventory, x, 8 + x * 18, startY + 58));
        }
    }
}
