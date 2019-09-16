package alexiil.mc.mod.pipes.items;

import net.minecraft.item.Item;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.util.ActionResult;
import net.minecraft.world.World;

import alexiil.mc.lib.multipart.api.AbstractPart;
import alexiil.mc.lib.multipart.api.MultipartContainer.PartOffer;
import alexiil.mc.lib.multipart.api.MultipartHolder;
import alexiil.mc.lib.multipart.api.MultipartUtil;
import alexiil.mc.lib.multipart.api.PartDefinition;
import alexiil.mc.mod.pipes.util.SoundUtil;

public class ItemSimplePart extends Item {

    @FunctionalInterface
    public interface PartCreator {
        AbstractPart create(PartDefinition definition, MultipartHolder holder);
    }

    public final PartDefinition definition;
    public final PartCreator creator;

    public ItemSimplePart(Settings settings, PartDefinition definition, PartCreator creator) {
        super(settings);
        this.definition = definition;
        this.creator = creator;
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext ctx) {
        World w = ctx.getWorld();
        if (w.isClient) {
            return ActionResult.PASS;
        }

        ItemPlacementContext plCtx = new ItemPlacementContext(ctx);
        if (!plCtx.canPlace()) {
            return ActionResult.FAIL;
        }
        PartOffer offer = MultipartUtil.offerNewPart(w, plCtx.getBlockPos(), h -> creator.create(definition, h));
        if (offer == null) {
            return ActionResult.FAIL;
        }
        offer.apply();
        ctx.getStack().increment(-1);
        SoundUtil.playBlockPlace(w, plCtx.getBlockPos());
        return ActionResult.SUCCESS;
    }
}
