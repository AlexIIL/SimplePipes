/*
 * Copyright (c) 2019 SpaceToad and the BuildCraft team
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0. If a copy of the MPL was not
 * distributed with this file, You can obtain one at https://mozilla.org/MPL/2.0/
 */
package alexiil.mc.mod.pipes.blocks;

import java.util.Objects;
import java.util.function.Function;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

import alexiil.mc.mod.pipes.pipe.ISimplePipe;
import alexiil.mc.mod.pipes.pipe.PartSpPipe;
import alexiil.mc.mod.pipes.pipe.PipeFlowItem;
import alexiil.mc.mod.pipes.pipe.PipeSpBehaviour;
import alexiil.mc.mod.pipes.pipe.PipeSpDef;
import alexiil.mc.mod.pipes.pipe.PipeSpFlow;

import alexiil.mc.lib.multipart.api.MultipartContainer;
import alexiil.mc.lib.multipart.api.MultipartHolder;
import alexiil.mc.lib.multipart.api.MultipartUtil;
import alexiil.mc.lib.multipart.api.render.PartModelKey;

public abstract class TilePipe extends TileBase implements ISimplePipe {

    protected static final double EXTRACT_SPEED = 0.08;

    public final BlockPipe pipeBlock;
    public volatile PipeBlockModelState blockModelState;
    byte connections;

    private final PipeSpFlow flow;

    public TilePipe(
        BlockEntityType<?> type, BlockPos pos, BlockState state, BlockPipe pipeBlock,
        Function<TilePipe, PipeSpFlow> flowConstructor
    ) {
        super(type, pos, state);
        this.pipeBlock = pipeBlock;
        this.blockModelState = createModelState();
        this.flow = flowConstructor.apply(this);
    }

    public final PartSpPipe getMultipartConversion(MultipartHolder holder) {
        PartSpPipe pipe = new PartSpPipe(pipeBlock.pipeDef, holder);
        pipe.connections = connections;
        pipe.flow.fromTag(flow.toTag());
        saveToBehaviour(pipe.behaviour);
        return pipe;
    }

    protected void saveToBehaviour(PipeSpBehaviour behaviour) {

    }

    @Override
    public PipeSpDef getDefinition() {
        return pipeBlock.pipeDef;
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        connections = nbt.getByte("c");
        getFlow().fromTag(nbt.getCompound("flow"));
    }

    @Override
    public NbtCompound writeNbt(NbtCompound tag) {
        tag = super.writeNbt(tag);
        tag.putByte("c", connections);
        tag.put("flow", getFlow().toTag());
        return tag;
    }

    @Override
    public void fromClientTag(NbtCompound tag) {
        if (tag.getBoolean("f")) {
            getFlow().fromClientTag(tag);
        } else {
            connections = tag.getByte("c");
            getFlow().fromInitialClientTag(tag);
            refreshModel();
        }
    }

    @Override
    public NbtCompound toClientTag(NbtCompound tag) {
        tag.putByte("c", connections);
        getFlow().toInitialClientTag(tag);
        return tag;
    }

    public PipeBlockModelState getBlockModelState() {
        return blockModelState;
    }

    byte getConnections() {
        return connections;
    }

    @Override
    public PipeSpFlow getFlow() {
        return flow;
    }

    protected void onNeighbourChange() {
        for (Direction dir : Direction.values()) {
            BlockEntity oTile = world.getBlockEntity(getPos().offset(dir));
            if (this instanceof TilePipeWood && oTile instanceof TilePipeWood) {
                disconnect(dir);
            } else if (oTile instanceof TilePipe) {
                if ((getFlow() instanceof PipeFlowItem) == (((ISimplePipe) oTile).getFlow() instanceof PipeFlowItem)) {
                    connect(dir);
                } else {
                    disconnect(dir);
                }
            } else if (canConnect(dir)) {
                connect(dir);
            } else {
                disconnect(dir);
            }
        }
    }

