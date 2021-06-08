package alexiil.mc.mod.pipes.client.render;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Quaternion;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3f;
import net.minecraft.world.World;

import alexiil.mc.mod.pipes.blocks.TilePipe;
import alexiil.mc.mod.pipes.pipe.ISimplePipe;
import alexiil.mc.mod.pipes.pipe.PipeSpFlowItem;
import alexiil.mc.mod.pipes.pipe.TravellingItem;

public class PipeItemTileRenderer<T extends TilePipe> implements BlockEntityRenderer<T> {

    private static final Quaternion[] ROTATIONS = new Quaternion[6];

    static {
        ROTATIONS[Direction.SOUTH.ordinal()] = null;
        ROTATIONS[Direction.NORTH.ordinal()] = new Quaternion(new Vec3f(0, 1, 0), 180, true);
        ROTATIONS[Direction.EAST.ordinal()] = new Quaternion(new Vec3f(0, 1, 0), 90, true);
        ROTATIONS[Direction.WEST.ordinal()] = new Quaternion(new Vec3f(0, 1, 0), 270, true);
        ROTATIONS[Direction.UP.ordinal()] = new Quaternion(new Vec3f(1, 0, 0), 270, true);
        ROTATIONS[Direction.DOWN.ordinal()] = new Quaternion(new Vec3f(1, 0, 0), 90, true);
    }

    public PipeItemTileRenderer(BlockEntityRendererFactory.Context ctx) {

    }

    @Override
    public void render(
        T pipe, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay
    ) {
        render(tickDelta, matrices, vertexConsumers, light, overlay, (PipeSpFlowItem) pipe.getFlow());
    }

    public static void render(
        float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay,
        PipeSpFlowItem flow
    ) {
        ISimplePipe pipe = flow.pipe;
        World world = pipe.getWorld();
        long now = world == null ? 0 : world.getTime();

        Iterable<TravellingItem> toRender = flow.getAllItemsForRender();

        for (TravellingItem item : toRender) {
            Vec3d pos = item.getRenderPosition(BlockPos.ORIGIN, now, tickDelta, pipe);
            ItemStack stack = item.stack;
            if (!stack.isEmpty()) {
                Direction renderDirection = item.getRenderDirection(now, tickDelta);

                matrices.push();
                matrices.translate(pos.x, pos.y, pos.z);
                matrices.scale(0.5f, 0.5f, 0.5f);

                if (renderDirection != null) {
                    Quaternion quat = ROTATIONS[renderDirection.ordinal()];
                    if (quat != null) {
                        matrices.multiply(quat);
                    }
                }
                MinecraftClient.getInstance().getItemRenderer()
                    .renderItem(stack, ModelTransformation.Mode.FIXED, light, overlay, matrices, vertexConsumers, 42);
                matrices.pop();
            }
        }
    }
}
