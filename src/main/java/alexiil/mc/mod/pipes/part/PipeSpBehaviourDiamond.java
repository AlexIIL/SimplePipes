package alexiil.mc.mod.pipes.part;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

import net.fabricmc.fabric.api.container.ContainerProviderRegistry;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.context.LootContext;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.Direction;

import alexiil.mc.mod.pipes.blocks.TilePipe;
import alexiil.mc.mod.pipes.blocks.TilePipeItemDiamond;
import alexiil.mc.mod.pipes.container.SimplePipeContainers;
import alexiil.mc.mod.pipes.pipe.PartSpPipe;
import alexiil.mc.mod.pipes.pipe.PipeSpBehaviour;
import alexiil.mc.mod.pipes.pipe.TravellingItem;

import alexiil.mc.lib.attributes.item.ItemAttributes;

import alexiil.mc.lib.multipart.api.AbstractPart.ItemDropTarget;

public class PipeSpBehaviourDiamond extends PipeSpBehaviour {

    public static final int INV_SIZE = 9 * 6;

    public final SimpleInventory filterInv = new SimpleInventory(INV_SIZE);

    public PipeSpBehaviourDiamond(PartSpPipe pipe) {
        super(pipe);
    }

    @Override
    public void copyFrom(TilePipe oldTile) {
        super.copyFrom(oldTile);

        TilePipeItemDiamond from = (TilePipeItemDiamond) oldTile;

        for (int i = 0; i < INV_SIZE; i++) {
            filterInv.setStack(i, from.filterInv.removeStack(i));
        }
    }

    @Override
    public void addDrops(ItemDropTarget target, LootContext context) {
        for (int i = 0; i < INV_SIZE; i++) {
            ItemStack stack = filterInv.getStack(i);
            if (!stack.isEmpty()) {
                target.drop(stack.copy());
            }
        }
    }

    public List<EnumSet<Direction>> getOrderForItem(TravellingItem item, EnumSet<Direction> validDirections) {
        List<EnumSet<Direction>> list = new ArrayList<>();
        EnumSet<Direction> matches = EnumSet.noneOf(Direction.class);
        EnumSet<Direction> empties = EnumSet.noneOf(Direction.class);

        for (Direction dir : validDirections) {
            boolean emptyRow = true;

            for (int x = 0; x < 9; x++) {
                ItemStack filter = filterInv.getStack(dir.getId() * 9 + x);

                if (!filter.isEmpty()) {
                    emptyRow = false;

                    if (filter.getItem().equals(item.stack.getItem())) {
                        matches.add(dir);
                    } else if (ItemAttributes.FILTER.get(filter).matches(item.stack)) {
                        matches.add(dir);
                    }
                }
            }

            if (emptyRow) {
                empties.add(dir);
            }
        }

        list.add(matches);
        list.add(empties);
        return list;
    }

    @Override
    public void fromNbt(NbtCompound nbt) {
        super.fromNbt(nbt);
        for (int i = 0; i < INV_SIZE; i++) {
            filterInv.setStack(i, ItemStack.fromNbt(nbt.getCompound("filterStack_" + i)));
        }
    }

    @Override
    public NbtCompound toNbt() {
        NbtCompound nbt = super.toNbt();
        for (int i = 0; i < INV_SIZE; i++) {
            ItemStack stack = filterInv.getStack(i);
            if (!stack.isEmpty()) {
                nbt.put("filterStack_" + i, stack.writeNbt(new NbtCompound()));
            }
        }
        return nbt;
    }

    @Override
    public ActionResult onUse(PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (!player.world.isClient) {
            ContainerProviderRegistry.INSTANCE
                .openContainer(SimplePipeContainers.PIPE_PART_DIAMOND_ITEM, player, (buffer) -> {
                    buffer.writeBlockPos(pipe.getPos());
                });
        }
        return ActionResult.SUCCESS;
    }
}
