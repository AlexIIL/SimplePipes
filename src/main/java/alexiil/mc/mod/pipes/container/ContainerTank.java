package alexiil.mc.mod.pipes.container;

import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;
import net.minecraft.util.math.BlockPos;

import alexiil.mc.lib.attributes.fluid.FluidInvUtil;
import alexiil.mc.lib.attributes.fluid.FluidTransferable;
import alexiil.mc.lib.attributes.misc.PlayerInvUtil;
import alexiil.mc.lib.attributes.misc.Reference;
import alexiil.mc.lib.multipart.api.MultipartContainer;
import alexiil.mc.lib.multipart.api.MultipartUtil;
import alexiil.mc.mod.pipes.part.PartTank;

public class ContainerTank extends ContainerPart<PartTank> {

    public static final ExtendedScreenHandlerType.ExtendedFactory<ContainerTank> FACTORY = (syncId, inv, buffer) -> {
        PlayerEntity player = inv.player;
        BlockPos pos = buffer.readBlockPos();
        MultipartContainer c = MultipartUtil.get(player.getWorld(), pos);
        if (c == null) {
            throw new IllegalStateException("Attempted to open a tank screen where there is no tank!");
        }
        for (PartTank tank : c.getParts(PartTank.class)) {
            return new ContainerTank(syncId, player, tank);
        }
        throw new IllegalStateException("Attempted to open a tank screen where there is no tank!");
    };

    private boolean isInCall = false;

    public ContainerTank(int syncId, PlayerEntity player, PartTank tank) {
        super(SimplePipeContainers.TANK, syncId, player, tank);
        addPlayerInventory(94);
    }

    @Override
    public ItemStack quickMove(PlayerEntity player, int index) {
        Slot slot = slots.get(index);
        if (slot == null || !slot.hasStack()) {
            return ItemStack.EMPTY;
        }
        if (player.getEntityWorld().isClient) {
            // Force a sync
            // (see how ScreenHandler works with this for info)
            isInCall = !isInCall;
            if (isInCall) {
                return slot.getStack();
            } else {
                return ItemStack.EMPTY;
            }
        }
        Reference<ItemStack> ref = PlayerInvUtil.referenceSlot(slot);
        FluidInvUtil.interactWithTank((FluidTransferable) part.fluidInv, player, ref);
        return ItemStack.EMPTY;
    }
}
