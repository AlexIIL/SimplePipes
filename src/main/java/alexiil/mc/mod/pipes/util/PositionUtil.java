package alexiil.mc.mod.pipes.util;

import javax.annotation.Nullable;

import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Direction.Axis;
import net.minecraft.util.math.Vec3d;

public final class PositionUtil {
    private PositionUtil() {}

    public static Vec3d rotateVec(Vec3d vec, Direction from, Direction to) {
        return rotateVec(vec, from, to, null, 0.5, 0.5, 0.5);
    }

    public static Vec3d rotateVec(Vec3d vec, Direction from, Direction to, @Nullable Axis axis) {
        return rotateVec(vec, from, to, axis, 0.5, 0.5, 0.5);
    }

    public static Vec3d rotateVec(Vec3d vec, Direction from, Direction to, double ox, double oy, double oz) {
        return rotateVec(vec, from, to, null, ox, oy, oz);
    }

    public static Vec3d rotateVec(Vec3d vec, Direction from, Direction to, @Nullable Axis axis, double ox, double oy,
        double oz) {
        if (from == to) {
            return vec;
        }

        double x = vec.x - ox;
        double y = vec.y - oy;
        double z = vec.z - oz;

        // Copied directly from MutableQuad and MutableVertex
        switch (from.getAxis()) {
            case X: {
                int mult = from.getOffsetX();
                switch (to.getAxis()) {
                    case X: {
                        if (axis != Axis.Y) {
                            x = -x;
                            y = -y;
                        } else {
                            x = -x;
                            z = -z;
                        }
                        break;
                    }
                    case Y:
                        double zm = mult * to.getOffsetY();
                        double y0 = x * zm;
                        x = y * -zm;
                        y = y0;
                        break;
                    case Z:
                        float xm = mult * to.getOffsetZ();
                        double z0 = x * xm;
                        x = z * -xm;
                        z = z0;
                        break;
                    default: {
                        throw new IllegalArgumentException("Unknown Axis " + to.getAxis());
                    }
                }
                break;
            }
            case Y: {
                int mult = from.getOffsetY();
                switch (to.getAxis()) {
                    case X:
                        double xm = mult * to.getOffsetX();
                        double y0 = x * -xm;
                        x = y * xm;
                        y = y0;
                        break;
                    case Y: {
                        if (axis != Axis.Z) {
                            y = -y;
                            z = -z;
                        } else {
                            x = -x;
                            y = -y;
                        }
                        break;
                    }
                    case Z:
                        double ym = mult * to.getOffsetZ();
                        double z0 = y * ym;
                        y = z * -ym;
                        z = z0;
                        break;
                    default: {
                        throw new IllegalArgumentException("Unknown Axis " + to.getAxis());
                    }
                }
                break;
            }
            case Z: {
                int mult = -from.getOffsetZ();
                switch (to.getAxis()) {
                    case X:
                        double xm = mult * to.getOffsetX();
                        double z0 = x * xm;
                        x = z * -xm;
                        z = z0;
                        break;
                    case Y:
                        double ym = mult * to.getOffsetY();
                        double z1 = y * ym;
                        y = z * -ym;
                        z = z1;
                        break;
                    case Z: {
                        if (axis != Axis.Y) {
                            y = -y;
                            z = -z;
                        } else {
                            x = -x;
                            z = -z;
                        }
                        break;
                    }
                    default: {
                        throw new IllegalArgumentException("Unknown Axis " + to.getAxis());
                    }
                }
                break;
            }
            default: {
                throw new IllegalArgumentException("Unknown Axis " + from.getAxis());
            }
        }

        return new Vec3d(x + ox, y + oy, z + oz);
    }
}
