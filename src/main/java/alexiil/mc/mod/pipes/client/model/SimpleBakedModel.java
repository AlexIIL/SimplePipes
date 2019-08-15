package alexiil.mc.mod.pipes.client.model;

import java.util.List;
import java.util.Random;

import com.google.common.collect.ImmutableList;

import net.minecraft.block.BlockState;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.BakedQuad;
import net.minecraft.client.render.model.json.ModelItemPropertyOverrideList;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.render.model.json.Transformation;
import net.minecraft.client.texture.MissingSprite;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.util.math.Direction;

/** Provides a simple way of creating a {@link BakedModel} with just a list of quads. This provides some transforms to
 * use that make it simple to render item models with various different transforms. */
public class SimpleBakedModel implements BakedModel {
    public static final ModelTransformation TRANSFORM_DEFAULT = ModelTransformation.NONE;
    public static final ModelTransformation TRANSFORM_BLOCK;
    public static final ModelTransformation TRANSFORM_ROBOT;
    public static final ModelTransformation TRANSFORM_PLUG_AS_ITEM;
    public static final ModelTransformation TRANSFORM_PLUG_AS_ITEM_BIGGER;
    public static final ModelTransformation TRANSFORM_PLUG_AS_BLOCK;
    public static final ModelTransformation TRANSFORM_ITEM;
    // TODO: TRANSFORM_TOOL

    static {
        // Values taken from "minecraft:models/block/block.json"
        Transformation thirdp_left = def(75, 45, 0, 0, 2.5, 0, 0.375);
        Transformation thirdp_right = def(75, 225, 0, 0, 2.5, 0, 0.375);
        Transformation firstp_left = def(0, 225, 0, 0, 0, 0, 0.4);
        Transformation firstp_right = def(0, 45, 0, 0, 0, 0, 0.4);
        Transformation head = def(0, 0, 0, 0, 0, 0, 1);
        Transformation gui = def(30, 225, 0, 0, 0, 0, 0.625);
        Transformation ground = def(0, 0, 0, 0, 3, 0, 0.25);
        Transformation fixed = def(0, 0, 0, 0, 0, 0, 0.5);
        TRANSFORM_BLOCK = new ModelTransformation(
            thirdp_left, thirdp_right, firstp_left, firstp_right, head, gui, ground, fixed
        );

        Transformation r_firstp_left = translate(firstp_left, 0, 0.25, 0);
        Transformation r_firstp_right = translate(firstp_right, 0, 0.25, 0);
        TRANSFORM_ROBOT = new ModelTransformation(
            thirdp_left, thirdp_right, r_firstp_left, r_firstp_right, head, gui, ground, fixed
        );

        Transformation item_head = def(0, 0, 0, 0, 0, 0, 1);
        Transformation item_gui = def(0, 90, 0, 0, 0, 0, 1);
        Transformation item_ground = def(0, 0, 0, 0, 3, 0, 0.5);
        Transformation item_fixed = def(0, 0, 0, 0, 0, 0, 0.85);
        firstp_left = def(0, 225, 0, 0, 0, -4, 0.4);
        firstp_right = def(0, 45, 0, 0, 0, -4, 0.4);
        TRANSFORM_PLUG_AS_ITEM = new ModelTransformation(
            thirdp_left, thirdp_right, firstp_left, firstp_right, item_head, item_gui, item_ground, item_fixed
        );
        TRANSFORM_PLUG_AS_ITEM_BIGGER = scale(TRANSFORM_PLUG_AS_ITEM, 1.8);

        thirdp_left = def(75, 45, 0, 0, 2.5, 0, 0.375);
        thirdp_right = def(75, 225, 0, 0, 2.5, 0, 0.375);
        firstp_left = def(0, 45, 0, 0, 0, 0, 0.4);
        firstp_right = def(0, 225, 0, 0, 0, 0, 0.4);
        gui = def(30, 135, 0, -3, 1.5, 0, 0.625);
        TRANSFORM_PLUG_AS_BLOCK = new ModelTransformation(
            thirdp_left, thirdp_right, firstp_left, firstp_right, head, gui, ground, fixed
        );

        ground = def(0, 0, 0, 0, 2, 0, 0.5);
        head = def(0, 180, 0, 0, 13, 7, 1);
        thirdp_right = def(0, 0, 0, 0, 3, 1, 0.55);
        firstp_right = def(0, -90, 25, 1.13, 3.2, 1.13, 0.68);
        thirdp_left = thirdp_right;
        firstp_left = firstp_right;
        fixed = def(0, 180, 0, 0, 0, 0, 1);
        gui = def(0, 0, 0, 0, 0, 0, 1);
        TRANSFORM_ITEM = new ModelTransformation(
            thirdp_left, thirdp_right, firstp_left, firstp_right, head, gui, ground, fixed
        );
    }

