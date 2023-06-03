package alexiil.mc.mod.pipes.client.screen;

import net.minecraft.client.gui.screen.ingame.HandledScreens;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;

import alexiil.mc.mod.pipes.container.SimplePipeContainers;

public class SimplePipeScreens {

    public static void load() {
        register(SimplePipeContainers.TRIGGER_ITEM_INV_SPACE, ScreenTriggerItemInvSpace.FACTORY);
        register(SimplePipeContainers.TRIGGER_ITEM_INV_CONTAINS, ScreenTriggerItemInvContains.FACTORY);
        register(SimplePipeContainers.TRIGGER_FLUID_INV_SPACE, ScreenTriggerFluidInvSpace.FACTORY);
        register(SimplePipeContainers.TRIGGER_FLUID_INV_CONTAINS, ScreenTriggerFluidInvContains.FACTORY);
        register(SimplePipeContainers.PIPE_PART_DIAMOND_ITEM, ScreenPipeDiamondItem.FACTORY);

        register(SimplePipeContainers.TANK, ScreenTank.FACTORY);
    }

    private static <C extends ScreenHandler> void register(ScreenHandlerType<? extends C> type, HandledScreens.Provider<C, ?> factory) {
        HandledScreens.register(type, factory);
    }
}
