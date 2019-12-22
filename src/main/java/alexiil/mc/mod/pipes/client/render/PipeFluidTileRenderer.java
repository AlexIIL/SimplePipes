package alexiil.mc.mod.pipes.client.render;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Direction.Axis;
import net.minecraft.util.math.Vec3d;

import alexiil.mc.lib.attributes.fluid.render.FluidRenderFace;
import alexiil.mc.lib.attributes.fluid.volume.FluidVolume;
import alexiil.mc.mod.pipes.blocks.PipeFlowFluid;
import alexiil.mc.mod.pipes.blocks.TilePipe;
import alexiil.mc.mod.pipes.util.VecUtil;

public class PipeFluidTileRenderer<T extends TilePipe> extends BlockEntityRenderer<T> {

    public PipeFluidTileRenderer(BlockEntityRenderDispatcher dispatcher) {
        super(dispatcher);
    }

    @Override
    public void render(
        T pipe, float tickDelta, MatrixStack matrices, VertexConsumerProvider vcp, int light, int overlay
    ) {
        PipeFlowFluid fluidFlow = (PipeFlowFluid) pipe.flow;

        boolean gas = false;// TODO!
        boolean horizontal = false;
        boolean vertical = pipe.isConnected(gas ? Direction.DOWN : Direction.UP);

        for (Direction side : Direction.values()) {
            FluidVolume fluid = fluidFlow.getClientSideFluid(side);
            int amount = fluid.getAmount();
            if (amount <= 0) {
                continue;
            }

            if (side.getAxis() != Axis.Y) {
                horizontal |= pipe.isConnected(side);
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
                FluidRenderFace.appendCuboid(min.x, min.y, min.z, max.x, max.y, max.z, 1, sides, faces);
            } else {
                EnumSet<Direction> sides = EnumSet.allOf(Direction.class);
                double max_y = (max.y - min.y) * perc + min.y;
                FluidRenderFace.appendCuboid(min.x, min.y, min.z, max.x, max_y, max.z, 1, sides, faces);
            }
            fluid.render(faces, vcp, matrices);
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
                FluidRenderFace.appendCuboid(min.x, min.y, min.z, max.x, max_y, max.z, 1, sides, faces);

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
                FluidRenderFace.appendCuboid(min.x, min.y, min.z, max.x, max.y, max.z, 1, sides, faces);
            }
            center.render(faces, vcp, matrices);
        }
    }
}
