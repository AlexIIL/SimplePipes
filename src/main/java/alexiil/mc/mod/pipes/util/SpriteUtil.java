package alexiil.mc.mod.pipes.util;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

import alexiil.mc.mod.pipes.client.model.ModelUtil;

public final class SpriteUtil {
    private SpriteUtil() {}

    public static Sprite getMissingSprite() {
        return ModelUtil.getMissingModel().getSprite();
    }

    public static float getU(Sprite sprite, float u) {
        return MathHelper.lerp(u, sprite.getMinU(), sprite.getMaxU());
    }

    public static float getV(Sprite sprite, float v) {
        return MathHelper.lerp(v, sprite.getMinV(), sprite.getMaxV());
    }

    @Deprecated
    public static Sprite getSprite(String string) {
        return getSprite(new Identifier(string));
    }

    @Deprecated
    public static Sprite getSprite(Identifier id) {
        return MinecraftClient.getInstance().getSpriteAtlas(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE).apply(id);
    }
}
