package alexiil.mc.mod.pipes.client.screen;

import net.fabricmc.fabric.api.client.screen.ContainerScreenFactory;
import net.fabricmc.fabric.api.client.screen.ScreenProviderRegistry;

import net.minecraft.screen.ScreenHandler;
import net.minecraft.util.Identifier;

import alexiil.mc.mod.pipes.container.SimplePipeContainers;

public class SimplePipeScreens {

    public static void load() {
        register(SimplePipeContainers.TRIGGER_ITEM_INV_SPACE, ScreenTriggerItemInvSpace.FACTORY);
        register(SimplePipeContainers.TRIGGER_ITEM_INV_CONTAINS, ScreenTriggerItemInvContains.FACTORY);
        register(SimplePipeContainers.TRIGGER_FLUID_INV_SPACE, ScreenTriggerFluidInvSpace.FACTORY);
        register(SimplePipeContainers.TRIGGER_FLUID_INV_CONTAINS, ScreenTriggerFluidInvContains.FACTORY);
        register(SimplePipeContainers.PIPE_DIAMOND_ITEM, ScreenPipeSorter.FACTORY);
        register(SimplePipeContainers.PIPE_PART_DIAMOND_ITEM, ScreenPipeDiamondItem.FACTORY);

        register(SimplePipeContainers.TANK, ScreenTank.FACTORY);
    }

    private static void register(Identifier id, ContainerScreenFactory<? extends ScreenHandler> factory) {
        ScreenProviderRegistry.INSTANCE.registerFactory(id, factory);
    }
}
