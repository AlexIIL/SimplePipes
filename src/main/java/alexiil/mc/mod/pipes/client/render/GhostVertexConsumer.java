package alexiil.mc.mod.pipes.client.render;

import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.VertexConsumer;

public class GhostVertexConsumer implements VertexConsumer {
    private static int alpha = 255;

    public static void renderStart(WorldRenderContext context) {
        ClientPlayerEntity player = MinecraftClient.getInstance().player;
        if (player != null) {
            float placementDelta = (player.getWorld().getTime() % 100) + context.tickDelta();
            alpha = (int) ((Math.sin(placementDelta / 4f) / 4f + 0.75f) * 255f + 0.5f);
        }
    }

    private final VertexConsumer delegate;

    public GhostVertexConsumer(VertexConsumer delegate) {this.delegate = delegate;}

    @Override
    public VertexConsumer vertex(double x, double y, double z) {
        delegate.vertex(x, y, z);
        return this;
    }

    @Override
    public VertexConsumer color(int red, int green, int blue, int alpha) {
        delegate.color(red, green, blue, GhostVertexConsumer.alpha);
        return this;
    }

    @Override
    public VertexConsumer texture(float u, float v) {
        delegate.texture(u, v);
        return null;
    }

    @Override
    public VertexConsumer overlay(int u, int v) {
        delegate.overlay(u, v);
        return null;
    }

    @Override
    public VertexConsumer light(int u, int v) {
        delegate.light(u, v);
        return null;
    }

    @Override
    public VertexConsumer normal(float x, float y, float z) {
        delegate.normal(x, y, z);
        return null;
    }

    @Override
    public void next() {
        delegate.next();
    }

    @Override
    public void fixedColor(int red, int green, int blue, int alpha) {
        delegate.fixedColor(red, green, blue, GhostVertexConsumer.alpha);
    }

    @Override
    public void unfixColor() {
        delegate.unfixColor();
    }
}
