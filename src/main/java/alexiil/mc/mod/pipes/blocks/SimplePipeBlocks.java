/*
 * Copyright (c) 2019 SpaceToad and the BuildCraft team
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0. If a copy of the MPL was not
 * distributed with this file, You can obtain one at https://mozilla.org/MPL/2.0/
 */
package alexiil.mc.mod.pipes.blocks;

import java.util.Arrays;
import java.util.HashSet;
import java.util.function.Supplier;

import net.fabricmc.fabric.api.block.FabricBlockSettings;

import net.minecraft.block.Block;
import net.minecraft.block.Material;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.registry.Registry;

import alexiil.mc.mod.pipes.SimplePipes;

public class SimplePipeBlocks {

    public static final BlockTriggerInvEmpty TRIGGER_ITEM_INV_EMPTY;
    public static final BlockTriggerInvFull TRIGGER_ITEM_INV_FULL;
    public static final BlockTriggerInvSpace TRIGGER_ITEM_INV_SPACE;
    public static final BlockTriggerInvContains TRIGGER_ITEM_INV_CONTAINS;

    public static final BlockTriggerFluidEmpty TRIGGER_FLUID_INV_EMPTY;
    public static final BlockTriggerFluidFull TRIGGER_FLUID_INV_FULL;
    public static final BlockTriggerFluidSpace TRIGGER_FLUID_INV_SPACE;
    public static final BlockTriggerFluidContains TRIGGER_FLUID_INV_CONTAINS;

    public static final BlockPipeItemWooden WOODEN_PIPE_ITEMS;
    public static final BlockPipeItemStone STONE_PIPE_ITEMS;
    public static final BlockPipeItemClay CLAY_PIPE_ITEMS;
    public static final BlockPipeItemIron IRON_PIPE_ITEMS;
    public static final BlockPipeItemGold GOLD_PIPE_ITEMS;
    public static final BlockPipeItemDiamond DIAMOND_PIPE_ITEMS;

    public static final BlockPipeFluidWooden WOODEN_PIPE_FLUIDS;
    public static final BlockPipeFluidStone STONE_PIPE_FLUIDS;
    public static final BlockPipeFluidClay CLAY_PIPE_FLUIDS;
    public static final BlockPipeFluidIron IRON_PIPE_FLUIDS;

    public static final BlockTank TANK;
    public static final BlockPump PUMP;

    public static final BlockEntityType<TilePipeItemWood> WOODEN_PIPE_ITEM_TILE;
    public static final BlockEntityType<TilePipeItemStone> STONE_PIPE_ITEM_TILE;
    public static final BlockEntityType<TilePipeItemClay> CLAY_PIPE_ITEM_TILE;
    public static final BlockEntityType<TilePipeItemIron> IRON_PIPE_ITEM_TILE;
    public static final BlockEntityType<TilePipeItemGold> GOLD_PIPE_ITEM_TILE;
    public static final BlockEntityType<TilePipeItemDiamond> DIAMOND_PIPE_ITEM_TILE;

    public static final BlockEntityType<TilePipeFluidWood> WOODEN_PIPE_FLUID_TILE;
    public static final BlockEntityType<TilePipeFluidStone> STONE_PIPE_FLUID_TILE;
    public static final BlockEntityType<TilePipeFluidClay> CLAY_PIPE_FLUID_TILE;
    public static final BlockEntityType<TilePipeFluidIron> IRON_PIPE_FLUID_TILE;

    public static final BlockEntityType<TileTriggerInvEmpty> TRIGGER_ITEM_INV_EMPTY_TILE;
    public static final BlockEntityType<TileTriggerInvFull> TRIGGER_ITEM_INV_FULL_TILE;
    public static final BlockEntityType<TileTriggerInvSpace> TRIGGER_ITEM_INV_SPACE_TILE;
    public static final BlockEntityType<TileTriggerInvContains> TRIGGER_ITEM_INV_CONTAINS_TILE;

    public static final BlockEntityType<TileTriggerFluidEmpty> TRIGGER_FLUID_INV_EMPTY_TILE;
    public static final BlockEntityType<TileTriggerFluidFull> TRIGGER_FLUID_INV_FULL_TILE;
    public static final BlockEntityType<TileTriggerFluidSpace> TRIGGER_FLUID_INV_SPACE_TILE;
    public static final BlockEntityType<TileTriggerFluidContains> TRIGGER_FLUID_INV_CONTAINS_TILE;

    public static final BlockEntityType<TileTank> TANK_TILE;
    public static final BlockEntityType<TilePump> PUMP_TILE;

