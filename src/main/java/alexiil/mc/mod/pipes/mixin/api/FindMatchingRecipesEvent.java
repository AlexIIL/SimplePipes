package alexiil.mc.mod.pipes.mixin.api;

import java.util.function.Consumer;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

import net.minecraft.recipe.Recipe;

@FunctionalInterface
public interface FindMatchingRecipesEvent {

    public static final Event<FindMatchingRecipesEvent> EVENT = EventFactory.createArrayBacked(
        FindMatchingRecipesEvent.class, (listeners) -> (context, recipeAdder) -> {
            for (FindMatchingRecipesEvent listener : listeners) {
                listener.addRecipes(context, recipeAdder);
            }
        }
    );

    /** @param recipeAdder A {@link Consumer} which will add more valid recipes to the internal list. */
    void addRecipes(RecipeMatchFinder<?, ?> context, Consumer<Recipe<?>> recipeAdder);
}
