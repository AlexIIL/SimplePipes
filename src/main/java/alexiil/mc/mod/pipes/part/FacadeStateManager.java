/*
 * Copyright (c) 2017 SpaceToad and the BuildCraft team
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0. If a copy of the MPL was not
 * distributed with this file, You can obtain one at https://mozilla.org/MPL/2.0/
 */

package alexiil.mc.mod.pipes.part;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Optional;
import java.util.SortedMap;
import java.util.TreeMap;

import javax.annotation.Nonnull;

import com.google.common.collect.ImmutableSet;

import net.fabricmc.fabric.api.event.registry.RegistryEntryAddedCallback;
import net.fabricmc.fabric.api.event.registry.RegistryEntryRemovedCallback;

import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.ConnectedPlantBlock;
import net.minecraft.block.FluidBlock;
import net.minecraft.block.GlassBlock;
import net.minecraft.block.StainedGlassBlock;
import net.minecraft.block.enums.Instrument;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.state.property.Property;
import net.minecraft.util.ActionResult;
import net.minecraft.util.BooleanBiFunction;
import net.minecraft.util.Identifier;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;

import alexiil.mc.lib.attributes.item.ItemStackCollections;
import alexiil.mc.lib.net.NetByteBuf;
import alexiil.mc.mod.pipes.SimplePipes;
import alexiil.mc.mod.pipes.util.BlockUtil;
import alexiil.mc.mod.pipes.util.SingleBlockView;

public final class FacadeStateManager {
    private static final FacadeStateManager INSTANCE;

    public static final boolean DEBUG = Boolean.getBoolean("simplepipes.debug_facades");
    private static final Map<Block, String> disabledBlocks = new HashMap<>();
    private static final Map<BlockState, ItemStack> customBlocks = new HashMap<>();
    private static final Map<Property<?>, Comparable<?>> limitedProperties = new IdentityHashMap<>();

    private static final FacadeBlockStateInfo defaultState;
    private static final SortedMap<BlockState, FacadeBlockStateInfo> validFacadeStates;
    private static final Map<ItemStack, List<FacadeBlockStateInfo>> stackFacades;
    private static FacadeBlockStateInfo previewState;

    /** An array containing all mods that fail the {@link #ensurePropertyConforms(Property)} check, and any others.
     * <p>
     * Note: Mods should ONLY be added to this list AFTER it has been reported to them, and taken off the list once a
     * version has been released with the fix. */
    private static final List<String> KNOWN_INVALID_REPORTED_MODS = Arrays.asList(new String[] { //
    });

    static {
        if (DEBUG) {
            SimplePipes.LOGGER.info("Debugging enabled for facades. Prepare for log spam!");
        } else {
            SimplePipes.LOGGER.debug(
                "Debugging disabled for facades. (Add -Dsimplepipes.debug_facades=true to enable)"
            );
        }

        limitedProperties.put(Properties.PERSISTENT, false);
        limitedProperties.put(Properties.DISTANCE_0_7, 0);
        limitedProperties.put(Properties.DISTANCE_1_7, 0);
        limitedProperties.put(Properties.AGE_1, 0);
        limitedProperties.put(Properties.AGE_2, 0);
        limitedProperties.put(Properties.AGE_3, 0);
        limitedProperties.put(Properties.AGE_5, 0);
        limitedProperties.put(Properties.AGE_7, 0);
        limitedProperties.put(Properties.AGE_15, 0);
        limitedProperties.put(Properties.AGE_25, 0);
        limitedProperties.put(Properties.POWERED, false);
        limitedProperties.put(Properties.INSTRUMENT, Instrument.HARP);
        limitedProperties.put(Properties.NOTE, 0);
        limitedProperties.put(Properties.WATERLOGGED, false);

        defaultState = new FacadeBlockStateInfo(Blocks.AIR.getDefaultState(), ItemStack.EMPTY, ImmutableSet.of());
        validFacadeStates = new TreeMap<>(BlockUtil.blockStateComparator());
        stackFacades = ItemStackCollections.map();

        previewState = defaultState;

        INSTANCE = new FacadeStateManager();
    }

    public static FacadeStateManager getInstance() {
        return INSTANCE;
    }

