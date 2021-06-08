package alexiil.mc.mod.pipes.pipe;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.google.common.collect.ImmutableList;

import net.minecraft.entity.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.context.LootContext;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.util.DyeColor;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import alexiil.mc.mod.pipes.part.SimplePipeParts;
import alexiil.mc.mod.pipes.pipe.PipeSpDef.PipeDefItem;
import alexiil.mc.mod.pipes.util.DelayedList;
import alexiil.mc.mod.pipes.util.TagUtil;

import alexiil.mc.lib.attributes.Simulation;
import alexiil.mc.lib.attributes.item.ItemInsertable;
import alexiil.mc.lib.attributes.item.filter.ConstantItemFilter;
import alexiil.mc.lib.attributes.item.filter.ItemFilter;
import alexiil.mc.lib.attributes.item.impl.EmptyItemExtractable;
import alexiil.mc.lib.attributes.item.impl.RejectingItemInsertable;

import alexiil.mc.lib.multipart.api.AbstractPart.ItemDropTarget;

public class PipeSpFlowItem extends PipeSpFlow {

    final ItemInsertable[] insertables;

    private final DelayedList<TravellingItem> items = new DelayedList<>();

    public PipeSpFlowItem(ISimplePipe pipe) {
        super(pipe);

        this.insertables = new ItemInsertable[6];
        for (Direction dir : Direction.values()) {
            insertables[dir.getOpposite().ordinal()] = new ItemInsertable() {
                @Override
                public ItemStack attemptInsertion(ItemStack stack, Simulation simulation) {
                    if (stack.isEmpty()) {
                        return stack;
                    }
                    return injectItem(stack, simulation.isAction(), dir, null, 0.04);
                }

                @Override
                public ItemFilter getInsertionFilter() {
                    return ConstantItemFilter.ANYTHING;
                }
            };
        }
    }

    @Override
    public void fromTag(NbtCompound tag) {
        NbtList list = tag.getList("items", new NbtCompound().getType());
        for (int i = 0; i < list.size(); i++) {
            TravellingItem item = new TravellingItem(list.getCompound(i), 0);
            if (!item.stack.isEmpty()) {
                items.add(item.getCurrentDelay(0), item);
            }
        }
    }

    @Override
    public NbtCompound toTag() {
        NbtCompound nbt = new NbtCompound();
        Iterable<? extends Iterable<TravellingItem>> allItems = items.getAllElements();
        NbtList list = new NbtList();

        long tickNow = pipe.getWorldTime();
        for (Iterable<TravellingItem> l : allItems) {
            if (l != null) {
                for (TravellingItem item : l) {
                    list.add(item.writeToNbt(tickNow));
                }
            }
        }
        nbt.put("items", list);
        return nbt;
    }

    @Override
    public void fromClientTag(NbtCompound tag) {

        // tag.put("item", item.stack.toTag(new CompoundTag()));
        // tag.putBoolean("to_center", item.toCenter);
        // tag.put("side", TagUtil.writeEnum(item.side));
        // tag.put("colour", TagUtil.writeEnum(item.colour));
        // tag.putShort("time", item.timeToDest > Short.MAX_VALUE ? Short.MAX_VALUE :(short) item.timeToDest);

        TravellingItem item = new TravellingItem(ItemStack.fromNbt(tag.getCompound("item")));
        item.toCenter = tag.getBoolean("to_center");
        item.side = TagUtil.readEnum(tag.get("side"), Direction.class);
        item.colour = TagUtil.readEnum(tag.get("colour"), DyeColor.class);
        item.timeToDest = Short.toUnsignedInt(tag.getShort("time"));
        item.tickStarted = pipe.getWorldTime() + 1;
        item.tickFinished = item.tickStarted + item.timeToDest;
        item.speed *= getSpeedModifier();
        items.add(item.timeToDest + 1, item);
    }

    @Override
    public Object getInsertable(Direction searchDirection) {
        return insertables[searchDirection.getId()];
    }

    @Override
    public boolean hasInsertable(Direction dir) {
        return pipe.getItemInsertable(dir) != RejectingItemInsertable.NULL;
    }

    @Override
    public boolean hasExtractable(Direction dir) {
        return pipe.getItemExtractable(dir) != EmptyItemExtractable.NULL;
    }

    @Override
    public void tick() {
        World w = pipe.getPipeWorld();
        if (w == null) {
            return;
        }

        List<TravellingItem> toTick = items.advance();
        if (toTick == null) {
            return;
        }
        long currentTime = pipe.getWorldTime();

        for (TravellingItem item : toTick) {
            if (item.tickFinished > currentTime) {
                // Can happen if something ticks this tile multiple times in a single real tick
                items.add((int) (item.tickFinished - currentTime), item);
                continue;
            }
            if (item.isPhantom) {
                continue;
            }
            if (w.isClient) {
                continue;
            }
            if (item.toCenter) {
                onItemReachCenter(item);
            } else {
                onItemReachEnd(item);
            }
        }
    }

