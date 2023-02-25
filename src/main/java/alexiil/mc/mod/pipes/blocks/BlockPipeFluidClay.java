/*
 * Copyright (c) 2019 SpaceToad and the BuildCraft team
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0. If a copy of the MPL was not
 * distributed with this file, You can obtain one at https://mozilla.org/MPL/2.0/
 */
package alexiil.mc.mod.pipes.blocks;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;

import alexiil.mc.mod.pipes.part.SimplePipeParts;

@Deprecated
public class BlockPipeFluidClay extends BlockPipe implements BlockPipeFluid {

    public BlockPipeFluidClay(Settings settings) {
        super(settings, SimplePipeParts.CLAY_PIPE_FLUIDS);
    }

    @Override
    public TilePipe createBlockEntity(BlockPos pos, BlockState state) {
        return new TilePipeFluidClay(pos, state);
    }
}
