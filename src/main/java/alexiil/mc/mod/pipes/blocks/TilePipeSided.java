/*
 * Copyright (c) 2019 SpaceToad and the BuildCraft team
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0. If a copy of the MPL was not
 * distributed with this file, You can obtain one at https://mozilla.org/MPL/2.0/
 */
package alexiil.mc.mod.pipes.blocks;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

import javax.annotation.Nullable;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

import alexiil.mc.mod.pipes.pipe.ISimplePipe;
import alexiil.mc.mod.pipes.pipe.PipeSpDef;
import alexiil.mc.mod.pipes.pipe.PipeSpFlow;

@Deprecated
public abstract class TilePipeSided extends TilePipe {

    private Direction currentDirection = null;

    public TilePipeSided(
        BlockEntityType<?> type, BlockPos pos, BlockState state, BlockPipe pipeBlock,
        Function<TilePipe, PipeSpFlow> flowConstructor
    ) {
        super(type, pos, state, pipeBlock, flowConstructor);
    }

    @Override
    public void writeNbt(NbtCompound tag) {
        super.writeNbt(tag);
        tag.putByte("dir", (byte) (currentDirection == null ? 0xFF : currentDirection.getId()));
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        byte b = nbt.getByte("dir");
        if (b >= 0 && b < 6) {
            currentDirection = Direction.byId(b);
        } else {
            currentDirection = null;
        }
    }

    @Override
    public NbtCompound toClientTag(NbtCompound tag) {
        tag = super.toClientTag(tag);
        tag.putByte("dir", (byte) (currentDirection == null ? 0xFF : currentDirection.getId()));
        return tag;
    }

    @Override
    public void readPacket(NbtCompound tag) {
        super.readPacket(tag);
        if (!tag.getBoolean("f")) {
            byte b = tag.getByte("dir");
            if (b >= 0 && b < 6) {
                currentDirection = Direction.byId(b);
            } else {
                currentDirection = null;
            }
            refreshModel();
        }
    }

    @Override
    public NbtCompound toInitialChunkDataNbt() {
        NbtCompound tag = super.toInitialChunkDataNbt();
        tag.putByte("dir", (byte) (currentDirection == null ? 0xFF : currentDirection.getId()));
        return tag;
    }

    public Direction currentDirection() {
        return currentDirection;
    }

    public void currentDirection(Direction dir) {
        if (currentDirection != dir && canFaceDirection(dir)) {
            this.currentDirection = dir;
            refreshModel();
        }
    }

    @Override
    protected boolean canConnect(Direction dir) {
        return super.canConnect(dir) || canFaceDirection(dir);
    }

    protected abstract boolean canFaceDirection(Direction dir);

    public boolean attemptRotation() {
        List<Direction> dirs = new ArrayList<>();
        Collections.addAll(dirs, Direction.values());

        Direction old = currentDirection;

        if (old != null) {
            int idx = old.getId();
            if (idx < 5) {
                dirs.addAll(dirs.subList(0, idx + 1));
                dirs.subList(0, idx + 1).clear();
            }
        }

        boolean connectedToAny = false;
        for (Direction dir : dirs) {
            if (canFaceDirection(dir) && !connectedToAny) {
                if (!isConnected(dir)) {
                    connect(dir);
                }
                currentDirection = dir;
                connectedToAny = true;
            } else if (isConnected(dir)) {
                BlockEntity oTile = world.getBlockEntity(getPipePos().offset(dir));
                if (!(oTile instanceof ISimplePipe) && !canConnect(dir)) {
                    disconnect(dir);
                }
            }
        }

        if (!connectedToAny) {
            currentDirection = null;
        }

        if (currentDirection != old) {
            refreshModel();
            return true;
        } else {
            return false;
        }
    }

    @Override
    protected PipeBlockModelStateSided createModelState() {
        return new PipeBlockModelStateSided(pipeBlock.pipeDef, encodeConnectedSides(), currentDirection);
    }

    public static class PipeBlockModelStateSided extends PipeBlockModelState {
        @Nullable
        public final Direction mainSide;

        public PipeBlockModelStateSided(PipeSpDef pipeDef, byte isConnected, Direction mainSide) {
            super(pipeDef, isConnected);
            this.mainSide = mainSide;
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = super.hashCode();
            result = prime * result + ((mainSide == null) ? 0 : mainSide.hashCode());
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (!super.equals(obj)) return false;
            if (getClass() != obj.getClass()) return false;
            PipeBlockModelStateSided other = (PipeBlockModelStateSided) obj;
            if (mainSide != other.mainSide) return false;
            return true;
        }
    }
}