    private FacadeStateManager() {
        RuntimeException ex = null;
        for (Block block : Registry.BLOCK) {
            Identifier blockId = Registry.BLOCK.getId(block);
            try {
                scanBlock(block, blockId);
            } catch (Throwable t) {
                if (ex == null) {
                    ex = new RuntimeException("Failed to scan all block's while loading facades!");
                }
                ex.addSuppressed(t);
            }
        }
        if (ex != null) {
            throw ex;
        }
        // FIXME: This doesn't work! (Because the item won't have been registered for the block yet)
        RegistryEntryAddedCallback.event(Registry.BLOCK).register((rawId, identifier, object) -> {
            scanBlock(object, identifier);
        });
        RegistryEntryRemovedCallback.event(Registry.BLOCK).register((rawId, identifier, object) -> {
            // TODO: Implement block removal!
        });
        previewState = validFacadeStates.get(Blocks.BRICKS.getDefaultState());
    }

    public static SortedMap<BlockState, FacadeBlockStateInfo> getValidFacadeStates() {
        return validFacadeStates;
    }

    public static Map<ItemStack, List<FacadeBlockStateInfo>> getStackFacades() {
        return stackFacades;
    }

    public static FacadeBlockStateInfo getDefaultState() {
        return defaultState;
    }

    public static FacadeBlockStateInfo getPreviewState() {
        return previewState;
    }

    public static FacadeBlockStateInfo getInfoForBlock(Block block) {
        return getInfoForState(block.getDefaultState());
    }

    private static FacadeBlockStateInfo getInfoForState(BlockState state) {
        return getValidFacadeStates().get(state);
    }

    public static void load() {
        // Just to call the static init
    }