    static {
        Block.Settings pipeSettings = FabricBlockSettings.of(Material.PART)//
            .strength(0.5f, 1f)//
            .build();

        WOODEN_PIPE_ITEMS = new BlockPipeItemWooden(pipeSettings);
        STONE_PIPE_ITEMS = new BlockPipeItemStone(pipeSettings);
        CLAY_PIPE_ITEMS = new BlockPipeItemClay(pipeSettings);
        IRON_PIPE_ITEMS = new BlockPipeItemIron(pipeSettings);
        GOLD_PIPE_ITEMS = new BlockPipeItemGold(pipeSettings);
        DIAMOND_PIPE_ITEMS = new BlockPipeItemDiamond(pipeSettings);

        WOODEN_PIPE_FLUIDS = new BlockPipeFluidWooden(pipeSettings);
        STONE_PIPE_FLUIDS = new BlockPipeFluidStone(pipeSettings);
        CLAY_PIPE_FLUIDS = new BlockPipeFluidClay(pipeSettings);
        IRON_PIPE_FLUIDS = new BlockPipeFluidIron(pipeSettings);

        Block.Settings triggerSettings = FabricBlockSettings.of(Material.STONE)//
            .strength(1.5F, 6.0F)//
            .build();

        TRIGGER_ITEM_INV_EMPTY = new BlockTriggerInvEmpty(triggerSettings);
        TRIGGER_ITEM_INV_FULL = new BlockTriggerInvFull(triggerSettings);
        TRIGGER_ITEM_INV_SPACE = new BlockTriggerInvSpace(triggerSettings);
        TRIGGER_ITEM_INV_CONTAINS = new BlockTriggerInvContains(triggerSettings);

        TRIGGER_FLUID_INV_EMPTY = new BlockTriggerFluidEmpty(triggerSettings);
        TRIGGER_FLUID_INV_FULL = new BlockTriggerFluidFull(triggerSettings);
        TRIGGER_FLUID_INV_SPACE = new BlockTriggerFluidSpace(triggerSettings);
        TRIGGER_FLUID_INV_CONTAINS = new BlockTriggerFluidContains(triggerSettings);

        TANK = new BlockTank(triggerSettings);
        PUMP = new BlockPump(triggerSettings);

        WOODEN_PIPE_ITEM_TILE = create(TilePipeItemWood::new, WOODEN_PIPE_ITEMS);
        STONE_PIPE_ITEM_TILE = create(TilePipeItemStone::new, STONE_PIPE_ITEMS);
        CLAY_PIPE_ITEM_TILE = create(TilePipeItemClay::new, CLAY_PIPE_ITEMS);
        IRON_PIPE_ITEM_TILE = create(TilePipeItemIron::new, IRON_PIPE_ITEMS);
        GOLD_PIPE_ITEM_TILE = create(TilePipeItemGold::new, GOLD_PIPE_ITEMS);
        DIAMOND_PIPE_ITEM_TILE = create(TilePipeItemDiamond::new, DIAMOND_PIPE_ITEMS);

        WOODEN_PIPE_FLUID_TILE = create(TilePipeFluidWood::new, WOODEN_PIPE_FLUIDS);
        STONE_PIPE_FLUID_TILE = create(TilePipeFluidStone::new, STONE_PIPE_FLUIDS);
        CLAY_PIPE_FLUID_TILE = create(TilePipeFluidClay::new, CLAY_PIPE_FLUIDS);
        IRON_PIPE_FLUID_TILE = create(TilePipeFluidIron::new, IRON_PIPE_FLUIDS);

        TRIGGER_ITEM_INV_EMPTY_TILE = create(TileTriggerInvEmpty::new, TRIGGER_ITEM_INV_EMPTY);
        TRIGGER_ITEM_INV_FULL_TILE = create(TileTriggerInvFull::new, TRIGGER_ITEM_INV_FULL);
        TRIGGER_ITEM_INV_SPACE_TILE = create(TileTriggerInvSpace::new, TRIGGER_ITEM_INV_SPACE);
        TRIGGER_ITEM_INV_CONTAINS_TILE = create(TileTriggerInvContains::new, TRIGGER_ITEM_INV_CONTAINS);

        TRIGGER_FLUID_INV_EMPTY_TILE = create(TileTriggerFluidEmpty::new, TRIGGER_FLUID_INV_EMPTY);
        TRIGGER_FLUID_INV_FULL_TILE = create(TileTriggerFluidFull::new, TRIGGER_FLUID_INV_FULL);
        TRIGGER_FLUID_INV_SPACE_TILE = create(TileTriggerFluidSpace::new, TRIGGER_FLUID_INV_SPACE);
        TRIGGER_FLUID_INV_CONTAINS_TILE = create(TileTriggerFluidContains::new, TRIGGER_FLUID_INV_CONTAINS);

        TANK_TILE = create(TileTank::new, TANK);
        PUMP_TILE = create(TilePump::new, PUMP);
    }

