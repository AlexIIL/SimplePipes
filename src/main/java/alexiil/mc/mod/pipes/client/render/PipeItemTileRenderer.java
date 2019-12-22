package alexiil.mc.mod.pipes.client.render;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Quaternion;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import alexiil.mc.mod.pipes.blocks.PipeFlowItem;
import alexiil.mc.mod.pipes.blocks.TilePipe;
import alexiil.mc.mod.pipes.blocks.TravellingItem;

public class PipeItemTileRenderer<T extends TilePipe> extends BlockEntityRenderer<T> {

    private static final Quaternion[] ROTATIONS = new Quaternion[6];

    static {
        ROTATIONS[Direction.SOUTH.ordinal()] = null;
        ROTATIONS[Direction.NORTH.ordinal()] = new Quaternion(new Vector3f(0, 1, 0), 180, true);
        ROTATIONS[Direction.EAST.ordinal()] = new Quaternion(new Vector3f(0, 1, 0), 90, true);
        ROTATIONS[Direction.WEST.ordinal()] = new Quaternion(new Vector3f(0, 1, 0), 270, true);
        ROTATIONS[Direction.UP.ordinal()] = new Quaternion(new Vector3f(1, 0, 0), 270, true);
        ROTATIONS[Direction.DOWN.ordinal()] = new Quaternion(new Vector3f(1, 0, 0), 90, true);
    }

    public PipeItemTileRenderer(BlockEntityRenderDispatcher dispatcher) {
        super(dispatcher);
    }

    @Override
    public void render(
        T pipe, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay
    ) {
        World world = pipe.getWorld();
        long now = world == null ? 0 : world.getTime();
        PipeFlowItem itemFlow = (PipeFlowItem) pipe.flow;

        Iterable<TravellingItem> toRender = itemFlow.getAllItemsForRender();

        boolean sawWool = false;

        for (TravellingItem item : toRender) {
            Vec3d pos = item.getRenderPosition(BlockPos.ORIGIN, now, tickDelta, pipe);
            ItemStack stack = item.stack;
            if (!stack.isEmpty()) {
                sawWool |= stack.getItem() == Items.WHITE_WOOL;

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
                    .renderItem(stack, ModelTransformation.Mode.FIXED, light, overlay, matrices, vertexConsumers);
                matrices.pop();
            }
        }

        if (sawWool) {
            GL11.glPushMatrix();
            Camera camera = MinecraftClient.getInstance().gameRenderer.getCamera();
            GL11.glRotated(camera.getPitch(), 1, 0, 0);
            GL11.glRotated(camera.getYaw() + 180, 0, 1, 0);
            GL11.glTranslated(
                pipe.getPos().getX() - camera.getPos().x, pipe.getPos().getY() - camera.getPos().y,
                pipe.getPos().getZ() - camera.getPos().z
            );

            GL11.glColor3f(1, 1, 1);
            GL11.glDisable(GL11.GL_TEXTURE_2D);
            GL11.glBegin(GL11.GL_LINE_STRIP);
            double max = 0.1;
            GL11.glVertex3d(max, max, max);
            GL11.glVertex3d(0, max, max);
            GL11.glVertex3d(0, max, 0);
            GL11.glVertex3d(max, max, 0);
            GL11.glVertex3d(max, 0, max);
            GL11.glVertex3d(max, 0, max);
            GL11.glVertex3d(max, 0, max);
            GL11.glVertex3d(max, max, max);
            GL11.glEnd();
            GL11.glEnable(GL11.GL_TEXTURE_2D);
            GL11.glPopMatrix();
        }
    }
}
