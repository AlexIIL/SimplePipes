package alexiil.mc.mod.pipes.client.model;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.MissingSprite;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.util.Identifier;

public interface SpriteSupplier {

    public static final SpriteSupplier NO_CONTEXT_SUPPLIER
        = (atlas, sprite) -> MinecraftClient.getInstance().getBakedModelManager().method_24153(atlas).getSprite(sprite);

    default Sprite getMissingBlockSprite() {
        return getSprite(SpriteAtlasTexture.BLOCK_ATLAS_TEX, MissingSprite.getMissingSpriteId());
    }

    default Sprite getBlockSprite(String id) {
        return getSprite(SpriteAtlasTexture.BLOCK_ATLAS_TEX, new Identifier(id));
    }

    default Sprite getBlockSprite(Identifier id) {
        return getSprite(SpriteAtlasTexture.BLOCK_ATLAS_TEX, id);
    }

    Sprite getSprite(Identifier atlasId, Identifier spriteId);

    default Sprite getSprite(SpriteIdentifier spriteId) {
        return getSprite(spriteId.getAtlasId(), spriteId.getTextureId());
    }
}