    private static ModelTransformation scale(ModelTransformation from, double by) {
        Transformation thirdperson_left = scale(from.thirdPersonLeftHand, by);
        Transformation thirdperson_right = scale(from.thirdPersonRightHand, by);
        Transformation firstperson_left = scale(from.firstPersonLeftHand, by);
        Transformation firstperson_right = scale(from.firstPersonRightHand, by);
        Transformation head = scale(from.head, by);
        Transformation gui = scale(from.gui, by);
        Transformation ground = scale(from.ground, by);
        Transformation fixed = scale(from.fixed, by);
        return new ModelTransformation(
            thirdperson_left, thirdperson_right, firstperson_left, firstperson_right, head, gui, ground, fixed
        );
    }

    private static Transformation scale(Transformation from, double by) {

        float scale = (float) by;
        Vector3f nScale = new Vector3f(from.scale);
        nScale.scale(scale);

        return new Transformation(from.rotation, from.translation, nScale);
    }

    private static Transformation translate(Transformation from, double dx, double dy, double dz) {
        Vector3f nTranslation = new Vector3f(from.translation);
        nTranslation.add((float) dx, (float) dy, (float) dz);
        return new Transformation(from.rotation, nTranslation, from.scale);
    }

    private static Transformation def(double rx, double ry, double rz, double tx, double ty, double tz, double scale) {
        return def((float) rx, (float) ry, (float) rz, (float) tx, (float) ty, (float) tz, (float) scale);
    }

    private static Transformation def(float rx, float ry, float rz, float tx, float ty, float tz, float scale) {
        Vector3f rot = new Vector3f(rx, ry, rz);
        Vector3f translate = new Vector3f(tx / 16f, ty / 16f, tz / 16f);
        return new Transformation(rot, translate, new Vector3f(scale, scale, scale));
    }

    private final boolean hasDepthInGui;
    private final List<BakedQuad> quads;
    private final Sprite sprite;
    private final ModelTransformation transformation;

    /** Variant for creating item models. */
    public SimpleBakedModel(List<BakedQuad> quads, ModelTransformation transforms, boolean hasDepthInGui) {
        this.quads = quads == null ? ImmutableList.of() : quads;
        this.hasDepthInGui = hasDepthInGui;
        if (this.quads.isEmpty()) {
            sprite = MissingSprite.getMissingSprite();
        } else {
            sprite = this.quads.get(0).getSprite();
            assert sprite != null : "The first quad had a null sprite!";
        }
        this.transformation = transforms;
    }

    /** Variant for creating dynamic block models. This is intended to be used for subclassing. */
    protected SimpleBakedModel(Sprite particleSprite) {
        this(ImmutableList.of(), particleSprite);
    }

    /** Variant for creating dynamic block models. */
    public SimpleBakedModel(List<BakedQuad> quads, Sprite particleSprite) {
        this.quads = quads;
        hasDepthInGui = false;
        sprite = particleSprite;
        transformation = TRANSFORM_DEFAULT;
    }

    @Override
    public List<BakedQuad> getQuads(BlockState state, Direction side, Random rand) {
        return side == null ? quads : ImmutableList.of();
    }

    @Override
    public boolean useAmbientOcclusion() {
        return false;
    }

    @Override
    public boolean hasDepthInGui() {
        return hasDepthInGui;
    }

    @Override
    public boolean isBuiltin() {
        return false;
    }

    @Override
    public Sprite getSprite() {
        return sprite;
    }

    @Override
    public ModelTransformation getTransformation() {
        return transformation;
    }

    @Override
    public ModelItemPropertyOverrideList getItemPropertyOverrides() {
        return ModelItemPropertyOverrideList.EMPTY;
    }
}
