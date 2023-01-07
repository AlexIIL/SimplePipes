/*
 * Copyright (c) 2019 SpaceToad and the BuildCraft team
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0. If a copy of the MPL was not
 * distributed with this file, You can obtain one at https://mozilla.org/MPL/2.0/
 */
package alexiil.mc.mod.pipes.client.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.BakedQuad;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Direction.Axis;
import net.minecraft.util.math.Direction.AxisDirection;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;

import alexiil.mc.mod.pipes.util.SpriteUtil;

/** Provides various utilities for creating {@link MutableQuad} out of various position information, such as a single
 * face of a cuboid. */
public class ModelUtil {

    public static BakedModel getModel(ModelIdentifier modelLocation) {
        return MinecraftClient.getInstance().getBakedModelManager().getModel(modelLocation);
    }

    public static BakedModel getMissingModel() {
        return MinecraftClient.getInstance().getBakedModelManager().getMissingModel();
    }

    public static BakedModel getItemModel(ItemStack stack) {
        return MinecraftClient.getInstance().getItemRenderer().getModels().getModel(stack);
    }

    public static BakedModel getBlockModel(BlockState state) {
        return MinecraftClient.getInstance().getBakedModelManager().getBlockModels().getModel(state);
    }

    public static List<BakedQuad> getQuads(BlockState state, int seed) {
        return getQuads(getBlockModel(state), state, seed);
    }

    public static List<BakedQuad> getQuads(BakedModel model, BlockState state, int seed) {
        if (model == null) {
            return Collections.emptyList();
        }
        List<BakedQuad> quads = new ArrayList<>();
        quads.addAll(model.getQuads(state, null, Random.create(seed)));
        for (Direction dir : Direction.values()) {
            quads.addAll(model.getQuads(state, dir, Random.create(seed)));
        }
        return quads;
    }

    /** Mutable class for holding the current {@link #minU}, {@link #maxU}, {@link #minV} and {@link #maxV} of a
     * face. */
    public static class UvFaceData {
        private static final UvFaceData DEFAULT = new UvFaceData(0, 0, 1, 1);

        public float minU, maxU, minV, maxV;

        public UvFaceData() {}

        public UvFaceData(UvFaceData from) {
            this.minU = from.minU;
            this.maxU = from.maxU;
            this.minV = from.minV;
            this.maxV = from.maxV;
        }

        public static UvFaceData from16(double minU, double minV, double maxU, double maxV) {
            return new UvFaceData(minU / 16.0, minV / 16.0, maxU / 16.0, maxV / 16.0);
        }

        public static UvFaceData from16(int minU, int minV, int maxU, int maxV) {
            return new UvFaceData(minU / 16f, minV / 16f, maxU / 16f, maxV / 16f);
        }

        public UvFaceData(float uMin, float vMin, float uMax, float vMax) {
            this.minU = uMin;
            this.maxU = uMax;
            this.minV = vMin;
            this.maxV = vMax;
        }

        public UvFaceData(double minU, double minV, double maxU, double maxV) {
            this((float) minU, (float) minV, (float) maxU, (float) maxV);
        }

        public UvFaceData andSub(UvFaceData sub) {
            float size_u = maxU - minU;
            float size_v = maxV - minV;

            float min_u = minU + sub.minU * size_u;
            float min_v = minV + sub.minV * size_v;
            float max_u = minU + sub.maxU * size_u;
            float max_v = minV + sub.maxV * size_v;

            return new UvFaceData(min_u, min_v, max_u, max_v);
        }

        public UvFaceData inParent(UvFaceData parent) {
            return parent.andSub(this);
        }

        @Override
        public String toString() {
            return "[ " + minU * 16 + ", " + minV * 16 + ", " + maxU * 16 + ", " + maxV * 16 + " ]";
        }

        public void inSprite(Sprite sprite) {
            minU = SpriteUtil.getU(sprite, minU);
            maxU = SpriteUtil.getU(sprite, maxU);
            minV = SpriteUtil.getV(sprite, minV);
            maxV = SpriteUtil.getV(sprite, maxV);
        }

        // public void inSprite(ISprite sprite) {
        // minU = (float) sprite.getInterpU(minU);
        // minV = (float) sprite.getInterpV(minV);
        // maxU = (float) sprite.getInterpU(maxU);
        // maxV = (float) sprite.getInterpV(maxV);
        // }
    }

    public static class TexturedFace {
        public Sprite sprite;
        public UvFaceData faceData = new UvFaceData();
    }

    public static MutableQuad createFace(Direction face, Vec3d a, Vec3d b, Vec3d c, Vec3d d, UvFaceData uvs) {
        MutableQuad quad = new MutableQuad(-1, face);
        if (uvs == null) {
            uvs = UvFaceData.DEFAULT;
        }
        if (face == null || shouldInvertForRender(face)) {
            quad.vertex_0.positionv(a).texf(uvs.minU, uvs.minV);
            quad.vertex_1.positionv(b).texf(uvs.minU, uvs.maxV);
            quad.vertex_2.positionv(c).texf(uvs.maxU, uvs.maxV);
            quad.vertex_3.positionv(d).texf(uvs.maxU, uvs.minV);
        } else {
            quad.vertex_3.positionv(a).texf(uvs.minU, uvs.minV);
            quad.vertex_2.positionv(b).texf(uvs.minU, uvs.maxV);
            quad.vertex_1.positionv(c).texf(uvs.maxU, uvs.maxV);
            quad.vertex_0.positionv(d).texf(uvs.maxU, uvs.minV);
        }
        return quad;
    }

