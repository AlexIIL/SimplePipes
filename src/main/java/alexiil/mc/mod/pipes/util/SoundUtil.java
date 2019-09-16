/*
 * Copyright (c) 2017 SpaceToad and the BuildCraft team
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0. If a copy of the MPL was not
 * distributed with this file, You can obtain one at https://mozilla.org/MPL/2.0/
 */

package alexiil.mc.mod.pipes.util;

import javax.annotation.Nullable;

import net.minecraft.block.BlockState;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.DyeColor;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class SoundUtil {
    public static void playBlockPlace(World world, BlockPos pos) {
        playBlockPlace(world, pos, world.getBlockState(pos));
    }

    public static void playBlockPlace(World world, BlockPos pos, BlockState state) {
        BlockSoundGroup soundType = state.getSoundGroup();
        float volume = (soundType.getVolume() + 1.0F) / 2.0F;
        float pitch = soundType.getPitch() * 0.8F;
        world.playSound(null, pos, soundType.getPlaceSound(), SoundCategory.BLOCKS, volume, pitch);
    }

    public static void playBlockBreak(World world, BlockPos pos) {
        playBlockBreak(world, pos, world.getBlockState(pos));
    }

    public static void playBlockBreak(World world, BlockPos pos, BlockState state) {
        BlockSoundGroup soundType = state.getSoundGroup();
        float volume = (soundType.getVolume() + 1.0F) / 2.0F;
        float pitch = soundType.getPitch() * 0.8F;
        world.playSound(null, pos, soundType.getBreakSound(), SoundCategory.BLOCKS, volume, pitch);
    }

    public static void playLeverSwitch(World world, BlockPos pos, boolean isNowOn) {
        float pitch = isNowOn ? 0.6f : 0.5f;
        SoundEvent soundEvent = SoundEvents.BLOCK_LEVER_CLICK;
        world.playSound(null, pos, soundEvent, SoundCategory.BLOCKS, 0.2f, pitch);
    }

    public static void playChangeColour(World world, BlockPos pos, @Nullable DyeColor colour) {
        BlockSoundGroup soundType = BlockSoundGroup.SLIME;
        final SoundEvent soundEvent;
        if (colour == null) {
            soundEvent = SoundEvents.ITEM_BUCKET_EMPTY;
        } else {
            // FIXME: is this a good sound? Idk tbh.
            // TODO: Look into configuring this kind of stuff.
            soundEvent = SoundEvents.ENTITY_SLIME_SQUISH;
        }
        float volume = (soundType.getVolume() + 1.0F) / 2.0F;
        float pitch = soundType.getPitch() * 0.8F;
        world.playSound(null, pos, soundEvent, SoundCategory.BLOCKS, volume, pitch);
    }

    public static void playSlideSound(World world, BlockPos pos) {
        playSlideSound(world, pos, world.getBlockState(pos));
    }

    public static void playSlideSound(World world, BlockPos pos, ActionResult result) {
        playSlideSound(world, pos, world.getBlockState(pos), result);
    }

    public static void playSlideSound(World world, BlockPos pos, BlockState state) {
        playSlideSound(world, pos, state, ActionResult.SUCCESS);
    }

    public static void playSlideSound(World world, BlockPos pos, BlockState state, ActionResult result) {
        if (result == ActionResult.PASS) return;
        BlockSoundGroup soundType = state.getSoundGroup();
        SoundEvent event;
        if (result == ActionResult.SUCCESS) {
            event = SoundEvents.BLOCK_PISTON_CONTRACT;
        } else {
            event = SoundEvents.BLOCK_PISTON_EXTEND;
        }
        float volume = (soundType.getVolume() + 1.0F) / 2.0F;
        float pitch = soundType.getPitch() * 0.8F;
        world.playSound(null, pos, event, SoundCategory.BLOCKS, volume, pitch);
    }
}
