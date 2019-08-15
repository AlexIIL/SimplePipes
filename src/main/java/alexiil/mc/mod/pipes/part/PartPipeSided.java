package alexiil.mc.mod.pipes.part;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.math.Direction;

import alexiil.mc.lib.multipart.api.MultipartHolder;
import alexiil.mc.lib.multipart.api.PartDefinition;
import alexiil.mc.lib.multipart.api.render.PartModelKey;
import alexiil.mc.lib.net.IMsgReadCtx;
import alexiil.mc.lib.net.InvalidInputDataException;
import alexiil.mc.lib.net.NetByteBuf;
import alexiil.mc.mod.pipes.blocks.TilePipe;
import alexiil.mc.mod.pipes.client.model.part.PipePartKey;

public abstract class PartPipeSided extends PartPipe {

    private Direction currentDirection = null;

    public PartPipeSided(PartDefinition definition, MultipartHolder holder, Function<PartPipe, PipeFlow> flowFn) {
        super(definition, holder, flowFn);
    }

    public PartPipeSided(PartDefinition definition, MultipartHolder holder, Function<PartPipe, PipeFlow> flowFn,
        CompoundTag tag) {
        super(definition, holder, flowFn, tag);
    }

    @Override
    public CompoundTag toTag() {
        CompoundTag tag = super.toTag();

        return tag;
    }

    public PartPipeSided(PartDefinition definition, MultipartHolder holder, Function<PartPipe, PipeFlow> flowFn,
        NetByteBuf buf, IMsgReadCtx ctx) throws InvalidInputDataException {
        super(definition, holder, flowFn, buf, ctx);
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
                BlockEntity oTile = getWorld().getBlockEntity(getPos().offset(dir));
                if (!(oTile instanceof TilePipe) && !canConnect(dir)) {
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
    public PartModelKey getModelKey() {
        return new PipePartKey.Sided(definition, connections, currentDirection);
    }
}
