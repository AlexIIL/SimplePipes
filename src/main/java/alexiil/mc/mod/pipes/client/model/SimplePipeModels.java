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

import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;

import alexiil.mc.mod.pipes.SimplePipes;
import alexiil.mc.mod.pipes.client.model.DelayedBakedModel.IModelBaker;

public class SimplePipeModels {

    public static final ModelIdentifier TANK_BLOCK_ID;

    static {
        TANK_BLOCK_ID = new ModelIdentifier("simple_pipes", "tank", "");
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
            if (resourceId.getNamespace().equals(SimplePipes.MODID)) {
                if (resourceId.getPath().equals("facade")) {
                    return ModelFacadeItem::new;
                }
                return null;
            }
            return null;
        }
        return null;
    }
}
