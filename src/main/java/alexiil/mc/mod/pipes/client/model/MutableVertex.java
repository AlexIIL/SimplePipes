/*
 * Copyright (c) 2017 SpaceToad and the BuildCraft team
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0. If a copy of the MPL was not
 * distributed with this file, You can obtain one at https://mozilla.org/MPL/2.0/
 */

/*
 * Copyright (c) 2019 SpaceToad and the BuildCraft team
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0. If a copy of the MPL was not
 * distributed with this file, You can obtain one at https://mozilla.org/MPL/2.0/
 */
package alexiil.mc.mod.pipes.client.model;

import alexiil.mc.mod.pipes.mixin.impl.BufferBuilderAccessor;
import alexiil.mc.mod.pipes.util.SpriteUtil;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.renderer.v1.mesh.QuadEmitter;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormatElement;
import net.minecraft.client.render.VertexFormatElement.ComponentType;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.model.BakedQuad;
import net.minecraft.client.texture.Sprite;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import org.joml.Vector3f;
import org.joml.Vector4f;

/** Holds all of the information necessary to make one of the verticies in a {@link BakedQuad}. This provides a variety
 * of methods to quickly set or get different elements. This should be used with {@link MutableQuad} to make a face, or
 * by itself if you only need to define a single vertex. <br>
 * This currently holds the 3D position, normal, colour, 2D texture, skylight and blocklight. Note that you don't have
 * to use all of the elements for this to work - the extra elements come with sensible defaults. <br>
 * All of the mutating methods are in the form {@literal <element><type>}, where {@literal <element>} is the element to
 * set/get, and {@literal <type>} is the type that they should be set as. So {@link #positiond(double, double, double)}
 * will take in 3 doubles and set them to the position element, and {@link #colouri(int, int, int, int)} will take in 4
 * int's and set them to the colour elements. */
@Environment(EnvType.CLIENT)
public class MutableVertex {
    /** The position of this vertex. */
    public float position_x, position_y, position_z;
    /** The normal of this vertex. Might not be normalised. Default value is [0, 1, 0]. */
    public float normal_x, normal_y, normal_z;
    /** The colour of this vertex, where each one is a number in the range 0-255. Default value is 255. */
    public short colour_r, colour_g, colour_b, colour_a;
    /** The texture co-ord of this vertex. Should usually be between 0-1 */
    public float tex_u, tex_v;
    /** The light of this vertex. Should be in the range 0-15. */
    public byte light_block, light_sky;

    public MutableVertex() {
        normal_x = 0;
        normal_y = 1;
        normal_z = 0;

        colour_r = 0xFF;
        colour_g = 0xFF;
        colour_b = 0xFF;
        colour_a = 0xFF;
    }

    public MutableVertex(MutableVertex from) {
        copyFrom(from);
    }

    public MutableVertex(MutableVertex a, MutableVertex b, float interp) {
        position_x = MathHelper.lerp(a.position_x, b.position_x, interp);
        position_y = MathHelper.lerp(a.position_y, b.position_y, interp);
        position_z = MathHelper.lerp(a.position_z, b.position_z, interp);

        normal_x = MathHelper.lerp(a.normal_x, b.normal_x, interp);
        normal_y = MathHelper.lerp(a.normal_y, b.normal_y, interp);
        normal_z = MathHelper.lerp(a.normal_z, b.normal_z, interp);

        colour_r = (short) MathHelper.lerp(a.colour_r, b.colour_r, interp);
        colour_g = (short) MathHelper.lerp(a.colour_g, b.colour_g, interp);
        colour_b = (short) MathHelper.lerp(a.colour_b, b.colour_b, interp);
        colour_a = (short) MathHelper.lerp(a.colour_a, b.colour_a, interp);

        tex_u = MathHelper.lerp(a.tex_u, b.tex_u, interp);
        tex_v = MathHelper.lerp(a.tex_v, b.tex_v, interp);

        light_block = (byte) MathHelper.lerp(a.light_block, b.light_block, interp);
        light_sky = (byte) MathHelper.lerp(a.light_sky, b.light_sky, interp);
    }

    @Override
    public String toString() {
        return "{ pos = [ " + position_x + ", " + position_y + ", " + position_z //
            + " ], norm = [ " + normal_x + ", " + normal_y + ", " + normal_z//
            + " ], colour = [ " + colour_r + ", " + colour_g + ", " + colour_b + ", " + colour_a//
            + " ], tex = [ " + tex_u + ", " + tex_v //
            + " ], light_block = " + light_block + ", light_sky = " + light_sky + " }";
    }

