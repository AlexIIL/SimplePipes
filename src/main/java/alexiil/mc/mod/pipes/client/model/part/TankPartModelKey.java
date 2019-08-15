package alexiil.mc.mod.pipes.client.model.part;

import alexiil.mc.lib.multipart.api.render.PartModelKey;

public final class TankPartModelKey extends PartModelKey {
    public static final TankPartModelKey INSTANCE = new TankPartModelKey();

    @Override
    public boolean equals(Object obj) {
        return this == obj;
    }

    @Override
    public int hashCode() {
        return System.identityHashCode(this);
    }
}
