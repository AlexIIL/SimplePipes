/*
 * Copyright (c) 2019 SpaceToad and the BuildCraft team
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0. If a copy of the MPL was not
 * distributed with this file, You can obtain one at https://mozilla.org/MPL/2.0/
 */
package alexiil.mc.mod.pipes.blocks;

import java.util.Arrays;
import java.util.HashSet;

import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.MapColor;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.enums.Instrument;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.math.BlockPos;

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

    public static final BlockTank TANK;
    public static final BlockPump PUMP;

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
        Block.Settings triggerSettings = FabricBlockSettings.create()//
            .mapColor(MapColor.STONE_GRAY)//
            .instrument(Instrument.BASEDRUM)//
            .strength(1.5F, 6.0F);

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

    private static <T extends BlockEntity> BlockEntityType<T> create(IBeCreator<T> supplier, Block... blocks) {
        return new BlockEntityType<>(null, new HashSet<>(Arrays.asList(blocks)), null) {
            @Override
            public T instantiate(BlockPos pos, BlockState state) {
                return supplier.create(pos, state);
            }
        };
    }

    public interface IBeCreator<T extends BlockEntity> {
        T create(BlockPos pos, BlockState state);
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

        registerBlock(TANK, "tank");
        registerBlock(PUMP, "pump");

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
        Registry.register(Registries.BLOCK, SimplePipes.MODID + ":" + name, block);
    }

    private static void registerTile(BlockEntityType<?> type, String name) {
        Registry.register(Registries.BLOCK_ENTITY_TYPE, SimplePipes.MODID + ":" + name, type);
    }
}
