package alexiil.mc.mod.pipes.client.model.part;

import alexiil.mc.lib.multipart.api.render.PartBreakContext;
import alexiil.mc.lib.multipart.api.render.PartModelBaker;
import alexiil.mc.lib.multipart.api.render.PartRenderContext;
import alexiil.mc.mod.pipes.client.model.ModelUtil;
import alexiil.mc.mod.pipes.client.model.SimplePipeModels;

public enum TankPartBaker implements PartModelBaker<TankPartModelKey> {
    INSTANCE;

    @Override
    public void emitQuads(TankPartModelKey key, PartRenderContext ctx) {
        PartBreakContext breakContext = ctx.getBreakContext();
        if (breakContext != null) {

        }
        ctx.fallbackConsumer().accept(ModelUtil.getModel(SimplePipeModels.TANK_BLOCK_ID));
    }
}
