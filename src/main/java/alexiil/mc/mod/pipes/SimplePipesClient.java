/*
 * Copyright (c) 2019 SpaceToad and the BuildCraft team
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0. If a copy of the MPL was not
 * distributed with this file, You can obtain one at https://mozilla.org/MPL/2.0/
 */
package alexiil.mc.mod.pipes;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.model.loading.v1.ModelLoadingPlugin;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;

import alexiil.mc.mod.pipes.client.model.SimplePipeModels;
import alexiil.mc.mod.pipes.client.model.part.FacadePartBaker;
import alexiil.mc.mod.pipes.client.model.part.FacadePartKey;
import alexiil.mc.mod.pipes.client.model.part.PipeSpPartBaker;
import alexiil.mc.mod.pipes.client.model.part.PipeSpPartKey;
import alexiil.mc.mod.pipes.client.model.part.TankPartBaker;
import alexiil.mc.mod.pipes.client.model.part.TankPartModelKey;
import alexiil.mc.mod.pipes.client.render.GhostVertexConsumer;
import alexiil.mc.mod.pipes.client.render.ItemPlacementGhostRenderer;
import alexiil.mc.mod.pipes.client.render.PipePartRenderer;
import alexiil.mc.mod.pipes.client.render.TankPartRenderer;
import alexiil.mc.mod.pipes.client.screen.SimplePipeScreens;
import alexiil.mc.mod.pipes.part.PartTank;
import alexiil.mc.mod.pipes.pipe.PartSpPipe;

import alexiil.mc.lib.multipart.api.render.PartDynamicModelRegisterEvent;
import alexiil.mc.lib.multipart.api.render.PartStaticModelRegisterEvent;

public class SimplePipesClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        ModelLoadingPlugin.register(SimplePipeModels::modelLoadingPlugin);

        PartStaticModelRegisterEvent.EVENT.register(model -> {
            model.register(TankPartModelKey.class, TankPartBaker.INSTANCE);
            model.register(FacadePartKey.class, FacadePartBaker.INSTANCE);
            model.register(PipeSpPartKey.class, new PipeSpPartBaker(model::getSprite));
        });
        PartDynamicModelRegisterEvent.EVENT.register(renderer -> {
            renderer.register(PartTank.class, new TankPartRenderer());
            renderer.register(PartSpPipe.class, new PipePartRenderer());
        });
        SimplePipeScreens.load();
        // RenderMatrixType.FROM_WORLD_ORIGIN.addRenderer(ItemPlacemenentGhostRenderer::render);
        ClientTickEvents.END_CLIENT_TICK.register(client -> ItemPlacementGhostRenderer.clientTick());

        WorldRenderEvents.START.register(GhostVertexConsumer::renderStart);
        WorldRenderEvents.END.register(ItemPlacementGhostRenderer::render);
    }
}
