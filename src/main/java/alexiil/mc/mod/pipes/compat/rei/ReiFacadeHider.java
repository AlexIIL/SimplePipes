package alexiil.mc.mod.pipes.compat.rei;

import net.minecraft.util.Identifier;

import alexiil.mc.mod.pipes.SimplePipes;
import alexiil.mc.mod.pipes.items.SimplePipeItems;

import me.shedaniel.rei.api.EntryRegistry;
import me.shedaniel.rei.api.RecipeHelper;
import me.shedaniel.rei.api.plugins.REIPluginV0;

public class ReiFacadeHider implements REIPluginV0 {

    @Override
    public Identifier getPluginIdentifier() {
        return SimplePipes.id("facade_hider");
    }

    @Override
    public void postRegister() {
        EntryRegistry.getInstance().removeEntryIf(stack -> stack.getItem() == SimplePipeItems.FACADE);
    }

    @Override
    public void registerOthers(RecipeHelper recipeHelper) {
        recipeHelper.registerLiveRecipeGenerator(new FacadeRecipeGenerator());
    }
}
