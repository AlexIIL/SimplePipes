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
public class BlockPipeItemWooden extends BlockPipeSided implements BlockPipeItem {

    public BlockPipeItemWooden(Settings settings) {
        super(settings, SimplePipeParts.WOODEN_PIPE_ITEMS);
    }

    @Override
    public TilePipeSided createBlockEntity(BlockPos pos, BlockState state) {
        return new TilePipeItemWood(pos, state);
    }
}
