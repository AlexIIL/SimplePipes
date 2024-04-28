/*
 * Copyright (c) 2019 SpaceToad and the BuildCraft team
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0. If a copy of the MPL was not
 * distributed with this file, You can obtain one at https://mozilla.org/MPL/2.0/
 */
package alexiil.mc.mod.pipes.items;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.item.TooltipType;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.registry.Registries;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import alexiil.mc.mod.pipes.SimplePipes;
import alexiil.mc.mod.pipes.part.FacadeBlockStateInfo;
import alexiil.mc.mod.pipes.part.FacadePart;
import alexiil.mc.mod.pipes.part.FacadeShape;
import alexiil.mc.mod.pipes.part.FacadeSize;
import alexiil.mc.mod.pipes.part.FacadeStateManager;
import alexiil.mc.mod.pipes.part.FullFacade;
import alexiil.mc.mod.pipes.part.SimplePipeParts;
import alexiil.mc.mod.pipes.util.BlockUtil;
import alexiil.mc.mod.pipes.util.EnumCuboidCorner;
import alexiil.mc.mod.pipes.util.EnumCuboidEdge;
import alexiil.mc.mod.pipes.util.SoundUtil;
import alexiil.mc.mod.pipes.util.TagUtil;

import alexiil.mc.lib.multipart.api.AbstractPart;
import alexiil.mc.lib.multipart.api.MultipartContainer.PartOffer;
import alexiil.mc.lib.multipart.api.MultipartHolder;
import alexiil.mc.lib.multipart.api.MultipartUtil;

public class ItemFacade extends Item implements IItemPlacmentGhost {
    public static final FacadeShape DEFAULT_SHAPE = FacadeShape.Sided.get(FacadeSize.THIN, Direction.WEST, false);

    private static final FacadeShape[] PREVIEW_SHAPES = { //
        FacadeShape.Sided.get(FacadeSize.SLAB, Direction.WEST, false), //
        FacadeShape.Sided.get(FacadeSize.SLAB, Direction.WEST, true), //
        FacadeShape.Sided.get(FacadeSize.THICK, Direction.WEST, false), //
        FacadeShape.Sided.get(FacadeSize.THICK, Direction.WEST, true), //
        FacadeShape.Sided.get(FacadeSize.THIN, Direction.WEST, false), //
        FacadeShape.Sided.get(FacadeSize.THIN, Direction.WEST, true), //
        FacadeShape.Strip.get(FacadeSize.SLAB, EnumCuboidEdge.Z_NN), //
        FacadeShape.Strip.get(FacadeSize.THICK, EnumCuboidEdge.Z_NN), //
        FacadeShape.Strip.get(FacadeSize.THIN, EnumCuboidEdge.Z_NN), //
        FacadeShape.Corner.get(FacadeSize.SLAB, EnumCuboidCorner.NNN), //
        FacadeShape.Corner.get(FacadeSize.THICK, EnumCuboidCorner.NNN), //
        FacadeShape.Corner.get(FacadeSize.THIN, EnumCuboidCorner.NNN),//
    };

    public ItemFacade(Item.Settings settings) {
        super(settings);
    }

    @Nonnull
    public ItemStack createItemStack(FullFacade state) {
        ItemStack item = new ItemStack(this);
        item.set(FullFacade.TYPE, state);
        return item;
    }

    public static FullFacade getStates(ItemStack item) {
        FullFacade facadeComponent = item.get(FullFacade.TYPE);
        if (facadeComponent != null) {
            return facadeComponent;
        }

        // begin 1.20.5 migration
        NbtCompound nbt = TagUtil.getItemData(item);

        String strPreview = nbt.getString("preview");
        if ("basic".equalsIgnoreCase(strPreview)) {
            return new FullFacade(FacadeStateManager.getPreviewState(), DEFAULT_SHAPE);
        }

        // older migration code
        if (!nbt.contains("facade") && nbt.contains("states")) {
            NbtList states = nbt.getList("states", new NbtCompound().getType());
            if (states.size() > 0) {
                // Only migrate if we actually have a facade to migrate.
                boolean isHollow = states.getCompound(0).getBoolean("isHollow");
                NbtCompound tagFacade = new NbtCompound();
                tagFacade.putBoolean("isHollow", isHollow);
                tagFacade.put("states", states);
                nbt.put("facade", tagFacade);
            }
        }

        FullFacade full = new FullFacade(nbt.getCompound("facade"));
        if (full.shape instanceof FacadeShape.Sided) {
            full = new FullFacade(full.state, ((FacadeShape.Sided) full.shape).withSide(Direction.WEST));
        }
        if (full.shape instanceof FacadeShape.Strip) {
            full = new FullFacade(full.state, ((FacadeShape.Strip) full.shape).withEdge(EnumCuboidEdge.Z_NN));
        }

        // actually convert the item
        item.set(FullFacade.TYPE, full);
        if ((nbt.getSize() == 1 && nbt.contains("facade")) ||
            (nbt.getSize() == 2 && nbt.contains("facade") && nbt.contains("states"))) {
            // this means the only tags left are ours
            item.remove(DataComponentTypes.CUSTOM_DATA);
        }
        return full;
    }

    @Nonnull
    public ItemStack getFacadeForBlock(BlockState state, FacadeShape shape) {
        FacadeBlockStateInfo info = FacadeStateManager.getValidFacadeStates().get(state);
        if (info == null) {
            return ItemStack.EMPTY;
        } else {
            return createItemStack(new FullFacade(info, shape));
        }
    }

