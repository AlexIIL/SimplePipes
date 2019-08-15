/*
 * Copyright (c) 2019 SpaceToad and the BuildCraft team
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0. If a copy of the MPL was not
 * distributed with this file, You can obtain one at https://mozilla.org/MPL/2.0/
 */
package alexiil.mc.mod.pipes.blocks;

import java.util.function.Function;

import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.math.Direction;

import alexiil.mc.mod.pipes.part.PipeFlow;

public abstract class TilePipeIron extends TilePipeSided {

    public TilePipeIron(BlockEntityType<?> type, BlockPipe pipeBlock, Function<TilePipe, PipeFlow> flowConstructor) {
        super(type, pipeBlock, flowConstructor);
    }

    @Override
    protected boolean canConnect(Direction dir) {
        return false;
    }

    @Override
    protected boolean canFaceDirection(Direction dir) {
        return isConnected(dir);
    }
}
