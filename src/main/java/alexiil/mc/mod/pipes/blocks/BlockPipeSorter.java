/*
 * Copyright (c) 2019 SpaceToad and the BuildCraft team
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0. If a copy of the MPL was not
 * distributed with this file, You can obtain one at https://mozilla.org/MPL/2.0/
 */
package alexiil.mc.mod.pipes.blocks;

import net.fabricmc.fabric.api.container.ContainerProviderRegistry;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import alexiil.mc.mod.pipes.container.SimplePipeContainers;
import alexiil.mc.mod.pipes.pipe.PipeSpDef;

public abstract class BlockPipeSorter extends BlockPipe {

    public BlockPipeSorter(Settings settings, PipeSpDef pipeDef) {
        super(settings, pipeDef);
    }

    @Override
    public ActionResult onUse(
        BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hitResult
    ) {
        BlockEntity be = world.getBlockEntity(pos);

        if (be instanceof TilePipeItemDiamond) {
            if (!world.isClient) {
                ContainerProviderRegistry.INSTANCE
                    .openContainer(SimplePipeContainers.PIPE_DIAMOND_ITEM, player, (buffer) -> {
                        buffer.writeBlockPos(pos);
                    });
            }
            return ActionResult.SUCCESS;
        }

        return super.onUse(state, world, pos, player, hand, hitResult);
    }

}
