/*
 * Copyright (c) 2019 SpaceToad and the BuildCraft team
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0. If a copy of the MPL was not
 * distributed with this file, You can obtain one at https://mozilla.org/MPL/2.0/
 */
package alexiil.mc.mod.pipes.client.model;

import net.fabricmc.fabric.api.client.model.ModelProviderContext;
import net.fabricmc.fabric.api.client.model.ModelResourceProvider;
import net.fabricmc.fabric.api.client.model.ModelVariantProvider;

import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;

import alexiil.mc.mod.pipes.SimplePipes;
import alexiil.mc.mod.pipes.blocks.SimplePipeBlocks;

public class SimplePipeModels {

    public static ModelResourceProvider createResourceProvider(ResourceManager manager) {
        return (Identifier resourceId, ModelProviderContext context) -> {
            return null;
        };
    }

    public static ModelVariantProvider createVariantProvider(ResourceManager manager) {
        return (ModelIdentifier resourceId, ModelProviderContext context) -> {
            BakedModel baked = getModel(manager, resourceId, context);
            if (baked != null) {
                return new PreBakedModel(baked);
            }
            return null;
        };
    }

    private static BakedModel getModel(ResourceManager manager, ModelIdentifier resourceId,
        ModelProviderContext context) {
        if ("inventory".equals(resourceId.getVariant())) {
            return null;
        }

        switch (resourceId.getNamespace()) {
            case SimplePipes.MODID: {

                switch (resourceId.getPath()) {

                    case "pipe_wooden_item":
                        return new PipeBlockModel(SimplePipeBlocks.WOODEN_PIPE_ITEMS);
                    case "pipe_stone_item":
                        return new PipeBlockModel(SimplePipeBlocks.STONE_PIPE_ITEMS);
                    case "pipe_clay_item":
                        return new PipeBlockModel(SimplePipeBlocks.CLAY_PIPE_ITEMS);
                    case "pipe_iron_item":
                        return new PipeBlockModel(SimplePipeBlocks.IRON_PIPE_ITEMS);

                    case "pipe_wooden_fluid":
                        return new PipeBlockModel(SimplePipeBlocks.WOODEN_PIPE_FLUIDS);
                    case "pipe_stone_fluid":
                        return new PipeBlockModel(SimplePipeBlocks.STONE_PIPE_FLUIDS);
                    case "pipe_clay_fluid":
                        return new PipeBlockModel(SimplePipeBlocks.CLAY_PIPE_FLUIDS);
                    case "pipe_iron_fluid":
                        return new PipeBlockModel(SimplePipeBlocks.IRON_PIPE_FLUIDS);

                    default:
                        return null;
                }
            }
            default: {
                return null;
            }
        }
    }
}
