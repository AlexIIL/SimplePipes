package alexiil.mc.mod.pipes.items;

import alexiil.mc.mod.pipes.pipe.PartSpPipe;
import alexiil.mc.mod.pipes.pipe.PipeSpDef;

public class ItemPipePart extends ItemSimplePart {
    public ItemPipePart(Settings settings, PipeSpDef definition) {
        super(settings, definition, (c, h) -> new PartSpPipe(definition, h));
    }
}
