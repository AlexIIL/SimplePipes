/*
 * Copyright (c) 2017 SpaceToad and the BuildCraft team
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0. If a copy of the MPL was not
 * distributed with this file, You can obtain one at https://mozilla.org/MPL/2.0/
 */

package alexiil.mc.mod.pipes.client.render;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import org.lwjgl.opengl.GL11;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Camera;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.entity.player.PlayerEntity;

/** Dispatches "detached renderer elements" - rendering that does not require a specific tile or entity in the world. */
@Environment(EnvType.CLIENT)
public final class DetachedRenderer {
    public static final DetachedRenderer INSTANCE = new DetachedRenderer();

    public enum RenderMatrixType implements IGlPre, IGLPost {
        FROM_PLAYER(null, null),
        FROM_WORLD_ORIGIN(DetachedRenderer::fromWorldOriginPre, DetachedRenderer::fromWorldOriginPost);

        public final IGlPre pre;
        public final IGLPost post;

        RenderMatrixType(IGlPre pre, IGLPost post) {
            this.pre = pre;
            this.post = post;
        }

        @Override
        public void glPre(PlayerEntity clientPlayer, float partialTicks) {
            if (pre != null) pre.glPre(clientPlayer, partialTicks);
        }

        @Override
        public void glPost() {
            if (post != null) post.glPost();
        }

        public void addRenderer(IDetachedRenderer renderer) {
            INSTANCE.addRenderer(this, renderer);
        }
    }

    @FunctionalInterface
    public interface IGlPre {
        void glPre(PlayerEntity clientPlayer, float partialTicks);
    }

    @FunctionalInterface
    public interface IGLPost {
        void glPost();
    }

    @FunctionalInterface
    public interface IDetachedRenderer {
        void render(PlayerEntity player, float partialTicks);
    }

    private final Map<RenderMatrixType, List<IDetachedRenderer>> renders = new EnumMap<>(RenderMatrixType.class);

    private DetachedRenderer() {
        for (RenderMatrixType type : RenderMatrixType.values()) {
            renders.put(type, new ArrayList<>());
        }
    }

    public void addRenderer(RenderMatrixType type, IDetachedRenderer renderer) {
        renders.get(type).add(renderer);
    }

    public void renderAfterCutout(PlayerEntity player, float partialTicks) {
        MinecraftClient.getInstance().getTextureManager().bindTexture(SpriteAtlasTexture.BLOCK_ATLAS_TEX);
        MinecraftClient.getInstance().gameRenderer.enableLightmap();

        for (RenderMatrixType type : RenderMatrixType.values()) {
            List<IDetachedRenderer> rendersForType = this.renders.get(type);
            if (rendersForType.isEmpty()) continue;
            type.glPre(player, partialTicks);
            for (IDetachedRenderer render : rendersForType) {
                render.render(player, partialTicks);
            }
            type.glPost();
        }

        MinecraftClient.getInstance().gameRenderer.disableLightmap();
    }

    public static void fromWorldOriginPre(PlayerEntity player, float partialTicks) {
        GL11.glPushMatrix();

        Camera camera = MinecraftClient.getInstance().gameRenderer.getCamera();
        GL11.glTranslated(-camera.getPos().x, -camera.getPos().y, -camera.getPos().z);
    }

    public static void fromWorldOriginPost() {
        GL11.glPopMatrix();
    }
}
