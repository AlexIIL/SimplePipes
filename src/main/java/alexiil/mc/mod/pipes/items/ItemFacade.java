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
import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.ActionResult;
import net.minecraft.util.DefaultedList;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;

import alexiil.mc.lib.multipart.api.AbstractPart;
import alexiil.mc.lib.multipart.api.MultipartContainer.PartOffer;
import alexiil.mc.lib.multipart.api.MultipartHolder;
import alexiil.mc.lib.multipart.api.MultipartUtil;
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
        CompoundTag nbt = TagUtil.getItemData(item);
        nbt.put("facade", state.toTag());
        return item;
    }

    public static FullFacade getStates(ItemStack item) {
        CompoundTag nbt = TagUtil.getItemData(item);

        String strPreview = nbt.getString("preview");
        if ("basic".equalsIgnoreCase(strPreview)) {
            return new FullFacade(FacadeStateManager.getPreviewState(), DEFAULT_SHAPE);
        }

        if (!nbt.containsKey("facade") && nbt.containsKey("states")) {
            ListTag states = nbt.getList("states", new CompoundTag().getType());
            if (states.size() > 0) {
                // Only migrate if we actually have a facade to migrate.
                boolean isHollow = states.getCompoundTag(0).getBoolean("isHollow");
                CompoundTag tagFacade = new CompoundTag();
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

    @Override
    public void appendStacks(ItemGroup group, DefaultedList<ItemStack> subItems) {
        if (isIn(group)) {
            addSubItems(group, subItems);
        }
    }

    private void addSubItems(ItemGroup group, DefaultedList<ItemStack> subItems) {
        // Add a single phased facade as a default
        // check if the data is present as we only process in post-init
        FacadeBlockStateInfo stone = FacadeStateManager.getInfoForBlock(Blocks.STONE);
        if (stone != null) {
            for (FacadeBlockStateInfo info : FacadeStateManager.getValidFacadeStates().values()) {
                if (Registry.BLOCK.getDefaultId().equals(Registry.BLOCK.getId(info.state.getBlock()))) {
                    // Entries are removed from the registry(?) if the client has values that the server doesn't
                    continue;
                }
                if (!info.isVisible) {
                    continue;
                }
                for (FacadeShape shape : PREVIEW_SHAPES) {
                    subItems.add(createItemStack(new FullFacade(info, shape)));
                }
            }
        }
        SimplePipes.LOGGER.info("[facades] " + subItems.size() + " sub facade items");
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
        return new TranslatableText(key, getFacadeStateDisplayName(facade.state.state));
    }

    public static TranslatableText getFacadeStateDisplayName(BlockState state) {
        return new TranslatableText(state.getBlock().getTranslationKey());
    }

    @Environment(EnvType.CLIENT)
    @Override
    public void appendTooltip(ItemStack stack, World world, List<Text> tooltip, TooltipContext flag) {
        FullFacade states = getStates(stack);
        if (flag.isAdvanced()) {
            Identifier blockId = Registry.BLOCK.getId(states.state.state.getBlock());
            tooltip.add(new LiteralText(blockId.toString()));
        }
        String propertiesStart = Formatting.GRAY + "" + Formatting.ITALIC;
        FacadeBlockStateInfo info = states.state;
        BlockUtil.getPropertiesStringMap(info.state, info.varyingProperties).forEach(
            (name, value) -> tooltip.add(new LiteralText(propertiesStart + name + " = " + value))
        );
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
            PartOffer offer = MultipartUtil.offerNewPart(
                w, variant.pos, h -> createFacade(fullState.state, variant.shape, h)
            );
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
            return ActionResult.PASS;
        }

        PartOffer offer = offer(ctx);
        if (offer != null) {
            FullFacade fullState = getStates(ctx.getStack());
            offer.apply();
            ctx.getStack().increment(-1);
            SoundUtil.playBlockPlace(ctx.getWorld(), ctx.getBlockPos(), fullState.state.state);
            return ActionResult.SUCCESS;
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
            this.centre = new Vec3d(pos).add(shape.centerOfMass);
        }
    }

    class FacadePlacement extends GhostPlacementPart {

        @Override
        public GhostPlacement preRender(ItemUsageContext ctx) {
            if (ctx.getStack().getItem() != ItemFacade.this) {
                return null;
            }

            if (setup(offer(ctx))) {
                return this;
            }
            return null;
        }
    }
}
