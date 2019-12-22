package alexiil.mc.mod.pipes.util;

public final class BitUtil {
    private BitUtil() {}

    public static int roundUpToPowerOf2(int i) {
        i--;
        i |= i >> 1;
        i |= i >> 2;
        i |= i >> 4;
        i |= i >> 8;
        i |= i >> 16;
        i++;
        return i;
    }
}
