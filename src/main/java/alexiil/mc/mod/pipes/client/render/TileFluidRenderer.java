package alexiil.mc.mod.pipes.client.render;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.texture.MissingSprite;
import net.minecraft.client.texture.Sprite;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Direction.Axis;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

import alexiil.mc.lib.attributes.fluid.IFixedFluidInv;
import alexiil.mc.lib.attributes.fluid.volume.FluidVolume;
import alexiil.mc.mod.pipes.client.model.MutableVertex;
import alexiil.mc.mod.pipes.util.VecUtil;

@Deprecated
public class TileFluidRenderer {

    // private static final EnumMap<FluidSpriteType, Map<String, Sprite>> fluidSprites =
    // new EnumMap<>(FluidSpriteType.class);
    public static final MutableVertex vertex = new MutableVertex();
    private static final boolean[] DEFAULT_FACES = { true, true, true, true, true, true };

    // Cached fields that prevent lots of arguments on most methods
    private static BufferBuilder bb;
    private static Sprite sprite;
    private static TexMap texmap;
    private static boolean invertU, invertV;
    private static double xTexDiff, yTexDiff, zTexDiff;

    static {
        // TODO: allow the caller to change the light level
        vertex.lighti(0xF, 0xF);
        // for (FluidSpriteType type : FluidSpriteType.values()) {
        // fluidSprites.put(type, new HashMap<>());
        // }
    }

    // public static void onTextureStitchPre(TextureMap map) {
    // for (FluidSpriteType type : FluidSpriteType.values()) {
    // fluidSprites.get(type).clear();
    // }
    // Map<Identifier, SpriteFluidFrozen> spritesStitched = new HashMap<>();
    // for (Fluid fluid : FluidRegistry.getRegisteredFluids().values()) {
    // Identifier still = fluid.getStill();
    // Identifier flowing = fluid.getFlowing();
    // if (still == null || flowing == null) {
    // throw new IllegalStateException("Encountered a fluid with a null still sprite! (" + fluid.getName()
    // + " - " + FluidRegistry.getDefaultFluidName(fluid) + ")");
    // }
    // if (spritesStitched.containsKey(still)) {
    // fluidSprites.get(FluidSpriteType.FROZEN).put(fluid.getName(), spritesStitched.get(still));
    // } else {
    // SpriteFluidFrozen spriteFrozen = new SpriteFluidFrozen(still);
    // spritesStitched.put(still, spriteFrozen);
    // if (!map.setTextureEntry(spriteFrozen)) {
    // throw new IllegalStateException("Failed to set the frozen variant of " + still + "!");
    // }
    // fluidSprites.get(FluidSpriteType.FROZEN).put(fluid.getName(), spriteFrozen);
    // }
    // // Note: this must be called with EventPriority.LOW so that we don't overwrite other custom sprites.
    // fluidSprites.get(FluidSpriteType.STILL).put(fluid.getName(), map.registerSprite(still));
    // fluidSprites.get(FluidSpriteType.FLOWING).put(fluid.getName(), map.registerSprite(flowing));
    // }
    // }

    /** Renders a fluid cuboid to the given vertex buffer. The cube shouldn't cross over any {@literal 0->1} boundary
     * (so the cube must be contained within a block).
     * 
     * @param type The type of sprite to use. See {@link FluidSpriteType} for more details.
     * @param tank The fluid tank that should be rendered.
     * @param min The minimum coordinate that the tank should be rendered from
     * @param max The maximum coordinate that the tank will be rendered to.
     * @param bbIn The {@link BufferBuilder} that the fluid will be rendered into.
     * @param sideRender A size 6 boolean array that determines if the face will be rendered. If it is null then all
     *            faces will be rendered. The indexes are determined by what {@link Direction#ordinal()} returns.
     * @see #renderFluid(FluidSpriteType, FluidVolume, double, double, Vec3d, Vec3d, BufferBuilder, boolean[]) */
    public static void renderFluid(FluidSpriteType type, IFixedFluidInv tankInv, int tank, Vec3d min, Vec3d max,
        BufferBuilder bbIn, boolean[] sideRender) {
        renderFluid(type, tankInv.getInvFluid(tank), tankInv.getMaxAmount(tank), min, max, bbIn, sideRender);
    }

