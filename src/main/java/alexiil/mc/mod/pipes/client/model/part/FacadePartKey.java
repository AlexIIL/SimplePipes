package alexiil.mc.mod.pipes.client.model.part;

import java.util.Objects;

import net.minecraft.block.BlockState;

import alexiil.mc.lib.multipart.api.render.PartModelKey;
import alexiil.mc.mod.pipes.part.FacadeShape;

public class FacadePartKey extends PartModelKey {
    public final FacadeShape shape;
    public final BlockState state;
    public final int insetSides;
    private final int hash;

    public FacadePartKey(FacadeShape shape, BlockState state, int insetSides) {
        this.shape = shape;
        this.state = state;
        this.insetSides = insetSides;
        this.hash = Objects.hash(shape, state, insetSides);
    }

    @Override
    public int hashCode() {
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null) return false;
        if (obj.getClass() != getClass()) return false;
        FacadePartKey other = (FacadePartKey) obj;
        return Objects.equals(shape, other.shape) //
            && Objects.equals(state, other.state) //
            && insetSides == other.insetSides;
    }
}
