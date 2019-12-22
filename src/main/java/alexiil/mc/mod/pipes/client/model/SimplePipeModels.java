/*
 * Copyright (c) 2019 SpaceToad and the BuildCraft team
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0. If a copy of the MPL was not
 * distributed with this file, You can obtain one at https://mozilla.org/MPL/2.0/
 */
package alexiil.mc.mod.pipes.client.model;

import java.util.function.Consumer;

import net.fabricmc.fabric.api.client.model.ModelProviderContext;
import net.fabricmc.fabric.api.client.model.ModelResourceProvider;
import net.fabricmc.fabric.api.client.model.ModelVariantProvider;

import net.minecraft.block.Block;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import alexiil.mc.mod.pipes.SimplePipes;
import alexiil.mc.mod.pipes.blocks.BlockPipe;
import alexiil.mc.mod.pipes.client.model.DelayedBakedModel.IModelBaker;

public class SimplePipeModels {

    public static final ModelIdentifier TANK_BLOCK_ID;

    static {
        TANK_BLOCK_ID = new ModelIdentifier("simple_pipes:tank");
    }

    public static ModelResourceProvider createResourceProvider(ResourceManager manager) {
        return (Identifier resourceId, ModelProviderContext context) -> {
            return null;
        };
    }

    public static ModelVariantProvider createVariantProvider(ResourceManager manager) {
        return (ModelIdentifier resourceId, ModelProviderContext context) -> {
            IModelBaker supplier = getModelBaker(manager, resourceId, context);
            if (supplier != null) {
                return new DelayedBakedModel(supplier);
            }
            return null;
        };
    }

    public static void appendModels(ResourceManager manager, Consumer<ModelIdentifier> out) {
        out.accept(TANK_BLOCK_ID);
    }

    private static IModelBaker getModelBaker(
        ResourceManager manager, ModelIdentifier resourceId, ModelProviderContext context
    ) {
        if ("inventory".equals(resourceId.getVariant())) {
            switch (resourceId.getNamespace()) {
                case SimplePipes.MODID:
                    switch (resourceId.getPath()) {
                        case "facade":
                            return ModelFacadeItem::new;
                        default:
                            return null;
                    }
                default: {
                    return null;
                }
            }
        }
        Block block = Registry.BLOCK.get(new Identifier(resourceId.getNamespace(), resourceId.getPath()));
        if (block instanceof BlockPipe) {
            return ctx -> new PipeBlockModel(ctx, (BlockPipe) block);
        }
        switch (resourceId.getNamespace()) {
            // case SimplePipes.MODID: {
            // switch (resourceId.getPath()) {
            // case "pipe_wooden_item": return ctx -> new PipeBlockModel(ctx, SimplePipeBlocks.WOODEN_PIPE_ITEMS);
            // case "pipe_stone_item": return ctx -> new PipeBlockModel(SimplePipeBlocks.STONE_PIPE_ITEMS);
            // case "pipe_clay_item":return () -> new PipeBlockModel(SimplePipeBlocks.CLAY_PIPE_ITEMS);
            // case "pipe_iron_item": return () -> new PipeBlockModel(SimplePipeBlocks.IRON_PIPE_ITEMS);
            // case "pipe_gold_item":
            // return () -> new PipeBlockModel(SimplePipeBlocks.GOLD_PIPE_ITEMS);
            // case "pipe_diamond_item":
            // return () -> new PipeBlockModel(SimplePipeBlocks.DIAMOND_PIPE_ITEMS);
            //
            // case "pipe_wooden_fluid":
            // return () -> new PipeBlockModel(SimplePipeBlocks.WOODEN_PIPE_FLUIDS);
            // case "pipe_stone_fluid":
            // return () -> new PipeBlockModel(SimplePipeBlocks.STONE_PIPE_FLUIDS);
            // case "pipe_clay_fluid":
            // return () -> new PipeBlockModel(SimplePipeBlocks.CLAY_PIPE_FLUIDS);
            // case "pipe_iron_fluid":
            // return () -> new PipeBlockModel(SimplePipeBlocks.IRON_PIPE_FLUIDS);
            //
            // default:
            // return null;
            // }
            // }
            default: {
                return null;
            }
        }
    }
}