    public MutableVertex copyFrom(MutableVertex from) {
        position_x = from.position_x;
        position_y = from.position_y;
        position_z = from.position_z;

        normal_x = from.normal_x;
        normal_y = from.normal_y;
        normal_z = from.normal_z;

        colour_r = from.colour_r;
        colour_g = from.colour_g;
        colour_b = from.colour_b;
        colour_a = from.colour_a;

        tex_u = from.tex_u;
        tex_v = from.tex_v;

        light_block = from.light_block;
        light_sky = from.light_sky;
        return this;
    }

    public void toBakedBlock(int[] data, int offset) {
        // POS_TEX_COL_LIGHT_NORM
        // POSITION_3F
        data[offset++] = Float.floatToRawIntBits(position_x);
        data[offset++] = Float.floatToRawIntBits(position_y);
        data[offset++] = Float.floatToRawIntBits(position_z);
        // COLOR_4UB
        data[offset++] = colourRGBA();
        // TEX_2F
        data[offset++] = Float.floatToRawIntBits(tex_u);
        data[offset++] = Float.floatToRawIntBits(tex_v);
        // TEX_2S
        data[offset++] = lightc();
        // NORMAL_3B
        data[offset++] = normalToPackedInt();
    }

    public void toBakedItem(int[] data, int offset) {
        // POSITION_3F
        data[offset++] = Float.floatToRawIntBits(position_x);
        data[offset++] = Float.floatToRawIntBits(position_y);
        data[offset++] = Float.floatToRawIntBits(position_z);
        // COLOR_4UB
        data[offset++] = colourRGBA();
        // TEX_2F
        data[offset++] = Float.floatToRawIntBits(tex_u);
        data[offset++] = Float.floatToRawIntBits(tex_v);
        // NORMAL_3B
        data[offset++] = normalToPackedInt();
    }

    public void fromBakedBlock(int[] data, int offset) {
        // POS_TEX_COL_LIGHT_NORM
        // POSITION_3F
        position_x = Float.intBitsToFloat(data[offset + 0]);
        position_y = Float.intBitsToFloat(data[offset + 1]);
        position_z = Float.intBitsToFloat(data[offset + 2]);
        // TEX_2F
        tex_u = Float.intBitsToFloat(data[offset + 4]);
        tex_v = Float.intBitsToFloat(data[offset + 5]);
        // COLOR_4UB
        colouri(data[offset + 3]);
        // TEX_2S
        lighti(data[offset + 6]);
        // NORMAL_3B
        data[offset + 6] = normalToPackedInt();
    }

    public void fromBakedItem(int[] data, int offset) {
        // POSITION_3F
        position_x = Float.intBitsToFloat(data[offset + 0]);
        position_y = Float.intBitsToFloat(data[offset + 1]);
        position_z = Float.intBitsToFloat(data[offset + 2]);
        // COLOR_4UB
        colouri(data[offset + 3]);
        // TEX_2F
        tex_u = Float.intBitsToFloat(data[offset + 4]);
        tex_v = Float.intBitsToFloat(data[offset + 5]);
        // NORMAL_3B
        normali(data[offset + 6]);
        lightf(1, 1);
    }

    public void fromBakedFormat(int[] data, VertexFormat format, int offset) {
        int o = offset;
        for (VertexFormatElement elem : format.getElements()) {
            switch (elem.usage()) {
                case POSITION: {
                    assert elem.type() == ComponentType.FLOAT;
                    position_x = Float.intBitsToFloat(data[o++]);
                    position_y = Float.intBitsToFloat(data[o++]);
                    position_z = Float.intBitsToFloat(data[o++]);
                    break;
                }
                case COLOR: {
                    assert elem.type() == ComponentType.UBYTE;
                    colouri(data[o++]);
                    break;
                }
                case NORMAL: {
                    assert elem.type() == ComponentType.BYTE;
                    normali(data[o++]);
                    break;
                }
                case UV: {
                    if (elem.uvIndex() == 0) {
                        tex_u = Float.intBitsToFloat(data[o++]);
                        tex_v = Float.intBitsToFloat(data[o++]);
                        break;
                    } else if (elem.uvIndex() == 1) {
                        lighti(data[o++]);
                        break;
                    }
                }
                // TODO!
            }
        }
    }

    // Rendering