    /** Render's a fluid cuboid to the given vertex buffer. The cube shouldn't cross over any {@literal 0->1} boundary
     * (so the cube must be contained within a block).
     * 
     * @param type The type of sprite to use. See {@link FluidSpriteType} for more details.
     * @param fluid The stack that represents the fluid to render
     * @param cap The maximum amount of fluid that could be in the stack. Usually the capacity of the tank.
     * @param min The minimum coordinate that the tank should be rendered from
     * @param max The maximum coordinate that the tank will be rendered to.
     * @param bbIn The {@link BufferBuilder} that the fluid will be rendered into.
     * @param sideRender A size 6 boolean array that determines if the face will be rendered. If it is null then all
     *            faces will be rendered. The indexes are determined by what {@link Direction#ordinal()} returns. */
    public static void renderFluid(FluidSpriteType type, FluidVolume fluid, int cap, Vec3d min, Vec3d max,
        BufferBuilder bbIn, boolean[] sideRender) {
        renderFluid(type, fluid, fluid.getAmount(), cap, min, max, bbIn, sideRender);
    }

    /** Render's a fluid cuboid to the given vertex buffer. The cube shouldn't cross over any {@literal 0->1} boundary
     * (so the cube must be contained within a block).
     * 
     * @param type The type of sprite to use. See {@link FluidSpriteType} for more details.
     * @param fluid The stack that represents the fluid to render. Note that the amount from the stack is NOT used.
     * @param amount The actual amount of fluid in the stack. Is a "double" rather than an "int" as then you can
     *            interpolate between frames.
     * @param cap The maximum amount of fluid that could be in the stack. Usually the capacity of the tank.
     * @param min The minimum coordinate that the tank should be rendered from
     * @param max The maximum coordinate that the tank will be rendered to.
     * @param bbIn The {@link BufferBuilder} that the fluid will be rendered into.
     * @param sideRender A size 6 boolean array that determines if the face will be rendered. If it is null then all
     *            faces will be rendered. The indexes are determined by what {@link Direction#ordinal()} returns. */
    public static void renderFluid(FluidSpriteType type, FluidVolume fluid, double amount, double cap, Vec3d min,
        Vec3d max, BufferBuilder bbIn, boolean[] sideRender) {
        if (fluid.isEmpty() || amount <= 0) {
            return;
        }
        if (sideRender == null) {
            sideRender = DEFAULT_FACES;
        }

        double height = MathHelper.clamp(amount / cap, 0, 1);
        final Vec3d realMin, realMax;
        // if (fluid.getFluid().isGaseous(fluid)) {
        // realMin = VecUtil.replaceValue(min, Axis.Y, MathUtil.interp(1 - height, min.y, max.y));
        // realMax = max;
        // } else {
        realMin = min;
        realMax = VecUtil.replaceValue(max, Axis.Y, VecUtil.interp(height, min.y, max.y));
        // }

        bb = bbIn;

        if (type == null) {
            type = FluidSpriteType.STILL;
        }
        sprite = getFluidSprite(type, fluid);

        final double xs = realMin.x;
        final double ys = realMin.y;
        final double zs = realMin.z;

        final double xb = realMax.x;
        final double yb = realMax.y;
        final double zb = realMax.z;

        if (type == FluidSpriteType.FROZEN) {
            if (min.x > 1) {
                xTexDiff = Math.floor(min.x);
            } else if (min.x < 0) {
                xTexDiff = Math.floor(min.x);
            } else {
                xTexDiff = 0;
            }
            if (min.y > 1) {
                yTexDiff = Math.floor(min.y);
            } else if (min.y < 0) {
                yTexDiff = Math.floor(min.y);
            } else {
                yTexDiff = 0;
            }
            if (min.z > 1) {
                zTexDiff = Math.floor(min.z);
            } else if (min.z < 0) {
                zTexDiff = Math.floor(min.z);
            } else {
                zTexDiff = 0;
            }
        } else {
            xTexDiff = 0;
            yTexDiff = 0;
            zTexDiff = 0;
        }

        vertex.colouri(0xFF_00_00_00 | fluid.getRenderColor());

        texmap = TexMap.XZ;
        // TODO: Enable/disable inversion for the correct faces
        invertU = false;
        invertV = false;
        if (sideRender[Direction.UP.ordinal()]) {
            vertex(xs, yb, zb);
            vertex(xb, yb, zb);
            vertex(xb, yb, zs);
            vertex(xs, yb, zs);
        }

        if (sideRender[Direction.DOWN.ordinal()]) {
            vertex(xs, ys, zs);
            vertex(xb, ys, zs);
            vertex(xb, ys, zb);
            vertex(xs, ys, zb);
        }

        texmap = TexMap.ZY;
        if (sideRender[Direction.WEST.ordinal()]) {
            vertex(xs, ys, zs);
            vertex(xs, ys, zb);
            vertex(xs, yb, zb);
            vertex(xs, yb, zs);
        }

        if (sideRender[Direction.EAST.ordinal()]) {
            vertex(xb, yb, zs);
            vertex(xb, yb, zb);
            vertex(xb, ys, zb);
            vertex(xb, ys, zs);
        }

        texmap = TexMap.XY;
        if (sideRender[Direction.NORTH.ordinal()]) {
            vertex(xs, yb, zs);
            vertex(xb, yb, zs);
            vertex(xb, ys, zs);
            vertex(xs, ys, zs);
        }

        if (sideRender[Direction.SOUTH.ordinal()]) {
            vertex(xs, ys, zb);
            vertex(xb, ys, zb);
            vertex(xb, yb, zb);
            vertex(xs, yb, zb);
        }

        sprite = null;
        texmap = null;
        bb = null;
    }

