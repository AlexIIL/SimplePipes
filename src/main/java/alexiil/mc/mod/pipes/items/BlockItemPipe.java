package alexiil.mc.mod.pipes.items;

import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.util.ActionResult;
import net.minecraft.world.World;

import alexiil.mc.mod.pipes.blocks.BlockPipe;
import alexiil.mc.mod.pipes.pipe.PartSpPipe;
import alexiil.mc.mod.pipes.util.SoundUtil;

import alexiil.mc.lib.multipart.api.MultipartContainer.MultipartCreator;
import alexiil.mc.lib.multipart.api.MultipartContainer.PartOffer;
import alexiil.mc.lib.multipart.api.MultipartUtil;

public class BlockItemPipe extends BlockItem {

    public BlockItemPipe(Block block, Item.Settings settings) {
        super(block, settings);
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {

        World w = context.getWorld();
        if (w.isClient) {
            return ActionResult.PASS;
        }

        PartOffer offer = getOffer(context);
        if (offer == null) {
            return super.useOnBlock(context);
        }
        offer.apply();
        offer.getHolder().getPart().onPlacedBy(context.getPlayer(), context.getHand());
        context.getStack().increment(-1);
        SoundUtil.playBlockPlace(w, offer.getHolder().getContainer().getMultipartPos());
        return ActionResult.SUCCESS;
    }

    private PartOffer getOffer(ItemUsageContext context) {
        MultipartCreator c = h -> new PartSpPipe(((BlockPipe) getBlock()).pipeDef, h);
        World w = context.getWorld();
        return MultipartUtil.offerNewPart(w, context.getBlockPos(), c);
    }
}
