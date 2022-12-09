package alexiil.mc.mod.pipes.part;

import java.util.Iterator;
import java.util.Objects;
import java.util.Optional;
import java.util.SortedMap;

import com.google.common.collect.ImmutableSet;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryEntryLookup;
import net.minecraft.state.State;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.Property;

import alexiil.mc.lib.net.NetByteBuf;
import alexiil.mc.mod.pipes.util.MessageUtil;
import net.minecraft.util.Identifier;

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
        Objects.requireNonNull(Registries.BLOCK.getId(state.getBlock()));
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

    public static FacadeBlockStateInfo fromTag(NbtCompound nbt) {
        return fromTag(nbt, FacadeStateManager.getValidFacadeStates());
    }

    public static BlockState toBlockState(NbtCompound compound) {
        if (!compound.contains("Name", 8)) {
            return Blocks.AIR.getDefaultState();
        } else {
            Block block = Registries.BLOCK.get(new Identifier(compound.getString("Name")));
            BlockState blockState = block.getDefaultState();
            if (compound.contains("Properties", 10)) {
                NbtCompound nbtCompound = compound.getCompound("Properties");
                StateManager<Block, BlockState> stateManager = block.getStateManager();
                Iterator var5 = nbtCompound.getKeys().iterator();

                while(var5.hasNext()) {
                    String string = (String)var5.next();
                    net.minecraft.state.property.Property<?> property = stateManager.getProperty(string);
                    if (property != null) {
                        blockState = withProperty(blockState, property, string, nbtCompound, compound);
                    }
                }
            }

            return blockState;
        }
    }

    private static <S extends State<?, S>, T extends Comparable<T>> S withProperty(S state, net.minecraft.state.property.Property<T> property, String key, NbtCompound properties, NbtCompound root) {
        Optional<T> optional = property.parse(properties.getString(key));
        if (optional.isPresent()) {
            return state.with(property, optional.get());
        } else {
            System.out.printf("Unable to read property: {%s} with value: {%s} for blockstate: {%s}", key, properties.getString(key), root.toString());
            return state;
        }
    }

    static FacadeBlockStateInfo fromTag(NbtCompound nbt, SortedMap<BlockState, FacadeBlockStateInfo> validStates) {
        try {
            FacadeBlockStateInfo stateInfo = FacadeStateManager.getDefaultState();
            BlockState blockState = FacadeBlockStateInfo.toBlockState(nbt);
            stateInfo = validStates.get(blockState);
            if (stateInfo == null) {
                stateInfo = FacadeStateManager.getDefaultState();
            }
            return stateInfo;
        } catch (Throwable t) {
            throw new RuntimeException("Failed badly when reading a facade state!", t);
        }
    }

    public NbtCompound toTag() {
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
