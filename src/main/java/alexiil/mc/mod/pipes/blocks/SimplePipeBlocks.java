/*
 * Copyright (c) 2019 SpaceToad and the BuildCraft team
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0. If a copy of the MPL was not
 * distributed with this file, You can obtain one at https://mozilla.org/MPL/2.0/
 */
package alexiil.mc.mod.pipes.blocks;

import java.util.function.Supplier;

import com.mojang.datafixers.types.Type;

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

    public static final BlockPipeItemWooden WOODEN_PIPE_ITEMS;
    public static final BlockPipeItemStone STONE_PIPE_ITEMS;
    public static final BlockPipeItemIron IRON_PIPE_ITEMS;

    public static final BlockTank TANK;

    public static final BlockEntityType<TilePipeItemWood> WOODEN_PIPE_ITEM_TILE;
    public static final BlockEntityType<TilePipeItemStone> STONE_PIPE_ITEM_TILE;
    public static final BlockEntityType<TilePipeItemIron> IRON_PIPE_ITEM_TILE;

    public static final BlockEntityType<TileTriggerInvEmpty> TRIGGER_ITEM_INV_EMPTY_TILE;
    public static final BlockEntityType<TileTriggerInvFull> TRIGGER_ITEM_INV_FULL_TILE;
    public static final BlockEntityType<TileTriggerInvSpace> TRIGGER_ITEM_INV_SPACE_TILE;
    public static final BlockEntityType<TileTriggerInvContains> TRIGGER_ITEM_INV_CONTAINS_TILE;

    public static final BlockEntityType<TileTank> TANK_TILE;

    static {
        Block.Settings pipeSettings = FabricBlockSettings.of(Material.PART)//
            .strength(0.5f, 1f)//
            .build();

        WOODEN_PIPE_ITEMS = new BlockPipeItemWooden(pipeSettings);
        STONE_PIPE_ITEMS = new BlockPipeItemStone(pipeSettings);
        IRON_PIPE_ITEMS = new BlockPipeItemIron(pipeSettings);

        Block.Settings triggerSettings = FabricBlockSettings.of(Material.STONE)//
            .strength(1.5F, 6.0F)//
            .build();

        TRIGGER_ITEM_INV_EMPTY = new BlockTriggerInvEmpty(triggerSettings);
        TRIGGER_ITEM_INV_FULL = new BlockTriggerInvFull(triggerSettings);
        TRIGGER_ITEM_INV_SPACE = new BlockTriggerInvSpace(triggerSettings);
        TRIGGER_ITEM_INV_CONTAINS = new BlockTriggerInvContains(triggerSettings);

        TANK = new BlockTank(triggerSettings);

        WOODEN_PIPE_ITEM_TILE = create(TilePipeItemWood::new);
        STONE_PIPE_ITEM_TILE = create(TilePipeItemStone::new);
        IRON_PIPE_ITEM_TILE = create(TilePipeItemIron::new);

        TRIGGER_ITEM_INV_EMPTY_TILE = create(TileTriggerInvEmpty::new);
        TRIGGER_ITEM_INV_FULL_TILE = create(TileTriggerInvFull::new);
        TRIGGER_ITEM_INV_SPACE_TILE = create(TileTriggerInvSpace::new);
        TRIGGER_ITEM_INV_CONTAINS_TILE = create(TileTriggerInvContains::new);

        TANK_TILE = create(TileTank::new);
    }

    private static <T extends BlockEntity> BlockEntityType<T> create(Supplier<T> supplier) {
        Type<?> choiceType = null;
        // Schemas.getFixer().getSchema(DataFixUtils.makeKey(SharedConstants.getGameVersion().getWorldVersion()))
        // .getChoiceType(TypeReferences.BLOCK_ENTITY, name);
        return BlockEntityType.Builder.create(supplier).build(choiceType);
    }

    public static void load() {
        registerBlock(TRIGGER_ITEM_INV_EMPTY, "trigger_item_inv_empty");
        registerBlock(TRIGGER_ITEM_INV_FULL, "trigger_item_inv_full");
        registerBlock(TRIGGER_ITEM_INV_SPACE, "trigger_item_inv_space");
        registerBlock(TRIGGER_ITEM_INV_CONTAINS, "trigger_item_inv_contains");

        registerBlock(WOODEN_PIPE_ITEMS, "pipe_wooden_item");
        registerBlock(STONE_PIPE_ITEMS, "pipe_stone_item");
        registerBlock(IRON_PIPE_ITEMS, "pipe_iron_item");

        registerBlock(TANK, "tank");

        registerTile(WOODEN_PIPE_ITEM_TILE, "pipe_wooden_item");
        registerTile(STONE_PIPE_ITEM_TILE, "pipe_stone_item");
        registerTile(IRON_PIPE_ITEM_TILE, "pipe_iron_item");

        registerTile(TRIGGER_ITEM_INV_EMPTY_TILE, "trigger_item_inv_empty");
        registerTile(TRIGGER_ITEM_INV_FULL_TILE, "trigger_item_inv_full");
        registerTile(TRIGGER_ITEM_INV_SPACE_TILE, "trigger_item_inv_space");
        registerTile(TRIGGER_ITEM_INV_CONTAINS_TILE, "trigger_item_inv_contains");

        registerTile(TANK_TILE, "tank");
    }

    private static void registerBlock(Block block, String name) {
        Registry.register(Registry.BLOCK, SimplePipes.MODID + ":" + name, block);
    }

    private static void registerTile(BlockEntityType<?> type, String name) {
        Registry.register(Registry.BLOCK_ENTITY, SimplePipes.MODID + ":" + name, type);
    }
}