    public void putData(int vertexIndex, QuadEmitter emitter) {
        emitter.pos(vertexIndex, position_x, position_y, position_z);
        emitter.spriteColor(vertexIndex, 0, colourBGRA());
        emitter.sprite(vertexIndex, 0, tex_u, tex_v);
        emitter.normal(vertexIndex, normal_x, normal_y, normal_z);
        emitter.lightmap(vertexIndex, lightc());
    }

    public void render(BufferBuilder bb) {
        VertexFormat vf = ((BufferBuilderAccessor) bb).simplepipes_getFormat();
        if (vf == VertexFormats.POSITION_COLOR_TEXTURE_LIGHT) {
            renderAsBlock(bb);
        } else {
            for (VertexFormatElement vfe : vf.getElements()) {
                switch (vfe.usage()) {
                    case POSITION:
                        renderPosition(bb);
                        break;
                    case NORMAL:
                        renderNormal(bb);
                        break;
                    case COLOR:
                        renderColour(bb);
                        break;
                    case UV:
                        if (vfe.uvIndex() == 0) renderTex(bb);
                        else if (vfe.uvIndex() == 1) renderLightMap(bb);
                        break;
                    default:
                        break;
                }
            }
        }
    }

    /** Renders this vertex into the given {@link BufferBuilder}, assuming that the {@link VertexFormat} is
     * {@link VertexFormats#POSITION_COLOR_TEXTURE_LIGHT}.
     * <p>
     * Slight performance increase over {@link #render(BufferBuilder)}. */
    public void renderAsBlock(BufferBuilder bb) {
        renderPosition(bb);
        renderColour(bb);
        renderTex(bb);
        renderLightMap(bb);
    }

    public void renderPosition(BufferBuilder bb) {
        bb.vertex(position_x, position_y, position_z);
    }

    public void renderNormal(BufferBuilder bb) {
        bb.normal(normal_x, normal_y, normal_z);
    }

    public void renderColour(BufferBuilder bb) {
        bb.color(colour_r, colour_g, colour_b, colour_a);
    }

    public void renderTex(BufferBuilder bb) {
        bb.texture(tex_u, tex_v);
    }

    // public void renderTex(BufferBuilder bb, buildcraft.api.core.ISprite sprite) {
    // bb.tex(sprite.getInterpU(tex_u), sprite.getInterpV(tex_v));
    // }

    public void renderLightMap(BufferBuilder bb) {
        bb.texture(light_sky << 4, light_block << 4);
    }

    // Mutating

    public MutableVertex positionv(Vector3f vec) {
        return positionf(vec.x, vec.y, vec.z);
    }

    public MutableVertex positionv(Vec3d vec) {
        return positiond(vec.x, vec.y, vec.z);
    }

    public MutableVertex positiond(double x, double y, double z) {
        return positionf((float) x, (float) y, (float) z);
    }

    public MutableVertex positionf(float x, float y, float z) {
        position_x = x;
        position_y = y;
        position_z = z;
        return this;
    }

    public Vector3f positionvf() {
        return new Vector3f(position_x, position_y, position_z);
    }

    public Vec3d positionvd() {
        return new Vec3d(position_x, position_y, position_z);
    }

    /** Sets the current normal for this vertex based off the given vector.<br>
     * Note: This calls {@link #normalf(float, float, float)} internally, so refer to that for more warnings.
     * 
     * @see #normalf(float, float, float) */
    public MutableVertex normalv(Vector3f vec) {
        return normalf(vec.x, vec.y, vec.z);
    }

    /** Sets the current normal given the x, y, and z coordinates. These are NOT normalised or checked. */
    public MutableVertex normalf(float x, float y, float z) {
        normal_x = x;
        normal_y = y;
        normal_z = z;
        return this;
    }

    public MutableVertex normali(int combined) {
        normal_x = ((combined >> 0) & 0xFF) / 0x7f;
        normal_y = ((combined >> 8) & 0xFF) / 0x7f;
        normal_z = ((combined >> 16) & 0xFF) / 0x7f;
        return this;
    }

    public MutableVertex invertNormal() {
        return normalf(-normal_x, -normal_y, -normal_z);
    }

    /** @return The current normal vector of this vertex. This might be normalised. */
    public Vector3f normal() {
        return new Vector3f(normal_x, normal_y, normal_z);
    }

    public int normalToPackedInt() {
        return normalAsByte(normal_x, 0) //
            | normalAsByte(normal_y, 8) //
            | normalAsByte(normal_z, 16);
    }

    private static int normalAsByte(float norm, int offset) {
        int as = (int) (norm * 0x7f);
        return as << offset;
    }

    public MutableVertex colourv(Vector4f vec) {
        return colourf(vec.x, vec.y, vec.z, vec.w);
    }

