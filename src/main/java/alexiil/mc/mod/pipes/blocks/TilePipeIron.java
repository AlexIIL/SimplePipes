/*
 * Copyright (c) 2019 SpaceToad and the BuildCraft team
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0. If a copy of the MPL was not
 * distributed with this file, You can obtain one at https://mozilla.org/MPL/2.0/
 */
package alexiil.mc.mod.pipes.blocks;

import java.util.function.Function;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

import alexiil.mc.mod.pipes.pipe.ISimplePipe;
import alexiil.mc.mod.pipes.pipe.PipeSpFlowItem;
import alexiil.mc.mod.pipes.pipe.PipeSpFlow;

public abstract class TilePipeIron extends TilePipeSided {

    public TilePipeIron(
        BlockEntityType<?> type, BlockPos pos, BlockState state, BlockPipe pipeBlock,
        Function<TilePipe, PipeSpFlow> flowConstructor
    ) {
        super(type, pos, state, pipeBlock, flowConstructor);
    }

    @Override
    protected boolean canFaceDirection(Direction dir) {
        BlockEntity other = world.getBlockEntity(getPos().offset(dir));
        if (other instanceof ISimplePipe) {
            return (getFlow() instanceof PipeSpFlowItem) == (((ISimplePipe) other).getFlow() instanceof PipeSpFlowItem);
        }
        return getFlow().hasInsertable(dir);
    }
}
