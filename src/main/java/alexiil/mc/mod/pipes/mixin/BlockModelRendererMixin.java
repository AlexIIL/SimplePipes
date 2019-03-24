/*
 * Copyright (c) 2019 SpaceToad and the BuildCraft team
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0. If a copy of the MPL was not
 * distributed with this file, You can obtain one at https://mozilla.org/MPL/2.0/
 */
package alexiil.mc.mod.pipes.mixin;

import java.util.Random;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

import net.minecraft.block.BlockState;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.block.BlockModelRenderer;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ExtendedBlockView;

import alexiil.mc.mod.pipes.client.model.IWorldDependentModel;

@Mixin(BlockModelRenderer.class)
public class BlockModelRendererMixin {

    @ModifyArg(
        method = {
            "net/minecraft/client/render/block/BlockModelRenderer.tesselate(Lnet/minecraft/world/ExtendedBlockView;Lnet/minecraft/client/render/model/BakedModel;Lnet/minecraft/block/BlockState;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/client/render/BufferBuilder;ZLjava/util/Random;J)Z" },
        at = @At(
            value = "INVOKE",
            target = "net/minecraft/client/render/block/BlockModelRenderer.tesselateSmooth(Lnet/minecraft/world/ExtendedBlockView;"
                + "Lnet/minecraft/client/render/model/BakedModel;Lnet/minecraft/block/BlockState;Lnet/minecraft/util/math/BlockPos;"
                + "Lnet/minecraft/client/render/BufferBuilder;ZLjava/util/Random;J)Z"))
    public BakedModel tesselate_get_model_smooth(ExtendedBlockView view, BakedModel model, BlockState state,
        BlockPos pos, BufferBuilder builder, boolean b, Random r, long l) {
        return replaceModel(view, pos, state, model);
    }

    @ModifyArg(
        method = {
            "net/minecraft/client/render/block/BlockModelRenderer.tesselate(Lnet/minecraft/world/ExtendedBlockView;Lnet/minecraft/client/render/model/BakedModel;Lnet/minecraft/block/BlockState;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/client/render/BufferBuilder;ZLjava/util/Random;J)Z" },
        at = @At(
            value = "INVOKE",
            target = "net/minecraft/client/render/block/BlockModelRenderer.tesselateFlat(Lnet/minecraft/world/ExtendedBlockView;"
                + "Lnet/minecraft/client/render/model/BakedModel;Lnet/minecraft/block/BlockState;Lnet/minecraft/util/math/BlockPos;"
                + "Lnet/minecraft/client/render/BufferBuilder;ZLjava/util/Random;J)Z"))
    public BakedModel tesselate_get_model_flat(ExtendedBlockView view, BakedModel model, BlockState state, BlockPos pos,
        BufferBuilder builder, boolean b, Random r, long l) {
        return replaceModel(view, pos, state, model);
    }

    @Unique
    private static BakedModel replaceModel(ExtendedBlockView view, BlockPos pos, BlockState state, BakedModel model) {

        if (model instanceof IWorldDependentModel) {
            model = ((IWorldDependentModel) model).getRealModel(view, pos, state);
        }

        return model;
    }
}
