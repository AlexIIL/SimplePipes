package alexiil.mc.mod.pipes.part;

import java.util.function.Function;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.BooleanBiFunction;
import net.minecraft.util.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Direction.Axis;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.World;

import alexiil.mc.lib.attributes.Attribute;
import alexiil.mc.lib.attributes.Attributes;
import alexiil.mc.lib.attributes.CombinableAttribute;
import alexiil.mc.lib.attributes.SearchOptions;
import alexiil.mc.lib.attributes.fluid.FluidAttributes;
import alexiil.mc.lib.attributes.fluid.FluidExtractable;
import alexiil.mc.lib.attributes.fluid.FluidInsertable;
import alexiil.mc.lib.attributes.item.ItemAttributes;
import alexiil.mc.lib.attributes.item.ItemExtractable;
import alexiil.mc.lib.attributes.item.ItemInsertable;
import alexiil.mc.lib.multipart.api.AbstractPart;
import alexiil.mc.lib.multipart.api.MultipartEventBus;
import alexiil.mc.lib.multipart.api.MultipartHolder;
import alexiil.mc.lib.multipart.api.PartDefinition;
import alexiil.mc.lib.multipart.api.event.PartTickEvent;
import alexiil.mc.lib.multipart.api.render.PartModelKey;
import alexiil.mc.lib.net.IMsgReadCtx;
import alexiil.mc.lib.net.IMsgWriteCtx;
import alexiil.mc.lib.net.InvalidInputDataException;
import alexiil.mc.lib.net.NetByteBuf;
import alexiil.mc.lib.net.NetIdDataK;
import alexiil.mc.lib.net.ParentNetIdSingle;
import alexiil.mc.mod.pipes.client.model.part.PipePartKey;

public abstract class PartPipe extends AbstractPart {

    public static final Attribute<PartPipe> PIPE_ATTRIBUTE = Attributes.create(PartPipe.class);

    public static final VoxelShape CENTER_SHAPE;
    private static final VoxelShape[] FACE_SHAPES;
    private static final VoxelShape[] FACE_CENTER_SHAPES;
    private static final VoxelShape[] SHAPES;

    public static final ParentNetIdSingle<PartPipe> PIPE_NET_ID;
    public static final NetIdDataK<PartPipe> NET_ID_CONNECTION_CHANGE;

    static {
        CENTER_SHAPE = VoxelShapes.cuboid(0.25, 0.25, 0.25, 0.75, 0.75, 0.75);
        FACE_SHAPES = new VoxelShape[6];
        FACE_CENTER_SHAPES = new VoxelShape[6];
        for (Direction dir : Direction.values()) {
            double x = 0.5 + dir.getOffsetX() * 0.375;
            double y = 0.5 + dir.getOffsetY() * 0.375;
            double z = 0.5 + dir.getOffsetZ() * 0.375;
            double rx = dir.getAxis() == Axis.X ? 0.125 : 0.25;
            double ry = dir.getAxis() == Axis.Y ? 0.125 : 0.25;
            double rz = dir.getAxis() == Axis.Z ? 0.125 : 0.25;
            VoxelShape faceShape = VoxelShapes.cuboid(x - rx, y - ry, z - rz, x + rx, y + ry, z + rz);
            FACE_SHAPES[dir.ordinal()] = faceShape;
            FACE_CENTER_SHAPES[dir.ordinal()] = VoxelShapes.union(faceShape, CENTER_SHAPE);
        }

        SHAPES = new VoxelShape[2 * 2 * 2 * 2 * 2 * 2];
        for (int c = 0; c <= 0b111_111; c++) {
            VoxelShape shape = CENTER_SHAPE;
            for (Direction dir : Direction.values()) {
                if ((c & (1 << dir.ordinal())) != 0) {
                    shape = VoxelShapes.combine(shape, FACE_SHAPES[dir.ordinal()], BooleanBiFunction.OR);
                }
            }
            SHAPES[c] = shape.simplify();
        }
        PIPE_NET_ID = AbstractPart.NET_ID.subType(PartPipe.class, "simple_pipes:pipe");
        NET_ID_CONNECTION_CHANGE = PIPE_NET_ID.idData("connections", 1);
        NET_ID_CONNECTION_CHANGE.setReadWrite(PartPipe::receiveConnectionChange, PartPipe::writeConnectionChange);
    }

    public final PipeFlow flow;
    byte connections, prevConnections;

