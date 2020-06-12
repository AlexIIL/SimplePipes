package alexiil.mc.mod.pipes.part;

import java.util.Arrays;
import java.util.Locale;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.function.BooleanBiFunction;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Direction.Axis;
import net.minecraft.util.math.Direction.AxisDirection;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;

import alexiil.mc.lib.net.NetByteBuf;
import alexiil.mc.mod.pipes.util.EnumCuboidCorner;
import alexiil.mc.mod.pipes.util.EnumCuboidEdge;
import alexiil.mc.mod.pipes.util.ShapeUtil;
import alexiil.mc.mod.pipes.util.TagUtil;
import alexiil.mc.mod.pipes.util.VecUtil;

public abstract class FacadeShape {

    private static final String NBT_KEY_CORNER = "corner";
    private static final String NBT_KEY_EDGE = "edge";
    private static final String NBT_KEY_SIDE = "side";

    private static final String NBT_TYPE_SIDED = "Sided";
    private static final String NBT_TYPE_STRIP = "Strip";
    private static final String NBT_TYPE_CORNER = "Corner";

    private static final int SHAPE_COUNT = 3 * (12 + 8 + 2 * 6);
    private static final FacadeShape[] ALL_SHAPES;
    private static final FacadeShape[] ITEM_SHAPES;
    private static final int VALUES_BIT_COUNT;

    static {
        ALL_SHAPES = new FacadeShape[SHAPE_COUNT];
        VALUES_BIT_COUNT = MathHelper.log2DeBruijn(SHAPE_COUNT);

        ITEM_SHAPES = new FacadeShape[3 * (1 + 1 + 2)];
    }

    public final int shapeOrdinal;
    public final VoxelShape shape;
    public final Vec3d centerOfMass;

    /** The volume of a block that this facade takes up. (Where a block is 16x16x16, or 4096). */
    public final int recipeMicroVoxelVolume;

    private FacadeShape(int shapeOrdinal, VoxelShape shape, Vec3d centerOfMass, int recipeMicroVoxelVolume) {
        this.shapeOrdinal = shapeOrdinal;
        this.shape = shape;
        this.centerOfMass = centerOfMass;
        this.recipeMicroVoxelVolume = recipeMicroVoxelVolume;
        ALL_SHAPES[shapeOrdinal] = this;
    }

    private FacadeShape(int shapeOrdinal, VoxelShape shape, int recipeMicroVoxelVolume) {
        this(shapeOrdinal, shape, shape.getBoundingBox().getCenter(), recipeMicroVoxelVolume);
    }

    public static FacadeShape[] getAllShapes() {
        return Arrays.copyOf(ALL_SHAPES, ALL_SHAPES.length);
    }

    public static FacadeShape[] getAllItemShapes() {
        return Arrays.copyOf(ITEM_SHAPES, ITEM_SHAPES.length);
    }

    public abstract CompoundTag toTag();

    public static FacadeShape fromTag(CompoundTag tag) {
        String type = tag.getString("type").toLowerCase(Locale.ROOT);
        FacadeSize size = TagUtil.readEnum(tag.get("size"), FacadeSize.class, FacadeSize.SLAB);
        // We're fairly forgiving about what we can load from
        if (type.startsWith("str")) {
            return Strip.get(
                size, TagUtil.readEnum(tag.get(NBT_KEY_EDGE), EnumCuboidEdge.class, EnumCuboidEdge.X_NN)
            );
        } else if (type.startsWith("c")) {
            return Corner.get(
                size, TagUtil.readEnum(tag.get(NBT_KEY_CORNER), EnumCuboidCorner.class, EnumCuboidCorner.NNN)
            );
        } else {
            return Sided.get(
                size, TagUtil.readEnum(tag.get(NBT_KEY_SIDE), Direction.class, Direction.UP), tag.getBoolean(
                    "hollow"
                )
            );
        }
    }

    public final void toBuffer(NetByteBuf buffer) {
        buffer.writeFixedBits(shapeOrdinal, VALUES_BIT_COUNT);
    }

