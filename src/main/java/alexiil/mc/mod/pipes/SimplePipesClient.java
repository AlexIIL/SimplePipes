/*
 * Copyright (c) 2019 SpaceToad and the BuildCraft team
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0. If a copy of the MPL was not
 * distributed with this file, You can obtain one at https://mozilla.org/MPL/2.0/
 */
package alexiil.mc.mod.pipes;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.model.ModelLoadingRegistry;
import net.fabricmc.fabric.api.client.render.BlockEntityRendererRegistry;
import net.fabricmc.fabric.api.event.client.ClientSpriteRegistryCallback;

import net.minecraft.client.texture.SpriteAtlasTexture;

import alexiil.mc.lib.multipart.api.render.MultipartRenderRegistry;
import alexiil.mc.mod.pipes.blocks.TilePipe;
import alexiil.mc.mod.pipes.client.model.SimplePipeModels;
import alexiil.mc.mod.pipes.client.model.part.FacadePartBaker;
import alexiil.mc.mod.pipes.client.model.part.FacadePartKey;
import alexiil.mc.mod.pipes.client.model.part.TankPartBaker;
import alexiil.mc.mod.pipes.client.model.part.TankPartModelKey;
import alexiil.mc.mod.pipes.client.render.PipeBlockEntityRenderer;
import alexiil.mc.mod.pipes.client.render.TankPartRenderer;
import alexiil.mc.mod.pipes.client.screen.SimplePipeScreens;
import alexiil.mc.mod.pipes.part.PartTank;

public class SimplePipesClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        ModelLoadingRegistry.INSTANCE.registerVariantProvider(SimplePipeModels::createVariantProvider);
        ModelLoadingRegistry.INSTANCE.registerResourceProvider(SimplePipeModels::createResourceProvider);
        ModelLoadingRegistry.INSTANCE.registerAppender(SimplePipeModels::appendModels);
        ClientSpriteRegistryCallback.event(SpriteAtlasTexture.BLOCK_ATLAS_TEX).register(this::registerSprites);
        BlockEntityRendererRegistry.INSTANCE.register(TilePipe.class, new PipeBlockEntityRenderer());
        MultipartRenderRegistry.registerRenderer(PartTank.class, new TankPartRenderer());
        MultipartRenderRegistry.registerBaker(TankPartModelKey.class, TankPartBaker.INSTANCE);
        MultipartRenderRegistry.registerBaker(FacadePartKey.class, FacadePartBaker.INSTANCE);
        SimplePipeScreens.load();
    }

    private void registerSprites(SpriteAtlasTexture atlasTexture, ClientSpriteRegistryCallback.Registry registry) {
        registry.register(SimplePipes.id("pipe_wooden_item_clear"));
        registry.register(SimplePipes.id("pipe_wooden_item_filled"));
        registry.register(SimplePipes.id("pipe_wooden_fluid_clear"));
        registry.register(SimplePipes.id("pipe_wooden_fluid_filled"));
        registry.register(SimplePipes.id("pipe_stone_item"));
        registry.register(SimplePipes.id("pipe_stone_fluid"));
        registry.register(SimplePipes.id("pipe_iron_fluid_clear"));
        registry.register(SimplePipes.id("pipe_iron_fluid_filled"));
        registry.register(SimplePipes.id("pipe_iron_item_clear"));
        registry.register(SimplePipes.id("pipe_iron_item_filled"));
        registry.register(SimplePipes.id("pipe_gold_item"));
        registry.register(SimplePipes.id("pipe_diamond_item"));
        registry.register(SimplePipes.id("pipe_diamond_item_down"));
        registry.register(SimplePipes.id("pipe_diamond_item_up"));
        registry.register(SimplePipes.id("pipe_diamond_item_north"));
        registry.register(SimplePipes.id("pipe_diamond_item_south"));
        registry.register(SimplePipes.id("pipe_diamond_item_west"));
        registry.register(SimplePipes.id("pipe_diamond_item_east"));
    }
}
