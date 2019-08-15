package alexiil.mc.mod.pipes.part;

import net.minecraft.util.Identifier;

import alexiil.mc.lib.multipart.api.PartDefinition;
import alexiil.mc.lib.multipart.api.PartDefinition.IPartNbtReader;
import alexiil.mc.lib.multipart.api.PartDefinition.IPartNetLoader;
import alexiil.mc.mod.pipes.SimplePipes;

public final class SimplePipeParts {
    private SimplePipeParts() {}

    public static final PartDefinition FACADE;
    public static final PartDefinition TANK;

    static {
        FACADE = def("facade", FacadePart::new, FacadePart::new);
        TANK = def("tank", PartTank::new, PartTank::new);
    }

    private static PartDefinition def(String post, IPartNbtReader reader, IPartNetLoader loader) {
        return new PartDefinition(new Identifier(SimplePipes.MODID, post), reader, loader);
    }

    public static void load() {
        PartDefinition.PARTS.put(FACADE.identifier, FACADE);
        PartDefinition.PARTS.put(TANK.identifier, TANK);
    }
}
