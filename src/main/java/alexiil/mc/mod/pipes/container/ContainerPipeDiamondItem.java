package alexiil.mc.mod.pipes.container;

import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;
import net.minecraft.util.math.BlockPos;

import alexiil.mc.mod.pipes.part.PipeSpBehaviourDiamond;
import alexiil.mc.mod.pipes.pipe.PartSpPipe;

import alexiil.mc.lib.multipart.api.MultipartContainer;

public class ContainerPipeDiamondItem extends ContainerPart<PartSpPipe> {

    public static final ExtendedScreenHandlerType.ExtendedFactory<ContainerPipeDiamondItem, BlockPos> FACTORY = (syncId, inv, pos) -> {
        PlayerEntity player = inv.player;
        MultipartContainer container = MultipartContainer.ATTRIBUTE.getFirstOrNull(player.getWorld(), pos);

        if (container == null) {
            throw new IllegalStateException("Attempted to open a diamond pipe screen where there is no diamond pipe!");
        }

        PartSpPipe pipe = container.getFirstPart(PartSpPipe.class);

        if (pipe.behaviour instanceof PipeSpBehaviourDiamond) {
            return new ContainerPipeDiamondItem(syncId, player, (PipeSpBehaviourDiamond) pipe.behaviour);
        }

        throw new IllegalStateException("Attempted to open a diamond pipe screen where there is no diamond pipe!");
    };

    public final int startY = 18;
    public final PipeSpBehaviourDiamond behaviour;

    public ContainerPipeDiamondItem(int syncId, PlayerEntity player, PipeSpBehaviourDiamond behaviour) {
        super(SimplePipeContainers.PIPE_PART_DIAMOND_ITEM, syncId, player, behaviour.pipe);
        this.behaviour = behaviour;

        for (int y = 0; y < 6; y++) {
            for (int x = 0; x < 9; x++) {
                addSlot(new Slot(behaviour.filterInv, x + y * 9, 8 + x * 18, startY + y * 18));
            }
        }
        addPlayerInventory(140);
    }

    @Override
    public ItemStack quickMove(PlayerEntity player, int slotIndex) {
        ItemStack stack = ItemStack.EMPTY;
        Slot slot = slots.get(slotIndex);

        if (slot != null && slot.hasStack()) {
            ItemStack slotStack = slot.getStack();
            stack = slotStack.copy();

            if (slotIndex < behaviour.filterInv.size()) {
                if (!insertItem(slotStack, behaviour.filterInv.size(), slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!insertItem(slotStack, 0, behaviour.filterInv.size(), false)) {
                return ItemStack.EMPTY;
            }

            if (slotStack.isEmpty()) {
                slot.setStack(ItemStack.EMPTY);
            } else {
                slot.markDirty();
            }
        }

        return stack;
    }
}
