package alexiil.mc.mod.pipes.part;

import com.mojang.serialization.Codec;

import net.minecraft.util.StringIdentifiable;

public enum FacadeSize implements StringIdentifiable {
    SLAB(8),
    THICK(4),
    THIN(2);

    public static final Codec<FacadeSize> CODEC = StringIdentifiable.createCodec(FacadeSize::values);

    public final int microVoxelSize;

    private FacadeSize(int microVoxelSize) {
        this.microVoxelSize = microVoxelSize;
    }

    public int voxelVolume() {
        return microVoxelSize * microVoxelSize * microVoxelSize;
    }

    @Override
    public String asString() {
        return name();
    }
}
