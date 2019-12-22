package alexiil.mc.mod.pipes.client.model;

import java.util.function.Function;

import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.util.Identifier;

public class ModelCtx implements SpriteSupplier {

    public final Function<SpriteIdentifier, Sprite> spriteGetter;

    public ModelCtx(Function<SpriteIdentifier, Sprite> spriteGetter) {
        this.spriteGetter = spriteGetter;
    }

    @Override
    public Sprite getSprite(Identifier atlasId, Identifier spriteId) {
        return spriteGetter.apply(new SpriteIdentifier(atlasId, spriteId));
    }
}
