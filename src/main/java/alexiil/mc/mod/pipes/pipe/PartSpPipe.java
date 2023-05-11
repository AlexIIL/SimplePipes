package alexiil.mc.mod.pipes.pipe;

import org.jetbrains.annotations.Nullable;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.class_8567;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.context.LootContext;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.function.BooleanBiFunction;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Direction.Axis;
import net.minecraft.util.math.DirectionTransformation;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.World;

import alexiil.mc.lib.net.IMsgReadCtx;
import alexiil.mc.lib.net.IMsgWriteCtx;
import alexiil.mc.lib.net.InvalidInputDataException;
import alexiil.mc.lib.net.NetByteBuf;
import alexiil.mc.lib.net.NetIdDataK;
import alexiil.mc.lib.net.ParentNetIdSingle;

import alexiil.mc.lib.attributes.AttributeList;
import alexiil.mc.lib.attributes.CombinableAttribute;
import alexiil.mc.lib.attributes.SearchOptions;

import alexiil.mc.lib.multipart.api.AbstractPart;
import alexiil.mc.lib.multipart.api.MultipartContainer;
import alexiil.mc.lib.multipart.api.MultipartEventBus;
import alexiil.mc.lib.multipart.api.MultipartHolder;
import alexiil.mc.lib.multipart.api.event.NeighbourUpdateEvent;
import alexiil.mc.lib.multipart.api.event.PartAddedEvent;
import alexiil.mc.lib.multipart.api.event.PartRemovedEvent;
import alexiil.mc.lib.multipart.api.event.PartTickEvent;
import alexiil.mc.lib.multipart.api.event.PartTransformEvent;
import alexiil.mc.lib.multipart.api.render.PartModelKey;

public class PartSpPipe extends AbstractPart implements ISimplePipe {

    public static final VoxelShape CENTER_SHAPE;
    public static final VoxelShape[] FACE_SHAPES;
    public static final VoxelShape[] FACE_CENTER_SHAPES;
    public static final VoxelShape[] SHAPES;

    public static final ParentNetIdSingle<PartSpPipe> NET_PARENT;
    public static final NetIdDataK<PartSpPipe> ID_FLOW;

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

