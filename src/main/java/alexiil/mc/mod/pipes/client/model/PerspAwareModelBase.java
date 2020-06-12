/*
 * Copyright (c) 2019 SpaceToad and the BuildCraft team
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0. If a copy of the MPL was not
 * distributed with this file, You can obtain one at https://mozilla.org/MPL/2.0/
 */
package alexiil.mc.mod.pipes.client.model;

import java.util.List;
import java.util.Random;

import com.google.common.collect.ImmutableList;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.BakedQuad;
import net.minecraft.client.render.model.json.ModelOverrideList;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.texture.Sprite;
import net.minecraft.util.math.Direction;

import alexiil.mc.mod.pipes.util.SpriteUtil;

public class PerspAwareModelBase implements BakedModel {
    private final ImmutableList<BakedQuad> quads;
    private final Sprite particle;

    public PerspAwareModelBase(List<BakedQuad> quads, Sprite particle) {
        this.quads = quads == null ? ImmutableList.of() : ImmutableList.copyOf(quads);
        this.particle = particle != null ? particle : SpriteUtil.getMissingSprite();
    }

    public static List<BakedQuad> missingModel() {
        BakedModel model = ModelUtil.getMissingModel();
        return model.getQuads(Blocks.AIR.getDefaultState(), null, new Random());
    }

    @Override
    public List<BakedQuad> getQuads(BlockState state, Direction side, Random rand) {
        return side == null ? quads : ImmutableList.of();
    }

    @Override
    public boolean useAmbientOcclusion() {
        return false;
    }

    @Override
    public boolean hasDepth() {
        return false;
    }

    @Override
    public boolean isSideLit() {
        return true;
    }

    @Override
    public boolean isBuiltin() {
        return false;
    }

    @Override
    public Sprite getSprite() {
        return particle;
    }

    @Override
    public ModelTransformation getTransformation() {
        return ModelTransformation.NONE;
    }

    @Override
    public ModelOverrideList getOverrides() {
        return ModelOverrideList.EMPTY;
    }
}
