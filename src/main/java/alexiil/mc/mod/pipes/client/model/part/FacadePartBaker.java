package alexiil.mc.mod.pipes.client.model.part;

import net.fabricmc.fabric.api.renderer.v1.RendererAccess;
import net.fabricmc.fabric.api.renderer.v1.material.MaterialFinder;
import net.fabricmc.fabric.api.renderer.v1.material.RenderMaterial;
import net.fabricmc.fabric.api.renderer.v1.mesh.QuadEmitter;

import net.minecraft.block.BlockRenderLayer;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.texture.MissingSprite;
import net.minecraft.client.texture.Sprite;

import alexiil.mc.lib.multipart.api.render.PartModelBaker;
import alexiil.mc.lib.multipart.api.render.PartRenderContext;
import alexiil.mc.mod.pipes.client.model.ModelUtil;
import alexiil.mc.mod.pipes.client.model.MutableQuad;

public enum FacadePartBaker implements PartModelBaker<FacadePartKey> {
    INSTANCE;

    @Override
    public void emitQuads(FacadePartKey key, PartRenderContext ctx) {
        // TODO: Replace this with a more useful shape-based texture applicator!
        BakedModel model = ModelUtil.getBlockModel(key.state);
        Sprite sprite = model.getSprite();
        if (sprite == null) {
            sprite = MissingSprite.getMissingSprite();
        }

        BlockRenderLayer blockLayer = key.state.getBlock().getRenderLayer();
        QuadEmitter emitter = ctx.getEmitter();
        MaterialFinder finder = RendererAccess.INSTANCE.getRenderer().materialFinder();
        finder = finder.blendMode(0, blockLayer);
        final RenderMaterial mat = finder.find();

        for (MutableQuad quad : ModelUtil.createModel(key.shape.shape, sprite)) {
            quad.multShade();
            emitter.material(mat);
            emitter.nominalFace(quad.getFace());
            quad.putData(emitter);
            emitter.emit();
        }
    }
}
