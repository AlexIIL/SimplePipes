package alexiil.mc.mod.pipes.part;

import javax.annotation.Nullable;

import alexiil.mc.mod.pipes.container.SimplePipeContainerFactory;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.ItemActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.World;

import alexiil.mc.mod.pipes.client.model.part.TankPartModelKey;
import alexiil.mc.mod.pipes.container.ContainerTank;
import alexiil.mc.mod.pipes.items.SimplePipeItems;
import alexiil.mc.mod.pipes.util.FluidSmoother;
import alexiil.mc.mod.pipes.util.FluidSmoother.FluidStackInterp;

import alexiil.mc.lib.net.IMsgReadCtx;
import alexiil.mc.lib.net.IMsgWriteCtx;
import alexiil.mc.lib.net.NetByteBuf;
import alexiil.mc.lib.net.NetIdDataK;
import alexiil.mc.lib.net.ParentNetIdSingle;

import alexiil.mc.lib.attributes.AttributeList;
import alexiil.mc.lib.attributes.fluid.FixedFluidInv;
import alexiil.mc.lib.attributes.fluid.FluidInvUtil;
import alexiil.mc.lib.attributes.fluid.amount.FluidAmount;
import alexiil.mc.lib.attributes.fluid.impl.SimpleFixedFluidInv;

import alexiil.mc.lib.multipart.api.AbstractPart;
import alexiil.mc.lib.multipart.api.MultipartEventBus;
import alexiil.mc.lib.multipart.api.MultipartHolder;
import alexiil.mc.lib.multipart.api.PartDefinition;
import alexiil.mc.lib.multipart.api.event.PartTickEvent;
import alexiil.mc.lib.multipart.api.render.PartModelKey;

public class PartTank extends AbstractPart {

    public static final ParentNetIdSingle<PartTank> NET_TANK;
    public static final NetIdDataK<PartTank> SMOOTHED_TANK_DATA;

    public static final VoxelShape SHAPE;
    private static final FluidAmount SINGLE_TANK_CAPACITY;

    static {
        NET_TANK = NET_ID.subType(PartTank.class, "simple_pipes:tank");
        SMOOTHED_TANK_DATA = NET_TANK.idData("smoothed_tank_data").setReceiver(PartTank::receiveSmoothedTankData);

        SHAPE = VoxelShapes.cuboid(2 / 16.0, 0, 2 / 16.0, 14 / 16.0, 12 / 16.0, 14 / 16.0);
        SINGLE_TANK_CAPACITY = FluidAmount.BUCKET.mul(16);
    }

    private boolean isPlayerInteracting = false;

    public final SimpleFixedFluidInv fluidInv = new SimpleFixedFluidInv(1, SINGLE_TANK_CAPACITY);
    public final FluidSmoother smoothedTank = new FluidSmoother(writer -> {
        holder.getContainer().sendNetworkUpdate(PartTank.this, SMOOTHED_TANK_DATA, (obj, buf, ctx) -> {
            buf.writeBoolean(isPlayerInteracting);
            writer.write(buf, ctx);
        });
    }, fluidInv);

    public PartTank(PartDefinition definition, MultipartHolder holder) {
        super(definition, holder);
        fluidInv.setOwnerListener(
            (tank, slot, prev, current) -> holder.getContainer().getMultipartBlockEntity().markDirty()
        );
    }

    public PartTank(PartDefinition definition, MultipartHolder holder, NbtCompound tag, RegistryWrapper.WrapperLookup lookup) {
        this(definition, holder);
        if (tag.contains("fluidInv")) {
            fluidInv.fromTag(tag.getCompound("fluidInv"), lookup);
        }
    }

    @Override
    public NbtCompound toTag(RegistryWrapper.WrapperLookup lookup) {
        NbtCompound tag = super.toTag(lookup);
        tag.put("fluidInv", fluidInv.toTag(lookup));
        return tag;
    }

    public PartTank(PartDefinition definition, MultipartHolder holder, NetByteBuf buf, IMsgReadCtx ctx) {
        this(definition, holder);
        smoothedTank.handleMessage(holder.getContainer().getMultipartWorld(), buf, ctx);
    }

    @Override
    public void writeCreationData(NetByteBuf buffer, IMsgWriteCtx ctx) {
        super.writeCreationData(buffer, ctx);
        smoothedTank.writeInit(buffer, ctx);
    }

    @Override
    public VoxelShape getShape() {
        return SHAPE;
    }

    @Override
    public VoxelShape getCullingShape() {
        return VoxelShapes.empty();
    }

    @Override
    public PartModelKey getModelKey() {
        return TankPartModelKey.INSTANCE;
    }

    private final void receiveSmoothedTankData(NetByteBuf buffer, IMsgReadCtx ctx) {
        boolean playerInteraction = buffer.readBoolean();
        World world = holder.getContainer().getMultipartWorld();
        smoothedTank.handleMessage(world, buffer, ctx);
        if (playerInteraction) {
            smoothedTank.resetSmoothing(world);
        }
    }

    @Override
    public void addAllAttributes(AttributeList<?> list) {
        super.addAllAttributes(list);
        list.offer(fluidInv, SHAPE);
    }

    @Override
    public void onAdded(MultipartEventBus bus) {
        super.onAdded(bus);
        bus.addContextlessListener(this, PartTickEvent.class, this::onTick);
    }

    @Override
    public ItemStack getPickStack() {
        return new ItemStack(SimplePipeItems.TANK);
    }

    @Override
    protected BlockState getClosestBlockState() {
        return Blocks.GLASS.getDefaultState();
    }

    protected void onTick() {
        smoothedTank.tick(holder.getContainer().getMultipartWorld());
        isPlayerInteracting = false;

        // if (!world.isClient) {
        // int compLevel = getComparatorLevel();
        // if (compLevel != lastComparatorLevel) {
        // lastComparatorLevel = compLevel;
        // markDirty();
        // }
        // }
    }

    @Override
    public ActionResult onUse(PlayerEntity player, BlockHitResult hit) {
        if (!player.getWorld().isClient) {
            player.openHandledScreen(new SimplePipeContainerFactory(SimplePipeItems.TANK.getName(),
                (syncId, inv, player1) -> new ContainerTank(syncId, player1, this),
                (player1) -> holder.getContainer().getMultipartPos()));
        }
        return ActionResult.SUCCESS;
    }

    @Override
    public ItemActionResult onUseWithItem(ItemStack stack, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (player.getWorld().isClient) {
            return ItemActionResult.SUCCESS;
        }
        try {
            isPlayerInteracting = true;
            return FluidInvUtil.interactHandWithTank((FixedFluidInv) fluidInv, player, hand).asItemActionResult();
        } finally {
            isPlayerInteracting = false;
        }
    }

    @Override
    public void onRemoved() {
        super.onRemoved();
        fluidInv.invalidateListeners();
    }

    // Rendering

    @Nullable
    public FluidStackInterp getFluidForRender(float partialTicks) {
        return smoothedTank.getFluidForRender(partialTicks);
    }
}
