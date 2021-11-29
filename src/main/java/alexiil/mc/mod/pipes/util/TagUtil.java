/*
 * Copyright (c) 2019 SpaceToad and the BuildCraft team
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0. If a copy of the MPL was not
 * distributed with this file, You can obtain one at https://mozilla.org/MPL/2.0/
 */
package alexiil.mc.mod.pipes.util;

import java.util.BitSet;
import java.util.EnumSet;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtByte;
import net.minecraft.nbt.NbtByteArray;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtString;

public final class TagUtil {
    private TagUtil() {}

    private static final String NULL_ENUM_STRING = "_NULL";

    public static <E extends Enum<E>> NbtElement writeEnum(E value) {
        if (value == null) {
            return NbtString.of(NULL_ENUM_STRING);
        }
        return NbtString.of(value.name());
    }

    public static <E extends Enum<E>> E readEnum(NbtElement tag, Class<E> clazz) {
        return readEnum(tag, clazz, null);
    }

    public static <E extends Enum<E>> E readEnum(NbtElement tag, Class<E> clazz, E defaultValue) {
        if (tag instanceof NbtString) {
            String value = ((NbtString) tag).asString();
            if (NULL_ENUM_STRING.equals(value)) {
                return defaultValue;
            }
            try {
                return Enum.valueOf(clazz, value);
            } catch (Throwable t) {
                // In case we didn't find the constant
                System.out.println("Tried and failed to read the value(" + value + ") from " + clazz.getSimpleName());
                t.printStackTrace();
                return defaultValue;
            }
        } else if (tag == null) {
            return defaultValue;
        } else {
            new IllegalArgumentException(
                "Tried to read an enum value when it was not a string! This is probably not good!"
            ).printStackTrace();
            return defaultValue;
        }
    }

    /** Writes an {@link EnumSet} to an {@link Tag}. The returned type will either be {@link ByteTag} or
     * {@link ByteArrayTag}.
     * 
     * @param clazz The class that the {@link EnumSet} is of. This is required as we have no way of getting the class
     *            from the set. */
    public static <E extends Enum<E>> NbtElement writeEnumSet(EnumSet<E> set, Class<E> clazz) {
        E[] constants = clazz.getEnumConstants();
        if (constants == null) throw new IllegalArgumentException("Not an enum type " + clazz);
        BitSet bitset = new BitSet();
        for (E e : constants) {
            if (set.contains(e)) {
                bitset.set(e.ordinal());
            }
        }
        byte[] bytes = bitset.toByteArray();
        if (bytes.length == 1) {
            return NbtByte.of(bytes[0]);
        } else {
            return new NbtByteArray(bytes);
        }
    }

    public static <E extends Enum<E>> EnumSet<E> readEnumSet(NbtElement tag, Class<E> clazz) {
        E[] constants = clazz.getEnumConstants();
        if (constants == null) throw new IllegalArgumentException("Not an enum type " + clazz);
        byte[] bytes;
        if (tag instanceof NbtByte) {
            bytes = new byte[] { ((NbtByte) tag).byteValue() };
        } else if (tag instanceof NbtByteArray) {
            bytes = ((NbtByteArray) tag).getByteArray();
        } else {
            bytes = new byte[] {};
            System.out.println("[lib.nbt] Tried to read an enum set from " + tag);
        }
        BitSet bitset = BitSet.valueOf(bytes);
        EnumSet<E> set = EnumSet.noneOf(clazz);
        for (E e : constants) {
            if (bitset.get(e.ordinal())) {
                set.add(e);
            }
        }
        return set;
    }

    public static NbtCompound getItemData(ItemStack stack) {
        if (stack.isEmpty()) {
            return new NbtCompound();
        }
        NbtCompound nbt = stack.getNbt();
        if (nbt == null) {
            nbt = new NbtCompound();
            stack.setNbt(nbt);
        }
        return nbt;
    }
}
