package alexiil.mc.mod.pipes.blocks;

import java.util.Locale;
import net.minecraft.util.StringIdentifiable;

public enum EnumTriggerState implements StringIdentifiable {
    NO_TARGET,
    OFF,
    ON;

    private final String lowerCase = name().toLowerCase(Locale.ROOT);

    @Override
    public String asString() {
        return lowerCase;
    }

    public static EnumTriggerState of(boolean value) {
        return value ? ON : OFF;
    }
}
