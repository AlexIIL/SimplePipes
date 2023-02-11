/*
 * Copyright (c) 2019 SpaceToad and the BuildCraft team
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0. If a copy of the MPL was not
 * distributed with this file, You can obtain one at https://mozilla.org/MPL/2.0/
 */
package alexiil.mc.mod.pipes.items;

import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;

import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.registry.Registry;

import alexiil.mc.mod.pipes.SimplePipes;
import alexiil.mc.mod.pipes.blocks.SimplePipeBlocks;
import alexiil.mc.mod.pipes.part.FacadeStateManager;
import alexiil.mc.mod.pipes.part.FullFacade;
import alexiil.mc.mod.pipes.part.PartTank;
import alexiil.mc.mod.pipes.part.SimplePipeParts;

public class SimplePipeItems {

    public static final ItemFacade FACADE;

    public static final ItemPipePart WOODEN_PIPE_ITEMS;
    public static final ItemPipePart STONE_PIPE_ITEMS;
    public static final ItemPipePart CLAY_PIPE_ITEMS;
    public static final ItemPipePart IRON_PIPE_ITEMS;
    public static final ItemPipePart GOLD_PIPE_ITEMS;
    public static final ItemPipePart DIAMOND_PIPE_ITEMS;

    public static final ItemPipePart WOODEN_PIPE_FLUIDS;
    public static final ItemPipePart STONE_PIPE_FLUIDS;
    public static final ItemPipePart CLAY_PIPE_FLUIDS;
    public static final ItemPipePart IRON_PIPE_FLUIDS;
    public static final ItemPipePart SPONGE_PIPE_FLUIDS;

    public static final ItemSimplePart TANK;
    public static final BlockItem PUMP;

    public static final BlockItem TRIGGER_ITEM_INV_EMPTY;
    public static final BlockItem TRIGGER_ITEM_INV_FULL;
    public static final BlockItem TRIGGER_ITEM_INV_SPACE;
    public static final BlockItem TRIGGER_ITEM_INV_CONTAINS;

    public static final BlockItem TRIGGER_FLUID_INV_EMPTY;
    public static final BlockItem TRIGGER_FLUID_INV_FULL;
    public static final BlockItem TRIGGER_FLUID_INV_SPACE;
    public static final BlockItem TRIGGER_FLUID_INV_CONTAINS;

    static {
        ItemGroup mainGroup = FabricItemGroupBuilder.build(SimplePipes.id("main"), SimplePipeItems::getMainGroupStack);
        ItemGroup facadeGroup
            = FabricItemGroupBuilder.build(SimplePipes.id("facades"), SimplePipeItems::getFacadeGroupStack);

        Item.Settings pipes = new Item.Settings();
        pipes.group(mainGroup);
        FACADE = new ItemFacade(new Item.Settings().group(facadeGroup));

        WOODEN_PIPE_ITEMS = new ItemPipePart(pipes, SimplePipeParts.WOODEN_PIPE_ITEMS);
        STONE_PIPE_ITEMS = new ItemPipePart(pipes, SimplePipeParts.STONE_PIPE_ITEMS);
        CLAY_PIPE_ITEMS = new ItemPipePart(pipes, SimplePipeParts.CLAY_PIPE_ITEMS);
        IRON_PIPE_ITEMS = new ItemPipePart(pipes, SimplePipeParts.IRON_PIPE_ITEMS);
        GOLD_PIPE_ITEMS = new ItemPipePart(pipes, SimplePipeParts.GOLD_PIPE_ITEMS);
        DIAMOND_PIPE_ITEMS = new ItemPipePart(pipes, SimplePipeParts.DIAMOND_PIPE_ITEMS);

        WOODEN_PIPE_FLUIDS = new ItemPipePart(pipes, SimplePipeParts.WOODEN_PIPE_FLUIDS);
        STONE_PIPE_FLUIDS = new ItemPipePart(pipes, SimplePipeParts.STONE_PIPE_FLUIDS);
        CLAY_PIPE_FLUIDS = new ItemPipePart(pipes, SimplePipeParts.CLAY_PIPE_FLUIDS);
        IRON_PIPE_FLUIDS = new ItemPipePart(pipes, SimplePipeParts.IRON_PIPE_FLUIDS);
        SPONGE_PIPE_FLUIDS = new ItemPipePart(pipes, SimplePipeParts.SPONGE_PIPE_FLUIDS);

        Item.Settings triggers = new Item.Settings();
        triggers.group(mainGroup);

        TANK = new ItemSimplePart(triggers, SimplePipeParts.TANK, PartTank::new);
        PUMP = new BlockItem(SimplePipeBlocks.PUMP, triggers);

        TRIGGER_ITEM_INV_EMPTY = new BlockItem(SimplePipeBlocks.TRIGGER_ITEM_INV_EMPTY, triggers);
        TRIGGER_ITEM_INV_FULL = new BlockItem(SimplePipeBlocks.TRIGGER_ITEM_INV_FULL, triggers);
        TRIGGER_ITEM_INV_SPACE = new BlockItem(SimplePipeBlocks.TRIGGER_ITEM_INV_SPACE, triggers);
        TRIGGER_ITEM_INV_CONTAINS = new BlockItem(SimplePipeBlocks.TRIGGER_ITEM_INV_CONTAINS, triggers);

        TRIGGER_FLUID_INV_EMPTY = new BlockItem(SimplePipeBlocks.TRIGGER_FLUID_INV_EMPTY, triggers);
        TRIGGER_FLUID_INV_FULL = new BlockItem(SimplePipeBlocks.TRIGGER_FLUID_INV_FULL, triggers);
        TRIGGER_FLUID_INV_SPACE = new BlockItem(SimplePipeBlocks.TRIGGER_FLUID_INV_SPACE, triggers);
        TRIGGER_FLUID_INV_CONTAINS = new BlockItem(SimplePipeBlocks.TRIGGER_FLUID_INV_CONTAINS, triggers);
    }