    public MutableVertex colourf(float r, float g, float b, float a) {
        return colouri((int) (r * 0xFF), (int) (g * 0xFF), (int) (b * 0xFF), (int) (a * 0xFF));
    }

    public MutableVertex colouri(int rgba) {
        return colouri(rgba, rgba >> 8, rgba >> 16, rgba >>> 24);
    }

    public MutableVertex colouri(int r, int g, int b, int a) {
        colour_r = (short) (r & 0xFF);
        colour_g = (short) (g & 0xFF);
        colour_b = (short) (b & 0xFF);
        colour_a = (short) (a & 0xFF);
        return this;
    }

    public Vector4f colourv() {
        return new Vector4f(colour_r / 255f, colour_g / 255f, colour_b / 255f, colour_a / 255f);
    }

    public int colourRGBA() {
        int rgba = 0;
        rgba |= (colour_r & 0xFF) << 0;
        rgba |= (colour_g & 0xFF) << 8;
        rgba |= (colour_b & 0xFF) << 16;
        rgba |= (colour_a & 0xFF) << 24;
        return rgba;
    }

    public int colourABGR() {
        int rgba = 0;
        rgba |= (colour_r & 0xFF) << 24;
        rgba |= (colour_g & 0xFF) << 16;
        rgba |= (colour_b & 0xFF) << 8;
        rgba |= (colour_a & 0xFF) << 0;
        return rgba;
    }

    public int colourBGRA() {
        int rgba = 0;
        rgba |= (colour_a & 0xFF) << 24;
        rgba |= (colour_r & 0xFF) << 16;
        rgba |= (colour_g & 0xFF) << 8;
        rgba |= (colour_b & 0xFF) << 0;
        return rgba;
    }

    public MutableVertex multColourd(double d) {
        int m = (int) (d * 255);
        return multColouri(m);
    }

    public MutableVertex multColourd(double r, double g, double b, double a) {
        return multColouri((int) (r * 255), (int) (g * 255), (int) (b * 255), (int) (a * 255));
    }

    public MutableVertex multColouri(int by) {
        return multColouri(by, by, by, 255);
    }

    public MutableVertex multColouri(int r, int g, int b, int a) {
        colour_r = (short) (colour_r * r / 255);
        colour_g = (short) (colour_g * g / 255);
        colour_b = (short) (colour_b * b / 255);
        colour_a = (short) (colour_a * a / 255);
        return this;
    }

    /** Multiplies the colour by {@link MutableQuad#diffuseLight(float, float, float)} for the normal. */
    public MutableVertex multShade() {
        return multColourd(MutableQuad.diffuseLight(normal_x, normal_y, normal_z));
    }

    public MutableVertex texFromSprite(Sprite sprite) {
        tex_u = SpriteUtil.getU(sprite, tex_u);
        tex_v = SpriteUtil.getV(sprite, tex_v);
        return this;
    }

    public MutableVertex texv(Vec2f vec) {
        return texf(vec.x, vec.y);
    }

    public MutableVertex texf(float u, float v) {
        tex_u = u;
        tex_v = v;
        return this;
    }

    public Vec2f tex() {
        return new Vec2f(tex_u, tex_v);
    }

    public MutableVertex lightv(Vec2f vec) {
        return lightf(vec.x, vec.y);
    }

    public MutableVertex lightf(float block, float sky) {
        return lighti((int) (block * 0xF), (int) (sky * 0xF));
    }

    public MutableVertex lighti(int combined) {
        return lighti(combined >> 4, combined >> 20);
    }

    public MutableVertex lighti(int block, int sky) {
        light_block = (byte) block;
        light_sky = (byte) sky;
        return this;
    }

    public MutableVertex maxLighti(int block, int sky) {
        return lighti(Math.max(block, light_block), Math.max(sky, light_sky));
    }

    public Vec2f lightvf() {
        return new Vec2f(light_block * 15f, light_sky * 15f);
    }

    public int lightc() {
        return light_block << 4 + light_sky << 20;
    }

    public int[] lighti() {
        return new int[] { light_block, light_sky };
    }

    // public MutableVertex transform(Matrix4f matrix) {
    // positionv(MatrixUtil.transform(matrix, positionvf()));
    // normalv(MatrixUtil.transform(matrix, normal()));
    // return this;
    // }

    public MutableVertex translatei(int x, int y, int z) {
        position_x += x;
        position_y += y;
        position_z += z;
        return this;
    }

