package alexiil.mc.mod.pipes.client.render;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

import com.mojang.blaze3d.platform.GLX;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.GuiLighting;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.util.math.Direction;

import alexiil.mc.lib.attributes.fluid.render.FluidRenderFace;
import alexiil.mc.lib.attributes.fluid.volume.FluidVolume;
import alexiil.mc.lib.multipart.api.MultipartContainer;
import alexiil.mc.lib.multipart.api.render.PartRenderer;
import alexiil.mc.mod.pipes.part.PartTank;
import alexiil.mc.mod.pipes.util.FluidSmoother.FluidStackInterp;

public class TankPartRenderer implements PartRenderer<PartTank> {

    @Override
    public void render(PartTank part, double x, double y, double z, float partialTicks, int breakProgress) {
        FluidStackInterp forRender = part.getFluidForRender(partialTicks);
        if (forRender == null) {
            return;
        }

        // gl state setup
        GuiLighting.disable();
        MinecraftClient.getInstance().getTextureManager().bindTexture(SpriteAtlasTexture.BLOCK_ATLAS_TEX);
//        GlStateManager.enableBlend();
//        GlStateManager.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA);

        // buffer setup

        FluidVolume fluid = forRender.fluid;
        int blocklight = 0;// fluid.fluidKey == FluidKeys.LAVA ? 15 : 0;
        MultipartContainer container = part.holder.getContainer();
        int combinedLight = container.getMultipartWorld().getLightmapIndex(container.getMultipartPos(), blocklight);
        GLX.glMultiTexCoord2f(GLX.GL_TEXTURE1, combinedLight & 0xFFFF, (combinedLight >> 16) & 0xFFFF);

        List<FluidRenderFace> faces = new ArrayList<>();

        double x0 = 0.126;
        double y0 = 0.001;
        double z0 = 0.126;
        double x1 = 0.874;
        double y1 = 0.001 + (12 / 16.0 - 0.002) * forRender.amount / part.fluidInv.tankCapacity;
        double z1 = 0.874;

        EnumSet<Direction> sides = EnumSet.allOf(Direction.class);
        FluidRenderFace.appendCuboid(x0, y0, z0, x1, y1, z1, 16, sides, faces);
        forRender.fluid.getRenderer().render(forRender.fluid, faces, x, y, z);

        // gl state finish
        GuiLighting.enable();
    }

//    private static boolean isFullyConnected(PartTank thisTank, Direction face, float partialTicks) {
//        BlockPos pos = thisTank.getPos().offset(face);
//        BlockEntity oTile = thisTank.getWorld().getBlockEntity(pos);
//        if (oTile instanceof TileTank) {
//            TileTank oTank = (TileTank) oTile;
//            // if (!TileTank.canTanksConnect(thisTank, oTank, face)) {
//            // return false;
//            // }
//            FluidStackInterp forRender = oTank.getFluidForRender(partialTicks);
//            if (forRender == null) {
//                return false;
//            }
//            FluidVolume fluid = forRender.fluid;
//            if (fluid == null || forRender.amount <= 0) {
//                return false;
//            } else if (thisTank.getFluidForRender(partialTicks) == null
//                || !fluid.equals(thisTank.getFluidForRender(partialTicks).fluid)) {
//                return false;
//            }
//            // if (fluid.getFluid().isGaseous(fluid)) {
//            // face = face.getOpposite();
//            // }
//            return forRender.amount >= oTank.fluidInv.getMaxAmount(0) || face == Direction.UP;
//        } else {
//            return false;
//        }
//    }
}
