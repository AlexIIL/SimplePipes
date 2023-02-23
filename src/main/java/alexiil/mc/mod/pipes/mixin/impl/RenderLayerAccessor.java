package alexiil.mc.mod.pipes.mixin.impl;

import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexFormat;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(RenderLayer.class)
public interface RenderLayerAccessor {
    @Invoker
    static RenderLayer.MultiPhase callOf(
        String name, VertexFormat vertexFormat, VertexFormat.DrawMode drawMode, int expectedBufferSize,
        boolean hasCrumbling, boolean translucent, RenderLayer.MultiPhaseParameters phases
    ) {
        throw new IllegalStateException("RenderLayerAccessor mixin error");
    }
}
