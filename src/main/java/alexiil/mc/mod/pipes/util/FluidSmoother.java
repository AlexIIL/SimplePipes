package alexiil.mc.mod.pipes.util;

import javax.annotation.Nullable;

import net.minecraft.world.World;

import alexiil.mc.lib.attributes.fluid.impl.SimpleFixedFluidInv;
import alexiil.mc.lib.attributes.fluid.volume.FluidVolume;
import alexiil.mc.lib.net.IMsgReadCtx;
import alexiil.mc.lib.net.IMsgWriteCtx;
import alexiil.mc.lib.net.NetByteBuf;

public class FluidSmoother {
    final IFluidDataSender sender;
    final SimpleFixedFluidInv tank;
    _Side data;

    public FluidSmoother(IFluidDataSender sender, SimpleFixedFluidInv tank) {
        this.sender = sender;
        this.tank = tank;
    }

    public void tick(World world) {
        if (data == null) {
            if (world == null) {
                return;
            }
            data = world.isClient ? new _Client() : new _Server();
        }
        data.tick(world);
    }

    public void handleMessage(World world, NetByteBuf buffer, IMsgReadCtx ctx) {
        if (data == null) {
            data = new _Client();
        }
        if (data instanceof _Client) {
            ((_Client) data).handleMessage(world, buffer, ctx);
        } else {
            throw new IllegalStateException("You can only call this on the client!");
        }
    }

    public void writeInit(NetByteBuf buffer, IMsgWriteCtx ctx) {
        if (data == null) {
            data = new _Server();
        }
        if (data instanceof _Server) {
            ((_Server) data).writeMessage(buffer, ctx);
        } else {
            throw new IllegalStateException("You can only call this on the server!");
        }
    }

    public void resetSmoothing(World world) {
        if (data == null && world.isClient) {
            data = new _Client();
        }
        if (data instanceof _Client) {
            _Client client = (_Client) data;
            client.resetSmoothing(world);
        } else {
            throw new IllegalStateException("You can only call this on the client!");
        }
    }

    @Nullable
    public FluidVolume getFluidForRender() {
        if (data instanceof _Client) {
            _Client client = (_Client) data;
            if (client.fluid == null) {
                return null;
            }
            return client.fluid;
        }
        return null;
    }

    @Nullable
    public FluidStackInterp getFluidForRender(double partialTicks) {
        if (data instanceof _Client) {
            _Client client = (_Client) data;
            if (client.fluid == null || client.fluid.isEmpty()) {
                return null;
            }
            double amount = client.amountLast * (1 - partialTicks) + client.amount * partialTicks;
            return new FluidStackInterp(client.fluid, amount);
        }
        return null;
    }

    public int getCapacity() {
        return tank.getMaxAmount(0);
    }

    @FunctionalInterface
    public interface IPayloadWriter {
        void write(NetByteBuf buffer, IMsgWriteCtx ctx);
    }

    @FunctionalInterface
    public interface IFluidDataSender {
        void writePacket(IPayloadWriter writer);
    }

    public static class FluidStackInterp {
        public final FluidVolume fluid;
        public final double amount;

        public FluidStackInterp(FluidVolume fluid, double amount) {
            this.fluid = fluid;
            this.amount = amount;
        }
    }

    abstract class _Side {
        abstract void tick(World world);
    }

    final class _Server extends _Side {
        private int sentAmount = -1;
        private boolean sentHasFluid = false;

        @Override
        void tick(World world) {
            FluidVolume fluid = tank.getInvFluid(0);
            boolean hasFluid = !fluid.isEmpty();
            if ((fluid.getAmount() != sentAmount || hasFluid != sentHasFluid)) {
                sender.writePacket(this::writeMessage);
            }
        }

        void writeMessage(NetByteBuf buffer, IMsgWriteCtx ctx) {
            FluidVolume fluid = tank.getInvFluid(0);
            boolean hasFluid = !fluid.isEmpty();

            sentAmount = fluid.getAmount();
            sentHasFluid = hasFluid;

            buffer.writeInt(sentAmount);
            // TODO: Replace this with buffer writing in LBA!
            buffer.writeCompoundTag(fluid.toTag());
        }
    }

    final class _Client extends _Side {
        private int target;
        int amount, amountLast;
        long lastMessage, lastMessageMinus1;
        FluidVolume fluid;

        @Override
        void tick(World world) {
            amountLast = amount;
            if (amount != target) {
                int delta = target - amount;
                long msgDelta = lastMessage - lastMessageMinus1;
                msgDelta = Math.min(Math.max((int) msgDelta, 1), 10);
                if (Math.abs(delta) < msgDelta) {
                    amount += delta;
                } else {
                    amount += delta / (int) msgDelta;
                }
            }
        }

        void handleMessage(World world, NetByteBuf buffer, IMsgReadCtx ctx) {
            target = buffer.readInt();
            fluid = FluidVolume.fromTag(buffer.readCompoundTag());
            lastMessageMinus1 = lastMessage;
            lastMessage = world.getTime();
        }

        void resetSmoothing(World world) {
            lastMessageMinus1 = lastMessage = world.getTime();
            lastMessageMinus1 -= 1;
        }
    }
}
