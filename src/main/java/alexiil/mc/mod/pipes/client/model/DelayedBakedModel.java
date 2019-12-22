package alexiil.mc.mod.pipes.client.model;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.function.Function;

import com.mojang.datafixers.util.Pair;

import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.ModelBakeSettings;
import net.minecraft.client.render.model.ModelLoader;
import net.minecraft.client.render.model.UnbakedModel;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.util.Identifier;

public final class DelayedBakedModel implements UnbakedModel {

    public static final class ModelBakeCtx implements SpriteSupplier {
        public final ModelCtx modelCtx;

        public ModelBakeCtx(Function<SpriteIdentifier, Sprite> spriteGetter) {
            this.modelCtx = new ModelCtx(spriteGetter);
        }

        @Override
        public Sprite getSprite(Identifier atlasId, Identifier spriteId) {
            return modelCtx.getSprite(atlasId, spriteId);
        }
    }

    @FunctionalInterface
    public interface IModelBaker {
        BakedModel bake(ModelBakeCtx ctx);
    }

    private final IModelBaker baker;

    public DelayedBakedModel(IModelBaker baker) {
        this.baker = baker;
    }

    @Override
    public Collection<Identifier> getModelDependencies() {
        return Collections.emptyList();
    }

    @Override
    public Collection<SpriteIdentifier> getTextureDependencies(
        Function<Identifier, UnbakedModel> unbakedModelGetter, Set<Pair<String, String>> unresolvedTextureReferences
    ) {
        return Collections.emptyList();
    }

    @Override
    public BakedModel bake(
        ModelLoader loader, Function<SpriteIdentifier, Sprite> textureGetter, ModelBakeSettings rotationContainer,
        Identifier modelId
    ) {
        return baker.bake(new ModelBakeCtx(textureGetter));
    }
}
