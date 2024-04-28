package alexiil.mc.mod.pipes.client.render;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.math.Direction;

import alexiil.mc.mod.pipes.part.PartTank;
import alexiil.mc.mod.pipes.util.FluidSmoother.FluidStackInterp;

import alexiil.mc.lib.attributes.fluid.render.FluidRenderFace;

import alexiil.mc.lib.multipart.api.render.PartRenderer;

public class TankPartRenderer implements PartRenderer<PartTank> {

    @Override
    public void render(
        PartTank part, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light,
        int breakProgress
    ) {
        FluidStackInterp forRender = part.getFluidForRender(tickDelta);
        if (forRender == null) {
            return;
        }
        // buffer setup

        List<FluidRenderFace> faces = new ArrayList<>();

        double x0 = 0.126;
        double y0 = 0.001;
        double z0 = 0.126;
        double x1 = 0.874;
        double y1 = 0.001 + (12 / 16.0 - 0.002) * forRender.amount / part.fluidInv.tankCapacity_F.asInexactDouble();
        double z1 = 0.874;

        if (false) {
            matrices.push();
            matrices.translate(0.5, 0.25, 0.5);
            MinecraftClient.getInstance().getItemRenderer().renderItem(
                new ItemStack(Items.YELLOW_DYE), ModelTransformationMode.GROUND, light, OverlayTexture.DEFAULT_UV,
                matrices, vertexConsumers, null, 42
            );
            matrices.pop();
        }

        EnumSet<Direction> sides = EnumSet.allOf(Direction.class);
        FluidRenderFace.appendCuboid(x0, y0, z0, x1, y1, z1, 1, sides, faces, light);
        forRender.fluid.getRenderer().render(forRender.fluid, faces, vertexConsumers, matrices);
    }
}