    /*
    @Override
    public void appendStacks(ItemGroup group, DefaultedList<ItemStack> subItems) {
        if (isIn(group)) {
            addSubItems(group, subItems);
        }
    }
     */

    public void addSubItems(ItemGroup.Entries subItems) {
        try {
            // this can be accessed from multiple threads, which may trample each other
            FacadeStateManager.lock();

            // Add a single phased facade as a default
            // check if the data is present as we only process in post-init
            int count = 0;
            FacadeBlockStateInfo stone = FacadeStateManager.getInfoForBlock(Blocks.STONE);
            if (stone != null) {
                for (FacadeBlockStateInfo info : FacadeStateManager.getValidFacadeStates().values()) {
                    if (Registries.BLOCK.getDefaultId().equals(Registries.BLOCK.getId(info.state.getBlock()))) {
                        // Entries are removed from the registry(?) if the client has values that the server doesn't
                        continue;
                    }
                    if (!info.isVisible) {
                        continue;
                    }
                    for (FacadeShape shape : PREVIEW_SHAPES) {
                        count++;
                        subItems.add(createItemStack(new FullFacade(info, shape)));
                    }
                }
            }

            if (FacadeStateManager.DEBUG) {
                SimplePipes.LOGGER.info("[facades] " + count + " sub facade items");
            }
        } finally {
            FacadeStateManager.unlock();
        }
    }

    @Override
    public Text getName(ItemStack stack) {
        FullFacade facade = getStates(stack);

        String key = "item.simple_pipes.plug_facade.";

        if (facade.shape instanceof FacadeShape.Sided) {
            FacadeShape.Sided sided = (FacadeShape.Sided) facade.shape;

            if (sided.isHollow()) {
                key += "hollow.";
            }

        } else if (facade.shape instanceof FacadeShape.Strip) {
            key += "strip.";
        } else if (facade.shape instanceof FacadeShape.Corner) {
            key += "corner.";
        } else /* Corner */ {
            key += "unknown_shape";
        }
        key += facade.shape.getSize().name().toLowerCase(Locale.ROOT);
        return Text.translatable(key, getFacadeStateDisplayName(facade.state.state));
    }

    public static MutableText getFacadeStateDisplayName(BlockState state) {
        return Text.translatable(state.getBlock().getTranslationKey());
    }

    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
        FullFacade states = getStates(stack);
        if (type.isAdvanced()) {
            Identifier blockId = Registries.BLOCK.getId(states.state.state.getBlock());
            tooltip.add(Text.of(blockId.toString()));
        }
        String propertiesStart = Formatting.GRAY + "" + Formatting.ITALIC;
        FacadeBlockStateInfo info = states.state;
        BlockUtil.getPropertiesStringMap(info.state, info.varyingProperties)
            .forEach((name, value) -> tooltip.add(Text.of(propertiesStart + name + " = " + value)));
    }

    // Placement

    @Nullable
    private static PartOffer offer(ItemUsageContext ctx) {
        World w = ctx.getWorld();
        FullFacade fullState = getStates(ctx.getStack());

        // Try to add it to the first valid block position, in the 27(!) positions surrounding the hit vec
        List<FacadePotentialPlacament> variants = new ArrayList<>();
        FacadeShape[] shapeVariants = fullState.shape.getPlacementVariants();
        for (BlockPos pos : BlockPos.iterate(ctx.getBlockPos().add(-1, -1, -1), ctx.getBlockPos().add(1, 1, 1))) {
            for (FacadeShape shape : shapeVariants) {
                variants.add(new FacadePotentialPlacament(shape, pos.toImmutable()));
            }
        }
        Vec3d hit = ctx.getHitPos();
        variants.sort(Comparator.comparingDouble(potential -> potential.centre.distanceTo(hit)));

        for (FacadePotentialPlacament variant : variants) {
            PartOffer offer
                = MultipartUtil.offerNewPart(w, variant.pos, h -> createFacade(fullState.state, variant.shape, h));
            if (offer != null) {
                return offer;
            }
        }
        return null;
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext ctx) {
        World w = ctx.getWorld();
        if (w.isClient) {
            return ActionResult.SUCCESS;
        }

        PartOffer offer = offer(ctx);
        if (offer != null) {
            FullFacade fullState = getStates(ctx.getStack());
            offer.apply();
            ctx.getStack().increment(-1);
            SoundUtil.playBlockPlace(ctx.getWorld(), ctx.getBlockPos(), fullState.state.state);
            return ActionResult.CONSUME;
        }
        return ActionResult.FAIL;
    }

    private static AbstractPart createFacade(FacadeBlockStateInfo states, FacadeShape shape, MultipartHolder h) {
        return new FacadePart(SimplePipeParts.FACADE, h, states, shape);
    }

    @Override
    public GhostPlacement createGhostPlacement(ItemUsageContext ctx) {
        return new FacadePlacement();
    }

    static final class FacadePotentialPlacament {
        public final FacadeShape shape;
        public final BlockPos pos;
        public final Vec3d centre;

        FacadePotentialPlacament(FacadeShape shape, BlockPos pos) {
            this.shape = shape;
            this.pos = pos;
            this.centre = Vec3d.of(pos).add(shape.centerOfMass);
        }
    }

    class FacadePlacement extends GhostPlacementPart {
        @Override
        public GhostPlacement preRender(ItemUsageContext ctx) {
            if (ctx.getStack().getItem() != ItemFacade.this) {
                return null;
            }
            return setup(offer(ctx)) ? this : null;
        }
    }
}
