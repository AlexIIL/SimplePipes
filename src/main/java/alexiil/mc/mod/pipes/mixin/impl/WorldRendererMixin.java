package alexiil.mc.mod.pipes.mixin.impl;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Matrix4f;

import alexiil.mc.mod.pipes.client.render.ItemPlacemenentGhostRenderer;

@Mixin(WorldRenderer.class)
public class WorldRendererMixin {

    // TODO: Add a new RenderLayer to be used for the ghost placement
    // (and then draw it at some point)
    // actually it probably should not be added to BufferBuilderStorage, as then it would get drawn too early

    /* void net.minecraft.client.render.WorldRenderer.render(net.minecraft.client.util.math.MatrixStack matrices, float
     * tickDelta, long limitTime, boolean renderBlockOutline, net.minecraft.client.render.Camera camera,
     * net.minecraft.client.render.GameRenderer gameRenderer, net.minecraft.client.render.LightmapTextureManager
     * lightmapTextureManager, net.minecraft.client.util.math.Matrix4f matrix4f) */

    /*
     * void net.minecraft.client.render.WorldRenderer.render(net.minecraft.client.util.math.MatrixStack matrices, float
     * tickDelta, long limitTime, boolean renderBlockOutline, net.minecraft.client.render.Camera camera,
     * net.minecraft.client.render.GameRenderer gameRenderer, net.minecraft.client.render.LightmapTextureManager
     * lightmapTextureManager, net.minecraft.util.math.Matrix4f matrix4f)
     */

    @Inject(method = "render(Lnet/minecraft/client/util/math/MatrixStack;FJZLnet/minecraft/client/render/Camera;"
        + "Lnet/minecraft/client/render/GameRenderer;Lnet/minecraft/client/render/LightmapTextureManager;"
        + "Lnet/minecraft/util/math/Matrix4f;)V", at = @At(value = "CONSTANT", args = "stringValue=particles"))
    private void renderDetached(
        MatrixStack matrices, float tickDelta, long startTime, boolean renderBlockOutline, Camera camera,
        GameRenderer gameRenderer, LightmapTextureManager lightmapTextureManager, Matrix4f matrix4f, CallbackInfo ci
    ) {
        ClientPlayerEntity player = MinecraftClient.getInstance().player;
        if (player != null) {
            ItemPlacemenentGhostRenderer.render(matrices, camera, player, tickDelta);
        }
    }
}