    public static FacadeShape fromBuffer(NetByteBuf buffer) {
        return ALL_SHAPES[buffer.readFixedBits(VALUES_BIT_COUNT)];
    }

    @Override
    public final boolean equals(Object obj) {
        return this == obj;
    }

    @Override
    public final int hashCode() {
        return System.identityHashCode(this);
    }

    public abstract FacadeShape[] getPlacementVariants();

    public abstract FacadeSize getSize();

    public VoxelShape getVoxelShape() {
        return shape;
    }

    public Vec3d getCenter() {
        return centerOfMass;
    }

    public int getRecipeMicroVoxelVolume() {
        return recipeMicroVoxelVolume;
    }

    public static final class Sided extends FacadeShape {

        private static final Sided[] values;

        static {
            values = new Sided[6 * 2 * 3];
            int index = 0;
            for (FacadeSize size : FacadeSize.values()) {
                Sided[] solidVariants = new Sided[6];
                Sided[] hollowVariants = new Sided[6];
                for (Direction side : Direction.values()) {
                    solidVariants[side.ordinal()] = values[index] = new Sided(index, side, false, size, solidVariants);
                    index++;
                    hollowVariants[side.ordinal()] = values[index] = new Sided(index, side, true, size, hollowVariants);
                    index++;
                }
            }
            ITEM_SHAPES[0] = get(FacadeSize.SLAB, Direction.WEST, false);
            ITEM_SHAPES[1] = get(FacadeSize.SLAB, Direction.WEST, true);
            ITEM_SHAPES[2] = get(FacadeSize.THICK, Direction.WEST, false);
            ITEM_SHAPES[3] = get(FacadeSize.THICK, Direction.WEST, true);
            ITEM_SHAPES[4] = get(FacadeSize.THIN, Direction.WEST, false);
            ITEM_SHAPES[5] = get(FacadeSize.THIN, Direction.WEST, true);
        }

        public final int sidedOrdinal;

        public final Direction side;
        public final boolean hollow;
        public final FacadeSize size;

        private final Sided[] placementVariants;

        private Sided(int ordinal, Direction side, boolean hollow, FacadeSize size, Sided[] placementVariants) {
            super(ordinal, makeShape(side, hollow, size), makeCentreOfMass(side, size), 16 * 16 * size.microVoxelSize);
            this.sidedOrdinal = ordinal;
            this.side = side;
            this.hollow = hollow;
            this.size = size;
            this.placementVariants = placementVariants;
        }

        private static VoxelShape makeShape(Direction side, boolean hollow, FacadeSize size) {
            VoxelShape shape = ShapeUtil.cuboid16(0, 0, 0, 16, size.microVoxelSize, 16);
            if (hollow) {
                shape = VoxelShapes.combine(
                    shape, ShapeUtil.cuboid16(4, 0, 4, 12, size.microVoxelSize, 12), BooleanBiFunction.ONLY_FIRST
                );
            }
            return ShapeUtil.rotate90(shape, Direction.DOWN, side);
        }

        private static Vec3d makeCentreOfMass(Direction side, FacadeSize size) {
            Vec3d sideVec = Vec3d.of(side.getVector());
            return VecUtil.VEC_HALF.add(sideVec.multiply(0.5 - size.microVoxelSize / 32.0));
        }

        public static Sided get(FacadeSize size, Direction side, boolean hollow) {
            if (side == null) throw new NullPointerException(NBT_KEY_SIDE);
            if (size == null) throw new NullPointerException("size");
            final int startIndex;
            switch (size) {
                case SLAB:
                    startIndex = 0;
                    break;
                case THICK:
                    startIndex = 12;
                    break;
                case THIN:
                    startIndex = 24;
                    break;
                default:
                    throw new IllegalStateException("Unknown FacadeSize " + size + "!");
            }
            return values[startIndex + side.ordinal() * 2 + (hollow ? 1 : 0)];
        }

        public static Sided[] values() {
            return Arrays.copyOf(values, values.length);
        }

