/*
 * Copyright (c) 2019 SpaceToad and the BuildCraft team
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0. If a copy of the MPL was not
 * distributed with this file, You can obtain one at https://mozilla.org/MPL/2.0/
 */
package alexiil.mc.mod.pipes.blocks;

import net.minecraft.world.BlockView;

public class BlockPipeItemClay extends BlockPipe {

    public BlockPipeItemClay(Settings settings) {
        super(settings);
    }

    @Override
    public TilePipe createBlockEntity(BlockView var1) {
        return new TilePipeItemClay();
    }
}
