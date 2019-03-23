package alexiil.mc.mod.pipes.blocks;

import javax.annotation.Nullable;

import net.fabricmc.fabric.api.container.ContainerProviderRegistry;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Hand;
import net.minecraft.util.Tickable;
import net.minecraft.util.hit.BlockHitResult;

import alexiil.mc.lib.attributes.Simulation;
import alexiil.mc.lib.attributes.fluid.FluidVolumeUtil;
import alexiil.mc.lib.attributes.fluid.impl.SimpleFixedFluidInv;
import alexiil.mc.lib.attributes.fluid.volume.FluidVolume;
import alexiil.mc.mod.pipes.container.SimplePipeContainers;
import alexiil.mc.mod.pipes.util.FluidSmoother;
import alexiil.mc.mod.pipes.util.FluidSmoother.FluidStackInterp;

public class TileTank extends TileBase implements Tickable {

    private static final int SINGLE_TANK_CAPACITY = 16 * FluidVolume.BUCKET;

    private boolean isPlayerInteracting = false;

    public final SimpleFixedFluidInv fluidInv;
    public final FluidSmoother smoothedTank;

    public TileTank() {
        super(SimplePipeBlocks.TANK_TILE);
        fluidInv = new SimpleFixedFluidInv(1, SINGLE_TANK_CAPACITY);
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
        tag.putBoolean("p", isPlayerInteracting);
        return tag;
    }

    @Override
    public void fromClientTag(CompoundTag tag) {
        super.fromClientTag(tag);
        if (tag.getBoolean("f")) {
            smoothedTank.handleMessage(getWorld(), tag);
            if (tag.getBoolean("p")) {
                smoothedTank.resetSmoothing(getWorld());
            }
        }
    }

    // ITickable

    @Override
    public void tick() {
        smoothedTank.tick(world);
        isPlayerInteracting = false;

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
    public boolean activate(PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (player.getStackInHand(hand).isEmpty()) {
            if (!world.isClient) {
                ContainerProviderRegistry.INSTANCE.openContainer(SimplePipeContainers.TANK, player, (buffer) -> {
                    buffer.writeBlockPos(pos);
                });
                return true;
            }
            return true;
        }
        if (world.isClient) {
            return true;
        }
        isPlayerInteracting = true;
        boolean didChange = FluidVolumeUtil.interactWithTank(fluidInv, player, hand);
        return didChange;
    }

    @Override
    public void invalidate() {
        super.invalidate();
        fluidInv.invalidateListeners();
    }

    // Rendering

    @Nullable
    public FluidStackInterp getFluidForRender(float partialTicks) {
        return smoothedTank.getFluidForRender(partialTicks);
    }
}
