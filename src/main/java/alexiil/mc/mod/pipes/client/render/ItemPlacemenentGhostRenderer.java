package alexiil.mc.mod.pipes.client.render;

import com.mojang.blaze3d.platform.GlStateManager.DstFactor;
import com.mojang.blaze3d.platform.GlStateManager.SrcFactor;
import com.mojang.blaze3d.systems.RenderSystem;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderPhase;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.hit.HitResult.Type;

import alexiil.mc.mod.pipes.items.GhostPlacement;
import alexiil.mc.mod.pipes.items.IItemPlacmentGhost;

import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;

public final class ItemPlacemenentGhostRenderer {
    private ItemPlacemenentGhostRenderer() {}

    public static final RenderLayer GHOST;

    private static final RenderPhase TEXTURE;
    private static final Object2ObjectLinkedOpenHashMap<RenderLayer, BufferBuilder> LAYERS;
    private static final VertexConsumerProvider.Immediate VCPS;
    private static GhostPlacement currentPlacementGhost;

    static {
        class RenderPhaseAccess extends RenderPhase {
            public RenderPhaseAccess() {
                super(null, null, null);
            }

            public RenderPhase.Texture texture(Identifier id, boolean blur, boolean mipmap) {
                return new RenderPhase.Texture(id, blur, mipmap);
            }

            public RenderPhase.Texture blockAtlasMipmap() {
                return MIPMAP_BLOCK_ATLAS_TEXTURE;
            }
        }

        TEXTURE = new RenderPhaseAccess().blockAtlasMipmap();

        GHOST = new RenderLayer(
            "PLACEMENT_GHOST", VertexFormats.POSITION_COLOR_TEXTURE_LIGHT_NORMAL, VertexFormat.DrawMode.QUADS, 1 << 12,
            false, true, ItemPlacemenentGhostRenderer::prePlacementGhost,
            ItemPlacemenentGhostRenderer::postPlacementGhost
        ) {};

        LAYERS = new Object2ObjectLinkedOpenHashMap<>();
        addRenderLayer(GHOST);
        VCPS = VertexConsumerProvider.immediate(LAYERS, buffer());
    }

    private static void prePlacementGhost() {
        // Start action
        TEXTURE.startDrawing();
        RenderSystem.enableBlend();
        // TODO: Create a custom shader for this!
        RenderSystem.setShader(GameRenderer::getBlockShader);
        // RenderSystem.enableAlphaTest();
        // RenderSystem.defaultAlphaFunc();
        RenderSystem.blendFuncSeparate(SrcFactor.SRC_ALPHA, DstFactor.CONSTANT_ALPHA, SrcFactor.ONE, DstFactor.ZERO);
        // RenderSystem.blendColor(1, 1, 1, 0.9f);
        GL11.glDepthRange(0, 0);
        RenderSystem.enableCull();
        RenderSystem.enableDepthTest();
    }

    private static void postPlacementGhost() {
        // End action
        GL11.glDepthRange(0, 1);
        // RenderSystem.blendColor(0, 0, 0, 0);
        RenderSystem.disableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableCull();
        TEXTURE.endDrawing();
    }

    public static void addRenderLayer(RenderLayer layer) {
        if (!LAYERS.containsKey(layer)) {
            LAYERS.put(layer, buffer());
        }
    }

    private static BufferBuilder buffer() {
        return new BufferBuilder(1 << 12);
    }

    public static void render(MatrixStack matrices, Camera camera, PlayerEntity player, float partialTicks) {
        matrices.push();
        matrices.translate(-camera.getPos().x, -camera.getPos().y, -camera.getPos().z);
        render0(matrices, VCPS, player, partialTicks);
        VCPS.draw();
        matrices.pop();
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
