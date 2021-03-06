/*
 * Copyright (c) 2019 SpaceToad and the BuildCraft team
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0. If a copy of the MPL was not
 * distributed with this file, You can obtain one at https://mozilla.org/MPL/2.0/
 */
package alexiil.mc.mod.pipes;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.model.ModelLoadingRegistry;
import net.fabricmc.fabric.api.client.rendereregistry.v1.BlockEntityRendererRegistry;
import net.fabricmc.fabric.api.event.client.ClientSpriteRegistryCallback;
import net.fabricmc.fabric.api.event.client.ClientTickCallback;

import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.texture.SpriteAtlasTexture;

import alexiil.mc.mod.pipes.blocks.SimplePipeBlocks;
import alexiil.mc.mod.pipes.blocks.TilePipe;
import alexiil.mc.mod.pipes.client.model.SimplePipeModels;
import alexiil.mc.mod.pipes.client.model.part.FacadePartBaker;
import alexiil.mc.mod.pipes.client.model.part.FacadePartKey;
import alexiil.mc.mod.pipes.client.model.part.TankPartBaker;
import alexiil.mc.mod.pipes.client.model.part.TankPartModelKey;
import alexiil.mc.mod.pipes.client.render.ItemPlacemenentGhostRenderer;
import alexiil.mc.mod.pipes.client.render.PipeFluidTileRenderer;
import alexiil.mc.mod.pipes.client.render.PipeItemTileRenderer;
import alexiil.mc.mod.pipes.client.render.TankPartRenderer;
import alexiil.mc.mod.pipes.client.screen.SimplePipeScreens;
import alexiil.mc.mod.pipes.part.PartTank;

import alexiil.mc.lib.multipart.api.render.PartDynamicModelRegisterEvent;
import alexiil.mc.lib.multipart.api.render.PartStaticModelRegisterEvent;

public class SimplePipesClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        ModelLoadingRegistry.INSTANCE.registerVariantProvider(SimplePipeModels::createVariantProvider);
        ModelLoadingRegistry.INSTANCE.registerResourceProvider(SimplePipeModels::createResourceProvider);
        ModelLoadingRegistry.INSTANCE.registerAppender(SimplePipeModels::appendModels);
        ClientSpriteRegistryCallback.event(SpriteAtlasTexture.BLOCK_ATLAS_TEX).register(this::registerSprites);

        registerItemPipeRender(SimplePipeBlocks.WOODEN_PIPE_ITEM_TILE);
        registerItemPipeRender(SimplePipeBlocks.STONE_PIPE_ITEM_TILE);
        registerItemPipeRender(SimplePipeBlocks.CLAY_PIPE_ITEM_TILE);
        registerItemPipeRender(SimplePipeBlocks.IRON_PIPE_ITEM_TILE);
        registerItemPipeRender(SimplePipeBlocks.GOLD_PIPE_ITEM_TILE);
        registerItemPipeRender(SimplePipeBlocks.DIAMOND_PIPE_ITEM_TILE);

        registerFluidPipeRender(SimplePipeBlocks.WOODEN_PIPE_FLUID_TILE);
        registerFluidPipeRender(SimplePipeBlocks.STONE_PIPE_FLUID_TILE);
        registerFluidPipeRender(SimplePipeBlocks.CLAY_PIPE_FLUID_TILE);
        registerFluidPipeRender(SimplePipeBlocks.IRON_PIPE_FLUID_TILE);
        registerFluidPipeRender(SimplePipeBlocks.SPONGE_PIPE_FLUID_TILE);

        setCutoutLayer(SimplePipeBlocks.WOODEN_PIPE_ITEMS);
        setCutoutLayer(SimplePipeBlocks.STONE_PIPE_ITEMS);
        setCutoutLayer(SimplePipeBlocks.CLAY_PIPE_ITEMS);
        setCutoutLayer(SimplePipeBlocks.IRON_PIPE_ITEMS);
        setCutoutLayer(SimplePipeBlocks.GOLD_PIPE_ITEMS);
        setCutoutLayer(SimplePipeBlocks.DIAMOND_PIPE_ITEMS);

        setCutoutLayer(SimplePipeBlocks.WOODEN_PIPE_FLUIDS);
        setCutoutLayer(SimplePipeBlocks.STONE_PIPE_FLUIDS);
        setCutoutLayer(SimplePipeBlocks.CLAY_PIPE_FLUIDS);
        setCutoutLayer(SimplePipeBlocks.IRON_PIPE_FLUIDS);
        setCutoutLayer(SimplePipeBlocks.SPONGE_PIPE_FLUIDS);

        PartStaticModelRegisterEvent.EVENT.register(model -> {
            model.register(TankPartModelKey.class, TankPartBaker.INSTANCE);
            model.register(FacadePartKey.class, FacadePartBaker.INSTANCE);
        });
        PartDynamicModelRegisterEvent.EVENT.register(renderer -> {
            renderer.register(PartTank.class, new TankPartRenderer());
        });
        SimplePipeScreens.load();
        // RenderMatrixType.FROM_WORLD_ORIGIN.addRenderer(ItemPlacemenentGhostRenderer::render);
        ClientTickCallback.EVENT.register(mc -> {
            ItemPlacemenentGhostRenderer.clientTick();
        });
    }

    private static void setCutoutLayer(Block block) {
        BlockRenderLayerMap.INSTANCE.putBlock(block, RenderLayer.getCutout());
    }

    private static <T extends TilePipe> void registerItemPipeRender(BlockEntityType<T> type) {
        BlockEntityRendererRegistry.INSTANCE.register(type, PipeItemTileRenderer::new);
    }

    private static <T extends TilePipe> void registerFluidPipeRender(BlockEntityType<T> type) {
        BlockEntityRendererRegistry.INSTANCE.register(type, PipeFluidTileRenderer::new);
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
        registry.register(SimplePipes.id("pipe_sponge_fluid"));
    }
}
