/*
 * Copyright (c) 2019 SpaceToad and the BuildCraft team
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0. If a copy of the MPL was not
 * distributed with this file, You can obtain one at https://mozilla.org/MPL/2.0/
 */
package alexiil.mc.mod.pipes.client.model;

import net.fabricmc.fabric.api.client.model.loading.v1.ModelLoadingPlugin;
import net.fabricmc.fabric.api.client.model.loading.v1.ModelResolver;

import net.minecraft.client.render.model.UnbakedModel;
import net.minecraft.util.Identifier;

import alexiil.mc.mod.pipes.SimplePipes;
import alexiil.mc.mod.pipes.client.model.DelayedBakedModel.IModelBaker;

public class SimplePipeModels {

    public static final Identifier TANK_BLOCK_ID = Identifier.of("simple_pipes", "block/tank");

    public static void modelLoadingPlugin(ModelLoadingPlugin.Context context) {
        context.addModels(TANK_BLOCK_ID);
        context.resolveModel().register(SimplePipeModels::modelResolver);
    }

    private static UnbakedModel modelResolver(ModelResolver.Context context) {
        IModelBaker supplier = getModelBaker(context.id());
        if (supplier != null) {
            return new DelayedBakedModel(supplier);
        }
        return null;
    }

    private static IModelBaker getModelBaker(Identifier resourceId) {
        if (resourceId.getNamespace().equals(SimplePipes.MODID)) {
            if (resourceId.getPath().equals("item/facade")) {
                return ModelFacadeItem::new;
            }
            return null;
        }
        return null;
    }
}
