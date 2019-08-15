package alexiil.mc.mod.pipes.items;

import java.util.Random;

import javax.annotation.Nullable;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.GlStateManager.DestFactor;
import com.mojang.blaze3d.platform.GlStateManager.SourceFactor;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL14;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.block.BlockModelRenderer;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;

import alexiil.mc.lib.multipart.api.AbstractPart;
import alexiil.mc.lib.multipart.api.MultipartContainer.PartOffer;
import alexiil.mc.lib.multipart.api.MultipartHolder;
import alexiil.mc.lib.multipart.api.render.PartModelKey;
import alexiil.mc.lib.multipart.impl.LibMultiPart;
import alexiil.mc.lib.multipart.impl.client.model.SinglePartBakedModel;
import alexiil.mc.mod.pipes.util.RenderUtil;
import alexiil.mc.mod.pipes.util.RenderUtil.AutoTessellator;

public abstract class GhostPlacementPart extends GhostPlacement {

    protected BlockPos pos;
    protected PartModelKey modelKey;

    protected boolean setup(@Nullable PartOffer offer) {
        return offer != null && setup(offer.getHolder());
    }

    protected boolean setup(MultipartHolder holder) {
        pos = holder.getContainer().getMultipartPos();
        modelKey = holder.getPart().getModelKey();
        return pos != null && modelKey != null;
    }

    protected boolean setup(AbstractPart part) {
        pos = part.holder.getContainer().getMultipartPos();
        modelKey = part.getModelKey();
        return pos != null && modelKey != null;
    }

    @Override
    public void render(PlayerEntity player, float partialTicks) {
        assert pos != null;
        assert modelKey != null;

        BakedModel model = SinglePartBakedModel.create(modelKey);
        if (model == null) {
            return;
        }

        try (AutoTessellator at = RenderUtil.getThreadLocalUnusedTessellator()) {
            Tessellator tess = at.tessellator;
            BufferBuilder bb = tess.getBufferBuilder();
            bb.begin(GL11.GL_QUADS, VertexFormats.POSITION_COLOR_UV_LMAP);

            MinecraftClient mc = MinecraftClient.getInstance();
            BlockModelRenderer blockRenderer = mc.getBlockRenderManager().getModelRenderer();

            blockRenderer.tesselate(
                mc.world, model, LibMultiPart.BLOCK.getDefaultState(), pos, bb, false, new Random(), 0
            );

            bb.setOffset(0, 0, 0);
            GlStateManager.enableBlend();
            GlStateManager.blendFuncSeparate(
                SourceFactor.ONE, DestFactor.ONE_MINUS_SRC_COLOR, SourceFactor.ONE_MINUS_CONSTANT_ALPHA, DestFactor.ONE
            );
            GL14.glBlendColor(1, 1, 1, 0.5f);
            GL11.glDepthRange(0, 0.5);
            tess.draw();
            GL11.glDepthRange(0, 1);
            GL14.glBlendColor(0, 0, 0, 0);
            GlStateManager.blendFunc(SourceFactor.ONE, DestFactor.ZERO);
            GlStateManager.disableBlend();
        }
    }
}
