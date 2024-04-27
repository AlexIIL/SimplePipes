package alexiil.mc.mod.pipes.pipe;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.ActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.DirectionTransformation;

import alexiil.mc.mod.pipes.client.model.part.PipeSpSidedPartKey;

public abstract class PipeSpBehaviourSided extends PipeSpBehaviour {

    private Direction currentDirection = null;

    public PipeSpBehaviourSided(PartSpPipe pipe) {
        super(pipe);
    }

    @Override
    public void fromNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup lookup) {
        super.fromNbt(nbt, lookup);

        byte b = nbt.getByte("dir");
        if (b >= 0 && b < 6) {
            currentDirection = Direction.byId(b);
        } else {
            currentDirection = null;
        }
    }

    @Override
    public NbtCompound toNbt(RegistryWrapper.WrapperLookup lookup) {
        NbtCompound nbt = super.toNbt(lookup);
        nbt.putByte("dir", (byte) (currentDirection == null ? 0xFF : currentDirection.getId()));
        return nbt;
    }

    public Direction currentDirection() {
        return currentDirection;
    }

    public void currentDirection(Direction dir) {
        if (currentDirection != dir && canFaceDirection(dir)) {
            this.currentDirection = dir;
            pipe.refreshModel();
        }
    }

    @Override
    public boolean canConnect(Direction dir) {
        return super.canConnect(dir) || canFaceDirection(dir);
    }

    protected abstract boolean canFaceDirection(Direction dir);

    @Override
    public ActionResult onUse(PlayerEntity player, BlockHitResult hit) {
        if (player.getWorld().isClient) {
            return ActionResult.SUCCESS;
        }
        return attemptRotation() ? ActionResult.SUCCESS : ActionResult.FAIL;
    }

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
                if (!pipe.isConnected(dir)) {
                    pipe.connect(dir);
                }
                currentDirection = dir;
                connectedToAny = true;
            } else if (pipe.isConnected(dir)) {
                ISimplePipe oPipe = pipe.getNeighbourPipe(dir);
                if (oPipe == null && !canConnect(dir)) {
                    pipe.disconnect(dir);
                }
            }
        }

        if (!connectedToAny) {
            currentDirection = null;
        }

        if (currentDirection != old) {
            pipe.refreshModel();
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void transform(DirectionTransformation transform) {
        currentDirection = transform.map(currentDirection);
    }

    @Override
    protected PipeSpSidedPartKey createModelState() {
        return new PipeSpSidedPartKey(pipe.definition, pipe.encodeConnectedSides(), currentDirection);
    }
}
