package alexiil.mc.mod.pipes.container;

import net.fabricmc.fabric.api.container.ContainerFactory;
import net.fabricmc.fabric.api.container.ContainerProviderRegistry;

import net.minecraft.container.Container;
import net.minecraft.util.Identifier;

import alexiil.mc.mod.pipes.SimplePipes;

public class SimplePipeContainers {
    public static final Identifier TRIGGER_ITEM_INV_SPACE = id("trigger_item_inv_space");
    public static final Identifier TRIGGER_ITEM_INV_CONTAINS = id("trigger_item_inv_contains");
    public static final Identifier TRIGGER_FLUID_INV_SPACE = id("trigger_fluid_inv_space");
    public static final Identifier TRIGGER_FLUID_INV_CONTAINS = id("trigger_fluid_inv_contains");
    public static final Identifier PIPE_DIAMOND_ITEM = id("pipe_diamond_item");
    public static final Identifier TANK = id("tank");

    private static Identifier id(String name) {
        return new Identifier(SimplePipes.MODID, name);
    }

    public static void load() {
        register(TRIGGER_ITEM_INV_SPACE, ContainerTriggerInvSpace.FACTORY);
        register(TRIGGER_ITEM_INV_CONTAINS, ContainerTriggerInvContains.FACTORY);
        register(TRIGGER_FLUID_INV_SPACE, ContainerTriggerFluidSpace.FACTORY);
        register(TRIGGER_FLUID_INV_CONTAINS, ContainerTriggerFluidContains.FACTORY);
        register(PIPE_DIAMOND_ITEM, ContainerPipeSorter.FACTORY);
        register(TANK, ContainerTank.FACTORY);
    }

    private static void register(Identifier id, ContainerFactory<Container> factory) {
        ContainerProviderRegistry.INSTANCE.registerFactory(id, factory);
    }
}