    private static <T extends BlockEntity> BlockEntityType<T> create(Supplier<T> supplier, Block... blocks) {
        return new BlockEntityType<>(supplier, new HashSet<>(Arrays.asList(blocks)), null);
    }

    public static void load() {
        registerBlock(TRIGGER_ITEM_INV_EMPTY, "trigger_item_inv_empty");
        registerBlock(TRIGGER_ITEM_INV_FULL, "trigger_item_inv_full");
        registerBlock(TRIGGER_ITEM_INV_SPACE, "trigger_item_inv_space");
        registerBlock(TRIGGER_ITEM_INV_CONTAINS, "trigger_item_inv_contains");

        registerBlock(TRIGGER_FLUID_INV_EMPTY, "trigger_fluid_inv_empty");
        registerBlock(TRIGGER_FLUID_INV_FULL, "trigger_fluid_inv_full");
        registerBlock(TRIGGER_FLUID_INV_SPACE, "trigger_fluid_inv_space");
        registerBlock(TRIGGER_FLUID_INV_CONTAINS, "trigger_fluid_inv_contains");

        registerBlock(WOODEN_PIPE_ITEMS, "pipe_wooden_item");
        registerBlock(STONE_PIPE_ITEMS, "pipe_stone_item");
        registerBlock(CLAY_PIPE_ITEMS, "pipe_clay_item");
        registerBlock(IRON_PIPE_ITEMS, "pipe_iron_item");
        registerBlock(GOLD_PIPE_ITEMS, "pipe_gold_item");
        registerBlock(DIAMOND_PIPE_ITEMS, "pipe_diamond_item");

        registerBlock(WOODEN_PIPE_FLUIDS, "pipe_wooden_fluid");
        registerBlock(STONE_PIPE_FLUIDS, "pipe_stone_fluid");
        registerBlock(CLAY_PIPE_FLUIDS, "pipe_clay_fluid");
        registerBlock(IRON_PIPE_FLUIDS, "pipe_iron_fluid");

        registerBlock(TANK, "tank");
        registerBlock(PUMP, "pump");

        registerTile(WOODEN_PIPE_ITEM_TILE, "pipe_wooden_item");
        registerTile(STONE_PIPE_ITEM_TILE, "pipe_stone_item");
        registerTile(CLAY_PIPE_ITEM_TILE, "pipe_clay_item");
        registerTile(IRON_PIPE_ITEM_TILE, "pipe_iron_item");
        registerTile(GOLD_PIPE_ITEM_TILE, "pipe_gold_item");
        registerTile(DIAMOND_PIPE_ITEM_TILE, "pipe_diamond_item");

        registerTile(WOODEN_PIPE_FLUID_TILE, "pipe_wooden_fluid");
        registerTile(STONE_PIPE_FLUID_TILE, "pipe_stone_fluid");
        registerTile(CLAY_PIPE_FLUID_TILE, "pipe_clay_fluid");
        registerTile(IRON_PIPE_FLUID_TILE, "pipe_iron_fluid");

        registerTile(TRIGGER_ITEM_INV_EMPTY_TILE, "trigger_item_inv_empty");
        registerTile(TRIGGER_ITEM_INV_FULL_TILE, "trigger_item_inv_full");
        registerTile(TRIGGER_ITEM_INV_SPACE_TILE, "trigger_item_inv_space");
        registerTile(TRIGGER_ITEM_INV_CONTAINS_TILE, "trigger_item_inv_contains");

        registerTile(TRIGGER_FLUID_INV_EMPTY_TILE, "trigger_fluid_inv_empty");
        registerTile(TRIGGER_FLUID_INV_FULL_TILE, "trigger_fluid_inv_full");
        registerTile(TRIGGER_FLUID_INV_SPACE_TILE, "trigger_fluid_inv_space");
        registerTile(TRIGGER_FLUID_INV_CONTAINS_TILE, "trigger_fluid_inv_contains");

        registerTile(TANK_TILE, "tank");
        registerTile(PUMP_TILE, "pump");
    }

    private static void registerBlock(Block block, String name) {
        Registry.register(Registry.BLOCK, SimplePipes.MODID + ":" + name, block);
    }

    private static void registerTile(BlockEntityType<?> type, String name) {
        Registry.register(Registry.BLOCK_ENTITY, SimplePipes.MODID + ":" + name, type);
    }
}