    private static ItemStack getMainGroupStack() {
        return new ItemStack(WOODEN_PIPE_ITEMS);
    }

    private static ItemStack getFacadeGroupStack() {
        return FACADE.createItemStack(new FullFacade(FacadeStateManager.getPreviewState(), ItemFacade.DEFAULT_SHAPE));
    }

    public static void load() {
        registerItem(FACADE, "facade");

        registerItem(WOODEN_PIPE_ITEMS, "pipe_wooden_item");
        registerItem(STONE_PIPE_ITEMS, "pipe_stone_item");
        registerItem(CLAY_PIPE_ITEMS, "pipe_clay_item");
        registerItem(IRON_PIPE_ITEMS, "pipe_iron_item");
        registerItem(GOLD_PIPE_ITEMS, "pipe_gold_item");
        registerItem(DIAMOND_PIPE_ITEMS, "pipe_diamond_item");

        registerItem(WOODEN_PIPE_FLUIDS, "pipe_wooden_fluid");
        registerItem(STONE_PIPE_FLUIDS, "pipe_stone_fluid");
        registerItem(CLAY_PIPE_FLUIDS, "pipe_clay_fluid");
        registerItem(IRON_PIPE_FLUIDS, "pipe_iron_fluid");
        registerItem(SPONGE_PIPE_FLUIDS, "pipe_sponge_fluid");

        registerItem(TANK, "tank");
        registerItem(PUMP, "pump");

        registerItem(TRIGGER_ITEM_INV_EMPTY, "trigger_item_inv_empty");
        registerItem(TRIGGER_ITEM_INV_FULL, "trigger_item_inv_full");
        registerItem(TRIGGER_ITEM_INV_SPACE, "trigger_item_inv_space");
        registerItem(TRIGGER_ITEM_INV_CONTAINS, "trigger_item_inv_contains");

        registerItem(TRIGGER_FLUID_INV_EMPTY, "trigger_fluid_inv_empty");
        registerItem(TRIGGER_FLUID_INV_FULL, "trigger_fluid_inv_full");
        registerItem(TRIGGER_FLUID_INV_SPACE, "trigger_fluid_inv_space");
        registerItem(TRIGGER_FLUID_INV_CONTAINS, "trigger_fluid_inv_contains");
    }

    private static void registerItem(Item item, String name) {
        Registry.register(Registry.ITEM, SimplePipes.MODID + ":" + name, item);
    }
}