    public static Sprite getFluidSprite(FluidSpriteType type, FluidVolume fluid) {
        if (fluid == null) {
            return MissingSprite.getMissingSprite();
        }
        return MinecraftClient.getInstance().getSpriteAtlas().getSprite(fluid.getSprite());
    }

    /** Helper function to add a vertex. */
    private static void vertex(double x, double y, double z) {
        vertex.positiond(x, y, z);
        texmap.apply(x - xTexDiff, y - yTexDiff, z - zTexDiff);
        vertex.renderAsBlock(bb);
    }

    // /** Fills up the given region with the fluids texture, repeated. Ignores the value of {@link FluidStack#amount}.
    // Use
    // * {@link GuiUtil}'s fluid drawing methods in preference to this. */
    // public static void drawFluidForGui(FluidStack fluid, double startX, double startY, double endX, double endY) {
    //
    // sprite = FluidRenderer.fluidSprites.get(FluidSpriteType.STILL).get(fluid.getFluid().getName());
    // if (sprite == null) {
    // sprite = Minecraft.getMinecraft().getTextureMapBlocks().getMissingSprite();
    // }
    // Minecraft.getMinecraft().renderEngine.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
    // RenderUtil.setGLColorFromInt(fluid.getFluid().getColor(fluid));
    //
    // Tessellator tess = Tessellator.getInstance();
    // bb = tess.getBuffer();
    // bb.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
    //
    // // draw all the full sprites
    //
    // double diffX = endX - startX;
    // double diffY = endY - startY;
    //
    // int stepX = diffX > 0 ? 16 : -16;
    // int stepY = diffY > 0 ? 16 : -16;
    //
    // int loopCountX = (int) Math.abs(diffX / 16);
    // int loopCountY = (int) Math.abs(diffY / 16);
    //
    // double x = startX;
    // for (int xc = 0; xc < loopCountX; xc++) {
    // double y = startY;
    // for (int yc = 0; yc < loopCountY; yc++) {
    // guiVertex(x, y, 0, 0);
    // guiVertex(x + stepX, y, 16, 0);
    // guiVertex(x + stepX, y + stepY, 16, 16);
    // guiVertex(x, y + stepY, 0, 16);
    // y += stepY;
    // }
    // x += stepX;
    // }
    //
    // if (diffX % 16 != 0) {
    // double additionalWidth = diffX % 16;
    // x = endX - additionalWidth;
    // double xTex = Math.abs(additionalWidth);
    // double y = startY;
    // for (int yc = 0; yc < loopCountY; y++) {
    // guiVertex(x, y, 0, 0);
    // guiVertex(endX, y, xTex, 0);
    // guiVertex(endX, y + stepY, xTex, 16);
    // guiVertex(x, y + stepY, 0, 16);
    // y += stepY;
    // }
    // }
    //
    // if (diffY % 16 != 0) {
    // double additionalHeight = diffY % 16;
    // double y = endY - additionalHeight;
    // double yTex = Math.abs(additionalHeight);
    // x = startX;
    // for (int xc = 0; xc < loopCountX; xc++) {
    // guiVertex(x, y, 0, 0);
    // guiVertex(x + stepX, y, 16, 0);
    // guiVertex(x + stepX, endY, 16, yTex);
    // guiVertex(x, endY, 0, yTex);
    // x += stepX;
    // }
    // }
    //
    // if (diffX % 16 != 0 && diffY % 16 != 0) {
    // double w = diffX % 16;
    // double h = diffY % 16;
    // x = endX - w;
    // double y = endY - h;
    // double tx = w < 0 ? -w : w;
    // double ty = h < 0 ? -h : h;
    // guiVertex(x, y, 0, 0);
    // guiVertex(endX, y, tx, 0);
    // guiVertex(endX, endY, tx, ty);
    // guiVertex(x, endY, 0, ty);
    // }
    //
    // tess.draw();
    // GlStateManager.color(1, 1, 1);
    // sprite = null;
    // bb = null;
    // }
    //
    // private static void guiVertex(double x, double y, double u, double v) {
    // float ru = sprite.getInterpolatedU(u);
    // float rv = sprite.getInterpolatedV(v);
    // bb.pos(x, y, 0);
    // bb.tex(ru, rv);
    // bb.endVertex();
    // }

