package alexiil.mc.mod.pipes.client.model;

import com.google.common.collect.ImmutableList;

import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.json.ModelItemPropertyOverrideList;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.texture.MissingSprite;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import alexiil.mc.lib.multipart.impl.client.model.SinglePartBakedModel;
import alexiil.mc.mod.pipes.client.model.part.FacadePartKey;
import alexiil.mc.mod.pipes.items.ItemFacade;
import alexiil.mc.mod.pipes.part.FacadeBlockStateInfo;
import alexiil.mc.mod.pipes.part.FacadeShape;
import alexiil.mc.mod.pipes.part.FullFacade;

public final class ModelFacadeItem extends SimpleBakedModel {
    private final FacadeOverride override = new FacadeOverride();

    public ModelFacadeItem() {
        super(MissingSprite.getMissingSprite());
    }

    @Override
    public ModelItemPropertyOverrideList getItemPropertyOverrides() {
        return override;
    }

    public class FacadeOverride extends ModelItemPropertyOverrideList {
        private FacadeOverride() {
            super(null, null, null, ImmutableList.of());
        }

        @Override
        public BakedModel apply(BakedModel originalModel, ItemStack stack, World world, LivingEntity entity) {
            FullFacade inst = ItemFacade.getStates(stack);
            FacadeBlockStateInfo state = inst.state;
            FacadeShape shape = inst.shape;
            return new SinglePartBakedModel<FacadePartKey>(new FacadePartKey(shape, state.state), FacadePartKey.class) {
                @Override
                public boolean hasDepthInGui() {
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
