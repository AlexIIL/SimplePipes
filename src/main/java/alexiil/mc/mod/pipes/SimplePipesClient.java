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
import net.minecraft.util.Identifier;

import alexiil.mc.mod.pipes.blocks.TilePipe;
import alexiil.mc.mod.pipes.blocks.TileTank;
import alexiil.mc.mod.pipes.client.model.SimplePipeModels;
import alexiil.mc.mod.pipes.client.render.PipeBlockEntityRenderer;
import alexiil.mc.mod.pipes.client.render.TankBlockEntityRenderer;
import alexiil.mc.mod.pipes.client.screen.SimplePipeScreens;

public class SimplePipesClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        ModelLoadingRegistry.INSTANCE.registerVariantProvider(SimplePipeModels::createVariantProvider);
        ModelLoadingRegistry.INSTANCE.registerResourceProvider(SimplePipeModels::createResourceProvider);
        ClientSpriteRegistryCallback.registerBlockAtlas(this::registerSprites);
        BlockEntityRendererRegistry.INSTANCE.register(TilePipe.class, new PipeBlockEntityRenderer());
        BlockEntityRendererRegistry.INSTANCE.register(TileTank.class, new TankBlockEntityRenderer());
        SimplePipeScreens.load();
    }

    private void registerSprites(SpriteAtlasTexture atlasTexture, ClientSpriteRegistryCallback.Registry registry) {
        registry.register(new Identifier(SimplePipes.MODID, "pipe_wooden_item_clear"));
        registry.register(new Identifier(SimplePipes.MODID, "pipe_wooden_item_filled"));
        registry.register(new Identifier(SimplePipes.MODID, "pipe_stone_item"));
        registry.register(new Identifier(SimplePipes.MODID, "pipe_iron_item_clear"));
        registry.register(new Identifier(SimplePipes.MODID, "pipe_iron_item_filled"));
    }
}
