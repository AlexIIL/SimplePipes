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

    public static final BlockItem WOODEN_PIPE;
    public static final BlockItem STONE_PIPE;
    public static final BlockItem IRON_PIPE;

    public static final BlockItem TRIGGER_ITEM_INV_EMPTY;
    public static final BlockItem TRIGGER_ITEM_INV_FULL;

    static {
        Item.Settings pipes = new Item.Settings();
        pipes.itemGroup(ItemGroup.TRANSPORTATION);

        WOODEN_PIPE = new BlockItem(SimplePipeBlocks.WOODEN_PIPE, pipes);
        STONE_PIPE = new BlockItem(SimplePipeBlocks.STONE_PIPE, pipes);
        IRON_PIPE = new BlockItem(SimplePipeBlocks.IRON_PIPE, pipes);

        Item.Settings triggers = new Item.Settings();
        triggers.itemGroup(ItemGroup.REDSTONE);

        TRIGGER_ITEM_INV_EMPTY = new BlockItem(SimplePipeBlocks.TRIGGER_ITEM_INV_EMPTY, triggers);
        TRIGGER_ITEM_INV_FULL = new BlockItem(SimplePipeBlocks.TRIGGER_ITEM_INV_FULL, triggers);
    }

    public static void load() {
        registerItem(WOODEN_PIPE, "pipe_wooden");
        registerItem(STONE_PIPE, "pipe_stone");
        registerItem(IRON_PIPE, "pipe_iron");

        registerItem(TRIGGER_ITEM_INV_EMPTY, "trigger_item_inv_empty");
        registerItem(TRIGGER_ITEM_INV_FULL, "trigger_item_inv_full");
    }

    private static void registerItem(Item item, String name) {
        Registry.register(Registry.ITEM, SimplePipes.MODID + ":" + name, item);
    }
}
