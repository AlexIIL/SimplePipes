package alexiil.mc.mod.pipes.container;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.screen.slot.Slot;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public abstract class ContainerTile<T extends BlockEntity> extends ScreenHandler {

    public final PlayerEntity player;
    public final T tile;

    protected ContainerTile(ScreenHandlerType<?> type, int syncId, PlayerEntity player, T tile) {
        super(type, syncId);
        this.player = player;
        this.tile = tile;
    }

    @Override
    public boolean canUse(PlayerEntity p) {
        if (player != p) {
            return false;
        }
        World w = tile.getWorld();
        return w != null && w.getBlockEntity(tile.getPos()) == tile && p.squaredDistanceTo(Vec3d.of(tile.getPos())) < 8 * 8;
    }

    protected void addPlayerInventory(int startY) {
        for (int y = 0; y < 3; y++) {
            for (int x = 0; x < 9; ++x) {
                addSlot(new Slot(player.getInventory(), x + y * 9 + 9, 8 + x * 18, startY + y * 18));
            }
        }
        for (int x = 0; x < 9; x++) {
            addSlot(new Slot(player.getInventory(), x, 8 + x * 18, startY + 58));
        }
    }
}
