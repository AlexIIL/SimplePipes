package alexiil.mc.mod.pipes.blocks;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Hand;
import net.minecraft.util.Tickable;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.Direction;

import alexiil.mc.lib.attributes.Simulation;
import alexiil.mc.lib.attributes.fluid.FluidVolumeUtil;
import alexiil.mc.lib.attributes.fluid.IFluidItem;
import alexiil.mc.lib.attributes.fluid.impl.SimpleFixedFluidInv;
import alexiil.mc.lib.attributes.fluid.volume.FluidVolume;
import alexiil.mc.lib.attributes.misc.Ref;
import alexiil.mc.mod.pipes.util.FluidSmoother;
import alexiil.mc.mod.pipes.util.FluidSmoother.FluidStackInterp;

public class TileTank extends TileBase implements Tickable {

    private static boolean isPlayerInteracting = false;

    public final SimpleFixedFluidInv fluidInv;
    public final FluidSmoother smoothedTank;

    public TileTank() {
        super(SimplePipeBlocks.TANK_TILE);
        fluidInv = new SimpleFixedFluidInv(1, 16 * FluidVolume.BUCKET);
        smoothedTank = new FluidSmoother(writer -> {
            CompoundTag tag = new CompoundTag();
            writer.write(tag);
            tag.putBoolean("f", true);
            sendPacket((ServerWorld) world, tag);
        }, fluidInv);
    }

    @Override
    public CompoundTag toTag(CompoundTag tag) {
        tag = super.toTag(tag);
        FluidVolume invFluid = fluidInv.getInvFluid(0);
        if (!invFluid.isEmpty()) {
            tag.put("fluid", invFluid.toTag());
        }
        return tag;
    }

    @Override
    public void fromTag(CompoundTag tag) {
        super.fromTag(tag);
        if (tag.containsKey("fluid")) {
            FluidVolume fluid = FluidVolume.fromTag(tag.getCompound("fluid"));
            fluidInv.setInvFluid(0, fluid, Simulation.ACTION);
        }
    }

    @Override
    public CompoundTag toClientTag(CompoundTag tag) {
        tag = super.toClientTag(tag);
        smoothedTank.writeInit(tag);
        tag.putBoolean("f", true);
        return tag;
    }

    @Override
    public void fromClientTag(CompoundTag tag) {
        super.fromClientTag(tag);
        if (tag.getBoolean("f")) {
            smoothedTank.handleMessage(getWorld(), tag);
        }
    }

    // ITickable

    @Override
    public void tick() {
        smoothedTank.tick(world);

        // if (!world.isClient) {
        // int compLevel = getComparatorLevel();
        // if (compLevel != lastComparatorLevel) {
        // lastComparatorLevel = compLevel;
        // markDirty();
        // }
        // }
    }

    // Custom

    @Override
    public void onPlacedBy(LivingEntity placer, ItemStack stack) {
        if (!placer.world.isClient) {
            isPlayerInteracting = true;
            balanceTankFluids();
            isPlayerInteracting = false;
        }
    }

    /** Moves fluids around to their preferred positions. (For gaseous fluids this will move everything as high as
     * possible, for liquid fluids this will move everything as low as possible.) */
    public void balanceTankFluids() {
        List<TileTank> tanks = getTanks();
        FluidVolume fluid = null;
        for (TileTank tile : tanks) {
            FluidVolume held = tile.fluidInv.getInvFluid(0);
            if (held.isEmpty()) {
                continue;
            }
            if (fluid == null) {
                fluid = held;
            } else if (!FluidVolume.areEqualExceptAmounts(held, fluid)) {
                return;
            }
        }
        if (fluid == null) {
            return;
        }
        TileTank prev = null;
        for (TileTank tile : tanks) {
            if (prev != null) {
                FluidVolumeUtil.move(tile.fluidInv.getExtractable(), prev.fluidInv.getInsertable(), Integer.MAX_VALUE);
            }
            prev = tile;
        }
    }

    @Override
    public boolean activate(PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (world.isClient) {
            return true;
        }
        isPlayerInteracting = true;
        boolean didChange = activateInternal(player, hand);
        isPlayerInteracting = false;
        return didChange;
    }

