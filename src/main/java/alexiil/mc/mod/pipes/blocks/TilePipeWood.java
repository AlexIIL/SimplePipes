/*
 * Copyright (c) 2019 SpaceToad and the BuildCraft team
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0. If a copy of the MPL was not
 * distributed with this file, You can obtain one at https://mozilla.org/MPL/2.0/
 */
package alexiil.mc.mod.pipes.blocks;

import java.util.function.Function;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

import alexiil.mc.mod.pipes.pipe.PipeSpFlow;

public abstract class TilePipeWood extends TilePipeSided {

    private boolean lastRecv = true;

    public TilePipeWood(
        BlockEntityType<?> type, BlockPos pos, BlockState state, BlockPipe pipeBlock,
        Function<TilePipe, PipeSpFlow> flowConstructor
    ) {
        super(type, pos, state, pipeBlock, flowConstructor);
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
                tryExtract(dir, 1);
            }
        } else {
            lastRecv = false;
        }
    }

    /** @param pulses The number of redstone pulses to send. For item pipes this extracts up to the pulse count of
     *            items, has no effect on fluids. */
    public final void tryExtract(int pulses) {
        Direction dir = currentDirection();
        if (dir != null) {
            tryExtract(dir, pulses);
        }
    }

    protected abstract void tryExtract(Direction dir, int pulses);
}
