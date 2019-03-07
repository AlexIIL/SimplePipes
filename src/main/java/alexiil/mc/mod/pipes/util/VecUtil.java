package alexiil.mc.mod.pipes.util;

import net.minecraft.util.math.Direction.Axis;
import net.minecraft.util.math.Vec3d;

public enum VecUtil {
    ;

    public static Vec3d min(Vec3d a, Vec3d b) {
        return new Vec3d(Math.min(a.x, b.x), Math.min(a.y, b.y), Math.min(a.z, b.z));
    }

    public static Vec3d max(Vec3d a, Vec3d b) {
        return new Vec3d(Math.max(a.x, b.x), Math.max(a.y, b.y), Math.max(a.z, b.z));
    }

    public static double interp(double interp, double from, double to) {
        return from * (1 - interp) + to * interp;
    }

    public static Vec3d replaceValue(Vec3d v, Axis axis, double with) {
        switch (axis) {
            case X:
                return new Vec3d(with, v.y, v.z);
            case Y:
                return new Vec3d(v.x, with, v.z);
            case Z:
                return new Vec3d(v.x, v.y, with);
            default: {
                throw new IllegalStateException("Unknown axis: " + axis);
            }
        }
    }
}
