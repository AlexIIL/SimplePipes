package alexiil.mc.mod.pipes.mixin.api;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

@FunctionalInterface
public interface IPostCutoutRenderer {
    public static final Event<IPostCutoutRenderer> EVENT =
        EventFactory.createArrayBacked(IPostCutoutRenderer.class, (listeners) -> () -> {
            for (IPostCutoutRenderer listener : listeners) {
                listener.render();
            }
        });

    void render();
}