    /** Used to keep track of what position maps to what texture co-ord.
     * <p>
     * For example XY maps X to U and Y to V, and ignores Z */
    private enum TexMap {
        XY(true, true),
        XZ(true, false),
        ZY(false, true);

        /** If true, then X maps to U. Otherwise Z maps to U. */
        private final boolean ux;
        /** If true, then Y maps to V. Otherwise Z maps to V. */
        private final boolean vy;

        TexMap(boolean ux, boolean vy) {
            this.ux = ux;
            this.vy = vy;
        }

        /** Changes the vertex's texture co-ord to be the same as the position, for that face. (Uses {@link #ux} and
         * {@link #vy} to determine how they are mapped). */
        private void apply(double x, double y, double z) {
            double realu = ux ? x : z;
            double realv = vy ? y : z;
            if (invertU) {
                realu = 1 - realu;
            }
            if (invertV) {
                realv = 1 - realv;
            }
            vertex.texf(sprite.getU(realu * 16), sprite.getV(realv * 16));
        }
    }

    public static class TankSize {
        public final Vec3d min;
        public final Vec3d max;

        public TankSize(int sx, int sy, int sz, int ex, int ey, int ez) {
            this(new Vec3d(sx, sy, sz).multiply(1 / 16.0), new Vec3d(ex, ey, ez).multiply(1 / 16.0));
        }

        public TankSize(Vec3d min, Vec3d max) {
            this.min = min;
            this.max = max;
        }

        public TankSize shrink(double by) {
            return shrink(by, by, by);
        }

        public TankSize shrink(double x, double y, double z) {
            return new TankSize(min.add(x, y, z), max.subtract(x, y, z));
        }

        public TankSize shink(Vec3d by) {
            return shrink(by.x, by.y, by.z);
        }

        public TankSize rotateY() {
            Vec3d _min = rotateY(min);
            Vec3d _max = rotateY(max);
            return new TankSize(VecUtil.min(_min, _max), VecUtil.max(_min, _max));
        }

        private static Vec3d rotateY(Vec3d vec) {
            return new Vec3d(//
                1 - vec.z, //
                vec.y, //
                vec.x//
            );
        }
    }
}
