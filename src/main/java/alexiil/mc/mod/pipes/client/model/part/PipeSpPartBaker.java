package alexiil.mc.mod.pipes.client.model.part;

import net.fabricmc.fabric.api.renderer.v1.RendererAccess;
import net.fabricmc.fabric.api.renderer.v1.material.BlendMode;
import net.fabricmc.fabric.api.renderer.v1.material.RenderMaterial;
import net.fabricmc.fabric.api.renderer.v1.mesh.QuadEmitter;

import net.minecraft.client.render.model.BakedQuad;

import alexiil.mc.mod.pipes.blocks.TilePipe.PipeBlockModelState;
import alexiil.mc.mod.pipes.client.model.PipeBaseModelGenStandard;
import alexiil.mc.mod.pipes.client.model.SpriteSupplier;

import alexiil.mc.lib.multipart.api.render.PartModelBaker;
import alexiil.mc.lib.multipart.api.render.PartRenderContext;

public class PipeSpPartBaker implements PartModelBaker<PipeBlockModelState> {

    private final SpriteSupplier sprites;

    public PipeSpPartBaker(SpriteSupplier sprites) {
        this.sprites = sprites;
    }

    @Override
    public void emitQuads(PipeBlockModelState key, PartRenderContext ctx) {
        QuadEmitter emitter = ctx.getEmitter();
        RenderMaterial cutout
            = RendererAccess.INSTANCE.getRenderer().materialFinder().blendMode(0, BlendMode.CUTOUT).find();
        for (BakedQuad quad : PipeBaseModelGenStandard.generateCutout(sprites, key)) {
            emitter.fromVanilla(quad, cutout, null);
            emitter.emit();
        }
    }
}