        NET_PARENT = AbstractPart.NET_ID.subType(PartSpPipe.class, "simple_pipes:pipe_part");
        ID_FLOW = NET_PARENT.idData("flow").toClientOnly();
        ID_FLOW.setReceiver(PartSpPipe::receiveFlow);
    }

    public final PipeSpDef definition;
    public final PipeSpFlow flow;
    public final PipeSpBehaviour behaviour;

    public byte connections;

    public PartSpPipe(PipeSpDef definition, MultipartHolder holder) {
        super(definition, holder);
        this.definition = definition;
        this.flow = definition.createFlow(this);
        this.behaviour = definition.createBehaviour(this);
    }

    public void fromNbt(NbtCompound nbt) {
        connections = (byte) (nbt.getByte("c") & 0b111_111);
        flow.fromTag(nbt.getCompound("f"));
        behaviour.fromNbt(nbt.getCompound("b"));
    }

    @Override
    public NbtCompound toTag() {
        NbtCompound nbt = super.toTag();
        nbt.putByte("c", connections);
        nbt.put("f", flow.toTag());
        nbt.put("b", behaviour.toNbt());
        return nbt;
    }

    @Override
    public void writeCreationData(NetByteBuf buffer, IMsgWriteCtx ctx) {
        buffer.writeNbt(toTag());
    }

    @Override
    public void readRenderData(NetByteBuf buffer, IMsgReadCtx ctx) throws InvalidInputDataException {
        fromNbt(buffer.readNbt());

        refreshModel();
    }

    @Override
    public void writeRenderData(NetByteBuf buffer, IMsgWriteCtx ctx) {
        buffer.writeNbt(toTag());
    }

    @Override
    public PartModelKey getModelKey() {
        return behaviour.createModelState();
    }

    @Override
    public VoxelShape getShape() {
        return CENTER_SHAPE;
    }

    @Override
    public VoxelShape getCollisionShape() {
        return SHAPES[connections & 0b111111];
    }

    @Override
    protected BlockState getClosestBlockState() {
        return Blocks.STONE.getDefaultState();
    }

    @Override
    public float calculateBreakingDelta(PlayerEntity player) {
        return calcBreakingDelta(player, Blocks.GLASS.getDefaultState(), 0.5f);
    }

    @Override
    public ItemStack getPickStack(@Nullable BlockHitResult hitResult) {
        return definition.getPickStack();
    }

    @Override
    protected void spawnBreakParticles() {
        spawnBreakParticles(definition.pipeBlock.getDefaultState());
    }

    @Override
    public boolean spawnHitParticle(Direction side) {
        spawnHitParticle(side, definition.pipeBlock.getDefaultState());
        return true;
    }

    @Override
    public void addAllAttributes(AttributeList<?> list) {
        super.addAllAttributes(list);

        Direction pipeSide = list.getTargetSide();
        if (pipeSide == null) {
            // Pipes only work with physical connections
            return;
        }
        VoxelShape pipeShape = isConnected(pipeSide) ? FACE_CENTER_SHAPES[pipeSide.ordinal()] : CENTER_SHAPE;

        if (
            definition.isExtraction && behaviour instanceof PipeSpBehaviourSided
                && ((PipeSpBehaviourSided) behaviour).currentDirection() == pipeSide
        ) {
            list.offer(getFlow().getInsertable(list.getSearchDirection()), pipeShape);
        } else {
            list.offer(definition.getEmptyExtractable(), pipeShape);
        }

        list.offer(this, CENTER_SHAPE);
    }

    @Override
    public void onAdded(MultipartEventBus bus) {
        super.onAdded(bus);
        bus.addContextlessListener(this, PartTickEvent.class, this::tick);
        bus.addListener(this, NeighbourUpdateEvent.class, this::onNeighbourUpdate);
        bus.addListener(this, PartAddedEvent.class, e -> updateConnections());
        bus.addListener(this, PartRemovedEvent.class, e -> updateConnections());
        bus.addListener(this, PartTransformEvent.class, e -> transform(e.transformation));
    }

    public void tick() {
        behaviour.tick();
        flow.tick();
        World w = getPipeWorld();
        if (w != null) {
            w.markDirty(getPipePos());
        }
    }

    private void onNeighbourUpdate(NeighbourUpdateEvent event) {
        updateConnections();
    }

    private void updateConnections() {
        for (Direction dir : Direction.values()) {
            ISimplePipe oPipe = getNeighbourPipe(dir);
            if (definition.isExtraction && oPipe != null && oPipe.getDefinition().isExtraction) {
                disconnect(dir);
            } else if (oPipe != null) {
                if ((getFlow() instanceof PipeSpFlowItem) == (oPipe.getFlow() instanceof PipeSpFlowItem)) {
                    connect(dir);
                } else {
                    disconnect(dir);
                }
            } else if (!hasConnectionOverlap(dir, holder.getContainer()) && canConnect(dir)) {
                connect(dir);
            } else {
                disconnect(dir);
            }
        }
    }

    private void transform(DirectionTransformation transform) {
        transformConnections(transform);
        behaviour.transform(transform);
        flow.transform(transform);
    }

    @Override
    public ActionResult onUse(PlayerEntity player, Hand hand, BlockHitResult hit) {
        return behaviour.onUse(player, hand, hit);
    }

    @Override
    public void addDrops(ItemDropTarget target, class_8567 context) {
        super.addDrops(target, context);
        flow.addDrops(target, context);
        behaviour.addDrops(target, context);
    }

    @Override
    public BlockPos getPipePos() {
        return holder.getContainer().getMultipartPos();
    }

    @Override
    public World getPipeWorld() {
        return holder.getContainer().getMultipartWorld();
    }

    @Override
    public PipeSpDef getDefinition() {
        return definition;
    }

    @Override
    public PipeSpFlow getFlow() {
        return flow;
    }

    @Override
    public ISimplePipe getNeighbourPipe(Direction dir) {
        if (hasConnectionOverlap(dir, holder.getContainer())) {
            return null;
        }

        BlockEntity neighbour = holder.getContainer().getNeighbourBlockEntity(dir);
        if (neighbour instanceof ISimplePipe || neighbour == null) {
            return (ISimplePipe) neighbour;
        }
        MultipartContainer container = MultipartContainer.ATTRIBUTE.getFirstOrNull(getPipeWorld(), neighbour.getPos());
        if (container == null || hasConnectionOverlap(dir.getOpposite(), container)) {
            return null;
        }
        return container.getFirstPart(ISimplePipe.class);
    }

    public static boolean hasConnectionOverlap(Direction dir, MultipartContainer container) {
        return !container.getAllParts(part -> doesOverlapConnection(dir, part)).isEmpty();
    }

    private static boolean doesOverlapConnection(Direction dir, AbstractPart part) {
        return VoxelShapes.matchesAnywhere(part.getShape(), FACE_SHAPES[dir.ordinal()], BooleanBiFunction.AND);
    }

    @Override
    public <T> T getNeighbourAttribute(CombinableAttribute<T> attr, Direction dir) {
        if (hasConnectionOverlap(dir, holder.getContainer())) {
            return attr.defaultValue;
        }

        VoxelShape shape = SHAPES[(1 << dir.ordinal()) | (1 << dir.getOpposite().ordinal())];
        return attr.get(getPipeWorld(), getPipePos().offset(dir), SearchOptions.inDirectionalVoxel(dir, shape));
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

    private void transformConnections(DirectionTransformation transform) {
        byte newConnections = 0;
        for (Direction dir : Direction.values()) {
            if (isConnected(dir)) {
                Direction newDir = transform.map(dir);
                newConnections |= 1 << newDir.ordinal();
            }
        }
        connections = newConnections;
    }

    public void refreshModel() {
        if (holder.getContainer().isClientWorld()) {
            // This can be called on the server too, but this way everything's done with a single packet
            redrawIfChanged();
        } else {
            sendNetworkUpdate(this, NET_RENDER_DATA);
        }
        recalculateShape();
    }

    protected boolean canConnect(Direction dir) {
        return behaviour.canConnect(dir);
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

    @Override
    public void sendFlowPacket(NbtCompound nbt) {
        this.sendNetworkUpdate(this, ID_FLOW, (obj, buf, ctx) -> {
            buf.writeNbt(nbt);
        });
    }

    private void receiveFlow(NetByteBuf buffer, IMsgReadCtx ctx) {
        flow.fromClientTag(buffer.readNbt());
    }
}
