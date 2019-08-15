package alexiil.mc.mod.pipes.part;

public enum FacadeSize {
    SLAB(8),
    THICK(4),
    THIN(2);

    public final int microVoxelSize;

    private FacadeSize(int microVoxelSize) {
        this.microVoxelSize = microVoxelSize;
    }

    public int voxelVolume() {
        return microVoxelSize * microVoxelSize * microVoxelSize;
    }
}
