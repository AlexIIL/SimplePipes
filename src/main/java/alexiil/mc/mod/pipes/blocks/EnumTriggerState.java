package alexiil.mc.mod.pipes.blocks;

import java.util.Locale;

import net.minecraft.util.StringRepresentable;

public enum EnumTriggerState implements StringRepresentable {
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
