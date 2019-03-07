package alexiil.mc.mod.pipes.client.render;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.GlStateManager.DestFactor;
import com.mojang.blaze3d.platform.GlStateManager.SourceFactor;

import org.lwjgl.opengl.GL11;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.GuiLighting;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

import alexiil.mc.lib.attributes.fluid.volume.FluidKeys;
import alexiil.mc.lib.attributes.fluid.volume.FluidVolume;
import alexiil.mc.mod.pipes.blocks.TileTank;
import alexiil.mc.mod.pipes.util.FluidSmoother.FluidStackInterp;

public class TankBlockEntityRenderer extends BlockEntityRenderer<TileTank> {
    private static final Vec3d MIN = new Vec3d(0.13, 0.01, 0.13);
    private static final Vec3d MAX = new Vec3d(0.86, 0.99, 0.86);
    private static final Vec3d MIN_CONNECTED = new Vec3d(0.13, 0, 0.13);
    private static final Vec3d MAX_CONNECTED = new Vec3d(0.86, 1 - 1e-5, 0.86);

    @Override
    public void render(TileTank tile, double x, double y, double z, float partialTicks, int destroyStage) {
        FluidStackInterp forRender = tile.getFluidForRender(partialTicks);
        if (forRender == null) {
            return;
        }

        // gl state setup
        GuiLighting.disable();
        MinecraftClient.getInstance().getTextureManager().bindTexture(SpriteAtlasTexture.BLOCK_ATLAS_TEX);
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA);

        // buffer setup

        Tessellator tess = Tessellator.getInstance();
        BufferBuilder bb = tess.getBufferBuilder();
        bb.begin(GL11.GL_QUADS, VertexFormats.POSITION_COLOR_UV_LMAP);
        bb.setOffset(x, y, z);

        boolean[] sideRender = { true, true, true, true, true, true };
        boolean connectedUp = isFullyConnected(tile, Direction.UP, partialTicks);
        boolean connectedDown = isFullyConnected(tile, Direction.DOWN, partialTicks);
        sideRender[Direction.DOWN.ordinal()] = !connectedDown;
        sideRender[Direction.UP.ordinal()] = !connectedUp;

        Vec3d min = connectedDown ? MIN_CONNECTED : MIN;
        Vec3d max = connectedUp ? MAX_CONNECTED : MAX;
        FluidVolume fluid = forRender.fluid;
        int blocklight = fluid.fluidKey == FluidKeys.LAVA ? 15 : 0;
        int combinedLight = tile.getWorld().getLightLevel(tile.getPos(), blocklight);

        TileFluidRenderer.vertex.lighti(combinedLight);
        TileFluidRenderer.vertex.lighti(0xF0_F0);

        TileFluidRenderer.renderFluid(FluidSpriteType.STILL, fluid, forRender.amount, tile.fluidInv.tankCapacity, min,
            max, bb, sideRender);

        // buffer finish
        bb.setOffset(0, 0, 0);
        tess.draw();

        // gl state finish
        GuiLighting.enable();
    }

    private static boolean isFullyConnected(TileTank thisTank, Direction face, float partialTicks) {
        BlockPos pos = thisTank.getPos().offset(face);
        BlockEntity oTile = thisTank.getWorld().getBlockEntity(pos);
        if (oTile instanceof TileTank) {
            TileTank oTank = (TileTank) oTile;
            // if (!TileTank.canTanksConnect(thisTank, oTank, face)) {
            // return false;
            // }
            FluidStackInterp forRender = oTank.getFluidForRender(partialTicks);
            if (forRender == null) {
                return false;
            }
            FluidVolume fluid = forRender.fluid;
            if (fluid == null || forRender.amount <= 0) {
                return false;
            } else if (thisTank.getFluidForRender(partialTicks) == null
                || !fluid.equals(thisTank.getFluidForRender(partialTicks).fluid)) {
                return false;
            }
            // if (fluid.getFluid().isGaseous(fluid)) {
            // face = face.getOpposite();
            // }
            return forRender.amount >= oTank.fluidInv.getMaxAmount(0) || face == Direction.UP;
        } else {
            return false;
        }
    }
}