    public MutableVertex translatef(float x, float y, float z) {
        position_x += x;
        position_y += y;
        position_z += z;
        return this;
    }

    public MutableVertex translated(double x, double y, double z) {
        position_x += x;
        position_y += y;
        position_z += z;
        return this;
    }

    public MutableVertex translatevi(Vec3i vec) {
        return translatei(vec.getX(), vec.getY(), vec.getZ());
    }

    public MutableVertex translatevd(Vec3d vec) {
        return translated(vec.x, vec.y, vec.z);
    }

    public MutableVertex scalef(float scale) {
        position_x *= scale;
        position_y *= scale;
        position_z *= scale;
        return this;
    }

    public MutableVertex scaled(double scale) {
        return scalef((float) scale);
    }

    public MutableVertex scalef(float x, float y, float z) {
        position_x *= x;
        position_y *= y;
        position_z *= z;
        // TODO: scale normals?
        return this;
    }

    public MutableVertex scaled(double x, double y, double z) {
        return scalef((float) x, (float) y, (float) z);
    }

    /** Rotates around the X axis by angle. */
    public void rotateX(float angle) {
        float cos = MathHelper.cos(angle);
        float sin = MathHelper.sin(angle);
        rotateDirectlyX(cos, sin);
    }

    /** Rotates around the Y axis by angle. */
    public void rotateY(float angle) {
        float cos = MathHelper.cos(angle);
        float sin = MathHelper.sin(angle);
        rotateDirectlyY(cos, sin);
    }

    /** Rotates around the Z axis by angle. */
    public void rotateZ(float angle) {
        float cos = MathHelper.cos(angle);
        float sin = MathHelper.sin(angle);
        rotateDirectlyZ(cos, sin);
    }

    public void rotateDirectlyX(float cos, float sin) {
        float y = position_y;
        float z = position_z;
        position_y = y * cos - z * sin;
        position_z = y * sin + z * cos;
    }

    public void rotateDirectlyY(float cos, float sin) {
        float x = position_x;
        float z = position_z;
        position_x = x * cos - z * sin;
        position_z = x * sin + z * cos;
    }

    public void rotateDirectlyZ(float cos, float sin) {
        float x = position_x;
        float y = position_y;
        position_x = x * cos + y * sin;
        position_y = x * -sin + y * cos;
    }

    /** Rotates this vertex around the X axis 90 degrees.
     * 
     * @param scale The multiplier for scaling. Positive values will rotate clockwise, negative values rotate
     *            anti-clockwise. */
    public MutableVertex rotateX_90(float scale) {
        float ym = scale;
        float zm = -ym;

        float t = position_y * ym;
        position_y = position_z * zm;
        position_z = t;

        t = normal_y * ym;
        normal_y = normal_z * zm;
        normal_z = t;
        return this;
    }

    /** Rotates this vertex around the Y axis 90 degrees.
     * 
     * @param scale The multiplier for scaling. Positive values will rotate clockwise, negative values rotate
     *            anti-clockwise. */
    public MutableVertex rotateY_90(float scale) {
        float xm = scale;
        float zm = -xm;

        float t = position_x * xm;
        position_x = position_z * zm;
        position_z = t;

        t = normal_x * xm;
        normal_x = normal_z * zm;
        normal_z = t;
        return this;
    }

    /** Rotates this vertex around the Z axis 90 degrees.
     * 
     * @param scale The multiplier for scaling. Positive values will rotate clockwise, negative values rotate
     *            anti-clockwise. */
    public MutableVertex rotateZ_90(float scale) {
        float xm = scale;
        float ym = -xm;

        float t = position_x * xm;
        position_x = position_y * ym;
        position_y = t;

        t = normal_x * xm;
        normal_x = normal_y * ym;
        normal_y = t;
        return this;
    }

    /** Rotates this vertex around the X axis by 180 degrees. */
    public MutableVertex rotateX_180() {
        position_y = -position_y;
        position_z = -position_z;
        normal_y = -normal_y;
        normal_z = -normal_z;
        return this;
    }

    /** Rotates this vertex around the Y axis by 180 degrees. */
    public MutableVertex rotateY_180() {
        position_x = -position_x;
        position_z = -position_z;
        normal_x = -normal_x;
        normal_z = -normal_z;
        return this;
    }

    /** Rotates this vertex around the Z axis by 180 degrees. */
    public MutableVertex rotateZ_180() {
        position_x = -position_x;
        position_y = -position_y;
        normal_x = -normal_x;
        normal_y = -normal_y;
        return this;
    }
}
