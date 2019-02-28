/*
 * Copyright (c) 2019 SpaceToad and the BuildCraft team
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0. If a copy of the MPL was not
 * distributed with this file, You can obtain one at https://mozilla.org/MPL/2.0/
 */
package alexiil.mc.mod.pipes.blocks;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Direction;

import alexiil.mc.lib.attributes.Simulation;
import alexiil.mc.lib.attributes.item.IItemExtractable;
import alexiil.mc.lib.attributes.item.impl.EmptyItemExtractable;

public class TilePipeWood extends TilePipeSided {

    private boolean lastRecv = true;

    public TilePipeWood() {
        super(SimplePipeBlocks.WOODEN_PIPE_TILE, SimplePipeBlocks.WOODEN_PIPE);
    }

    @Override
    protected boolean canConnect(Direction dir, BlockEntity oTile) {
        return false;
    }

    @Override
    protected boolean canFaceDirection(Direction dir) {
        if (getNeighbourPipe(dir) != null) {
            return false;
        }
        return getNeighbourExtractable(dir) != EmptyItemExtractable.NULL;
    }

    @Override
    public void tick() {
        super.tick();
        if (world.isClient) {
            return;
        }
        Direction dir = currentDirection();
        if (dir == null) {
            return;
        }

        if (world.isReceivingRedstonePower(getPos())) {
            if (!lastRecv) {
                lastRecv = true;
                tryExtract(dir);
            }
        } else {
            lastRecv = false;
        }
    }

    private void tryExtract(Direction dir) {
        IItemExtractable extractable = getNeighbourExtractable(dir);
        ItemStack stack = extractable.attemptAnyExtraction(1, Simulation.ACTION);

        if (!stack.isEmpty()) {
            insertItemsForce(stack, dir, null, EXTRACT_SPEED);
        }
    }
}
