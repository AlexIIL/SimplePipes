/*
 * Copyright (c) 2019 SpaceToad and the BuildCraft team
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0. If a copy of the MPL was not
 * distributed with this file, You can obtain one at https://mozilla.org/MPL/2.0/
 */
package alexiil.mc.mod.pipes.client.model;

import java.util.List;
import java.util.Random;
import java.util.function.Supplier;

import javax.annotation.Nullable;

import com.google.common.collect.ImmutableList;

import net.fabricmc.fabric.api.renderer.v1.model.FabricBakedModel;
import net.fabricmc.fabric.api.renderer.v1.render.RenderContext;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.BakedQuad;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ExtendedBlockView;

import alexiil.mc.mod.pipes.blocks.BlockPipe;
import alexiil.mc.mod.pipes.blocks.TilePipe;
import alexiil.mc.mod.pipes.blocks.TilePipe.PipeBlockModelState;

public class PipeBlockModel extends PerspAwareModelBase implements FabricBakedModel {

    public PipeBlockModel(BlockPipe pipeBlock) {
        super(ImmutableList.of(), PipeBaseModelGenStandard.getCenterSprite(pipeBlock));
    }

    @Override
    public boolean isVanillaAdapter() {
        return false;
    }

    @Override
    public void emitBlockQuads(ExtendedBlockView blockView, BlockState state, BlockPos pos, Supplier<
        Random> randomSupplier, RenderContext context) {

        BakedModel model;

        BlockEntity tile = blockView.getBlockEntity(pos);
        if (tile instanceof TilePipe) {
            model = bakeModel(((TilePipe) tile).blockModelState);
        } else {
            model = bakeModel(null);
        }
        context.fallbackConsumer().accept(model);
    }

    private BakedModel bakeModel(@Nullable PipeBlockModelState state) {
        if (state == null) {
            state = new PipeBlockModelState(null, (byte) 0);
        }
        List<BakedQuad> quads = PipeBaseModelGenStandard.generateCutout(state);
        return new PerspAwareModelBase(quads, quads.isEmpty() ? getSprite() : quads.get(0).getSprite());
    }

    @Override
    public void emitItemQuads(ItemStack stack, Supplier<Random> randomSupplier, RenderContext context) {
        // This isn't an item model
    }
}