        @Override
        public CompoundTag toTag() {
            CompoundTag tag = new CompoundTag();
            tag.putString("type", NBT_TYPE_SIDED);
            tag.put(NBT_KEY_SIDE, TagUtil.writeEnum(side));
            tag.put("size", TagUtil.writeEnum(size));
            tag.putBoolean("hollow", hollow);
            return tag;
        }

        @Override
        public FacadeSize getSize() {
            return size;
        }

        public Direction getSide() {
            return side;
        }

        public boolean isHollow() {
            return hollow;
        }

        public Sided withSize(FacadeSize newSize) {
            return get(newSize, side, hollow);
        }

        public Sided withSide(Direction newSide) {
            return get(size, newSide, hollow);
        }

        public Sided withHollow(boolean newHollow) {
            return get(size, side, newHollow);
        }

        @Override
        public Sided[] getPlacementVariants() {
            return Arrays.copyOf(placementVariants, placementVariants.length);
        }
    }

    public static final class Strip extends FacadeShape {
        private static final int CUBOID_EDGES = EnumCuboidEdge.values().length;

        private static final Strip[] values;

        static {
            values = new Strip[3 * CUBOID_EDGES];
            int index = 0;
            for (FacadeSize size : FacadeSize.values()) {
                Strip[] variants = new Strip[12];
                for (EnumCuboidEdge edge : EnumCuboidEdge.values()) {
                    Strip strip = new Strip(index, size, edge, variants);
                    variants[edge.ordinal()] = values[index] = strip;
                    index++;
                }
            }
            ITEM_SHAPES[6] = get(FacadeSize.SLAB, EnumCuboidEdge.Z_NN);
            ITEM_SHAPES[7] = get(FacadeSize.THICK, EnumCuboidEdge.Z_NN);
            ITEM_SHAPES[8] = get(FacadeSize.THIN, EnumCuboidEdge.Z_NN);
        }

        public final int stripOrdinal;

        public final FacadeSize size;
        public final EnumCuboidEdge edge;

        private final Strip[] placementVariants;

        private Strip(int ordinal, FacadeSize size, EnumCuboidEdge edge, Strip[] placementVariants) {
            super(Sided.values.length + ordinal, makeShape(size, edge), 16 * size.microVoxelSize * size.microVoxelSize);
            this.stripOrdinal = ordinal;
            this.size = size;
            this.edge = edge;
            this.placementVariants = placementVariants;
        }

        private static VoxelShape makeShape(FacadeSize size, EnumCuboidEdge edge) {
            Vec3d min = Vec3d.ZERO;
            Vec3d max = VecUtil.replaceValue(Vec3d.ZERO, edge.axis, 1);

            double offset = size.microVoxelSize / 16.0;

            Axis axisA = edge.touchingSide1.getAxis();
            boolean positiveA = edge.touchingSide1.getDirection() == AxisDirection.POSITIVE;

            min = VecUtil.replaceValue(min, axisA, positiveA ? 1 - offset : 0);
            max = VecUtil.replaceValue(max, axisA, positiveA ? 1 : offset);

            Axis axisB = edge.touchingSide2.getAxis();
            boolean positiveB = edge.touchingSide2.getDirection() == AxisDirection.POSITIVE;

            min = VecUtil.replaceValue(min, axisB, positiveB ? 1 - offset : 0);
            max = VecUtil.replaceValue(max, axisB, positiveB ? 1 : offset);

            return VoxelShapes.cuboid(new Box(min, max));
        }

        public static Strip get(FacadeSize size, EnumCuboidEdge edge) {
            return values[size.ordinal() * CUBOID_EDGES + edge.ordinal()];
        }

        public static Strip[] values() {
            return Arrays.copyOf(values, values.length);
        }

        @Override
        public CompoundTag toTag() {
            CompoundTag tag = new CompoundTag();
            tag.putString("type", NBT_TYPE_STRIP);
            tag.put(NBT_KEY_EDGE, TagUtil.writeEnum(edge));
            tag.put("size", TagUtil.writeEnum(size));
            return tag;
        }

        @Override
        public FacadeSize getSize() {
            return size;
        }

        public EnumCuboidEdge getEdge() {
            return edge;
        }

        public Strip withSize(FacadeSize newSize) {
            return get(newSize, edge);
        }

