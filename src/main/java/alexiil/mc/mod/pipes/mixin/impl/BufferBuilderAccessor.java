package alexiil.mc.mod.pipes.mixin.impl;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.VertexFormat;

@Mixin(BufferBuilder.class)
public interface BufferBuilderAccessor {

    @Accessor("format")
    VertexFormat simplepipes_getFormat();
}
