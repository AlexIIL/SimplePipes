package alexiil.mc.mod.pipes.util;

import java.util.EnumMap;

import org.jetbrains.annotations.Nullable;

import com.mojang.serialization.Codec;

import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Direction.Axis;
import net.minecraft.util.math.Direction.AxisDirection;
import net.minecraft.util.math.DirectionTransformation;

public enum EnumCuboidEdge implements StringIdentifiable {
    X_NN(Axis.X, false, false),
    X_NP(Axis.X, false, true),
    X_PN(Axis.X, true, false),
    X_PP(Axis.X, true, true),

    Y_NN(Axis.Y, false, false),
    Y_NP(Axis.Y, false, true),
    Y_PN(Axis.Y, true, false),
    Y_PP(Axis.Y, true, true),

    Z_NN(Axis.Z, false, false),
    Z_NP(Axis.Z, false, true),
    Z_PN(Axis.Z, true, false),
    Z_PP(Axis.Z, true, true);
    
    public static final Codec<EnumCuboidEdge> CODEC = StringIdentifiable.createCodec(EnumCuboidEdge::values);

    /** Used to look up an edge based on the two sides it sits between. */
    private static final EnumCuboidEdge[] TOUCHING_SIDES = new EnumCuboidEdge[36];

    static {
        for (EnumCuboidEdge part : EnumCuboidEdge.values()) {
            assert TOUCHING_SIDES[part.touchingSide1.ordinal() + part.touchingSide2.ordinal() * 6] == null;
            assert TOUCHING_SIDES[part.touchingSide2.ordinal() + part.touchingSide1.ordinal() * 6] == null;

            TOUCHING_SIDES[part.touchingSide1.ordinal() + part.touchingSide2.ordinal() * 6] = part;
            TOUCHING_SIDES[part.touchingSide2.ordinal() + part.touchingSide1.ordinal() * 6] = part;
        }

        for (EnumCuboidEdge part : EnumCuboidEdge.values()) {
            Direction axisNeg = Direction.from(part.axis, AxisDirection.NEGATIVE);
            Direction axisPos = Direction.from(part.axis, AxisDirection.POSITIVE);

            part.connectableParts[0] = get(axisNeg, part.touchingSide1);
            part.connectableParts[1] = get(axisNeg, part.touchingSide2);
            part.connectableParts[2] = get(axisPos, part.touchingSide1);
            part.connectableParts[3] = get(axisPos, part.touchingSide2);

            part.neighbourConnectableParts.put(
                axisNeg, new EnumCuboidEdge[]{part, get(axisPos, part.touchingSide1), get(axisPos, part.touchingSide2)}
            );
            part.neighbourConnectableParts.put(
                axisPos, new EnumCuboidEdge[]{part, get(axisNeg, part.touchingSide1), get(axisNeg, part.touchingSide2)}
            );
            part.neighbourConnectableParts.put(
                part.touchingSide1, new EnumCuboidEdge[]{
                    get(part.touchingSide1.getOpposite(), part.touchingSide2),
                    get(axisNeg, part.touchingSide1.getOpposite()), get(axisNeg, part.touchingSide2),
                    get(axisPos, part.touchingSide1.getOpposite()), get(axisPos, part.touchingSide2)
                }
            );
            part.neighbourConnectableParts.put(
                part.touchingSide2, new EnumCuboidEdge[]{
                    get(part.touchingSide2.getOpposite(), part.touchingSide1),
                    get(axisNeg, part.touchingSide2.getOpposite()), get(axisNeg, part.touchingSide1),
                    get(axisPos, part.touchingSide2.getOpposite()), get(axisPos, part.touchingSide1)
                }
            );
        }
    }

    public final Axis axis;
    public final Direction touchingSide1, touchingSide2;

    /** Every {@link EnumCuboidEdge} that this can connect to within the same {@link BlockPos block space}. */
    private final EnumCuboidEdge[] connectableParts = new EnumCuboidEdge[4];

    /** Every {@link EnumCuboidEdge} that this can connect to in a neighbouring block space. */
    private final EnumMap<Direction, EnumCuboidEdge[]> neighbourConnectableParts = new EnumMap<>(Direction.class);

    EnumCuboidEdge(Axis axis, boolean previousAxisIsPositive, boolean nextAxisIsPositive) {
        this.axis = axis;

        Axis prevAxis = Axis.values()[(axis.ordinal() + 2) % 3];
        this.touchingSide1 = Direction.from(prevAxis, toAxisDirection(previousAxisIsPositive));

        Axis nextAxis = Axis.values()[(axis.ordinal() + 1) % 3];
        this.touchingSide2 = Direction.from(nextAxis, toAxisDirection(nextAxisIsPositive));
    }

    private static AxisDirection toAxisDirection(boolean positive) {
        return positive ? AxisDirection.POSITIVE : AxisDirection.NEGATIVE;
    }

    public static EnumCuboidEdge get(Axis axis, boolean previousAxisIsPositive, boolean nextAxisIsPositive) {
        final boolean a0 = previousAxisIsPositive;
        final boolean a1 = nextAxisIsPositive;
        return switch (axis) {
            case X -> a0 ? a1 ? X_PP : X_PN : a1 ? X_NP : X_NN;
            case Y -> a0 ? a1 ? Y_PP : Y_PN : a1 ? Y_NP : Y_NN;
            case Z -> a0 ? a1 ? Z_PP : Z_PN : a1 ? Z_NP : Z_NN;
        };
    }

    /** Gets the edge joining two sides.
     *
     * @param touchingSide1 the first side.
     * @param touchingSide2 the second side.
     * @return the edge joining two sides, or {@code null} if the two sides have no edge in common or are the same side. */
    public static @Nullable EnumCuboidEdge get(Direction touchingSide1, Direction touchingSide2) {
        return TOUCHING_SIDES[touchingSide1.ordinal() + touchingSide2.ordinal() * 6];
    }

    public EnumCuboidEdge transform(DirectionTransformation transformation) {
        EnumCuboidEdge transformed = get(transformation.map(touchingSide1), transformation.map(touchingSide2));
        assert transformed != null;
        return transformed;
    }

    @Override
    public String asString() {
        return name();
    }
}
