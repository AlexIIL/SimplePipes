package alexiil.mc.mod.pipes.items;

import java.util.List;

import net.fabricmc.loader.api.FabricLoader;

import net.minecraft.block.Block;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.text.Text;
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

    @Override
    public void appendTooltip(ItemStack stack, World world, List<Text> tooltip, TooltipContext context) {
        super.appendTooltip(stack, world, tooltip, context);
        if (FabricLoader.getInstance().isModLoaded("pipe_vacuum_pump")) {
            tooltip.add(Text.translatable("block.simple_pipes.pipe_wooden_item.disabled_redstone.1"));
            tooltip.add(Text.translatable("block.simple_pipes.pipe_wooden_item.disabled_redstone.2"));
        }
    }
}
