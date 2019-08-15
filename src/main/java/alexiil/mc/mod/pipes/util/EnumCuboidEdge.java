package alexiil.mc.mod.pipes.util;

import java.util.EnumMap;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Direction.Axis;
import net.minecraft.util.math.Direction.AxisDirection;

public enum EnumCuboidEdge {
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

    static {
        for (EnumCuboidEdge part : EnumCuboidEdge.values()) {

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
        switch (axis) {
            case X: {
                return a0 ? a1 ? X_PP : X_PN : a1 ? X_NP : X_NN;
            }
            case Y: {
                return a0 ? a1 ? Y_PP : Y_PN : a1 ? Y_NP : Y_NN;
            }
            case Z: {
                return a0 ? a1 ? Z_PP : Z_PN : a1 ? Z_NP : Z_NN;
            }
            default: {
                throw new IllegalStateException("Unknown Axis " + axis);
            }
        }
    }
}