    private boolean activateInternal(PlayerEntity player, Hand hand) {
        ItemStack stack = player.getStackInHand(hand);
        Item item = stack.getItem();
        if (item instanceof IFluidItem) {
            IFluidItem bucket = (IFluidItem) item;
            Ref<ItemStack> drainedStack = new Ref<>(stack);
            FluidVolume bucketFluid = bucket.drain(drainedStack);

            FluidVolume invFluid = this.fluidInv.getInvFluid(0);
            if (bucketFluid.isEmpty() && !invFluid.isEmpty()) {
                Ref<ItemStack> stackRef = new Ref<>(stack);
                Ref<FluidVolume> fluidRef = new Ref<>(invFluid);

                if (bucket.fill(stackRef, fluidRef)) {
                    if (!player.abilities.creativeMode) player.setStackInHand(hand, stackRef.obj);
                    this.fluidInv.setInvFluid(0, fluidRef.obj, Simulation.ACTION);
                    balanceTankFluids();
                    return true;
                }
            } else if (invFluid.isEmpty()) {
                if (this.fluidInv.setInvFluid(0, bucketFluid.copy(), Simulation.ACTION)) {
                    balanceTankFluids();
                    if (!player.abilities.creativeMode) player.setStackInHand(hand, drainedStack.obj);
                    return true;
                }
            } else {
                invFluid = invFluid.copy();
                invFluid = FluidVolume.merge(invFluid, bucketFluid);
                if (invFluid != null) {
                    if (this.fluidInv.setInvFluid(0, invFluid, Simulation.ACTION)) {
                        balanceTankFluids();
                        if (!player.abilities.creativeMode) player.setStackInHand(hand, drainedStack.obj);
                        return true;
                    }
                }
            }
        }
        return false;
    }

    // Networking

    // @Override
    // public void writePayload(int id, PacketBufferBC buffer, Side side) {
    // super.writePayload(id, buffer, side);
    // if (side == Side.SERVER) {
    // if (id == NET_RENDER_DATA) {
    // writePayload(NET_FLUID_DELTA, buffer, side);
    // } else if (id == NET_FLUID_DELTA) {
    // smoothedTank.writeInit(buffer);
    // }
    // }
    // }

    // @Override
    // public void readPayload(int id, PacketBufferBC buffer, Side side, MessageContext ctx) throws IOException {
    // super.readPayload(id, buffer, side, ctx);
    // if (side == Side.CLIENT) {
    // if (id == NET_RENDER_DATA) {
    // readPayload(NET_FLUID_DELTA, buffer, side, ctx);
    // smoothedTank.resetSmoothing(getWorld());
    // } else if (id == NET_FLUID_DELTA) {
    // smoothedTank.handleMessage(getWorld(), buffer);
    // }
    // }
    // }

    // Rendering

    public FluidStackInterp getFluidForRender(float partialTicks) {
        return smoothedTank.getFluidForRender(partialTicks);
    }

    // Tank helper methods

    /** @return A list of all connected tanks around this block, ordered by position from bottom to top. */
    private List<TileTank> getTanks() {
        // double-ended queue rather than array list to avoid
        // the copy operation when we search downwards
        Deque<TileTank> tanks = new ArrayDeque<>();
        tanks.add(this);
        TileTank prevTank = this;
        while (true) {
            BlockEntity tileAbove = prevTank.getNeighbourTile(Direction.UP);
            if (!(tileAbove instanceof TileTank)) {
                break;
            }
            TileTank tankUp = (TileTank) tileAbove;
            tanks.addLast(tankUp);
            prevTank = tankUp;
        }
        prevTank = this;
        while (true) {
            BlockEntity tileBelow = prevTank.getNeighbourTile(Direction.DOWN);
            if (!(tileBelow instanceof TileTank)) {
                break;
            }
            TileTank tankBelow = (TileTank) tileBelow;
            tanks.addFirst(tankBelow);
            prevTank = tankBelow;
        }
        return new ArrayList<>(tanks);
    }

    private BlockEntity getNeighbourTile(Direction dir) {
        return world.getBlockEntity(getPos().offset(dir));
    }
}
