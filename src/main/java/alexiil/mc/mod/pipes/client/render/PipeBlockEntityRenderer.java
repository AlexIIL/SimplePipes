/*
 * Copyright (c) 2019 SpaceToad and the BuildCraft team
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0. If a copy of the MPL was not
 * distributed with this file, You can obtain one at https://mozilla.org/MPL/2.0/
 */
package alexiil.mc.mod.pipes.client.render;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.GlStateManager.DestFactor;
import com.mojang.blaze3d.platform.GlStateManager.SourceFactor;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.GuiLighting;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Direction.Axis;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import alexiil.mc.lib.attributes.fluid.render.FluidRenderFace;
import alexiil.mc.lib.attributes.fluid.volume.FluidVolume;
import alexiil.mc.mod.pipes.blocks.PipeFlow;
import alexiil.mc.mod.pipes.blocks.PipeFlowFluid;
import alexiil.mc.mod.pipes.blocks.PipeFlowItem;
import alexiil.mc.mod.pipes.blocks.TilePipe;
import alexiil.mc.mod.pipes.blocks.TravellingItem;
import alexiil.mc.mod.pipes.util.VecUtil;

public class PipeBlockEntityRenderer extends BlockEntityRenderer<TilePipe> {

    private static boolean inBatch = false;

    @Override
    public void render(TilePipe pipe, double x, double y, double z, float partialTicks, int int_1) {
        World world = pipe.getWorld();
        long now = world.getTime();
        int lightc = world.getLightLevel(pipe.getPos(), 0);

        PipeFlow flow = pipe.flow;

        if (flow instanceof PipeFlowItem) {
            renderItems(pipe, x, y, z, partialTicks, now, lightc, flow);
        } else if (flow instanceof PipeFlowFluid) {
            PipeFlowFluid fluidFlow = (PipeFlowFluid) flow;

            boolean gas = false;// TODO!
            boolean horizontal = false;
            boolean vertical = flow.pipe.isConnected(gas ? Direction.DOWN : Direction.UP);

            // gl state setup
            GuiLighting.disable();
            MinecraftClient.getInstance().getTextureManager().bindTexture(SpriteAtlasTexture.BLOCK_ATLAS_TEX);
            GlStateManager.enableBlend();
            GlStateManager.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA);

            // buffer setup

            for (Direction side : Direction.values()) {
                FluidVolume fluid = fluidFlow.getClientSideFluid(side);
                int amount = fluid.getAmount();
                if (amount <= 0) {
                    continue;
                }

                if (side.getAxis() != Axis.Y) {
                    horizontal |= flow.pipe.isConnected(side);
                }

                Vec3d center = new Vec3d(0.5, 0.5, 0.5);
                center = VecUtil.replaceValue(center, side.getAxis(), 0.5 + side.getDirection().offset() * 0.37);

                Vec3d radius = new Vec3d(0.24, 0.24, 0.24);
                radius = VecUtil.replaceValue(radius, side.getAxis(), 0.13);

                double perc = amount / (double) PipeFlowFluid.SECTION_CAPACITY;
                if (side.getAxis() == Axis.Y) {
                    perc = Math.sqrt(perc);
                    radius = new Vec3d(perc * 0.24, radius.y, perc * 0.24);
                }

                Vec3d min = center.subtract(radius);
                Vec3d max = center.add(radius);

                List<FluidRenderFace> faces = new ArrayList<>();

                if (side.getAxis() == Axis.Y) {
                    EnumSet<Direction> sides = EnumSet.allOf(Direction.class);
                    FluidRenderFace.appendCuboid(min.x, min.y, min.z, max.x, max.y, max.z, 16, sides, faces);
                } else {
                    EnumSet<Direction> sides = EnumSet.allOf(Direction.class);
                    double max_y = (max.y - min.y) * perc + min.y;
                    FluidRenderFace.appendCuboid(min.x, min.y, min.z, max.x, max_y, max.z, 16, sides, faces);
                }
                fluid.render(faces, x, y, z);
            }

            FluidVolume center = fluidFlow.getClientCenterFluid();
            if (!center.isEmpty()) {

                double horizPos = 0.26;
                double perc = center.getAmount() / (double) PipeFlowFluid.SECTION_CAPACITY;
                List<FluidRenderFace> faces = new ArrayList<>();

                if (horizontal | !vertical) {
                    Vec3d min = new Vec3d(0.26, 0.26, 0.26);
                    Vec3d max = new Vec3d(0.74, 0.74, 0.74);

                    EnumSet<Direction> sides = EnumSet.allOf(Direction.class);
                    double max_y = (max.y - min.y) * perc + min.y;
                    FluidRenderFace.appendCuboid(min.x, min.y, min.z, max.x, max_y, max.z, 16, sides, faces);

                    horizPos += (max.y - min.y) * center.getAmount() / PipeFlowFluid.SECTION_CAPACITY;
                }

                if (vertical && horizPos < 0.74) {
                    perc = Math.sqrt(perc);
                    double minXZ = 0.5 - 0.24 * perc;
                    double maxXZ = 0.5 + 0.24 * perc;

                    double yMin = gas ? 0.26 : horizPos;
                    double yMax = gas ? 1 - horizPos : 0.74;

                    Vec3d min = new Vec3d(minXZ, yMin, minXZ);
                    Vec3d max = new Vec3d(maxXZ, yMax, maxXZ);

                    EnumSet<Direction> sides = EnumSet.allOf(Direction.class);
                    FluidRenderFace.appendCuboid(min.x, min.y, min.z, max.x, max.y, max.z, 16, sides, faces);
                }
                center.render(faces, x, y, z);
            }

            // gl state finish
            GuiLighting.enable();

        } else {

        }
    }

    private static void renderItems(TilePipe pipe, double x, double y, double z, float partialTicks, long now,
        int lightc, PipeFlow flow) {
        List<TravellingItem> toRender = ((PipeFlowItem) flow).getAllItemsForRender();

        for (TravellingItem item : toRender) {
            Vec3d pos = item.getRenderPosition(BlockPos.ORIGIN, now, partialTicks, pipe);

            ItemStack stack = item.stack;
            if (stack != null && !stack.isEmpty()) {
                renderItemStack(x + pos.x, y + pos.y, z + pos.z, //
                    stack, lightc, item.getRenderDirection(now, partialTicks));
            }
            // if (item.colour != null) {
            // bb.setTranslation(x + pos.x, y + pos.y, z + pos.z);
            // int col = ColourUtil.getLightHex(item.colour);
            // int r = (col >> 16) & 0xFF;
            // int g = (col >> 8) & 0xFF;
            // int b = col & 0xFF;
            // for (MutableQuad q : COLOURED_QUADS) {
            // MutableQuad q2 = new MutableQuad(q);
            // q2.lighti(lightc);
            // q2.multColouri(r, g, b, 255);
            // q2.render(bb);
            // }
            // bb.setTranslation(0, 0, 0);
            // }
        }

        endItemBatch();
    }

    private static void renderItemStack(double x, double y, double z, ItemStack stack, int lightc,
        Direction renderDirection) {

        // if (!inBatch) {
        // inBatch = true;
        MinecraftClient.getInstance().getTextureManager().bindTexture(SpriteAtlasTexture.BLOCK_ATLAS_TEX);
        GL11.glPushMatrix();
        GL11.glTranslated(x, y, z);
        GL11.glScaled(0.5, 0.5, 0.5);
        if (renderDirection != null && renderDirection != Direction.SOUTH) {
            switch (renderDirection) {
                case SOUTH: {
                    break;
                }
                case NORTH: {
                    GL11.glRotated(180.0f, 0, 1, 0);
                    break;
                }
                case EAST: {
                    GL11.glRotated(90.0f, 0, 1, 0);
                    break;
                }
                case WEST: {
                    GL11.glRotated(270.0f, 0, 1, 0);
                    break;
                }
                case UP: {
                    GL11.glRotated(270.0f, 1, 0, 0);
                    break;
                }
                case DOWN: {
                    GL11.glRotated(90.0f, 1, 0, 0);
                    break;
                }
                default: {
                    throw new IllegalStateException("Unknown Direction " + renderDirection);
                }
            }
        }
        GuiLighting.enable();
        // }
        // OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, lightc % (float) 0x1_00_00,
        // lightc / (float) 0x1_00_00);
        BakedModel model = MinecraftClient.getInstance().getItemRenderer().getModel(stack, null, null);
        model.getTransformation().applyGl(ModelTransformation.Type.FIXED);
        // model.getTransformation().applyGl(ModelTransformation.Type.GROUND);
        MinecraftClient.getInstance().getItemRenderer().renderItemAndGlow(stack, model);

        GL11.glPopMatrix();

    }

    private static void endItemBatch() {
        if (inBatch) {
            inBatch = false;
            GL11.glPopMatrix();
        }
    }

}
