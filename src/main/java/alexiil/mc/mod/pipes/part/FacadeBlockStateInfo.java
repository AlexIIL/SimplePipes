package alexiil.mc.mod.pipes.part;

import java.util.Objects;
import java.util.SortedMap;

import com.google.common.collect.ImmutableSet;

import net.minecraft.block.BlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.state.property.Property;
import net.minecraft.util.registry.Registry;

import alexiil.mc.lib.net.NetByteBuf;
import alexiil.mc.mod.pipes.util.MessageUtil;

public class FacadeBlockStateInfo {
    public final BlockState state;
    public final ItemStack requiredStack;
    public final ImmutableSet<Property<?>> varyingProperties;
    public final boolean isVisible;

    public FacadeBlockStateInfo(
        BlockState state, ItemStack requiredStack, ImmutableSet<Property<?>> varyingProperties
    ) {
        this.state = Objects.requireNonNull(state, "state must not be null!");
        Objects.requireNonNull(state.getBlock(), "state.getBlock must not be null!");
        Objects.requireNonNull(Registry.BLOCK.getId(state.getBlock()));
        this.requiredStack = requiredStack;
        this.varyingProperties = varyingProperties;
        this.isVisible = !requiredStack.isEmpty();
    }

    // Helper methods

    @Override
    public String toString() {
        return "StateInfo [id=" + System.identityHashCode(this) + ", block = " + state.getBlock() + ", state =  "
            + state.toString() + "]";
    }

    public static FacadeBlockStateInfo fromTag(CompoundTag nbt) {
        return fromTag(nbt, FacadeStateManager.getValidFacadeStates());
    }

    static FacadeBlockStateInfo fromTag(CompoundTag nbt, SortedMap<BlockState, FacadeBlockStateInfo> validStates) {
        try {
            FacadeBlockStateInfo stateInfo = FacadeStateManager.getDefaultState();
            BlockState blockState = NbtHelper.toBlockState(nbt);
            stateInfo = validStates.get(blockState);
            if (stateInfo == null) {
                stateInfo = FacadeStateManager.getDefaultState();
            }
            return stateInfo;
        } catch (Throwable t) {
            throw new RuntimeException("Failed badly when reading a facade state!", t);
        }
    }

    public CompoundTag toTag() {
        try {
            return NbtHelper.fromBlockState(state);
        } catch (Throwable t) {
            throw new IllegalStateException(
                "Writing facade block state"//
                    + "\n\tBlock = " + state.getBlock() + "\n\tBlock Class = " + state.getBlock().getClass(),
                t
            );
        }
    }

    public static FacadeBlockStateInfo readFromBuffer(NetByteBuf buf) {
        return readFromBuffer(buf, FacadeStateManager.getValidFacadeStates());
    }

    static FacadeBlockStateInfo readFromBuffer(
        NetByteBuf buf, SortedMap<BlockState, FacadeBlockStateInfo> validStates
    ) {
        BlockState state = MessageUtil.readBlockState(buf);
        FacadeBlockStateInfo info = validStates.get(state);
        if (info == null) {
            info = FacadeStateManager.getDefaultState();
        }
        return info;
    }

    public void writeToBuffer(NetByteBuf buf) {
        try {
            MessageUtil.writeBlockState(buf, state);
        } catch (Throwable t) {
            throw new IllegalStateException("Writing facade block state\n\tState = " + state, t);
        }
    }
}