        public Strip withEdge(EnumCuboidEdge newEdge) {
            return get(size, newEdge);
        }

        @Override
        public Strip[] getPlacementVariants() {
            return Arrays.copyOf(placementVariants, placementVariants.length);
        }
    }

    public static final class Corner extends FacadeShape {
        private static final int CUBOID_CORNERS = EnumCuboidCorner.values().length;

        private static final Corner[] values;

        static {
            values = new Corner[3 * CUBOID_CORNERS];
            int index = 0;
            for (FacadeSize size : FacadeSize.values()) {
                Corner[] variants = new Corner[8];
                for (EnumCuboidCorner corner : EnumCuboidCorner.values()) {
                    variants[corner.ordinal()] = values[index] = new Corner(index, size, corner, variants);
                    index++;
                }
            }
            ITEM_SHAPES[9] = get(FacadeSize.SLAB, EnumCuboidCorner.NNN);
            ITEM_SHAPES[10] = get(FacadeSize.THICK, EnumCuboidCorner.NNN);
            ITEM_SHAPES[11] = get(FacadeSize.THIN, EnumCuboidCorner.NNN);
        }

        public final int cornerOrdinal;

        // TODO: Decide whether or not to allow thick+thin corners!
        public final FacadeSize size;
        public final EnumCuboidCorner corner;

        private final Corner[] placementVariants;

        private Corner(int ordinal, FacadeSize size, EnumCuboidCorner corner, Corner[] placementVariants) {
            super(
                Sided.values.length + Strip.values.length + ordinal, makeShape(size, corner), //
                makeCentreOfMass(size, corner), size.voxelVolume()
            );
            this.cornerOrdinal = ordinal;
            this.size = size;
            this.corner = corner;
            this.placementVariants = placementVariants;
        }

        private static VoxelShape makeShape(FacadeSize size, EnumCuboidCorner corner) {
            boolean x = corner.x == AxisDirection.POSITIVE;
            boolean y = corner.y == AxisDirection.POSITIVE;
            boolean z = corner.z == AxisDirection.POSITIVE;
            int x0 = x ? 16 - size.microVoxelSize : 0;
            int y0 = y ? 16 - size.microVoxelSize : 0;
            int z0 = z ? 16 - size.microVoxelSize : 0;
            int x1 = x ? 16 : size.microVoxelSize;
            int y1 = y ? 16 : size.microVoxelSize;
            int z1 = z ? 16 : size.microVoxelSize;
            return ShapeUtil.cuboid16(x0, y0, z0, x1, y1, z1);
        }

        private static Vec3d makeCentreOfMass(FacadeSize size, EnumCuboidCorner corner) {
            boolean x = corner.x == AxisDirection.POSITIVE;
            boolean y = corner.y == AxisDirection.POSITIVE;
            boolean z = corner.z == AxisDirection.POSITIVE;
            double low = size.microVoxelSize / 32.0;
            double high = 1 - low;
            return new Vec3d(x ? high : low, y ? high : low, z ? high : low);
        }

        public static Corner get(FacadeSize size, EnumCuboidCorner corner) {
            return values[size.ordinal() * CUBOID_CORNERS + corner.ordinal()];
        }

        public static Corner[] values() {
            return Arrays.copyOf(values, values.length);
        }

        @Override
        public CompoundTag toTag() {
            CompoundTag tag = new CompoundTag();
            tag.putString("type", NBT_TYPE_CORNER);
            tag.put(NBT_KEY_CORNER, TagUtil.writeEnum(corner));
            tag.put("size", TagUtil.writeEnum(size));
            return tag;
        }

        @Override
        public FacadeSize getSize() {
            return size;
        }

        public EnumCuboidCorner getCorner() {
            return corner;
        }

        public Corner withSize(FacadeSize newSize) {
            return get(newSize, corner);
        }

        public Corner withCorner(EnumCuboidCorner newCorner) {
            return get(size, newCorner);
        }

        @Override
        public Corner[] getPlacementVariants() {
            return Arrays.copyOf(placementVariants, placementVariants.length);
        }
    }
}