    public static <T extends Vec3d> MutableQuad createFace(Direction face, T[] points, UvFaceData uvs) {
        return createFace(face, points[0], points[1], points[2], points[3], uvs);
    }

    public static MutableQuad createFace(Direction face, Vec3d center, Vec3d radius, UvFaceData uvs) {
        Vec3d[] points = getPointsForFace(face, center, radius);
        return createFace(face, points, uvs).normalf(face.getOffsetX(), face.getOffsetY(), face.getOffsetZ());
    }

    public static MutableQuad createInverseFace(Direction face, Vec3d center, Vec3d radius, UvFaceData uvs) {
        return createFace(face, center, radius, uvs).copyAndInvertNormal();
    }

    public static MutableQuad[] createDoubleFace(Direction face, Vec3d center, Vec3d radius, UvFaceData uvs) {
        MutableQuad norm = createFace(face, center, radius, uvs);
        return new MutableQuad[] { norm, norm.copyAndInvertNormal() };
    }

    public static List<MutableQuad> createModel(VoxelShape shape, Sprite sprite) {
        List<MutableQuad> list = new ArrayList<>();
        UvFaceData uvs = new UvFaceData();
        for (Box box : shape.getBoundingBoxes()) {
            Vec3d center = box.getCenter();
            Vec3d radius = new Vec3d(box.maxX - box.minX, box.maxY - box.minY, box.maxZ - box.minZ).multiply(0.5);
            for (Direction dir : Direction.values()) {
                mapBoxToUvs(box, dir, uvs);
                uvs.inSprite(sprite);
                list.add(createFace(dir, center, radius, uvs));
            }
        }
        return list;
    }

    public static void mapBoxToUvs(Box box, Direction side, UvFaceData uvs) {
        // TODO: Fix these!
        switch (side) {
            case WEST: /* -X */ {
                uvs.minU = (float) box.minZ;
                uvs.maxU = (float) box.maxZ;
                uvs.minV = 1 - (float) box.maxY;
                uvs.maxV = 1 - (float) box.minY;
                return;
            }
            case EAST: /* +X */ {
                uvs.minU = 1 - (float) box.minZ;
                uvs.maxU = 1 - (float) box.maxZ;
                uvs.minV = 1 - (float) box.maxY;
                uvs.maxV = 1 - (float) box.minY;
                return;
            }
            case DOWN: /* -Y */ {
                uvs.minU = (float) box.minX;
                uvs.maxU = (float) box.maxX;
                uvs.minV = 1 - (float) box.maxZ;
                uvs.maxV = 1 - (float) box.minZ;
                return;
            }
            case UP: /* +Y */ {
                uvs.minU = (float) box.minX;
                uvs.maxU = (float) box.maxX;
                uvs.minV = (float) box.maxZ;
                uvs.maxV = (float) box.minZ;
                return;
            }
            case NORTH: /* -Z */ {
                uvs.minU = 1 - (float) box.minX;
                uvs.maxU = 1 - (float) box.maxX;
                uvs.minV = 1 - (float) box.maxY;
                uvs.maxV = 1 - (float) box.minY;
                return;
            }
            case SOUTH: /* +Z */ {
                uvs.minU = (float) box.minX;
                uvs.maxU = (float) box.maxX;
                uvs.minV = 1 - (float) box.maxY;
                uvs.maxV = 1 - (float) box.minY;
                return;
            }
            default: {
                throw new IllegalStateException("Unknown Direction " + side);
            }
        }
    }

    public static Vec3d[] getPointsForFace(Direction face, Vec3d center, Vec3d radius) {
        Vec3d faceAdd = new Vec3d(
            //
            face.getOffsetX() * radius.x, //
            face.getOffsetY() * radius.y, //
            face.getOffsetZ() * radius.z //
        );
        Vec3d centerOfFace = center.add(faceAdd);
        final Vec3d faceRadius;
        if (face.getDirection() == AxisDirection.POSITIVE) {
            faceRadius = radius.subtract(faceAdd);
        } else {
            faceRadius = radius.add(faceAdd);
        }
        return getPoints(centerOfFace, faceRadius);
    }

    public static Vec3d[] getPoints(Vec3d centerFace, Vec3d faceRadius) {
        return new Vec3d[] { //
            centerFace.add(addOrNegate(faceRadius, false, false)), //
            centerFace.add(addOrNegate(faceRadius, false, true)), //
            centerFace.add(addOrNegate(faceRadius, true, true)), //
            centerFace.add(addOrNegate(faceRadius, true, false))//
        };
    }

    public static Vec3d addOrNegate(Vec3d coord, boolean u, boolean v) {
        boolean zisv = coord.x != 0 && coord.y == 0;
        double x = coord.x * (u ? 1 : -1);
        double y = coord.y * (v ? -1 : 1);
        double z = coord.z * (zisv ? (v ? -1 : 1) : (u ? 1 : -1));
        return new Vec3d(x, y, z);
    }

    public static boolean shouldInvertForRender(Direction face) {
        boolean flip = face.getDirection() == AxisDirection.NEGATIVE;
        return face.getAxis() == Axis.Z ? !flip : flip;
    }

    public static Direction faceForRender(Direction face) {
        return shouldInvertForRender(face) ? face.getOpposite() : face;
    }
}