    @Override
    public void addDrops(ItemDropTarget target, LootContext context) {
        BlockPos pos = pipe.getPipePos();
        long tick = pipe.getWorldTime();
        for (Iterable<TravellingItem> list : this.items.getAllElements()) {
            if (list == null) {
                continue;
            }
            for (TravellingItem travel : list) {
                if (!travel.isPhantom) {
                    target.drop(travel.stack, travel.getRenderPosition(pos, tick, 1, pipe), Vec3d.ZERO);
                }
            }
        }
    }

    @Override
    public void removeItemsForDrop(DefaultedList<ItemStack> all) {
        for (Iterable<TravellingItem> list : this.items.getAllElements()) {
            if (list == null) {
                continue;
            }
            for (TravellingItem travel : list) {
                if (!travel.isPhantom) {
                    all.add(travel.stack);
                }
            }
        }
        items.clear();
    }

    void sendItemDataToClient(TravellingItem item) {
        // TODO :p
        // System.out.println(getPos() + " - " + item.stack + " - " + item.side);
        NbtCompound tag = new NbtCompound();

        tag.put("item", item.stack.writeNbt(new NbtCompound()));
        tag.putBoolean("to_center", item.toCenter);
        tag.put("side", TagUtil.writeEnum(item.side));
        tag.put("colour", TagUtil.writeEnum(item.colour));
        tag.putShort("time", item.timeToDest > Short.MAX_VALUE ? Short.MAX_VALUE : (short) item.timeToDest);

        pipe.sendFlowPacket(tag);
    }

    protected List<EnumSet<Direction>> getOrderForItem(TravellingItem item, EnumSet<Direction> validDirections) {
        List<EnumSet<Direction>> list = new ArrayList<>();

        if (
            pipe.getDefinition() == SimplePipeParts.CLAY_PIPE_FLUIDS
                || pipe.getDefinition() == SimplePipeParts.CLAY_PIPE_ITEMS
        ) {
            EnumSet<Direction> invs = EnumSet.noneOf(Direction.class);
            EnumSet<Direction> others = EnumSet.noneOf(Direction.class);
            for (Direction dir : validDirections) {
                if (pipe.getNeighbourPipe(dir) != null) {
                    others.add(dir);
                } else {
                    invs.add(dir);
                }
            }
            list.add(invs);
            list.add(others);
        } else {
            if (!validDirections.isEmpty()) {
                list.add(validDirections);
            }
        }
        return list;
    }

    protected boolean canBounce() {
        return ((PipeDefItem) pipe.getDefinition()).canBounce;
    }

    protected double getSpeedModifier() {
        return ((PipeDefItem) pipe.getDefinition()).speedModifier;
    }

    private void onItemReachCenter(TravellingItem item) {

        if (item.stack.isEmpty()) {
            return;
        }

        EnumSet<Direction> dirs = EnumSet.allOf(Direction.class);
        dirs.remove(item.side);
        dirs.removeAll(item.tried);
        for (Direction dir : Direction.values()) {
            if (!pipe.isConnected(dir) || pipe.getItemInsertable(dir) == null) {
                dirs.remove(dir);
            }
        }

        List<EnumSet<Direction>> order = getOrderForItem(item, dirs);
        if (order.isEmpty()) {
            if (canBounce()) {
                order = ImmutableList.of(EnumSet.of(item.side));
            } else {
                dropItem(item.stack, null, item.side.getOpposite(), item.speed);
                return;
            }
        }

        long now = pipe.getWorldTime();
        // Saves effort :p
        final double newSpeed = 0.08 * getSpeedModifier();
        //
        // if (holder.fireEvent(modifySpeed)) {
        // double target = modifySpeed.targetSpeed;
        // double maxDelta = modifySpeed.maxSpeedChange;
        // if (item.speed < target) {
        // newSpeed = Math.min(target, item.speed + maxDelta);
        // } else if (item.speed > target) {
        // newSpeed = Math.max(target, item.speed - maxDelta);
        // } else {
        // newSpeed = item.speed;
        // }
        // } else {
        // // Nothing affected the speed
        // // so just fallback to a sensible default
        // if (item.speed > 0.03) {
        // newSpeed = Math.max(0.03, item.speed - PipeBehaviourStone.SPEED_DELTA);
        // } else {
        // newSpeed = item.speed;
        // }
        // }

        List<Direction> destinations = new ArrayList<>();

        for (EnumSet<Direction> set : order) {
            List<Direction> shuffled = new ArrayList<>();
            shuffled.addAll(set);
            Collections.shuffle(shuffled);
            destinations.addAll(shuffled);
        }

        if (destinations.size() == 0) {
            dropItem(item.stack, null, item.side.getOpposite(), newSpeed);
        } else {
            TravellingItem newItem = new TravellingItem(item.stack);
            newItem.tried.addAll(item.tried);
            newItem.toCenter = false;
            newItem.colour = item.colour;
            newItem.side = destinations.get(0);
            newItem.speed = newSpeed;
            newItem.genTimings(now, pipe.getPipeLength(newItem.side));
            items.add(newItem.timeToDest, newItem);
            sendItemDataToClient(newItem);
        }
    }

