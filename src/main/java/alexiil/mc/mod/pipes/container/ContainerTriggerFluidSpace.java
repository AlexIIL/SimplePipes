/*
 * Copyright (c) 2019 SpaceToad and the BuildCraft team
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0. If a copy of the MPL was not
 * distributed with this file, You can obtain one at https://mozilla.org/MPL/2.0/
 */
package alexiil.mc.mod.pipes.container;

import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerType;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;

import alexiil.mc.mod.pipes.blocks.TileTriggerFluidSpace;

public class ContainerTriggerFluidSpace extends ContainerTile<TileTriggerFluidSpace> {

    public static final ExtendedScreenHandlerType.ExtendedFactory<ContainerTriggerFluidSpace, BlockPos> FACTORY = (syncId, inv, pos) -> {
        PlayerEntity player = inv.player;
        BlockEntity be = player.getWorld().getBlockEntity(pos);
        if (be instanceof TileTriggerFluidSpace) {
            return new ContainerTriggerFluidSpace(syncId, player, (TileTriggerFluidSpace) be);
        }
        return null;
    };

    public ContainerTriggerFluidSpace(int syncId, PlayerEntity player, TileTriggerFluidSpace tile) {
        super(SimplePipeContainers.TRIGGER_FLUID_INV_SPACE, syncId, player, tile);
        addPlayerInventory(71);
        // addSlot(new Slot(tile.filterInv, 0, 80, 26));
    }

    @Override
    public ItemStack quickMove(PlayerEntity player, int index) {
        return ItemStack.EMPTY;
    }
}
