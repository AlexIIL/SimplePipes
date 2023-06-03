package alexiil.mc.mod.pipes.client.model.part;

import javax.annotation.Nullable;

import net.minecraft.util.math.Direction;

import alexiil.mc.mod.pipes.pipe.PipeSpDef;

public class PipeSPSidedPartKey extends PipeSpPartKey {
    @Nullable
    public final Direction mainSide;

    public PipeSPSidedPartKey(PipeSpDef pipeDef, byte isConnected, Direction mainSide) {
        super(pipeDef, isConnected);
        this.mainSide = mainSide;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((mainSide == null) ? 0 : mainSide.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!super.equals(obj)) return false;
        if (getClass() != obj.getClass()) return false;
        PipeSPSidedPartKey other = (PipeSPSidedPartKey) obj;
        if (mainSide != other.mainSide) return false;
        return true;
    }
}
