package alexiil.mc.mod.pipes.client.screen;

import net.fabricmc.fabric.api.client.screen.ContainerScreenFactory;
import net.fabricmc.fabric.api.client.screen.ScreenProviderRegistry;

import net.minecraft.container.Container;
import net.minecraft.util.Identifier;

import alexiil.mc.mod.pipes.container.SimplePipeContainers;

public class SimplePipeScreens {

    public static void load() {
        register(SimplePipeContainers.TRIGGER_ITEM_INV_SPACE, ScreenTriggerItemInvSpace.FACTORY);
        register(SimplePipeContainers.TRIGGER_ITEM_INV_CONTAINS, ScreenTriggerItemInvContains.FACTORY);
    }

    private static void register(Identifier id, ContainerScreenFactory<? extends Container> factory) {
        ScreenProviderRegistry.INSTANCE.registerFactory(id, factory);
    }
}