    public PartPipe(PartDefinition definition, MultipartHolder holder, Function<PartPipe, PipeFlow> flowFn) {
        super(definition, holder);
        this.flow = flowFn.apply(this);
    }

    public PartPipe(PartDefinition definition, MultipartHolder holder, Function<PartPipe, PipeFlow> flowFn,
        CompoundTag tag) {
        super(definition, holder);
        connections = tag.getByte("c");
        prevConnections = connections;
        this.flow = flowFn.apply(this);
        flow.fromTag(tag.getCompound("flow"));
    }

    @Override
    public CompoundTag toTag() {
        CompoundTag tag = super.toTag();
        tag.putByte("c", connections);
        tag.put("flow", flow.toTag());
        return tag;
    }

    public PartPipe(PartDefinition definition, MultipartHolder holder, Function<PartPipe, PipeFlow> flowFn,
        NetByteBuf buf, IMsgReadCtx ctx) throws InvalidInputDataException {
        super(definition, holder);
        connections = buf.readByte();
        prevConnections = connections;
        flow = flowFn.apply(this);
        flow.readCreationData(buf, ctx);
    }

    @Override
    public void writeCreationData(NetByteBuf buffer, IMsgWriteCtx ctx) {
        super.writeCreationData(buffer, ctx);
        flow.writeCreationData(buffer, ctx);
    }

    @Override
    public VoxelShape getShape() {
        return SHAPES[connections];
    }

    @Override
    public PartModelKey getModelKey() {
        return new PipePartKey(definition, connections);
    }

    @Override
    public void onAdded(MultipartEventBus bus) {
        super.onAdded(bus);
        bus.addContextlessListener(this, PartTickEvent.class, this::tick);
    }

    protected void onNeighbourChange() {
        for (Direction dir : Direction.values()) {
            PartPipe oPipe = getNeighbourPipe(dir);
            if (this instanceof PartPipeWood && oPipe instanceof PartPipeWood) {
                disconnect(dir);
            } else if (
                oPipe != null || canConnect(dir) || (this instanceof PartPipeSided && ((PartPipeSided) this)
                    .currentDirection() == dir && ((PartPipeSided) this).canFaceDirection(dir))
            ) {
                connect(dir);
            } else {
                disconnect(dir);
            }
        }
    }

    public final long getWorldTime() {
        World world = getWorld();
        return world != null ? world.getTime() : 0;
    }

    protected boolean canConnect(Direction dir) {
        return flow.canConnect(dir);
    }

    @Nullable
    public final PartPipe getNeighbourPipe(Direction dir) {
        return getFirstNeighbourAttribute(PIPE_ATTRIBUTE, dir);
    }

    @Nonnull
    public <T> T getNeighbourAttribute(CombinableAttribute<T> attr, Direction dir) {
        return attr.get(getWorld(), getPos().offset(dir), SearchOptions.inDirection(dir));
    }

    @Nullable
    public <T> T getFirstNeighbourAttribute(Attribute<T> attr, Direction dir) {
        return attr.getFirstOrNull(getWorld(), getPos().offset(dir), SearchOptions.inDirection(dir));
    }

    public final World getWorld() {
        return holder.getContainer().getMultipartWorld();
    }

    public final BlockPos getPos() {
        return holder.getContainer().getMultipartPos();
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

    private final void receiveConnectionChange(NetByteBuf buf, IMsgReadCtx ctx) throws InvalidInputDataException {
        ctx.assertClientSide();
        connections = buf.readByte();
        refreshModel();
    }

    private final void writeConnectionChange(NetByteBuf buf, IMsgWriteCtx ctx) {
        ctx.assertServerSide();
        buf.writeByte(connections);
    }

    protected void refreshModel() {
        if (connections == prevConnections) {
            return;
        }
        prevConnections = connections;
        World w = holder.getContainer().getMultipartWorld();
        if (w instanceof ServerWorld) {
            sendNetworkUpdate(this, NET_ID_CONNECTION_CHANGE);
        } else if (w != null) {
            w.scheduleBlockRender(holder.getContainer().getMultipartPos());
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

    protected void tick() {
        flow.tick();
    }

    public DefaultedList<ItemStack> removeItemsForDrop() {
        DefaultedList<ItemStack> all = super.removeItemsForDrop();
        flow.removeItemsForDrop(all);
        return all;
    }
}
