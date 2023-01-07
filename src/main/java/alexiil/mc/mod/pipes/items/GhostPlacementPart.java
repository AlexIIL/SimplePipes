/*
 * Copyright (c) 2019 SpaceToad and the BuildCraft team
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0. If a copy of the MPL was not
 * distributed with this file, You can obtain one at https://mozilla.org/MPL/2.0/
 */
package alexiil.mc.mod.pipes.items;

import javax.annotation.Nullable;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.BlockModelRenderer;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;

import alexiil.mc.lib.multipart.api.AbstractPart;
import alexiil.mc.lib.multipart.api.MultipartContainer.PartOffer;
import alexiil.mc.lib.multipart.api.MultipartHolder;
import alexiil.mc.lib.multipart.api.render.PartModelKey;
import alexiil.mc.lib.multipart.impl.LibMultiPart;
import alexiil.mc.lib.multipart.impl.client.model.SinglePartBakedModel;
import alexiil.mc.mod.pipes.client.render.ItemPlacemenentGhostRenderer;
import net.minecraft.util.math.random.Random;

public abstract class GhostPlacementPart extends GhostPlacement {

    protected BlockPos pos;
    protected PartModelKey modelKey;

    protected boolean setup(@Nullable PartOffer offer) {
        return offer != null && setup(offer.getHolder());
    }

    protected boolean setup(MultipartHolder holder) {
        pos = holder.getContainer().getMultipartPos();
        modelKey = holder.getPart().getModelKey();
        return pos != null && modelKey != null;
    }

    protected boolean setup(AbstractPart part) {
        pos = part.holder.getContainer().getMultipartPos();
        modelKey = part.getModelKey();
        return pos != null && modelKey != null;
    }

    @Override
    public void render(MatrixStack matrices, VertexConsumerProvider vcp, PlayerEntity player, float partialTicks) {
        assert pos != null;
        assert modelKey != null;

        BakedModel model = SinglePartBakedModel.create(modelKey);
        if (model == null) {
            return;
        }

        VertexConsumer buffer = vcp.getBuffer(ItemPlacemenentGhostRenderer.GHOST);

        MinecraftClient mc = MinecraftClient.getInstance();
        BlockModelRenderer blockRenderer = mc.getBlockRenderManager().getModelRenderer();

        matrices.push();
        matrices.translate(pos.getX(), pos.getY(), pos.getZ());
        blockRenderer.render(
            mc.world, model, LibMultiPart.BLOCK.getDefaultState(), pos, matrices, buffer, true, Random.create(), 0, -1
        );
        matrices.pop();
    }
}
