/*
 * Copyright (c) 2019 SpaceToad and the BuildCraft team
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0. If a copy of the MPL was not
 * distributed with this file, You can obtain one at https://mozilla.org/MPL/2.0/
 */
package alexiil.mc.mod.pipes.items;

import net.minecraft.item.Item;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.util.ActionResult;
import net.minecraft.world.World;

import alexiil.mc.lib.multipart.api.AbstractPart;
import alexiil.mc.lib.multipart.api.MultipartContainer.MultipartCreator;
import alexiil.mc.lib.multipart.api.MultipartContainer.PartOffer;
import alexiil.mc.lib.multipart.api.MultipartHolder;
import alexiil.mc.lib.multipart.api.MultipartUtil;
import alexiil.mc.lib.multipart.api.PartDefinition;
import alexiil.mc.mod.pipes.util.SoundUtil;

public class ItemSimplePart extends Item implements IItemPlacmentGhost {

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

        PartOffer offer = getOffer(ctx);
        if (offer == null) {
            return ActionResult.FAIL;
        }
        offer.apply();
        offer.getHolder().getPart().onPlacedBy(ctx.getPlayer(), ctx.getHand());
        ctx.getStack().increment(-1);
        SoundUtil.playBlockPlace(w, offer.getHolder().getContainer().getMultipartPos());
        return ActionResult.SUCCESS;
    }

    private PartOffer getOffer(ItemUsageContext ctx) {
        MultipartCreator c = h -> creator.create(definition, h);
        World w = ctx.getWorld();
        PartOffer offer = MultipartUtil.offerNewPart(w, ctx.getBlockPos(), c);
        if (offer == null) {
            offer = MultipartUtil.offerNewPart(w, ctx.getBlockPos().offset(ctx.getSide()), c);
        }
        return offer;
    }

    @Override
    public GhostPlacement createGhostPlacement(ItemUsageContext ctx) {
        return new GhostPlacementPart() {
            @Override
            public GhostPlacement preRender(ItemUsageContext ctx) {
                if (ctx.getStack().getItem() != ItemSimplePart.this) {
                    return null;
                }
                return setup(getOffer(ctx)) ? this : null;
            }
        };
    }
}
