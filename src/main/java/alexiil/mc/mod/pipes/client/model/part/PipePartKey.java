package alexiil.mc.mod.pipes.client.model.part;

import java.util.Objects;

import net.minecraft.util.math.Direction;

import alexiil.mc.lib.multipart.api.PartDefinition;
import alexiil.mc.lib.multipart.api.render.PartModelKey;

public class PipePartKey extends PartModelKey {

    public final PartDefinition definition;
    public final byte connections;

    public PipePartKey(PartDefinition definition, byte connections) {
        this.definition = definition;
        this.connections = connections;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (obj.getClass() != getClass()) {
            return false;
        }
        PipePartKey other = (PipePartKey) obj;
        return definition == other.definition //
            && connections == other.connections;
    }

    @Override
    public int hashCode() {
        return Objects.hash(definition, connections);
    }

    public static class Sided extends PipePartKey {
        public final Direction dir;

        public Sided(PartDefinition definition, byte connections, Direction dir) {
            super(definition, connections);
            this.dir = dir;
        }

        @Override
        public int hashCode() {
            return Objects.hash(definition, connections, dir);
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null) return false;
            if (getClass() != obj.getClass()) {
                return false;
            }
            PipePartKey.Sided other = (PipePartKey.Sided) obj;
            return definition == other.definition //
                && connections == other.connections //
                && dir == other.dir;
        }
    }
}
