/*
 * Copyright (c) 2019 SpaceToad and the BuildCraft team
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0. If a copy of the MPL was not
 * distributed with this file, You can obtain one at https://mozilla.org/MPL/2.0/
 */
package alexiil.mc.mod.pipes.items;

import java.util.Random;

import javax.annotation.Nullable;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;

import alexiil.mc.lib.multipart.api.AbstractPart;
import alexiil.mc.lib.multipart.api.MultipartContainer.PartOffer;
import alexiil.mc.lib.multipart.api.MultipartHolder;
import alexiil.mc.lib.multipart.api.render.PartModelKey;
import alexiil.mc.lib.multipart.impl.LibMultiPart;
import alexiil.mc.lib.multipart.impl.client.model.SinglePartBakedModel;
import alexiil.mc.mod.pipes.client.render.ItemPlacemenentGhostRenderer;

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

    @Environment(EnvType.CLIENT)
    @Override
    public void render(net.minecraft.client.util.math.MatrixStack matrices, net.minecraft.client.render.VertexConsumerProvider vcp, PlayerEntity player, float partialTicks) {
        assert pos != null;
        assert modelKey != null;

        net.minecraft.client.render.model.BakedModel model = SinglePartBakedModel.create(modelKey);
        if (model == null) {
            return;
        }

        net.minecraft.client.render.VertexConsumer buffer = vcp.getBuffer(ItemPlacemenentGhostRenderer.GHOST);

        net.minecraft.client.MinecraftClient mc = net.minecraft.client.MinecraftClient.getInstance();
        net.minecraft.client.render.block.BlockModelRenderer blockRenderer = mc.getBlockRenderManager().getModelRenderer();

        matrices.push();
        matrices.translate(pos.getX(), pos.getY(), pos.getZ());
        blockRenderer.render(
            mc.world, model, LibMultiPart.BLOCK.getDefaultState(), pos, matrices, buffer, true, new Random(), 0, -1
        );
        matrices.pop();
    }
}
