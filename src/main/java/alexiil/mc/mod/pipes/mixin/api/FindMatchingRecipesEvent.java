package alexiil.mc.mod.pipes.mixin.api;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

@FunctionalInterface
public interface FindMatchingRecipesEvent {

    public static final Event<FindMatchingRecipesEvent> EVENT = EventFactory.createArrayBacked(
        FindMatchingRecipesEvent.class, (listeners) -> (context) -> {
            for (FindMatchingRecipesEvent listener : listeners) {
                listener.addRecipes(context);
            }
        }
    );

    void addRecipes(RecipeMatchFinder context);
}
