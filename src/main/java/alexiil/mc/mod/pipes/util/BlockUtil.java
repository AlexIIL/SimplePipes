package alexiil.mc.mod.pipes.util;

import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Sets;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.registry.Registries;
import net.minecraft.state.property.Property;
import net.minecraft.util.Identifier;

public final class BlockUtil {
    private BlockUtil() {}

    public static Comparator<BlockState> blockStateComparator() {
        return (blockStateA, blockStateB) -> {
            if (blockStateA == null) {
                return blockStateB == null ? 0 : 1;
            }
            if (blockStateB == null) {
                return -1;
            }
            Block blockA = blockStateA.getBlock();
            Block blockB = blockStateB.getBlock();
            if (blockA != blockB) {
                Identifier idA = Registries.BLOCK.getId(blockA);
                Identifier idB = Registries.BLOCK.getId(blockB);
                return Objects.toString(idA).compareTo(Objects.toString(idB));
            }
            for (Property<?> property : Sets
                .intersection(new HashSet<>(blockStateA.getProperties()), new HashSet<>(blockStateB.getProperties()))) {
                int compareResult = compareProperty(property, blockStateA, blockStateB);
                if (compareResult != 0) {
                    return compareResult;
                }
            }
            return 0;
        };
    }

    public static <T extends Comparable<T>> int compareProperty(Property<T> property, BlockState a, BlockState b) {
        return a.get(property).compareTo(b.get(property));
    }

    public static <T extends Comparable<T>> String getPropertyStringValue(BlockState blockState, Property<T> property) {
        return property.name(blockState.get(property));
    }

    public static Map<String, String> getPropertiesStringMap(
        BlockState blockState, Collection<Property<?>> properties
    ) {
        ImmutableMap.Builder<String, String> mapBuilder = new ImmutableMap.Builder<>();
        for (Property<?> property : properties) {
            mapBuilder.put(property.getName(), getPropertyStringValue(blockState, property));
        }
        return mapBuilder.build();
    }

    public static Map<String, String> getPropertiesStringMap(BlockState blockState) {
        return getPropertiesStringMap(blockState, blockState.getProperties());
    }
}