    private static void scanBlock(Block block, Identifier blockId) {
        if (!DEBUG && KNOWN_INVALID_REPORTED_MODS.contains(blockId.getNamespace())) {
            return;
        }

        // Check to make sure that all the properties work properly
        // Fixes a bug in extra utilities who doesn't serialise and deserialise properties properly
        {
            RuntimeException ex = null;
            for (Property<?> property : block.getStateFactory().getProperties()) {
                try {
                    ensurePropertyConforms(property);
                } catch (RuntimeException t) {
                    if (ex == null) {
                        ex = new IllegalStateException(
                            "Found some properties that didn't conform!\n(For block = " + blockId + ")"
                        );
                    }
                    ex.addSuppressed(t);
                }
            }
            if (ex != null) {
                throw ex;
            }
        }

        TypedActionResult<String> result = isValidFacadeBlock(block);
        if (result.getResult() != ActionResult.PASS && result.getResult() != ActionResult.SUCCESS) {
            if (DEBUG) {
                SimplePipes.LOGGER.info(
                    ("[silicon.facade] Disallowed block " + blockId) + " because " + result.getValue()
                );
            }
            return;
        } else if (DEBUG) {
            if (result.getResult() == ActionResult.SUCCESS) {
                SimplePipes.LOGGER.info("[silicon.facade] Allowed block " + blockId);
            }
        }
        Map<BlockState, ItemStack> usedStates = new HashMap<>();
        Map<ItemStack, Map<Property<?>, Comparable<?>>> varyingProperties = ItemStackCollections.map();
        for (BlockState state : block.getStateFactory().getStates()) {
            if (result.getResult() != ActionResult.SUCCESS) {
                TypedActionResult<String> stateResult = isValidFacadeState(state);
                if (stateResult.getResult() == ActionResult.SUCCESS) {
                    if (DEBUG) {
                        SimplePipes.LOGGER.info("[silicon.facade] Allowed state " + state);
                    }
                } else {
                    if (DEBUG) {
                        SimplePipes.LOGGER.info(
                            "[silicon.facade] Disallowed state " + state + " because " + stateResult.getValue()
                        );
                    }
                    continue;
                }
            }
            ItemStack stack = getRequiredStack(state);
            if (stack.isEmpty()) {
                if (DEBUG) {
                    SimplePipes.LOGGER.info(
                        "[silicon.facade] Disallowed state " + state + " because it didn't have an item!"
                    );
                }
                return;
            }
            usedStates.put(state, stack);
            Map<Property<?>, Comparable<?>> vars = varyingProperties.get(stack);
            if (vars == null) {
                vars = new HashMap<>(state.getEntries());
                varyingProperties.put(stack, vars);
            } else {
                for (Entry<Property<?>, Comparable<?>> entry : state.getEntries().entrySet()) {
                    Property<?> prop = entry.getKey();
                    Comparable<?> value = entry.getValue();
                    if (vars.get(prop) != value) {
                        vars.put(prop, null);
                    }
                }
            }
        }
        NetByteBuf testingBuffer = NetByteBuf.buffer();
        varyingProperties.forEach((key, vars) -> {
            if (DEBUG) {
                SimplePipes.LOGGER.info("[silicon.facade]   pre-" + key + ":");
                vars.keySet().forEach(p -> SimplePipes.LOGGER.info("[silicon.facade]       " + p));
            }
            vars.values().removeIf(Objects::nonNull);
            if (DEBUG && !vars.isEmpty()) {
                SimplePipes.LOGGER.info("[silicon.facade]   " + key + ":");
                vars.keySet().forEach(p -> SimplePipes.LOGGER.info("[silicon.facade]       " + p));
            }
        });
        for (Entry<BlockState, ItemStack> entry : usedStates.entrySet()) {
            BlockState state = entry.getKey();
            ItemStack stack = entry.getValue();
            Map<Property<?>, Comparable<?>> vars = varyingProperties.get(stack);
            try {
                ImmutableSet<Property<?>> varSet = ImmutableSet.copyOf(vars.keySet());
                FacadeBlockStateInfo info = new FacadeBlockStateInfo(state, stack, varSet);
                validFacadeStates.put(state, info);
                if (!info.requiredStack.isEmpty()) {
                    stackFacades.computeIfAbsent(info.requiredStack, k -> new ArrayList<>()).add(info);
                }

                // Test to make sure that we can read + write it
                CompoundTag nbt = info.toTag();
                FacadeBlockStateInfo read = FacadeBlockStateInfo.fromTag(nbt, validFacadeStates);
                if (read != info) {
                    throw new IllegalStateException(
                        "Read (from NBT) state was different! (\n\t" + read + "\n !=\n\t" + info + "\n\tNBT = " + nbt
                            + "\n)"
                    );
                }
                info.writeToBuffer(testingBuffer);
                read = FacadeBlockStateInfo.readFromBuffer(testingBuffer, validFacadeStates);
                if (read != info) {
                    throw new IllegalStateException(
                        "Read (from buffer) state was different! (\n\t" + read + "\n !=\n\t" + info + "\n)"
                    );
                }
                testingBuffer.clear();
                if (DEBUG) {
                    SimplePipes.LOGGER.info("[silicon.facade]   Added " + info);
                }
            } catch (Throwable t) {
                String msg = "Scanning facade states";
                msg += "\n\tState = " + state;
                msg += "\n\tBlock = " + blockId;
                msg += "\n\tStack = " + stack;
                msg += "\n\tvarying-properties: {";
                for (Entry<Property<?>, Comparable<?>> varEntry : vars.entrySet()) {
                    msg += "\n\t\t" + varEntry.getKey() + " = " + varEntry.getValue();
                }
                msg += "\n\t}";
                throw new IllegalStateException(msg.replace("\t", "    "), t);
            }
        }
    }

    /** @return One of:
     *         <ul>
     *         <li>{@link ActionResult#SUCCESS} if every state of the block is valid for a facade.
     *         <li>{@link ActionResult#PASS} if every metadata needs to be checked by
     *         {@link #isValidFacadeState(BlockState)}</li>
     *         <li>{@link ActionResult#FAIL} with string describing the problem with this block (if it is not valid for
     *         a facade)</li>
     *         </ul>
     */
    private static TypedActionResult<String> isValidFacadeBlock(Block block) {
        String disablingMod = disabledBlocks.get(block);
        if (disablingMod != null) {
            return new TypedActionResult<>(ActionResult.FAIL, "it has been disabled by " + disablingMod);
        }
        if (block instanceof FluidBlock) {
            return new TypedActionResult<>(ActionResult.FAIL, "it is a fluid block");
        }
        if (block instanceof GlassBlock || block instanceof StainedGlassBlock) {
            return new TypedActionResult<>(ActionResult.SUCCESS, "");
        }
        return new TypedActionResult<>(ActionResult.PASS, "");
    }

