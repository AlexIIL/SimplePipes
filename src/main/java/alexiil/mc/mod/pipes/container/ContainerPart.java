package alexiil.mc.mod.pipes.container;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import alexiil.mc.lib.multipart.api.AbstractPart;
import alexiil.mc.lib.multipart.api.MultipartContainer;

public class ContainerPart<P extends AbstractPart> extends ScreenHandler {

    public final PlayerEntity player;
    public final P part;

    protected ContainerPart(int syncId, PlayerEntity player, P part) {
        super(/* Custom containers don't use the ContainerType system */null, syncId);
        this.player = player;
        this.part = part;
    }

    @Override
    public boolean canUse(PlayerEntity pl) {
        if (this.player != pl) {
            return false;
        }
        MultipartContainer container = part.holder.getContainer();

        BlockPos pos = container.getMultipartPos();
        if (pl.squaredDistanceTo(Vec3d.of(pos)) > 8 * 8) {
            return false;
        }

        World world = container.getMultipartWorld();
        if (world.getBlockEntity(pos) != container.getMultipartBlockEntity()) {
            return false;
        }
        if (container.getAllParts(pa -> pa == part).isEmpty()) {
            return false;
        }
        return true;
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
