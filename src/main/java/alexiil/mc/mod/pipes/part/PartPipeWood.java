package alexiil.mc.mod.pipes.part;

import java.util.function.Function;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.math.Direction;

import alexiil.mc.lib.multipart.api.MultipartHolder;
import alexiil.mc.lib.multipart.api.PartDefinition;
import alexiil.mc.lib.net.IMsgReadCtx;
import alexiil.mc.lib.net.InvalidInputDataException;
import alexiil.mc.lib.net.NetByteBuf;

public abstract class PartPipeWood extends PartPipeSided {

    public static final int EXTRACT_DELAY = 10;

    int delay = 0;

    public PartPipeWood(PartDefinition definition, MultipartHolder holder, Function<PartPipe, PipeFlow> flowFn) {
        super(definition, holder, flowFn);
    }

    public PartPipeWood(PartDefinition definition, MultipartHolder holder, Function<PartPipe, PipeFlow> flowFn,
        CompoundTag tag) {
        super(definition, holder, flowFn, tag);
    }

    public PartPipeWood(PartDefinition definition, MultipartHolder holder, Function<PartPipe, PipeFlow> flowFn,
        NetByteBuf buf, IMsgReadCtx ctx) throws InvalidInputDataException {
        super(definition, holder, flowFn, buf, ctx);
    }

    @Override
    protected void tick() {
        super.tick();
        if (getWorld().isClient) {
            return;
        }
        Direction dir = currentDirection();
        if (dir == null) {
            return;
        }
        if (getWorld().isReceivingRedstonePower(getPos())) {
            delay++;
            if (delay < EXTRACT_DELAY) {
                return;
            } else {
                delay = 0;
                tryExtract(dir);
            }
        } else {
            delay--;
            if (delay < 0) {
                delay = 0;
            }
        }
    }

    protected abstract void tryExtract(Direction dir);
}