    /** @return Any of:
     *         <ul>
     *         <li>{@link ActionResult#SUCCESS} if this state is valid for a facade.
     *         <li>{@link ActionResult#FAIL} with string describing the problem with this state (if it is not valid for
     *         a facade)</li>
     *         </ul>
     */
    private static TypedActionResult<String> isValidFacadeState(BlockState state) {
        if (state.getBlock().hasBlockEntity()) {
            return new TypedActionResult<>(ActionResult.FAIL, "it has a tile entity");
        }
        if (state.getRenderType() != BlockRenderType.MODEL) {
            return new TypedActionResult<>(ActionResult.FAIL, "it doesn't have a normal model");
        }

        for (Property<?> prop : state.getProperties()) {
            Comparable<?> value = limitedProperties.get(prop);
            if (value != null && !Objects.equals(value, state.get(prop))) {
                return new TypedActionResult<>(
                    ActionResult.FAIL, "it has a property (" + prop + ") that doesn't match it's limited value!"
                );
            }
        }

        if (state.getProperties().containsAll(ConnectedPlantBlock.FACING_PROPERTIES.values())) {
            boolean val = state.get(ConnectedPlantBlock.DOWN);
            for (BooleanProperty prop : ConnectedPlantBlock.FACING_PROPERTIES.values()) {
                if (state.get(prop) != val) {
                    return new TypedActionResult<>(ActionResult.FAIL, "it is a mushroom with non-matching properties!");
                }
            }
        }

        VoxelShape shape = state.getCollisionShape(new SingleBlockView(state), SingleBlockView.POS);
        if (!VoxelShapes.combine(shape, VoxelShapes.fullCube(), BooleanBiFunction.ONLY_SECOND).isEmpty()) {
            return new TypedActionResult<>(ActionResult.FAIL, "it isn't a full cube");
        }
        return new TypedActionResult<>(ActionResult.SUCCESS, "");
    }

    @Nonnull
    private static ItemStack getRequiredStack(BlockState state) {
        ItemStack stack = customBlocks.get(state);
        if (stack != null) {
            return stack;
        }
        return new ItemStack(state.getBlock());
    }

    private static <V extends Comparable<V>> void ensurePropertyConforms(Property<V> property) throws RuntimeException {
        try {
            property.getValue("");
        } catch (AbstractMethodError error) {
            String message = "Invalid Property object detected!";
            message += "\n  Class = " + property.getClass();
            message += "\n  Method not overriden: Property.getValue(String)";
            RuntimeException exception = new RuntimeException(message, error);
            // if (BCLib.DEV || !BCLib.MC_VERSION.equals("1.12.2")) {
            throw exception;
            // } else {
            // SimplePipes.LOGGER.error("[silicon.facade] Invalid property!", exception);
            // }
            // return false;
        }

        for (V value : property.getValues()) {
            String name = property.getName(value);
            Optional<V> optional = property.getValue(name);
            V parsed = optional == null ? null : optional.orElse(null);
            if (!Objects.equals(value, parsed)) {
                // A property is *wrong*
                // this is a big problem
                String message = "Invalid property value detected!";
                message += "\n  Property class = " + property.getClass();
                message += "\n  Property = " + property;
                message += "\n  Possible Values = " + property.getValues();
                message += "\n  Value Name = " + name;
                message += "\n  Value (original) = " + value;
                message += "\n  Value (parsed) = " + parsed;
                message += "\n  Value class (original) = " + (value == null ? null : value.getClass());
                message += "\n  Value class (parsed) = " + (parsed == null ? null : parsed.getClass());
                if (optional == null) {
                    // Massive issue
                    message += "\n  Property.parseValue() -> Null java.util.Optional!!";
                }
                message += "\n";

                // This check *intentionally* crashes on a new MC version
                // or in a dev environment
                // as this really needs to be fixed

                // (fabric-port): For now just throw. Always
                RuntimeException exception = new RuntimeException(message);
                // if (BCLib.DEV || !BCLib.MC_VERSION.equals("1.12.2")) {
                throw exception;
                // } else {
                // SimplePipes.LOGGER.error("[silicon.facade] Invalid property!", exception);
                // }
            }
        }
    }

    // IFacadeRegistry

    public Collection<FacadeBlockStateInfo> getValidFacades() {
        return validFacadeStates.values();
    }
}
