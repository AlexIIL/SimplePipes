package alexiil.mc.mod.pipes.client.render;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Direction.Axis;
import net.minecraft.util.math.Vec3d;

import alexiil.mc.mod.pipes.pipe.ISimplePipe;
import alexiil.mc.mod.pipes.pipe.PipeSpFlowFluid;
import alexiil.mc.mod.pipes.util.VecUtil;

import alexiil.mc.lib.attributes.fluid.amount.FluidAmount;
import alexiil.mc.lib.attributes.fluid.render.FluidRenderFace;
import alexiil.mc.lib.attributes.fluid.volume.FluidVolume;

public class PipeFluidRenderer {
    public static void render(MatrixStack matrices, VertexConsumerProvider vcp, PipeSpFlowFluid flow, int light) {
        ISimplePipe pipe = flow.pipe;

        boolean gas = false;// TODO!
        boolean horizontal = false;
        boolean vertical = pipe.isConnected(gas ? Direction.DOWN : Direction.UP);

        for (Direction side : Direction.values()) {
            FluidVolume fluid = flow.getClientSideFluid(side);
            FluidAmount amount = fluid.amount();
            if (!amount.isPositive()) {
                continue;
            }

            if (side.getAxis() != Axis.Y) {
                horizontal |= pipe.isConnected(side);
            }

            Vec3d center = new Vec3d(0.5, 0.5, 0.5);
            center = VecUtil.replaceValue(center, side.getAxis(), 0.5 + side.getDirection().offset() * 0.34375);

            Vec3d radius = new Vec3d(0.1874, 0.1874, 0.1874);
            radius = VecUtil.replaceValue(radius, side.getAxis(), 0.15625);

            double perc = amount.asInexactDouble() / PipeSpFlowFluid.SECTION_CAPACITY.asInexactDouble();
            if (side.getAxis() == Axis.Y) {
                perc = Math.sqrt(perc);
                radius = new Vec3d(perc * 0.1874, radius.y, perc * 0.1874);
            }

            Vec3d min = center.subtract(radius);
            Vec3d max = center.add(radius);

            List<FluidRenderFace> faces = new ArrayList<>();

            if (side.getAxis() == Axis.Y) {
                EnumSet<Direction> sides = EnumSet.allOf(Direction.class);
                FluidRenderFace.appendCuboid(min.x, min.y, min.z, max.x, max.y, max.z, 1, sides, faces, light);
            } else {
                EnumSet<Direction> sides = EnumSet.allOf(Direction.class);
                double max_y = (max.y - min.y) * perc + min.y;
                FluidRenderFace.appendCuboid(min.x, min.y, min.z, max.x, max_y, max.z, 1, sides, faces, light);
            }
            fluid.render(faces, vcp, matrices);
        }

        FluidVolume center = flow.getClientCenterFluid();
        if (!center.isEmpty()) {

            double horizPos = 0.26;
            double perc = center.amount().asInexactDouble() / PipeSpFlowFluid.SECTION_CAPACITY.asInexactDouble();
            List<FluidRenderFace> faces = new ArrayList<>();

            if (horizontal | !vertical) {
                Vec3d min = new Vec3d(0.3126, 0.3126, 0.3126);
                Vec3d max = new Vec3d(0.6874, 0.6874, 0.6874);

                EnumSet<Direction> sides = EnumSet.allOf(Direction.class);
                double max_y = (max.y - min.y) * perc + min.y;
                FluidRenderFace.appendCuboid(min.x, min.y, min.z, max.x, max_y, max.z, 1, sides, faces, light);

                horizPos += (max.y - min.y) * perc;
            }

            if (vertical && horizPos < 0.6874) {
                perc = Math.sqrt(perc);
                double minXZ = 0.5 - 0.1874 * perc;
                double maxXZ = 0.5 + 0.1874 * perc;

                double yMin = gas ? 0.3126 : horizPos;
                double yMax = gas ? 1 - horizPos : 0.6874;

                Vec3d min = new Vec3d(minXZ, yMin, minXZ);
                Vec3d max = new Vec3d(maxXZ, yMax, maxXZ);

                EnumSet<Direction> sides = EnumSet.allOf(Direction.class);
                FluidRenderFace.appendCuboid(min.x, min.y, min.z, max.x, max.y, max.z, 1, sides, faces, light);
            }
            center.render(faces, vcp, matrices);
        }
    }
}
