/*
 * Copyright (c) 2019 SpaceToad and the BuildCraft team
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0. If a copy of the MPL was not
 * distributed with this file, You can obtain one at https://mozilla.org/MPL/2.0/
 */
package alexiil.mc.mod.pipes.items;

import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.block.BlockItem;
import net.minecraft.util.registry.Registry;

import alexiil.mc.mod.pipes.SimplePipes;
import alexiil.mc.mod.pipes.blocks.SimplePipeBlocks;

public class SimplePipeItems {

    public static final BlockItem WOODEN_PIPE_ITEMS;
    public static final BlockItem STONE_PIPE_ITEMS;
    public static final BlockItem IRON_PIPE_ITEMS;

    public static final BlockItem TANK;

    public static final BlockItem TRIGGER_ITEM_INV_EMPTY;
    public static final BlockItem TRIGGER_ITEM_INV_FULL;
    public static final BlockItem TRIGGER_ITEM_INV_SPACE;
    public static final BlockItem TRIGGER_ITEM_INV_CONTAINS;

    static {
        Item.Settings pipes = new Item.Settings();
        pipes.itemGroup(ItemGroup.TRANSPORTATION);

        WOODEN_PIPE_ITEMS = new BlockItem(SimplePipeBlocks.WOODEN_PIPE_ITEMS, pipes);
        STONE_PIPE_ITEMS = new BlockItem(SimplePipeBlocks.STONE_PIPE_ITEMS, pipes);
        IRON_PIPE_ITEMS = new BlockItem(SimplePipeBlocks.IRON_PIPE_ITEMS, pipes);

        Item.Settings triggers = new Item.Settings();
        triggers.itemGroup(ItemGroup.REDSTONE);

        TANK = new BlockItem(SimplePipeBlocks.TANK, triggers);

        TRIGGER_ITEM_INV_EMPTY = new BlockItem(SimplePipeBlocks.TRIGGER_ITEM_INV_EMPTY, triggers);
        TRIGGER_ITEM_INV_FULL = new BlockItem(SimplePipeBlocks.TRIGGER_ITEM_INV_FULL, triggers);
        TRIGGER_ITEM_INV_SPACE = new BlockItem(SimplePipeBlocks.TRIGGER_ITEM_INV_SPACE, triggers);
        TRIGGER_ITEM_INV_CONTAINS = new BlockItem(SimplePipeBlocks.TRIGGER_ITEM_INV_CONTAINS, triggers);
    }

    public static void load() {
        registerItem(WOODEN_PIPE_ITEMS, "pipe_wooden_item");
        registerItem(STONE_PIPE_ITEMS, "pipe_stone_item");
        registerItem(IRON_PIPE_ITEMS, "pipe_iron_item");

        registerItem(TANK, "tank");

        registerItem(TRIGGER_ITEM_INV_EMPTY, "trigger_item_inv_empty");
        registerItem(TRIGGER_ITEM_INV_FULL, "trigger_item_inv_full");
        registerItem(TRIGGER_ITEM_INV_SPACE, "trigger_item_inv_space");
        registerItem(TRIGGER_ITEM_INV_CONTAINS, "trigger_item_inv_contains");
    }

    private static void registerItem(Item item, String name) {
        Registry.register(Registry.ITEM, SimplePipes.MODID + ":" + name, item);
    }
}
