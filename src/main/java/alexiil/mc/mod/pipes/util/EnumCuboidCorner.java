package alexiil.mc.mod.pipes.util;

import org.joml.Vector3f;

import com.mojang.serialization.Codec;

import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Direction.Axis;
import net.minecraft.util.math.Direction.AxisDirection;
import net.minecraft.util.math.DirectionTransformation;

/** The 8 corners of a cuboid. The field name is in the form "X,Y,Z", for x is positive, y is positive, z is
 * positive. */
public enum EnumCuboidCorner implements StringIdentifiable {
    NNN,
    NNP,
    NPN,
    NPP,
    PNN,
    PNP,
    PPN,
    PPP;
    
    public static final Codec<EnumCuboidCorner> CODEC = StringIdentifiable.createCodec(EnumCuboidCorner::values);

    public final AxisDirection x, y, z;
    public final Direction xSide, ySide, zSide;

    private EnumCuboidCorner() {
        x = (ordinal() & 0b100) == 0 ? AxisDirection.NEGATIVE : AxisDirection.POSITIVE;
        y = (ordinal() & 0b010) == 0 ? AxisDirection.NEGATIVE : AxisDirection.POSITIVE;
        z = (ordinal() & 0b001) == 0 ? AxisDirection.NEGATIVE : AxisDirection.POSITIVE;

        xSide = Direction.from(Axis.X, x);
        ySide = Direction.from(Axis.Y, y);
        zSide = Direction.from(Axis.Z, z);
    }

    public static EnumCuboidCorner get(AxisDirection x, AxisDirection y, AxisDirection z) {
        return get(x == AxisDirection.POSITIVE, y == AxisDirection.POSITIVE, z == AxisDirection.POSITIVE);
    }

    public static EnumCuboidCorner get(boolean x, boolean y, boolean z) {
        return values()[(x ? 4 : 0) | (y ? 2 : 0) | (z ? 1 : 0)];
    }

    public Direction getDirection(Axis axis) {
        return Direction.from(axis, getAxisDirection(axis));
    }

    public AxisDirection getAxisDirection(Axis axis) {
        return axis == Axis.X ? x : axis == Axis.Y ? y : z;
    }

    public boolean touchesFace(Direction side) {
        return getDirection(side.getAxis()) == side;
    }

    public EnumCuboidCorner transform(DirectionTransformation transformation) {
        Vector3f vec = new Vector3f(x.offset(), y.offset(), z.offset());
        transformation.getMatrix().transform(vec);
        return get(vec.x > 0, vec.y > 0, vec.z > 0);
    }

    @Override
    public String asString() {
        return name();
    }
}
