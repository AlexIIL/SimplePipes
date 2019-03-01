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

    public static final BlockPipeWooden WOODEN_PIPE;
    public static final BlockPipeStone STONE_PIPE;
    public static final BlockPipeIron IRON_PIPE;

    public static final BlockTriggerInvEmpty TRIGGER_ITEM_INV_EMPTY;
    public static final BlockTriggerInvFull TRIGGER_ITEM_INV_FULL;

    public static final BlockEntityType<TilePipeWood> WOODEN_PIPE_TILE;
    public static final BlockEntityType<TilePipeStone> STONE_PIPE_TILE;
    public static final BlockEntityType<TilePipeIron> IRON_PIPE_TILE;

    public static final BlockEntityType<TileTriggerInvEmpty> TRIGGER_ITEM_INV_EMPTY_TILE;
    public static final BlockEntityType<TileTriggerInvFull> TRIGGER_ITEM_INV_FULL_TILE;

    static {
        Block.Settings pipeSettings = FabricBlockSettings.of(Material.PART)//
            .build();

        WOODEN_PIPE = new BlockPipeWooden(pipeSettings);
        STONE_PIPE = new BlockPipeStone(pipeSettings);
        IRON_PIPE = new BlockPipeIron(pipeSettings);

        Block.Settings triggerSettings = FabricBlockSettings.of(Material.STONE)//
            .build();

        TRIGGER_ITEM_INV_EMPTY = new BlockTriggerInvEmpty(triggerSettings);
        TRIGGER_ITEM_INV_FULL = new BlockTriggerInvFull(triggerSettings);

        WOODEN_PIPE_TILE = create(TilePipeWood::new);
        STONE_PIPE_TILE = create(TilePipeStone::new);
        IRON_PIPE_TILE = create(TilePipeIron::new);

        TRIGGER_ITEM_INV_EMPTY_TILE = create(TileTriggerInvEmpty::new);
        TRIGGER_ITEM_INV_FULL_TILE = create(TileTriggerInvFull::new);
    }

    private static <T extends BlockEntity> BlockEntityType<T> create(Supplier<T> supplier) {
        Type<?> choiceType = null;
        // Schemas.getFixer().getSchema(DataFixUtils.makeKey(SharedConstants.getGameVersion().getWorldVersion()))
        // .getChoiceType(TypeReferences.BLOCK_ENTITY, name);
        return BlockEntityType.Builder.create(supplier).build(choiceType);
    }

    public static void load() {
        registerBlock(WOODEN_PIPE, "pipe_wooden");
        registerBlock(STONE_PIPE, "pipe_stone");
        registerBlock(IRON_PIPE, "pipe_iron");

        registerBlock(TRIGGER_ITEM_INV_EMPTY, "trigger_item_inv_empty");
        registerBlock(TRIGGER_ITEM_INV_FULL, "trigger_item_inv_full");

        registerTile(WOODEN_PIPE_TILE, "pipe_wooden");
        registerTile(STONE_PIPE_TILE, "pipe_stone");
        registerTile(IRON_PIPE_TILE, "pipe_iron");

        registerTile(TRIGGER_ITEM_INV_EMPTY_TILE, "trigger_item_inv_empty");
        registerTile(TRIGGER_ITEM_INV_FULL_TILE, "trigger_item_inv_full");
    }

    private static void registerBlock(Block block, String name) {
        Registry.register(Registry.BLOCK, SimplePipes.MODID + ":" + name, block);
    }

    private static void registerTile(BlockEntityType<?> type, String name) {
        Registry.register(Registry.BLOCK_ENTITY, SimplePipes.MODID + ":" + name, type);
    }
}