    @Override
    public long getWorldTime() {
        return world != null ? world.getTime() : 0;
    }

    protected boolean canConnect(Direction dir) {
        return getFlow().hasExtractable(dir) || getFlow().hasInsertable(dir);
    }

    @Override
    public final ISimplePipe getNeighbourPipe(Direction dir) {
        World w = getWorld();
        if (w == null) {
            return null;
        }
        BlockEntity be = w.getBlockEntity(getPos().offset(dir));
        if (be instanceof TilePipe || be == null) {
            return (ISimplePipe) be;
        }
        MultipartContainer container = MultipartContainer.ATTRIBUTE.getFirstOrNull(getWorld(), be.getPos());
        if (container == null || PartSpPipe.hasConnectionOverlap(dir.getOpposite(), container)) {
            return null;
        }
        return container.getFirstPart(ISimplePipe.class);
    }

    protected PipeBlockModelState createModelState() {
        return new PipeBlockModelState(pipeBlock.pipeDef, encodeConnectedSides());
    }

    protected final byte encodeConnectedSides() {
        return connections;
    }

    @Override
    public boolean isConnected(Direction dir) {
        return (connections & (1 << dir.ordinal())) != 0;
    }

    @Override
    public void connect(Direction dir) {
        connections |= 1 << dir.ordinal();
        refreshModel();
    }

    @Override
    public void disconnect(Direction dir) {
        connections &= ~(1 << dir.ordinal());
        refreshModel();
    }

    protected void refreshModel() {
        PipeBlockModelState newState = createModelState();
        if (newState.equals(getBlockModelState())) {
            return;
        }
        blockModelState = newState;
        World w = getWorld();
        if (w instanceof ServerWorld) {
            sendPacket((ServerWorld) w, this.toUpdatePacket());
        } else if (w != null) {
            // air -> pipe
            // (This just forces the world to re-render us)
            w.scheduleBlockRerenderIfNeeded(getPos(), Blocks.AIR.getDefaultState(), getCachedState());
        }
    }

    @Override
    public void sendFlowPacket(NbtCompound tag) {
        tag.putBoolean("f", true);
        sendPacket((ServerWorld) world, tag);
    }

    public static class PipeBlockModelState extends PartModelKey {
        public final PipeSpDef def;
        final byte connections;

        public PipeBlockModelState(PipeSpDef def, byte isConnected) {
            this.def = def;
            this.connections = isConnected;
        }

        public boolean isConnected(Direction dir) {
            return (connections & (1 << dir.ordinal())) != 0;
        }

        @Override
        public String toString() {
            return "PipeBlockModel{" + def + ", " + connections + "}";
        }

        @Override
        public int hashCode() {
            return Objects.hash(def, connections);
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null) return false;
            if (getClass() != obj.getClass()) return false;
            PipeBlockModelState other = (PipeBlockModelState) obj;
            return connections == other.connections && Objects.equals(def, other.def);
        }
    }

    @Override
    public double getPipeLength(Direction side) {
        if (side == null) {
            return 0;
        }
        if (isConnected(side)) {
            if (getNeighbourPipe(side) == null/* pipe.getConnectedType(side) == ConnectedType.TILE */) {
                // TODO: Check the length between this pipes centre and the next block along
                return 0.5 + 0.25;// Tiny distance for fully pushing items in.
            }
            return 0.5;
        } else {
            return 0.25;
        }
    }

    public void tick() {
        flow.tick();
        World w = world;
        if (w != null) {
            w.markDirty(getPos());
        }

        if (false) {
            MultipartUtil.turnIntoMultipart(w, getPos());
        }
    }

    @Override
    public DefaultedList<ItemStack> removeItemsForDrop() {
        DefaultedList<ItemStack> all = super.removeItemsForDrop();
        getFlow().removeItemsForDrop(all);
        return all;
    }
}
