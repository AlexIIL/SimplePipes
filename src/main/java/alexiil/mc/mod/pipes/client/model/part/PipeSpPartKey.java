package alexiil.mc.mod.pipes.client.model.part;

import java.util.Objects;

import net.minecraft.util.math.Direction;

import alexiil.mc.mod.pipes.pipe.PipeSpDef;

import alexiil.mc.lib.multipart.api.render.PartModelKey;

public class PipeSpPartKey extends PartModelKey {
    public final PipeSpDef def;
    final byte connections;

    public PipeSpPartKey(PipeSpDef def, byte isConnected) {
        this.def = def;
        this.connections = isConnected;
    }

    public boolean isConnected(Direction dir) {
        return (connections & (1 << dir.ordinal())) != 0;
    }

    @Override
    public String toString() {
        return "PipeBlockModel{" + def + ", " + connections + "}";
    }

    @Override
    public int hashCode() {
        return Objects.hash(def, connections);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        PipeSpPartKey other = (PipeSpPartKey) obj;
        return connections == other.connections && Objects.equals(def, other.def);
    }
}