    private void onItemReachEnd(TravellingItem item) {
        ItemInsertable ins = pipe.getItemInsertable(item.side);
        ItemStack excess = item.stack;
        if (ins != null) {
            Direction oppositeSide = item.side.getOpposite();
            ISimplePipe oPipe = pipe.getNeighbourPipe(item.side);

            if (oPipe != null && oPipe.getFlow() instanceof PipeSpFlowItem) {
                excess
                    = ((PipeSpFlowItem) oPipe.getFlow()).injectItem(excess, true, oppositeSide, item.colour, item.speed);
            } else {
                excess = ins.attemptInsertion(excess, Simulation.ACTION);
            }
        }
        if (excess.isEmpty()) {
            return;
        }
        item.tried.add(item.side);
        item.toCenter = true;
        item.stack = excess;
        item.genTimings(pipe.getWorldTime(), pipe.getPipeLength(item.side));
        items.add(item.timeToDest, item);
        sendItemDataToClient(item);
    }

    private void dropItem(ItemStack stack, Direction side, Direction motion, double speed) {
        if (stack == null || stack.isEmpty()) {
            return;
        }

        double x = pipe.getPipePos().getX() + 0.5 + motion.getOffsetX() * 0.5;
        double y = pipe.getPipePos().getY() + 0.5 + motion.getOffsetY() * 0.5;
        double z = pipe.getPipePos().getZ() + 0.5 + motion.getOffsetZ() * 0.5;
        speed += 0.01;
        speed *= 2;
        ItemEntity ent = new ItemEntity(world(), x, y, z, stack);
        ent.setVelocity(Vec3d.of(motion.getVector()).multiply(speed));

        world().spawnEntity(ent);
    }

    public boolean canInjectItems(Direction from) {
        return pipe.isConnected(from);
    }

    public ItemStack injectItem(
        @Nonnull ItemStack stack, boolean doAdd, Direction from, DyeColor colour, double speed
    ) {
        if (world().isClient) {
            throw new IllegalStateException("Cannot inject items on the client side!");
        }
        if (!canInjectItems(from)) {
            return stack;
        }

        if (speed < 0.01) {
            speed = 0.01;
        }

        // Try insert

        ItemStack toSplit = ItemStack.EMPTY;
        ItemStack toInsert = stack;

        if (doAdd) {
            insertItemEvents(toInsert, colour, speed, from);
        }

        if (toSplit.isEmpty()) {
            toSplit = ItemStack.EMPTY;
        }

        return toSplit;
    }

    public void insertItemsForce(@Nonnull ItemStack stack, Direction from, DyeColor colour, double speed) {
        if (world().isClient) {
            throw new IllegalStateException("Cannot inject items on the client side!");
        }
        if (stack.isEmpty()) {
            return;
        }
        if (speed < 0.01) {
            speed = 0.01;
        }
        long now = pipe.getWorldTime();
        TravellingItem item = new TravellingItem(stack);
        item.side = from;
        item.toCenter = true;
        item.speed = speed;
        item.colour = colour;
        item.genTimings(now, 0);
        item.tried.add(from);
        addItemTryMerge(item);
    }

    /** Used internally to split up manual insertions from controlled extractions. */
    private void insertItemEvents(@Nonnull ItemStack toInsert, DyeColor colour, double speed, Direction from) {
        long now = world().getTime();

        TravellingItem item = new TravellingItem(toInsert);
        item.side = from;
        item.toCenter = true;
        item.speed = speed;
        item.colour = colour;
        item.stack = toInsert;
        item.genTimings(now, pipe.getPipeLength(from));
        item.tried.add(from);
        addItemTryMerge(item);
    }

    private void addItemTryMerge(TravellingItem item) {
        // for (List<TravellingItem> list : items.getAllElements()) {
        // for (TravellingItem item2 : list) {
        // if (item2.mergeWith(item)) {
        // return;
        // }
        // }
        // }
        items.add(item.timeToDest, item);
        sendItemDataToClient(item);
    }

    @Nullable
    private static EnumSet<Direction> getFirstNonEmptySet(List<EnumSet<Direction>> possible) {
        for (EnumSet<Direction> set : possible) {
            if (set.size() > 0) {
                return set;
            }
        }
        return null;
    }

    public List<TravellingItem> getAllItemsForRender() {
        List<TravellingItem> all = new ArrayList<>();
        for (List<TravellingItem> innerList : items.getAllElements()) {
            all.addAll(innerList);
        }
        return all;
    }
}
