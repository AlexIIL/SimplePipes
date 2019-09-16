/*
 * Copyright (c) 2019 SpaceToad and the BuildCraft team
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0. If a copy of the MPL was not
 * distributed with this file, You can obtain one at https://mozilla.org/MPL/2.0/
 */
package alexiil.mc.mod.pipes.blocks;

import java.util.Objects;
import java.util.function.Function;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.DefaultedList;
import net.minecraft.util.Tickable;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

import alexiil.mc.lib.attributes.fluid.FluidAttributes;
import alexiil.mc.lib.attributes.fluid.FluidExtractable;
import alexiil.mc.lib.attributes.fluid.FluidInsertable;
import alexiil.mc.lib.attributes.item.ItemAttributes;
import alexiil.mc.lib.attributes.item.ItemExtractable;
import alexiil.mc.lib.attributes.item.ItemInsertable;

public abstract class TilePipe extends TileBase implements Tickable {

    protected static final double EXTRACT_SPEED = 0.08;

    public final BlockPipe pipeBlock;
    public volatile PipeBlockModelState blockModelState;
    byte connections;

    public final PipeFlow flow;

    public TilePipe(BlockEntityType<?> type, BlockPipe pipeBlock, Function<TilePipe, PipeFlow> flowConstructor) {
        super(type);
        this.pipeBlock = pipeBlock;
        this.blockModelState = createModelState();
        this.flow = flowConstructor.apply(this);
    }

    @Override
    public void fromTag(CompoundTag tag) {
        super.fromTag(tag);
        connections = tag.getByte("c");
        flow.fromTag(tag.getCompound("flow"));
    }

    @Override
    public CompoundTag toTag(CompoundTag tag) {
        tag = super.toTag(tag);
        tag.putByte("c", connections);
        tag.put("flow", flow.toTag());
        return tag;
    }

    @Override
    public void fromClientTag(CompoundTag tag) {
        if (tag.getBoolean("f")) {
            flow.fromClientTag(tag);
        } else {
            connections = tag.getByte("c");
            flow.fromInitialClientTag(tag);
            refreshModel();
        }
    }

    @Override
    public CompoundTag toClientTag(CompoundTag tag) {
        tag.putByte("c", connections);
        flow.toInitialClientTag(tag);
        return tag;
    }

    protected void onNeighbourChange() {
        for (Direction dir : Direction.values()) {
            BlockEntity oTile = world.getBlockEntity(getPos().offset(dir));
            if (this instanceof TilePipeWood && oTile instanceof TilePipeWood) {
                disconnect(dir);
            } else if (
                oTile instanceof TilePipe || canConnect(dir) || (this instanceof TilePipeSided && ((TilePipeSided) this)
                    .currentDirection() == dir && ((TilePipeSided) this).canFaceDirection(dir))
            ) {
                connect(dir);
            } else {
                disconnect(dir);
            }
        }
    }

    public long getWorldTime() {
        return world != null ? world.getTime() : 0;
    }

    protected boolean canConnect(Direction dir) {
        return flow.canConnect(dir);
    }

    @Nullable
    public final TilePipe getNeighbourPipe(Direction dir) {
        World w = getWorld();
        if (w == null) {
            return null;
        }
        BlockEntity be = w.getBlockEntity(getPos().offset(dir));
        if (be instanceof TilePipe) {
            return (TilePipe) be;
        }
        return null;
    }

    @Nonnull
    public final ItemExtractable getItemExtractable(Direction dir) {
        return getNeighbourAttribute(ItemAttributes.EXTRACTABLE, dir);
    }

    @Nonnull
    public final ItemInsertable getItemInsertable(Direction dir) {
        return getNeighbourAttribute(ItemAttributes.INSERTABLE, dir);
    }

    @Nonnull
    public final FluidExtractable getFluidExtractable(Direction dir) {
        return getNeighbourAttribute(FluidAttributes.EXTRACTABLE, dir);
    }

    @Nonnull
    public final FluidInsertable getFluidInsertable(Direction dir) {
        return getNeighbourAttribute(FluidAttributes.INSERTABLE, dir);
    }

    protected PipeBlockModelState createModelState() {
        return new PipeBlockModelState(pipeBlock, encodeConnectedSides());
    }

    protected final byte encodeConnectedSides() {
        return connections;
    }

    public boolean isConnected(Direction dir) {
        return (connections & (1 << dir.ordinal())) != 0;
    }

    public void connect(Direction dir) {
        connections |= 1 << dir.ordinal();
        refreshModel();
    }

    public void disconnect(Direction dir) {
        connections &= ~(1 << dir.ordinal());
        refreshModel();
    }

    protected void refreshModel() {
        PipeBlockModelState newState = createModelState();
        if (newState.equals(blockModelState)) {
            return;
        }
        blockModelState = newState;
        World w = getWorld();
        if (w instanceof ServerWorld) {
            sendPacket((ServerWorld) w, this.toUpdatePacket());
        } else if (w != null) {
            // air -> pipe
            // (This just forces the world to re-render us)
            w.scheduleBlockRender(getPos(), Blocks.AIR.getDefaultState(), getCachedState());
        }
    }

    protected void sendFlowPacket(CompoundTag tag) {
        tag.putBoolean("f", true);
        sendPacket((ServerWorld) world, tag);
    }

    public static class PipeBlockModelState {
        public final BlockPipe block;
        final byte connections;

        public PipeBlockModelState(BlockPipe block, byte isConnected) {
            this.block = block;
            this.connections = isConnected;
        }

        public boolean isConnected(Direction dir) {
            return (connections & (1 << dir.ordinal())) != 0;
        }

        @Override
        public String toString() {
            return "PipeBlockModel{" + block + ", " + connections + "}";
        }

        @Override
        public int hashCode() {
            return Objects.hash(block, connections);
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null) return false;
            if (getClass() != obj.getClass()) return false;
            PipeBlockModelState other = (PipeBlockModelState) obj;
            if (block == null) {
                if (other.block != null) return false;
            } else if (!block.equals(other.block)) return false;
            if (connections != other.connections) return false;
            return true;
        }
    }

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

    @Override
    public void tick() {
        flow.tick();
    }

    @Override
    public DefaultedList<ItemStack> removeItemsForDrop() {
        DefaultedList<ItemStack> all = super.removeItemsForDrop();
        flow.removeItemsForDrop(all);
        return all;
    }
}
