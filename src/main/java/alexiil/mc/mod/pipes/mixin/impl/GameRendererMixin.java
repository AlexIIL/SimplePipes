package alexiil.mc.mod.pipes.mixin.impl;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.render.GameRenderer;

import alexiil.mc.mod.pipes.mixin.api.IPostCutoutRenderer;

@Mixin(GameRenderer.class)
public class GameRendererMixin {

    @Inject(
        at = @At(value = "INVOKE", ordinal = 1, target = "Lcom/mojang/blaze3d/platform/GlStateManager;shadeModel(I)V"),
        method = "Lnet/minecraft/client/render/GameRenderer;renderCenter(FJ)V")
    public void renderDetached(float f, long l, CallbackInfo ci) {
        IPostCutoutRenderer.EVENT.invoker().render();
    }
}
