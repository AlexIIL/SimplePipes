package alexiil.mc.mod.pipes.client.render;

import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;

import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;

import org.joml.Matrix4f;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.systems.VertexSorter;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.gl.SimpleFramebuffer;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderPhase;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.util.Window;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.hit.HitResult.Type;

import alexiil.mc.mod.pipes.items.GhostPlacement;
import alexiil.mc.mod.pipes.items.IItemPlacmentGhost;
import alexiil.mc.mod.pipes.mixin.impl.RenderLayerAccessor;

public final class ItemPlacementGhostRenderer {
    private ItemPlacementGhostRenderer() {}

    private static final Object2ObjectLinkedOpenHashMap<RenderLayer, BufferBuilder> LAYERS;
    private static final VertexConsumerProvider.Immediate VCPS;
    private static GhostPlacement currentPlacementGhost;
    private static Framebuffer framebuffer;

    public static class Layers extends RenderPhase {
        private Layers(String name, Runnable beginAction, Runnable endAction) {
            super(name, beginAction, endAction);
        }

        public static final RenderLayer GHOST = RenderLayerAccessor.callOf(
            "PLACEMENT_GHOST", VertexFormats.POSITION_COLOR_TEXTURE_LIGHT_NORMAL, VertexFormat.DrawMode.QUADS, 2 << 12,
            false, true, //
            RenderLayer.MultiPhaseParameters.builder() //
                .lightmap(ENABLE_LIGHTMAP) //
                .program(TRANSLUCENT_PROGRAM) //
                .texture(MIPMAP_BLOCK_ATLAS_TEXTURE) //
                .transparency(TRANSLUCENT_TRANSPARENCY) //
                .build(false) //
        );
    }

    static {
        LAYERS = new Object2ObjectLinkedOpenHashMap<>();
        addRenderLayer(Layers.GHOST);
        VCPS = VertexConsumerProvider.immediate(LAYERS, buffer());
    }

    public static void addRenderLayer(RenderLayer layer) {
        if (!LAYERS.containsKey(layer)) {
            LAYERS.put(layer, buffer());
        }
    }

    private static BufferBuilder buffer() {
        return new BufferBuilder(1 << 12);
    }

    private static void ensureFramebuffer() {
        MinecraftClient client = MinecraftClient.getInstance();
        Window window = client.getWindow();

        if (framebuffer == null) {
            framebuffer = new SimpleFramebuffer( //
                window.getFramebufferWidth(), window.getFramebufferHeight(), true,
                MinecraftClient.IS_SYSTEM_MAC
            );
            framebuffer.setClearColor(0f, 0f, 0f, 0f);
        }

        if (window.getFramebufferWidth() != framebuffer.textureWidth ||
            window.getFramebufferHeight() != framebuffer.textureHeight) {
            framebuffer.resize(
                window.getFramebufferWidth(), window.getFramebufferHeight(), MinecraftClient.IS_SYSTEM_MAC);
        }

        framebuffer.clear(MinecraftClient.IS_SYSTEM_MAC);

        // Copy MC framebuffer colors to our framebuffer so our transparency doesn't look too dark
        framebuffer.beginWrite(false);
        Matrix4f projBackup = RenderSystem.getProjectionMatrix();
        // Note: this doesn't write to the alpha channel or the depth buffer, which is exactly what we want.
        client.getFramebuffer().draw(framebuffer.textureWidth, framebuffer.textureHeight);
        RenderSystem.setProjectionMatrix(projBackup, RenderSystem.getVertexSorting());
    }

    public static void render(WorldRenderContext context) {
        ClientPlayerEntity player = MinecraftClient.getInstance().player;
        if (player != null) {
            render(context.matrixStack(), context.camera(), player, context.tickDelta());
        }
    }

    public static void render(MatrixStack matrices, Camera camera, PlayerEntity player, float partialTicks) {
        ensureFramebuffer();

        matrices.push();
        matrices.translate(-camera.getPos().x, -camera.getPos().y, -camera.getPos().z);
        render0(matrices, VCPS, player, partialTicks);
        VCPS.draw();
        matrices.pop();

        MinecraftClient mc = MinecraftClient.getInstance();
        mc.getFramebuffer().beginWrite(false);

        // Framebuffer.draw() messes with the projection matrix, so we're keeping a backup.
        Matrix4f projBackup = RenderSystem.getProjectionMatrix();
        RenderSystem.enableBlend();
        framebuffer.draw(mc.getWindow().getFramebufferWidth(), mc.getWindow().getFramebufferHeight(), false);
        RenderSystem.disableBlend();
        RenderSystem.setProjectionMatrix(projBackup, RenderSystem.getVertexSorting());
    }

    private static void render0(
        MatrixStack matrices, VertexConsumerProvider vcp, PlayerEntity player, float partialTicks
    ) {
        if (currentPlacementGhost != null && currentPlacementGhost.isLockedOpen()) {
            currentPlacementGhost.render(matrices, vcp, player, partialTicks);
            return;
        }

        if (render(matrices, vcp, player, partialTicks, Hand.MAIN_HAND, player.getMainHandStack())) {
            return;
        }
        render(matrices, vcp, player, partialTicks, Hand.OFF_HAND, player.getOffHandStack());
    }

    public static void clientTick() {
        if (currentPlacementGhost != null) {
            currentPlacementGhost.tick();
        }
    }

    private static boolean render(
        MatrixStack matrices, VertexConsumerProvider vcp, PlayerEntity player, float partialTicks, Hand hand,
        ItemStack stack
    ) {
        MinecraftClient mc = MinecraftClient.getInstance();
        HitResult hit = mc.crosshairTarget;
        if (!(hit instanceof BlockHitResult) || hit.getType() != Type.BLOCK) {
            setCurrentGhost(null);
            return true;
        }
        BlockHitResult blockHit = (BlockHitResult) hit;

        Item item = stack.getItem();
        if (item instanceof IItemPlacmentGhost) {
            IItemPlacmentGhost ghostItem = (IItemPlacmentGhost) item;
            ItemUsageContext ctx = new ItemUsageContext(player, hand, blockHit);

            if (currentPlacementGhost != null) {
                GhostPlacement ghost = currentPlacementGhost.preRender(ctx);
                if (ghost == null) {
                    setCurrentGhost(null);
                } else if (ghost != currentPlacementGhost) {
                    setCurrentGhost(ghost);
                }
            }

            if (currentPlacementGhost == null) {
                GhostPlacement ghost = ghostItem.createGhostPlacement(ctx);
                if (ghost != null) {
                    ghost = ghost.preRender(ctx);
                }
                setCurrentGhost(ghost);
            }
            if (currentPlacementGhost != null) {
                currentPlacementGhost.render(matrices, vcp, player, partialTicks);
                return true;
            }
        }
        return false;
    }

    private static void setCurrentGhost(GhostPlacement ghost) {
        if (currentPlacementGhost != null) {
            currentPlacementGhost.delete();
        }
        currentPlacementGhost = ghost;
    }
}
