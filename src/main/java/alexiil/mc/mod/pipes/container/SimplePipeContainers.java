/*
 * Copyright (c) 2019 SpaceToad and the BuildCraft team
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0. If a copy of the MPL was not
 * distributed with this file, You can obtain one at https://mozilla.org/MPL/2.0/
 */
package alexiil.mc.mod.pipes.container;

import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerType;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.Identifier;

import alexiil.mc.mod.pipes.SimplePipes;
import net.minecraft.util.registry.Registry;

public class SimplePipeContainers {
    public static final ScreenHandlerType<ContainerTriggerInvSpace> TRIGGER_ITEM_INV_SPACE
        = new ExtendedScreenHandlerType<>(ContainerTriggerInvSpace.FACTORY);
    public static final ScreenHandlerType<ContainerTriggerInvContains> TRIGGER_ITEM_INV_CONTAINS
        = new ExtendedScreenHandlerType<>(ContainerTriggerInvContains.FACTORY);
    public static final ScreenHandlerType<ContainerTriggerFluidSpace> TRIGGER_FLUID_INV_SPACE
        = new ExtendedScreenHandlerType<>(ContainerTriggerFluidSpace.FACTORY);
    public static final ScreenHandlerType<ContainerTriggerFluidContains> TRIGGER_FLUID_INV_CONTAINS
        = new ExtendedScreenHandlerType<>(ContainerTriggerFluidContains.FACTORY);
    public static final ScreenHandlerType<ContainerPipeSorter> PIPE_DIAMOND_ITEM
        = new ExtendedScreenHandlerType<>(ContainerPipeSorter.FACTORY);
    public static final ScreenHandlerType<ContainerPipeDiamondItem> PIPE_PART_DIAMOND_ITEM
        = new ExtendedScreenHandlerType<>(ContainerPipeDiamondItem.FACTORY);
    public static final ScreenHandlerType<ContainerTank> TANK = new ExtendedScreenHandlerType<>(ContainerTank.FACTORY);

    private static Identifier id(String name) {
        return new Identifier(SimplePipes.MODID, name);
    }

    public static void load() {
        register(id("trigger_item_inv_space"), TRIGGER_ITEM_INV_SPACE);
        register(id("trigger_item_inv_contains"), TRIGGER_ITEM_INV_CONTAINS);
        register(id("trigger_fluid_inv_space"), TRIGGER_FLUID_INV_SPACE);
        register(id("trigger_fluid_inv_contains"), TRIGGER_FLUID_INV_CONTAINS);
        register(id("pipe_diamond_item"), PIPE_DIAMOND_ITEM);
        register(id("pipe_part_diamond_item"), PIPE_PART_DIAMOND_ITEM);
        register(id("tank"), TANK);
    }

    private static void register(Identifier id, ScreenHandlerType<? extends ScreenHandler> type) {
        Registry.register(Registry.SCREEN_HANDLER, id, type);
    }
}
