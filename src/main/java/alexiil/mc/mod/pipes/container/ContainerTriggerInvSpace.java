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
import net.minecraft.screen.slot.Slot;
import net.minecraft.util.math.BlockPos;

import alexiil.mc.mod.pipes.blocks.TileTriggerInvSpace;

public class ContainerTriggerInvSpace extends ContainerTile<TileTriggerInvSpace> {

    public static final ExtendedScreenHandlerType.ExtendedFactory<ContainerTriggerInvSpace> FACTORY = (syncId, inv, buffer) -> {
        PlayerEntity player = inv.player;
        BlockPos pos = buffer.readBlockPos();
        BlockEntity be = player.world.getBlockEntity(pos);
        if (be instanceof TileTriggerInvSpace) {
            return new ContainerTriggerInvSpace(syncId, player, (TileTriggerInvSpace) be);
        }
        return null;
    };

    public ContainerTriggerInvSpace(int syncId, PlayerEntity player, TileTriggerInvSpace tile) {
        super(SimplePipeContainers.TRIGGER_ITEM_INV_SPACE, syncId, player, tile);
        addPlayerInventory(71);
        addSlot(new Slot(tile.filterInv, 0, 80, 26));
    }

    @Override
    public ItemStack transferSlot(PlayerEntity player, int index) {
        return ItemStack.EMPTY;
    }
}
