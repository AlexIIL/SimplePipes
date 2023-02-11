/*
 * Copyright (c) 2019 SpaceToad and the BuildCraft team
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0. If a copy of the MPL was not
 * distributed with this file, You can obtain one at https://mozilla.org/MPL/2.0/
 */
package alexiil.mc.mod.pipes.blocks;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;

import alexiil.mc.mod.pipes.pipe.PipeSpFlowItem;

@Deprecated
public class TilePipeItemGold extends TilePipe {
    public TilePipeItemGold(BlockPos pos, BlockState state) {
        super(SimplePipeBlocks.GOLD_PIPE_ITEM_TILE, pos, state, SimplePipeBlocks.GOLD_PIPE_ITEMS, PipeSpFlowItem::new);
    }
}
