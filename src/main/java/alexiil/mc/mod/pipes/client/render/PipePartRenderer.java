package alexiil.mc.mod.pipes.client.render;

import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;

import alexiil.mc.mod.pipes.pipe.PartSpPipe;
import alexiil.mc.mod.pipes.pipe.PipeSpFlowFluid;
import alexiil.mc.mod.pipes.pipe.PipeSpFlowItem;

import alexiil.mc.lib.multipart.api.render.PartRenderer;

public class PipePartRenderer implements PartRenderer<PartSpPipe> {

    @Override
    public void render(
        PartSpPipe part, float tickDelta, MatrixStack matrices, VertexConsumerProvider vc, int light, int overlay
    ) {
        if (part.flow instanceof PipeSpFlowItem) {
            PipeItemRenderer.render(tickDelta, matrices, vc, light, overlay, (PipeSpFlowItem) part.flow);
        } else if (part.flow instanceof PipeSpFlowFluid) {
            PipeFluidRenderer.render(matrices, vc, (PipeSpFlowFluid) part.flow);
        }
    }
}
