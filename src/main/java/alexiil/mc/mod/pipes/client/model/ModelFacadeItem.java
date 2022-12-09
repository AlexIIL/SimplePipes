package alexiil.mc.mod.pipes.client.model;

import com.google.common.collect.ImmutableList;

import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.json.ModelOverrideList;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;

import alexiil.mc.mod.pipes.client.model.DelayedBakedModel.ModelBakeCtx;
import alexiil.mc.mod.pipes.client.model.part.FacadePartKey;
import alexiil.mc.mod.pipes.items.ItemFacade;
import alexiil.mc.mod.pipes.part.FacadeBlockStateInfo;
import alexiil.mc.mod.pipes.part.FacadeShape;
import alexiil.mc.mod.pipes.part.FullFacade;

import alexiil.mc.lib.multipart.impl.client.model.SinglePartBakedModel;

public final class ModelFacadeItem extends SimpleBakedModel {
    private final FacadeOverride override = new FacadeOverride();

    public ModelFacadeItem(ModelBakeCtx ctx) {
        super(ctx.getMissingBlockSprite());
    }

    @Override
    public ModelOverrideList getOverrides() {
        return override;
    }

    public class FacadeOverride extends ModelOverrideList {
        private FacadeOverride() {
            super(null, null, ImmutableList.of());
        }

        @Override
        public BakedModel apply(
            BakedModel originalModel, ItemStack stack, ClientWorld world, LivingEntity entity, int seed
        ) {
            FullFacade inst = ItemFacade.getStates(stack);
            FacadeBlockStateInfo state = inst.state;
            FacadeShape shape = inst.shape;
            return new SinglePartBakedModel<FacadePartKey>(new FacadePartKey(shape, state.state), FacadePartKey.class) {
                @Override
                public boolean hasDepth() {
                    return true;
                }

                @Override
                public boolean isSideLit() {
                    return true;
                }

                @Override
                public ModelTransformation getTransformation() {
                    return TRANSFORM_PLUG_AS_BLOCK;
                }
            };
        }
    }
}
